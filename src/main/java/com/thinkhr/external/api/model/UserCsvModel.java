package com.thinkhr.external.api.model;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.thinkhr.external.api.repositories.QueryBuilder;

import lombok.Data;

@Data
public class UserCsvModel {
    List<String> fileContents ;
    String headerLine ;
    Map<String, Integer> headerIndexMap;
    Map<String, String> headerVsColumnMap ;
    
    List<String> userColumnsToInsert = new ArrayList<String>(QueryBuilder.userRequiredFields);
    List<Object> userColumnValues = new ArrayList<Object>();
    String insertUserSql;
    PreparedStatement preparedStatement ;
}
