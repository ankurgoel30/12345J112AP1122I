package com.thinkhr.external.api.model;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.thinkhr.external.api.ApplicationConstants.COMMA_SEPARATOR;
import static com.thinkhr.external.api.ApplicationConstants.UNDERSCORE;

import lombok.Data;

/**
 * Model class to support JSON bulk user upload
 * 
 * @author Surabhi Bhawsar
 * @since 2017-12-12
 *
 */

@Data
public class UserJsonBulk {

    private String firstName;
    private String lastName;
    private String clientName;
    private String email;
    private String userName;
    private String phone;
    private String businessId;

    /**
     * To get the header List from the attribute fields
     * 
     * @return
     */
    public String toHeadersFromField() {

        Field[] allFields = this.getClass().getDeclaredFields();
        StringBuilder companyModelHeader = new StringBuilder();

        for (Field field : allFields) {
            List<String> attrList = new ArrayList<String>(
                    Arrays.asList(field.getName().split("")));
            int index = 0;

            //If attribute is Camel case then insert an underscore just before the upper case character
            if (!field.getName().equals(field.getName().toLowerCase())) {
                for (String attr : attrList) {
                    if (!attr.equals(attr.toLowerCase())) {

                        //Adding the underscore in between the field name
                        companyModelHeader.append(COMMA_SEPARATOR)
                                .append(field.getName().substring(0, index))
                                .append(UNDERSCORE)
                                .append(field.getName().substring(index));
                        break;
                    }
                    index++;
                }
            } else {
                //Just appending the header as no camel casing for this attribute
                companyModelHeader.append(COMMA_SEPARATOR)
                        .append(field.getName());
            }
        }

        return companyModelHeader.toString().substring(1).toUpperCase();
    }

    /**
     * To get the value List from the attribute fields
     * 
     * @return
     */
    public String toValuesFromFields() {

        Field[] allFields = this.getClass().getDeclaredFields();
        StringBuilder companyModelValues = new StringBuilder();

        for (Field field : allFields) {
            try {
                field.setAccessible(true);
                if (field.get(this) != null) {
                    companyModelValues.append(COMMA_SEPARATOR)
                            .append(field.get(this).toString());
                } else {
                    companyModelValues.append(COMMA_SEPARATOR);
                }

            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        return companyModelValues.toString().substring(1);

    }

}
