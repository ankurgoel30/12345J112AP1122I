package com.thinkhr.external.api.repositories;

import org.springframework.data.repository.CrudRepository;

import com.thinkhr.external.api.db.entities.EmailTemplate;

/**
 * Email Template repository for EmailTemplate entity.
 * 
 * @author Surabhi Bhawsar
 * @since 2018-01-03
 *
 */
public interface EmailTemplateRepository extends CrudRepository<EmailTemplate, Integer> {

    /**
     * 
     * @param brokerId
     * @param type
     * @return
     */
    public EmailTemplate findFirstByBrokerIdAndType(Integer brokerId, String type);

}
