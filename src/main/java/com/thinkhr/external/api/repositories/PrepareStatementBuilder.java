package com.thinkhr.external.api.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.PreparedStatementCreator;

/**
 * Prepared statement builder 
 * 
 * @author Surabhi Bhawsar
 * @Since 2017-11-27
 *
 */
public class PrepareStatementBuilder {
    
    /**
     * To get an instance of preparedStatement
     * 
     * @param query
     * @param values
     * @return
     */
    public static PreparedStatementCreator buildPreparedStatementCreator(String query, Object value) {
        List<Object> objectList = new ArrayList<Object>();
        objectList.add(value);
        
        return buildPreparedStatementCreator(query, objectList);
    }
 

    /**
     * To get an instance of preparedStatement
     * 
     * @param query
     * @param values
     * @return
     */
    public static PreparedStatementCreator buildPreparedStatementCreator(String query, List<Object> values) {
        return new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement statement = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                if (values == null) {
                    return statement;
                }
                for (int i = 0; i < values.size(); i++) {
                    Object value = values.get(i);
                    if (value instanceof String) {
                        statement.setString(i+1, (String)value);
                    } else {
                        statement.setObject(i + 1, value);
                    }
                }
                return statement;
            }
        };
    }

}
