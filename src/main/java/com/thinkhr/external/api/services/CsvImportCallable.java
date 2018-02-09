package com.thinkhr.external.api.services;
import java.util.concurrent.Callable;

import com.thinkhr.external.api.model.CsvModel;

/**
 * Callable class to be used for importing Records in CSV in parallel
 * @author Ajay Jain
 *
 */
public class CsvImportCallable implements Callable<Void> {
    CsvModel csvModel;
    Integer recordIndex; //Index of the record to be imported from records in cSVModel
    Integer brokerId; //Broker Id for which record is to be imported

    ImportService importService;

    public CsvImportCallable(CsvModel csvModel, Integer recordIndex, Integer brokerId, ImportService service) {
        this.csvModel = csvModel;
        this.recordIndex = recordIndex;
        this.brokerId = brokerId;
        this.importService = service;
    }

    @Override
    public Void call() throws Exception {
        importService.addRecordForBulk(this.csvModel, recordIndex, brokerId);
        return null;
    }


}