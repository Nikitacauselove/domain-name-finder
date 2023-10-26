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

        try {
            httpClient.execute(httpGet, httpContext);
        } catch (ConnectTimeoutException exception) {
            log.info("{}: Поиск завершился из-за слишком долгого времени ожидания отклика.", httpGet.getURI());

            return Collections.emptySet();
        }

        X509Certificate x509Certificate = (X509Certificate) httpContext.getAttribute(SSL_CERTIFICATE);

        try {
            for (List<?> name : x509Certificate.getSubjectAlternativeNames()) {
                SanType sanType = SanType.of((int) name.get(0));

                if (sanType == SanType.DNS) {
                    domainNames.add((String) name.get(1));
                }
            }
        } catch (NullPointerException exception) {
            log.info("{}: Поиск завершился из-за отсутствия SSL-сертификата.", httpGet.getURI());

            return Collections.emptySet();
        } catch (Exception exception) {
            String name = x509Certificate.getSubjectX500Principal().getName().split(",")[0];
            String commonName = name.replace("CN=", "");

            domainNames.add(commonName);
        }

        log.info("{}: Поиск завершился успешно.", httpGet.getURI());
        return domainNames;
    }
}
