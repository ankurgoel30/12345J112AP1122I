package com.thinkhr.external.api.services;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.thinkhr.external.api.db.entities.Company;
import com.thinkhr.external.api.db.learn.entities.LearnCompany;
import com.thinkhr.external.api.db.learn.entities.Package;
import com.thinkhr.external.api.helpers.ModelConvertor;
import com.thinkhr.external.api.learn.repositories.LearnCompanyRepository;
import com.thinkhr.external.api.learn.repositories.PackageRepository;

/**
 * Provides a collection of all services related with LearnCompany
 * database object
 * 
 * @author Surabhi Bhawsar
 * @since 2017-12-14
 *
 */
@Service
public class LearnCompanyService {
    @Autowired
    LearnCompanyRepository learnCompanyRepository;

    @Autowired
    PackageRepository packageRepository;

    @Autowired
    protected ModelConvertor modelConvertor;

    @Autowired
    protected Environment env;

    private Logger logger = LoggerFactory.getLogger(LearnCompanyService.class);

    /**
     * Save learnCompany to database
     * @param learnCompany
     * @return
     */
    public LearnCompany addLearnCompany(LearnCompany learnCompany) {
        return learnCompanyRepository.save(learnCompany);
    }

    /**
     * Create a learnCompany from throneCompany and add it to database
     * @param throneCompany
     * @return
     */
    public LearnCompany addLearnCompany(Company throneCompany) {

        LearnCompany learnCompany = modelConvertor.convert(throneCompany);

        String inactiveCompanyName = this.generateCompanyNameForInactive(
                throneCompany.getCompanyName(),
                throneCompany.getBroker(),
                throneCompany.getCompanyId());

        learnCompany.setCompanyName(inactiveCompanyName);

        String defaultPackage = env.getProperty("com.thinkhr.external.api.learn.default.package");

        this.addPackage(learnCompany, defaultPackage);

        return this.addLearnCompany(learnCompany);
    }

    /**
     * Add Package for given packageName to learnCompany
     * @param learnCompany
     * @param packageName
     */
    public void addPackage(LearnCompany learnCompany, String packageName) {
        if (learnCompany == null || packageName == null) {
            return;
        }

        Package learnPackage = packageRepository.findFirstByName(packageName);
        if (learnPackage == null) {
            return;
        }

        List<Package> packages = learnCompany.getPackages();
        if (packages == null) {
            packages = new ArrayList<Package>();
            learnCompany.setPackages(packages);
        }

        packages.add(learnPackage);
    }

    public LearnCompany updateLearnCompany(LearnCompany learnCompany) {

        Long learnCompanyId = learnCompany.getId();

        if (null == learnCompanyRepository.findOne(learnCompanyId)) {
            // TODO : Find what to  do ?
        }

        return learnCompanyRepository.save(learnCompany);
    }

    /**
     * Returns true if learnCompany corresponding to  throneCompany and compannyKey
     * is deactivated successfully else false
     * 
     * @param throneCompany
     * @param companyKey
     * @return
     */
    public boolean deactivateLearnCompany(Company throneCompany, String companyKey) {

        boolean isDeactivated = false;

        if (throneCompany == null || throneCompany.getCompanyId() == null || companyKey == null) {
            return isDeactivated;
        }

        LearnCompany learnCompany = learnCompanyRepository.findFirstByCompanyIdAndCompanyKey(
                throneCompany.getCompanyId(),
                companyKey);

        if (learnCompany == null) {
            //TODO :  What to do ?
        }

        String companyName = throneCompany.getCompanyName();
        Integer brokerId = throneCompany.getBroker();
        Integer companyId = throneCompany.getCompanyId();
        String inactiveCompanyName = generateCompanyNameForInactive(companyName, brokerId, companyId);
        learnCompany.setCompanyName(inactiveCompanyName);

        learnCompanyRepository.save(learnCompany);
        isDeactivated = true;
        return isDeactivated;
    }

    /**
     * Generate company name to make company inactive
     * @param companyName
     * @return
     */
    public String generateCompanyNameForInactive(String companyName,Integer brokerId, Integer companyId) {
        return companyName + "_" + brokerId + "_" + companyId + "_inact";
    }

    /**
     * Based of companyName find if the given learnCompany is active or inactive
     * TODO: 
     * @param learnCompany
     * @return
     */
    private boolean isLearnCompanyActive(LearnCompany learnCompany) {
        return false;
    }

    /**
     * Returns true if learnCompany for given thrCompanyId and compannyKey
     * is deactivated successfully else false
     * 
     * @param thrCompanyId
     * @param companyKey
     * @return
     */
    public boolean activateLearnCompany(Company throneCompany, String companyKey) {
        boolean isActivated = false;

        if (throneCompany == null || throneCompany.getCompanyId() == null || companyKey == null) {
            return isActivated;
        }


        LearnCompany learnCompany = learnCompanyRepository.findFirstByCompanyIdAndCompanyKey(
                throneCompany.getCompanyId(),
                companyKey);

        if (learnCompany == null) {
            //TODO :  What to do ?
        }

        learnCompany.setCompanyName(throneCompany.getCompanyName());

        learnCompanyRepository.save(learnCompany);
        isActivated = true;
        return isActivated;
    }
}


