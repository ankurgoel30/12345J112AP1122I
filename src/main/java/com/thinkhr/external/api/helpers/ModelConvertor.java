package com.thinkhr.external.api.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.hashids.Hashids;
import org.springframework.stereotype.Service;

import com.thinkhr.external.api.db.entities.Company;
import com.thinkhr.external.api.db.entities.Location;
import com.thinkhr.external.api.db.entities.User;
import com.thinkhr.external.api.db.learn.entities.LearnCompany;
import com.thinkhr.external.api.db.learn.entities.LearnUser;
import com.thinkhr.external.api.services.LearnCompanyService;

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
        Date now = new Date();
        learnCompany.setTimeCreated(now.getTime());
        learnCompany.setTimeModified(now.getTime());

        
        return learnCompany;

    }
    
    /**
     * @param company
     * @return
     */
    public static List<Object> getColumnsForInsert(Company company) {
        if (company.getLocation() == null) {
            company.setLocation(new Location());
        }
        List<Object> learnCompanyFields = new ArrayList<Object>(Arrays.asList(
                company.getCompanyId(), 
                company.getCompanyName(),
                company.getCompanyType(),
                LearnCompanyService.generateCompanyKey(company.getCompanyId()),
                company.getLocation().getAddress(),
                company.getLocation().getAddress2(),
                company.getLocation().getCity(),
                company.getLocation().getState(),
                company.getLocation().getZip(),
                company.getBroker(),
                company.getCompanyPhone(),
                "1",
                String.valueOf(new Date().getTime()),
                String.valueOf(new Date().getTime())));
        
        return learnCompanyFields;
    }

    public LearnUser convert(User throneUser) {

        LearnUser learnUser = new LearnUser();
        learnUser.setBlockedAccount(throneUser.getBlockedAccount());
        learnUser.setBounced(throneUser.getBounced());
        learnUser.setCompanyId(throneUser.getCompanyId());
        learnUser.setDeleted(throneUser.getDeleted());
        learnUser.setEmail(throneUser.getEmail());
        learnUser.setFirstName(throneUser.getFirstName());
        learnUser.setJobTitle(throneUser.getTitle());
        learnUser.setLastName(throneUser.getLastName());
        learnUser.setPassword(throneUser.getPassword());
        learnUser.setPhone1(throneUser.getPhone());
        learnUser.setThrUserId(throneUser.getUserId());
        learnUser.setUserName(throneUser.getUserName());

        return learnUser;
    }

}
