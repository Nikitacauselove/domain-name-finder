package com.example.domainnamefinder.service;

import com.example.domainnamefinder.model.DomainNameFinder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.util.SubnetUtils;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ManagedHttpClientConnection;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpCoreContext;

import javax.net.ssl.SSLSession;
import java.util.*;
import java.util.concurrent.*;

@Slf4j
public class DomainNameService {
    private static final int CONNECT_TIMEOUT = 1000;
    private static final HttpResponseInterceptor CERTIFICATE_INTERCEPTOR = (httpResponse, context) -> {
        ManagedHttpClientConnection connection = (ManagedHttpClientConnection) context.getAttribute(HttpCoreContext.HTTP_CONNECTION);
        SSLSession sslSession = connection.getSSLSession();

        if (sslSession != null) {
            context.setAttribute(DomainNameFinder.SSL_CERTIFICATE, sslSession.getPeerCertificates()[0]);
        }
    };
    private static final RequestConfig REQUEST_CONFIG = RequestConfig.custom().setConnectTimeout(CONNECT_TIMEOUT).build();
    private static final CloseableHttpClient HTTP_CLIENT = HttpClients.custom().addInterceptorFirst(CERTIFICATE_INTERCEPTOR).setDefaultRequestConfig(REQUEST_CONFIG).build();

    public static Map<String, Set<String>> findAll(String ip, Integer numberOfThreads) {
        String[] allAddresses = new SubnetUtils(ip).getInfo().getAllAddresses();
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        Queue<Future<Set<String>>> futureQueue = new ConcurrentLinkedQueue<>();

        for (String address : allAddresses) {
            String uri = String.format("https://%s", address);
            DomainNameFinder finder = new DomainNameFinder(HTTP_CLIENT, new BasicHttpContext(), new HttpGet(uri));

            futureQueue.add(executorService.submit(finder));
        }

        Map<String, Set<String>> result = new HashMap<>();
        for (String address : allAddresses) {
            try {
                result.put(address, futureQueue.poll().get());
            } catch (ExecutionException | InterruptedException exception) {
                log.info("{}: Поиск доменных имен завершился раньше времени.", address);
            }
        }
        executorService.shutdown();
        return result;
    }
}
