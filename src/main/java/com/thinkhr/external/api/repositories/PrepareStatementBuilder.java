package com.thinkhr.external.api.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
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
     * @param query
     * @param values
     * @return
     */
    public static PreparedStatementCreator buildPreparedStatementCreator(String query, List<Object> values) {
        return new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement statement = con.prepareStatement(query.toString(), Statement.RETURN_GENERATED_KEYS);
                int i;
                for (i = 0; i < values.size(); i++) {
                    statement.setString(i + 1, (String) values.get(i));
                }
                return statement;
            }
        };
    }

}
