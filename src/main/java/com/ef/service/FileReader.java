package com.ef.service;

import com.ef.model.LogData;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Component
public class FileReader {

    /**
     * Method to read data from the file convert them to a list of LogData objects
     *
     * @return List<LogData> list of log data objects
     */
    public List<LogData> read(String filePath) {
        List<LogData> list = new ArrayList<>();
        Path path = Paths.get(filePath);
        try (Stream<String> lines = Files.lines(path)) {
            lines.forEach(line -> list.add(parse(line)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }


    /**
     *
     * @param line each line
     * @return LogData object
     */
    private LogData parse(String line){

        String[] arr = line.split("\\|");

        LogData logData = new LogData();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        logData.setLogDate(LocalDateTime.parse(arr[0], formatter));
        logData.setIp(arr[1]);
        logData.setRequest(arr[2]);
        logData.setStatus(Integer.parseInt(arr[3]));
        logData.setUserAgent(arr[4]);

        return logData;

    }

}
