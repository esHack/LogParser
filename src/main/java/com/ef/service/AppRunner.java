package com.ef.service;

import com.ef.model.ErrorLog;
import com.ef.model.LogData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Component
public class AppRunner implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(AppRunner.class);

    private LogDataService logDataService;

    private FileReader fileReader;

    public AppRunner(LogDataService logDataService, FileReader fileReader) {
        this.logDataService = logDataService;
        this.fileReader = fileReader;
    }

    /**
     * Method to parse the command line arguments that decide the operation
     *
     * @param args arguments from command line
     */
    @Override
    public void run(ApplicationArguments args) {

        if (args.getSourceArgs().length == 0)
            logger.info("data insertion " + insertData());
        else if (args.getSourceArgs().length != 3)
            printUsage();
        else
            fetchData(args);

    }

    /**
     * Method to fetch data from database and print it on console aand save the data as well to error log table
     *
     * @param args arguments from command line
     */
    private void fetchData(ApplicationArguments args) {

        String startTime = null;
        String duration = null;
        String threshold = null;

        for (String name : args.getOptionNames()) {
            logger.info("arg-" + name + "=" + args.getOptionValues(name));
            if (("startDate").equals(name))
                startTime = args.getOptionValues(name).get(0);
            else if (("duration").equals(name))
                duration = args.getOptionValues(name).get(0);
            else if (("threshold").equals(name))
                threshold = args.getOptionValues(name).get(0);
            else
                printUsage();

        }


        List<Object[]> results = logDataService.fetchData(getDate(startTime), getEndTime(getDate(startTime), duration), Integer.parseInt(threshold));
        List<ErrorLog> errorLogs = new ArrayList<>();

        for (Object[] obj : results) {
            logger.info("IP: " + obj[1] + " made " + obj[0] + " requests in which is more than specified threshold of " + threshold);
            ErrorLog errorLog = new ErrorLog();
            errorLog.setIp(obj[1].toString());
            errorLog.setRemarks(obj[0].toString());
            errorLogs.add(errorLog);
        }

        logDataService.insertErrorData(errorLogs);
    }

    /**
     * read from the file and update the database
     * <p>
     * Updates will be asynchronous , batch wise and parallel threads will be executing
     *
     * @return Success or failure
     */
    private String insertData() {

        // Start the clock
        long start = System.currentTimeMillis();

        // Kick of multiple, asynchronous lookups
        List<LogData> logData = fileReader.read("C:\\Users\\yaga\\Documents\\assign\\wallet\\Java_MySQL_Test\\access.log");
        List<List<LogData>> partitions = splitData(logData);
        List<CompletableFuture<Boolean>> pages = new ArrayList<>();

        for (int i = 0; i < partitions.size(); i++) {
            pages.add(logDataService.insertData(partitions.get(i)));
        }

        // Wait until they are all done
        CompletableFuture.allOf(pages.toArray(new CompletableFuture[pages.size()])).join();

        // Print results, including elapsed time
        logger.info("Elapsed time: " + (System.currentTimeMillis() - start));
        boolean status = true;
        for (CompletableFuture<Boolean> page : pages) {
            try {
                if (!page.get().booleanValue())
                    status = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        return status ? "SUCCESS" : "FAIL";
    }

    /**
     * Method to split the list into sub lists
     *
     * @param logData
     * @return List of LogData
     */
    private List<List<LogData>> splitData(List<LogData> logData) {
        int partitionSize = 100;
        List<List<LogData>> partitions = new ArrayList<>();

        for (int i = 0; i < logData.size(); i += partitionSize) {
            partitions.add(logData.subList(i, Math.min(i + partitionSize, logData.size())));
        }

        return partitions;
    }

    /**
     * Method to print usage
     */
    private void printUsage() {
        logger.info("Usage: java -jar logparser-0.0.1.jar ");
        logger.info("[or]");
        logger.info("Usage: java -jar logparser-0.0.1.jar --startDate=2017-01-01.13:00:00 --duration=hourly --threshold=100");
        System.exit(0);
    }

    /**
     * Convert String to LocalDateTime
     *
     * @param date date string
     * @return LocalDateTime type
     */
    private LocalDateTime getDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd.HH:mm:ss");
        return LocalDateTime.parse(date, formatter);
    }


    /**
     * Add hour / day to date passed
     *
     * @param startDate start date
     * @param duration  hourly/daily
     * @return LocalDateTime type
     */
    private LocalDateTime getEndTime(LocalDateTime startDate, String duration) {
        LocalDateTime endTime = startDate;
        if (duration.equals("hourly"))
            endTime = endTime.plusHours(1);
        if (duration.equals("daily"))
            endTime = endTime.plusDays(1);
        return endTime;
    }

}
