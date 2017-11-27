package com.thinkhr.external.api.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

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
    public static PreparedStatementCreator buildPreparedStatement(String query, Object[] values) {
        return new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement statement = con.prepareStatement(query.toString(), Statement.RETURN_GENERATED_KEYS);
                int i;
                for (i = 0; i < values.length; i++) {
                    statement.setString(i + 1, (String) values[i]);
                }
                statement.setString(++i, "");
                statement.setString(++i, "");
                statement.setString(++i, "");
                statement.setDate(++i, new java.sql.Date(System.currentTimeMillis()));
                statement.setInt(++i,1); //default all clients are active
                return statement;
            }
        };
    }

}
