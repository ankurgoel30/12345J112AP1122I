package com.thinkhr.external.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Surabhi Bhawsar
 * @since 2018-01-03
 *
 */
@Data
@AllArgsConstructor
public class KeyValuePair {
    private String key;
    private String value;
}
