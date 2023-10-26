package com.example.domainnamefinder.service;

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
    private static final ObjectWriter OBJECT_WRITER = new ObjectMapper().writerWithDefaultPrettyPrinter();
    private static final String OUTPUT_DIRECTORY = "output";

    static {
        new File(String.format("./%s", OUTPUT_DIRECTORY)).mkdirs();
    }

    public static void write(Map<String, Set<String>> domainNames, String fileName) {
        try (Writer fileWriter = new FileWriter(String.format("%s/%s", OUTPUT_DIRECTORY, fileName))) {
            fileWriter.write(OBJECT_WRITER.writeValueAsString(domainNames));
        } catch (IOException exception) {
            log.info("При попытке записи в файл {} возникла ошибка.", fileName);
        }
    }
}
