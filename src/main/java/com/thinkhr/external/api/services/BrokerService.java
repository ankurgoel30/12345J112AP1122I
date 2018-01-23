package com.thinkhr.external.api.services;

import static com.thinkhr.external.api.ApplicationConstants.COMPANY_TYPE_BROKER;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinkhr.external.api.db.entities.Company;
import com.thinkhr.external.api.db.entities.Configuration;
import com.thinkhr.external.api.exception.ApplicationException;


/**
 *
 * Provides a collection of all services related with Company with type = Broker
 * database object

 * @author Surabhi Bhawsar
 * @Since 2018-01-23
 *
 * 
 */
@Service
public class BrokerService extends CompanyService {
    
    /**
    *
    * To fetch companies records. Based on given parameters companies records will be filtered out.
    * 
    * @param Integer offset First record index from database after sorting. Default value is 0
    * @param Integer limit Number of records to be fetched. Default value is 50
    * @param String sortField Field on which records needs to be sorted
    * @param String searchSpec Search string for filtering results
    * @param Map<String, String>
    * @return List<Company> object 
    * @throws ApplicationException 
    * 
    */
   public List<Company> getAllCompany(Integer offset, 
           Integer limit,
           String sortField, 
           String searchSpec, 
           Map<String, String> requestParameters) throws ApplicationException {
       
       requestParameters.put("companyType", COMPANY_TYPE_BROKER); //To always filter broker records;
       return super.getAllCompany(offset, limit, sortField, searchSpec, requestParameters);
   }



    /**
     * Add a company in database with type = broker_partner
     * 
     * @param company object
     */
    @Transactional
    public Company addCompany(Company company, Integer brokerId) {
        
        company.setCompanyType(COMPANY_TYPE_BROKER);
        
        company = super.addCompany(company, null);
        
        //Create Master configuration with new company
        Configuration configuration = configurationRepository.save(createMasterConfiguration(company.getCompanyId()));
        company.setBroker(company.getCompanyId());
        company.setConfigurationId(configuration.getConfigurationId());
        
        return companyRepository.save(company); //To update with brokerId and master configuration id
    }
    
    /**
     * Validate and get broker for given brokerId
     * 
     * @param brokerId
     * @return
     */
    @Override
    public Company validateBrokerId(Integer brokerId) {
        if (brokerId == null) { //For broker creation, initially brokerId will be null
            return null;
        }
        
        return super.validateBrokerId(brokerId);
    }

    /**
     * To validate duplicate company record for client_type="broker_partner"
     */
    @Override
    public boolean isDuplicateCompany(String brokerName, Integer brokerId, String custom1) {
        //find matching company by given company name and company type = 'broker_partner'
        Company companyFromDB = companyRepository.findFirstByCompanyNameAndCompanyType(brokerName,
                COMPANY_TYPE_BROKER);

        return companyFromDB == null ? false : true;
    }
    
}
