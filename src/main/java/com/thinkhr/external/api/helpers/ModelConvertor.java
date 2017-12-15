package com.thinkhr.external.api.helpers;

import java.util.Date;

import org.springframework.stereotype.Service;

import com.thinkhr.external.api.db.entities.Company;
import com.thinkhr.external.api.db.entities.Location;
import com.thinkhr.external.api.db.learn.entities.LearnCompany;

/**
 * Model convertor
 * 
 * @author Surabhi Bhawsar
 * @Since 2017-12-14
 *
 */
@Service
public class ModelConvertor {

    /**
     * To convert ThroneCompany to LearnCompany. 
     * 
     * TODO: Use modelMapper, getting some issue so reverted code related with modelMapper.
     * 
     * @param company
     * @return
     */
    public LearnCompany convert(Company company) {
        
        LearnCompany learnCompany =  new LearnCompany() ;
        Location location = company.getLocation();
        if (location != null) {
            learnCompany.setAddress(location.getAddress());
            learnCompany.setAddress2(location.getAddress2());
            learnCompany.setCity(location.getCity());
            learnCompany.setState(location.getState());
            learnCompany.setZip(location.getZip());
        }
        
        learnCompany.setBroker(String.valueOf(company.getBroker()));
        learnCompany.setCompanyId(company.getCompanyId());
        learnCompany.setCompanyName(company.getCompanyName());
        learnCompany.setCompanyType(company.getCompanyType());
        learnCompany.setPhone(company.getCompanyPhone());
        learnCompany.setCompanyKey(company.getCompanyName()); //TODO: Once we have info, we will fix it
        Date now = new Date();
        learnCompany.setTimeCreated(now.getTime());
        learnCompany.setTimeModified(now.getTime());

        
        return learnCompany;

    }
    
    
}
