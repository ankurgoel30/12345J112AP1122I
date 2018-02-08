package com.thinkhr.external.api.model;

import static com.thinkhr.external.api.ApplicationConstants.COMMA_SEPARATOR;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import lombok.Data;

/**
 * Model class to be used for CSV import
 * @author Ajay Jain
 *
 */
@Data
public class CsvModel {
    List<String> records = new ArrayList<String>();
    String headerLine ;
    String[] headersInCSV ;
    Map<String, Integer> headerIndexMap = new HashMap<String,Integer>();
    Map<String, Map<String, String>> headerVsColumnMap = new HashMap<String, Map<String,String>>();
    FileImportResult importResult  =  new FileImportResult();
    
    /**
     * 
     * @param csvRecords
     * @param brokerId
     */
    public void initialize(List<String> csvRecords , Integer brokerId) {
        this.headerLine = csvRecords.get(0);
        csvRecords.remove(0);
        this.records = csvRecords;
        this.headersInCSV = headerLine.split(COMMA_SEPARATOR);
        IntStream.range(0, headersInCSV.length).forEach(i -> 
                 headerIndexMap.put(headersInCSV[i], i)
        );
        
        this.importResult.initialize(this, brokerId);
    }
}
