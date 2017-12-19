package com.thinkhr.external.api.services;

import static com.thinkhr.external.api.ApplicationConstants.INACT;
import static com.thinkhr.external.api.ApplicationConstants.UNDERSCORE;

import java.util.ArrayList;
import java.util.List;

import org.hashids.Hashids;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.thinkhr.external.api.db.entities.Company;
import com.thinkhr.external.api.db.learn.entities.LearnCompany;
import com.thinkhr.external.api.db.learn.entities.LearnPackageMaster;
import com.thinkhr.external.api.helpers.ModelConvertor;
import com.thinkhr.external.api.learn.repositories.LearnUserRepository;

/**
 * Provides a collection of all services related with LearnCompany
 * database object
 * 
 * @since 2017-12-19
 *
 */
@Service
public class LearnUserService {
    @Autowired
    LearnUserRepository learnUserRepository;


    @Autowired
    protected ModelConvertor modelConvertor;
}


