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

        Integer companyId = learnCompany.getCompanyId();

        String companyKey = generateCompanyKey(companyId);

        learnCompany.setCompanyKey(companyKey);

        String inactiveCompanyName = generateCompanyNameForInactive(
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
    public LearnPackageMaster addPackage(LearnCompany learnCompany, String packageName) {
        if (learnCompany == null || packageName == null) {
            return null;
        }

        LearnPackageMaster learnPackage = getDefaultPackageMaster();
        if (learnPackage == null) {
            return null;
        }

        List<LearnPackageMaster> packages = learnCompany.getPackages();
        if (packages == null) {
            packages = new ArrayList<LearnPackageMaster>();
            learnCompany.setPackages(packages);
        }

        packages.add(learnPackage);
        return learnPackage;
    }

    /**
     * To fetch default company package
     * 
     * @param packageName
     * @return
     */
    public LearnPackageMaster getDefaultPackageMaster() {
        return packageRepository.findFirstByName(defaultCompanyPackage);
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
     * @return
     */
    public boolean deactivateLearnCompany(Company throneCompany) {

        Integer companyId = throneCompany.getCompanyId();
        if (throneCompany == null || companyId == null) {
            return false;
        }

        LearnCompany learnCompany = learnCompanyRepository.findFirstByCompanyIdAndCompanyKey(
                companyId,
                generateCompanyKey(companyId));

        if (learnCompany == null) {
            //Skip this, as no company is exist in DB.
            return true;
        }

        String inactiveCompanyName = generateCompanyNameForInactive(throneCompany.getCompanyName(), 
                                                                    throneCompany.getBroker(), 
                                                                    companyId);
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
     * Generate company key from thrClientId on the basis of Hashids.
     * 
     * @param thrClientId
     * @return
     */
    public static String generateCompanyKey(Integer thrClientId) {
        Hashids hashids = new Hashids();
        String companyKey = hashids.encode(thrClientId);
        return companyKey;
    }

    /**
     * Returns true if learnCompany for given thrCompanyId and compannyKey is
     * activated successfully else false
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

    /**
     * Add learn company
     * 
     * @param throneCompany
     * @return
     */
    public Integer addLearnCompanyForBulk(Company throneCompany) {
        LearnPackageMaster pkg = this.getDefaultPackageMaster();
        Integer pkgId = pkg == null ? null : pkg.getId().intValue();
        return learnFileRepository.saveLearnCompanyRecord(modelConvertor.getColumnsForInsert(throneCompany), pkgId);

    }
}


