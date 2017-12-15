package com.thinkhr.external.api.helpers;

import java.util.Date;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    ModelMapper modelMapper;
    
    public LearnCompany convert(Company company) {
        PropertyMap<Company, LearnCompany> companyPropertyMap = new PropertyMap<Company, LearnCompany>() {
            @Override
            protected void configure() {
                //                map().setAddress(source.getLocation().getAddress());
                //                map().setAddress2(source.getLocation().getAddress2());
                //                map().setCity(source.getLocation().getCity());
                //                map().setState(source.getLocation().getState());
                //                map().setPhone(source.getCompanyPhone());
                //                //map().setStreet(source.getLocation().get);
                //                map().setZip(source.getLocation().getZip());
                //                map().setId(null);
            }
        };
        //modelMapper.addMappings(companyPropertyMap);
        
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
        //return modelMapper.map(company, LearnCompany.class);

    }
    
    
}
