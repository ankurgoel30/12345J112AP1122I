package com.thinkhr.external.api.model;

import static com.thinkhr.external.api.ApplicationConstants.COMMA_SEPARATOR;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.thinkhr.external.api.db.entities.Company;

import lombok.Data;

@Data
public class CsvModel {
    List<String> records = new ArrayList<String>();
    String headerLine ;
    String[] headersInCSV ;
    Map<String, Integer> headerIndexMap = new HashMap<String,Integer>();
    Map<String, String> headerVsColumnMap = new HashMap<String,String>() ;
    FileImportResult importResult  =  new FileImportResult();
    
    public void initialize(List<String> csvRecords , Integer brokerId) {
        this.headerLine = csvRecords.get(0);
        csvRecords.remove(0);
        this.records = csvRecords;
        this.headersInCSV = headerLine.split(COMMA_SEPARATOR);
        for (int i = 0; i < headersInCSV.length; i++) {
            headerIndexMap.put(headersInCSV[i], i);
        }
        
        this.importResult.initialize(this, brokerId);
    }
}
