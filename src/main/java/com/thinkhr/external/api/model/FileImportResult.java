package com.thinkhr.external.api.model;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.thinkhr.external.api.exception.APIErrorCodes;
import com.thinkhr.external.api.response.APIMessageUtil;

/**
 * This class is used to collect information when records from csv file
 * 
 * are imported into a table
 * @author Admin
 *
 */
@Data
public class FileImportResult {
    private int totalRecords;
    private int numSuccessRecords;
    private int numFailedRecords;

    @JsonIgnore
    private String headerLine; // For storing header to be used for creating responseFile

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<FailedRecord> failedRecords = new ArrayList<FailedRecord>();

    public void increamentSuccessRecords() {
        numSuccessRecords++;
    }

    public void increamentFailedRecords() {
        numFailedRecords++;
    }

    public void addFailedRecord(int index, String record, String failureCause, String info) {
        increamentFailedRecords();
        this.getFailedRecords().add(new FailedRecord(index, record, failureCause, info));
    }

    @Data
    @AllArgsConstructor
    public class FailedRecord {
        int index; //  line number of failed record
        String record;// Actual record in file
        String failureCause;
        String info;//additional information
    }

}
