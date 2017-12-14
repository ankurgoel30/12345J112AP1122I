package com.thinkhr.external.api.helpers;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.thinkhr.external.api.db.entities.Company;
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
            protected void configure() {
                map().setAddress(source.getLocation().getAddress());
                map().setAddress2(source.getLocation().getAddress2());
                map().setCity(source.getLocation().getCity());
                map().setState(source.getLocation().getState());
                map().setPhone(source.getCompanyPhone());
                //map().setStreet(source.getLocation().get);
                map().setZip(source.getLocation().getZip());
                map().setId(null);
            }
        };
        modelMapper.addMappings(companyPropertyMap);
        return modelMapper.map(company, LearnCompany.class);

    }
    
    
}
