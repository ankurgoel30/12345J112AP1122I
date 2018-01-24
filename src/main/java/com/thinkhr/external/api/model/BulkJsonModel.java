package com.thinkhr.external.api.model;

import static com.thinkhr.external.api.ApplicationConstants.COMMA_SEPARATOR;
import static com.thinkhr.external.api.ApplicationConstants.UNDERSCORE;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import lombok.Data;

/**
 * @author Surabhi Bhawsar
 *
 */
@Data
public class BulkJsonModel {

    /**
     * To get the header List from the attribute fields
     * 
     * @return
     * @throws Exception 
     */
    public String toHeadersFromField() throws Exception {
        return getCommaSeparatedStr(true);
    }

    /**
     * @param collectName
     * @return
     * @throws Exception 
     */
    private String getCommaSeparatedStr(Boolean collectName) throws Exception {
        StringBuilder strBuilder = null;
        
        Field[] allFields = this.getClass().getDeclaredFields();
        
        for (Field field : allFields) {
            
            if (strBuilder == null) {
                strBuilder = new StringBuilder();
            } else {
                strBuilder.append(COMMA_SEPARATOR);
            }
            
            if (collectName) {
                strBuilder.append(covertWithUnderscore(field.getName()));
            } else {
                strBuilder.append(getValue(field));
            }
        }
     
        return strBuilder.toString();
    }

    /**
     * To get the value List from the attribute fields
     * 
     * @return
     */
    public String getAttributeValueAsCommaSeparated() throws Exception {
        return getCommaSeparatedStr(false);
    }
    
    /**
     * @param field
     * @return
     * @throws Exception
     */
    public String getValue(Field field) throws Exception {
        return field.get(this) == null ? "" : field.get(this).toString();
    }

    /**
     * @param fieldName
     * @return
     */
    public String covertWithUnderscore(String fieldName) {
        if (StringUtils.isBlank(fieldName)) {
            return fieldName;
        }
        int uclIndex = getIndexOfUCL(fieldName);
        if (uclIndex > 0 ){
            char upperCaseLetter = fieldName.charAt(uclIndex);
            char convertedToLower = Character.toLowerCase(upperCaseLetter);
            return fieldName.replace(Character.toString(upperCaseLetter), "_" + Character.toString(convertedToLower));
        } 

        return fieldName;
    }
    
    /**
     * @param str
     * @return
     */
    public int getIndexOfUCL(String str) {
        for(int i=0; i<str.length(); i++) {
            if(Character.isUpperCase(str.charAt(i))) {
                return i;
            }
        }
        return -1;
    }
}
