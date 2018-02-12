package com.thinkhr.external.api.services;

import static com.thinkhr.external.api.services.utils.FileImportUtil.getRequiredHeaders;
import static com.thinkhr.external.api.services.utils.FileImportUtil.validateAndFilterCustomHeaders;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thinkhr.external.api.model.CsvModel;
import com.thinkhr.external.api.model.FileImportResult;

/**
 * Common Import Service to hold all general operations related with Bulk APIs
 * 
 * @author Surabhi Bhawsar
 * @Since 2018-02-09
 *
 */
public abstract class ImportService extends CommonService {
    
    private Logger logger = LoggerFactory.getLogger(ImportService.class);

    /**
     * 
     * @param csvModel
     * @param recordIndex
     * @param brokerId
     */
    protected abstract void addRecordForBulk(CsvModel csvModel, Integer recordIndex, Integer brokerId);
    
    /**
     * Process imported file to save records in database
     * 
     * @param resource
     * @param csvModel
     * @return
     */
    protected FileImportResult processCsvModel(String resource, CsvModel csvModel) {
        FileImportResult fileImportResult = csvModel.getImportResult();
        String[] headersInCSV = csvModel.getHeadersInCSV();
        Map<String, Map<String, String>> headerVsColumnMap = csvModel.getHeaderVsColumnMap();

        //Check every custom field from imported file has a corresponding column in database. If not, return error here.
        String[] requiredHeaders = getRequiredHeaders(resource);
        validateAndFilterCustomHeaders(headersInCSV, headerVsColumnMap.get(resource).values(), requiredHeaders, resourceHandler);

        //Setup executor service for adding records in parallel
        ExecutorService executor = Executors.newFixedThreadPool(threadPoolSize);
        List<Future<Void>> futureList = new ArrayList<Future<Void>>(csvModel.getRecords().size());

        for (int recordIndex = 0; recordIndex < csvModel.getRecords().size(); recordIndex++) {
            Callable<Void> worker = new CsvImportCallable(csvModel, recordIndex, fileImportResult.getBrokerId(), this);

            Future<Void> future = executor.submit(worker);
            futureList.add(future);
        }

        executor.shutdown();

        // Wait for all the task to be completed 
        while (!executor.isTerminated()) {
        }

        // Capture any exceptions if occurred during the execution of any tasks
        for (int i = 0; i < futureList.size(); i++) {
            Future<Void> future = futureList.get(i);
            try {
                future.get();
            } catch (InterruptedException e) {
                fileImportResult.addFailedRecord(csvModel.getRecords().get(i), e.getLocalizedMessage(), null);
            } catch (ExecutionException e) {
                fileImportResult.addFailedRecord(csvModel.getRecords().get(i), e.getLocalizedMessage(), null);
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug(fileImportResult.toString());
        }

        return fileImportResult;
    }
    
}