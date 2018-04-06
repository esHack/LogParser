package com.ef.service;

import com.ef.model.ErrorLog;
import com.ef.model.ErrorLogRepository;
import com.ef.model.LogData;
import com.ef.model.LogDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class LogDataService {

    @Autowired
    LogDataRepository logDataRepository;

    @Autowired
    ErrorLogRepository errorLog;

    private static final Logger logger = LoggerFactory.getLogger(LogDataService.class);

    /**
     * Method to insert log data into database. Method will be executed asynchronously
     *
     * @param data data
     * @return boolean values
     * @throws InterruptedException
     */

    @Async
    public CompletableFuture<Boolean> insertData(List<LogData> data) {

        logger.info("inserting records : " + data.size());
        logDataRepository.saveAll(data);
        logger.info(data.size() + " records saved");
        return CompletableFuture.completedFuture(true);
    }

    /**
     * Method to fetch count of IPs based on the command line arguments
     *
     * @param startTime start time
     * @param endTime   end time
     * @param count     threshold
     * @return list of results
     */
    public List<Object[]> fetchData(LocalDateTime startTime, LocalDateTime endTime, int count) {
        logger.info("fetching data ");
        return logDataRepository.getyDateasper(startTime, endTime, count);
    }

    /**
     * insert data to error log
     *
     * @param errData List of error logs
     */
    public void insertErrorData(List<ErrorLog> errData) {
        logger.info("saving data to error log table");
        errorLog.saveAll(errData);
    }

}
