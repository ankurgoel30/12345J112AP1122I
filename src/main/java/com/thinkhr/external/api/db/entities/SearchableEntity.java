package com.thinkhr.external.api.db.entities;

import java.util.List;

/**
 * Interface for Search utility
 * @author Surabhi Bhawsar
 * @since 2017-11-09
 *
 */
public interface SearchableEntity {
    public List<String> getSearchFields();
    public String getNodeName();
    public String getMultiDataNodeName();
    
}
