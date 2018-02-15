package com.thinkhr.external.api.model;

import static com.thinkhr.external.api.ApplicationConstants.COMMA_SEPARATOR;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

/**
 * Model Class for Bulk Json Uploads
 * 
 * @author Surabhi Bhawsar
 * @since 2018-01-24
 *
 */
@Data
public class BulkJsonModel {
    
    /**
     * Linked HashMap for storing all Json fields
     */
    @JsonIgnore
    private Map<String, Object> properties = new LinkedHashMap<>();

    @JsonAnySetter
    public void add(String key, String value) {
        properties.put(key, value);
    }

    @JsonAnyGetter
    public Map<String, Object> getProperties() {
        return properties;
    }

    /**
     * To get the header List from the attribute fields
     * 
     * @return
     * @throws Exception 
     */
    @JsonIgnore
    public String getAttributeNamesAsCommaSeparated() {
        return getCommaSeparatedStr(true).toUpperCase();
    }

    /**
     * To prepare comma seperated strings for Attribute Names and Attribute values
     * 
     * @param collectName
     * @return
     * @throws Exception 
     */
    private String getCommaSeparatedStr(Boolean collectName) {
        StringBuilder strBuilder = null;
        
        for (Map.Entry<String, Object> entry : this.properties.entrySet()) {

            if (strBuilder == null) {
                strBuilder = new StringBuilder();
            } else {
                strBuilder.append(COMMA_SEPARATOR);
            }
            
            if (collectName) {
                strBuilder.append(covertWithUnderscore(entry.getKey()));
            } else {
                strBuilder.append(entry.getValue().toString());
            }
        }
     
        return strBuilder.toString();
    }

    /**
     * To get the value List from the attribute fields
     * 
     * @return
     */
    @JsonIgnore
    public String getAttributeValueAsCommaSeparated() {
        return getCommaSeparatedStr(false);
    }

    /**
     * To insert underscore in between the Camel case attribute names
     * 
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
     * To get index of the first Upper Case character in String
     * 
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
