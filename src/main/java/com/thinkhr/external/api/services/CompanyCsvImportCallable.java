package com.thinkhr.external.api.services;

import java.util.Map;
import java.util.concurrent.Callable;

import com.thinkhr.external.api.model.CsvModel;

/**
 * Callable class to be used for importing Records in CSV in parallel
 * 
 * @author Ajay Jain
 *
 */
public class CompanyCsvImportCallable implements Callable<Void> {
    CsvModel csvModel;
    Integer recordIndex; //Index of the record to be imported from records in cSVModel
    Integer brokerId; //Broker Id for which record is to be imported

    CompanyService companyService;
    Map<String, String> locationHeaderColumnMap;

    public CompanyCsvImportCallable(CsvModel csvModel, Integer recordIndex, Integer brokerId, CompanyService companyService, Map<String, String> locationColumnMap) {
        this.csvModel = csvModel;
        this.recordIndex = recordIndex;
        this.brokerId = brokerId;
        this.companyService = companyService;
        this.locationHeaderColumnMap = locationColumnMap;
    }

    @Override
    public Void call() throws Exception {
        companyService.addCompanyRecordForBulk(this.csvModel, recordIndex, brokerId, locationHeaderColumnMap);
        return null;
    }
}