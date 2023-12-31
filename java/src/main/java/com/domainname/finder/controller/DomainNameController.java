package com.domainname.finder.controller;

import com.domainname.finder.service.DomainNameService;
import com.domainname.finder.service.FileWriterService;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;

@Slf4j
public class DomainNameController {
    public static void findAll(String ip, Integer thread) {
        log.info("{}: Новый запрос на поиск доменных имен с использованием {} потока(ов).", ip, thread);
        Map<String, Set<String>> domainNames = DomainNameService.findAll(ip, thread);
        String fileName = ip.replace('/', '(') + ").txt";

        FileWriterService.write(domainNames, fileName);
    }
}
