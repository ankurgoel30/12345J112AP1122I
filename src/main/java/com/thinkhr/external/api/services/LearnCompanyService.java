package com.thinkhr.external.api.services;

import static com.thinkhr.external.api.ApplicationConstants.INACT;
import static com.thinkhr.external.api.ApplicationConstants.UNDERSCORE;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${com.thinkhr.external.api.learn.default.package}")
    protected String defaultCompanyPackage;

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

        this.addPackage(learnCompany, defaultCompanyPackage);

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

    /**
     * To update learn company
     * 
     * @param learnCompany
     * @return
     */
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

        if (throneCompany == null || throneCompany.getCompanyId() == null || companyKey == null) {
            return false;
        }

        LearnCompany learnCompany = learnCompanyRepository.findFirstByCompanyIdAndCompanyKey(
                throneCompany.getCompanyId(),
                companyKey);

        if (learnCompany == null) {
            //Skip this, as no company is exist in DB.
            return true;
        }

        String inactiveCompanyName = generateCompanyNameForInactive(throneCompany.getCompanyName(), 
                                                                    throneCompany.getBroker(), 
                                                                    throneCompany.getCompanyId());
        learnCompany.setCompanyName(inactiveCompanyName);

        learnCompanyRepository.save(learnCompany);
        
        return true;
    }

    /**
     * Generate company name to make company inactive
     * @param companyName
     * @return
     */
    public static String generateCompanyNameForInactive(String companyName,Integer brokerId, Integer companyId) {
        return new StringBuffer(companyName)
                .append(UNDERSCORE)
                .append(brokerId)
                .append(UNDERSCORE)
                .append(companyId)
                .append(INACT).toString();
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


