package com.thinkhr.external.api.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.Data;

/**
 * Common Service to hold all general operations
 * 
 * @author Surabhi Bhawsar
 * @Since 2017-11-04
 *
 */
@Service
@Data
public class CommonService {

    @Autowired
    private ModelMapper modelMapper;

    /**
     * TODO
     * @param obj
     * @param fromClass
     * @param toClass
     */
    protected Object convert(Object fromObj,  Class toClass) {
        Object toObject = modelMapper.map(fromObj, toClass);
        return toObject;
    }
}
