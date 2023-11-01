package com.domainname.finder.model;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.HttpContext;

import java.security.cert.X509Certificate;
import java.util.*;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
@Slf4j
public class DomainNameFinder implements Callable<Set<String>> {
    private final CloseableHttpClient httpClient;
    private final HttpContext httpContext;
    private final HttpGet httpGet;

    public static final String SSL_CERTIFICATE = "sslCertificate";

    @Override
    public Set<String> call() throws Exception {
        log.info("{}: Начался поиск доменных имен.", httpGet.getURI());
        Set<String> domainNames = new HashSet<>();

        httpClient.execute(httpGet, httpContext);
        X509Certificate[] x509Certificates = (X509Certificate[]) httpContext.getAttribute(SSL_CERTIFICATE);

        for (X509Certificate x509Certificate : x509Certificates) {
            Collection<List<?>> alternativeNames = x509Certificate.getSubjectAlternativeNames() == null ? Collections.emptyList() : x509Certificate.getSubjectAlternativeNames();

            for (List<?> name : alternativeNames) {
                SanType sanType = SanType.of((int) name.get(0));

                if (sanType == SanType.DNS_NAME) {
                    domainNames.add((String) name.get(1));
                }
            }
        }

        log.info("{}: Поиск завершился успешно.", httpGet.getURI());
        return domainNames;
    }
}
