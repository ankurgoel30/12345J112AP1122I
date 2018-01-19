package com.thinkhr.external.api.model;

import static com.thinkhr.external.api.ApplicationConstants.COMMA_SEPARATOR;
import static com.thinkhr.external.api.ApplicationConstants.NEW_LINE;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * This class is used to collect information when records from csv file
 * 
 * are imported into a table
 * @author Admin
 *
 */
@Data
public class FileImportResult {
    @JsonIgnore
    private int brokerId;
    private int totalRecords;
    private int numSuccessRecords;
    private int numFailedRecords;
    private int numBlankRecords;
    private int recCount = 0;

    private String headerLine; // For storing header to be used for creating responseFile
    
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<FailedRecord> failedRecords = new ArrayList<FailedRecord>();

    public void increamentSuccessRecords() {
        numSuccessRecords++;
    }

    public void increamentFailedRecords() {
        numFailedRecords++;
    }
    
    public void increamentBlankRecords() {
        numBlankRecords++;
    }

    public void addFailedRecord(String record, String failureCause, String info) {
        increamentFailedRecords();
        this.getFailedRecords().add(new FailedRecord(recCount++, record, failureCause, info));
    }
    
    @Data
    @AllArgsConstructor
    public class FailedRecord {
        int index; //  line number of failed record
        String record;// Actual record in file
        String failureCause;
        String info;//additional information
    }

    /**
     * Print report
     * 
     * @param jobId
     * @param reportTitle
     * @param failureTitle
     * @param failureCause
     * @return
     */
    @JsonIgnore
    public String printReport(String jobId, String reportTitle, String failureTitle, String failureCause) {
        StringBuffer stb = new StringBuffer();
        stb.append("Job Id : " + jobId)
        .append(NEW_LINE)
        .append(reportTitle)
        .append(NEW_LINE);
        if (numFailedRecords > 0) {
            stb.append(failureTitle)
            .append(NEW_LINE)
            .append(getHeaderLine())
            .append(COMMA_SEPARATOR)
            .append(failureCause)
            .append(NEW_LINE);
        }

        for (FileImportResult.FailedRecord failedRecord : getFailedRecords()) {
            stb.append(failedRecord.getRecord()).append(COMMA_SEPARATOR).append(failedRecord.getFailureCause()).append(NEW_LINE);
        }

        return stb.toString();
    }
    
    @Override
    public String toString() {
        StringBuffer stb = new StringBuffer();
        stb.append("Total Number of Records: " + this.getTotalRecords()).append(NEW_LINE);
        stb.append("Total Number of Successful Records: " + this.getNumSuccessRecords()).append(NEW_LINE);
        stb.append("Total Number of Failure Records: " + this.getNumFailedRecords()).append(NEW_LINE);
        stb.append("Total Number of Blank Records: " + this.getNumBlankRecords()).append(NEW_LINE);
        
        if (this.getNumFailedRecords() > 0) {
            stb.append("List of Failure Records").append(NEW_LINE);
            for (FileImportResult.FailedRecord failedRecord : this.getFailedRecords()) {
                stb.append(failedRecord.getRecord() + COMMA_SEPARATOR + failedRecord.getFailureCause()).append(NEW_LINE);
            }
        }
        return stb.toString();
    }

    /**
     * @return
     */
    public boolean hasFailures() {
        return numSuccessRecords < totalRecords ? true : false;
    }
    
    /**
     * @return
     */
    public boolean allFailures() {
       return totalRecords > 0 && numSuccessRecords == 0 ? true : false;
    }
    
    /**
     * @return
     */
    public boolean allSuccess() {
        return totalRecords == numSuccessRecords ? true : false;
    }
    
    /**
     * @return
     */
    public HttpStatus getHttpStatus () {
        if (allSuccess()) {
            return HttpStatus.OK;
        } else if (allFailures()) {
            return HttpStatus.BAD_REQUEST;
        } else 
            return HttpStatus.MULTI_STATUS;
    }
}
