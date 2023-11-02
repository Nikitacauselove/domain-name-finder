package com.domainname.finder.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Set;

@Slf4j
public class FileWriterService {
    public static final String OUTPUT_DIRECTORY = "output";

    private static final ObjectWriter objectWriter = new ObjectMapper().writerWithDefaultPrettyPrinter();

    static {
        new File(String.format("./%s", OUTPUT_DIRECTORY)).mkdirs();
    }

    public static void write(Map<String, Set<String>> domainNames, String fileName) {
        try (Writer fileWriter = new FileWriter(String.format("%s/%s", OUTPUT_DIRECTORY, fileName))) {
            fileWriter.write(objectWriter.writeValueAsString(domainNames));
        } catch (IOException exception) {
            log.error(String.format("При попытке записи в файл %s возникла ошибка.", fileName), exception);
        }
    }
}
