package com.thinkhr.external.api.services;

import static com.thinkhr.external.api.ApplicationConstants.CONFIGURATION_ID_FOR_INACTIVE;
import static com.thinkhr.external.api.ApplicationConstants.INACT;
import static com.thinkhr.external.api.ApplicationConstants.UNDERSCORE;

import java.util.ArrayList;
import java.util.List;

import org.hashids.Hashids;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinkhr.external.api.db.entities.Company;
import com.thinkhr.external.api.db.learn.entities.LearnCompany;
import com.thinkhr.external.api.db.learn.entities.LearnPackageMaster;

/**
 * Provides a collection of all services related with LearnCompany
 * database object
 * 
 * @author Surabhi Bhawsar
 * @since 2017-12-14
 *
 */
@Service
public class LearnCompanyService extends CommonService {
    @Value("${com.thinkhr.external.api.learn.default.package}")
    protected String defaultCompanyPackage;

    @Autowired
    protected LearnUserService learnUserService;

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

        learnCompany.setCompanyName(getLearnCompanyNameByConfigurationId(throneCompany));

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
     * Update a learnCompany from throneCompany and add it to database
     * 
     * @param company
     * @return
     */
    public LearnCompany updateLearnCompany(Company throneCompany) {
        
        LearnCompany learnCompany = learnCompanyRepository.findFirstByCompanyIdAndCompanyKey(
                throneCompany.getCompanyId(), generateCompanyKey(throneCompany.getCompanyId()));
        
        if (learnCompany == null) {
            return null;
        }

        modelConvertor.update(learnCompany, throneCompany);
        learnCompany.setCompanyName(getLearnCompanyNameByConfigurationId(throneCompany));

        // If learn company name is changed so that it becomes inactive from active or vice versa then 
        // deactivate/activate all learn users corresponding to learn company 
        if (!learnCompany.getCompanyName().equals(throneCompany.getCompanyName())) { // This means learn company is inactive
            learnUserService.deactivateAllLearnUsers(throneCompany);
        } else {
            learnUserService.activateAllLearnUsers(throneCompany);
        }

        return this.updateLearnCompany(learnCompany);
    }

    /**
     * Returns true if learnCompany corresponding to  throneCompany and compannyKey
     * is deactivated successfully else false
     * 
     * Also deactivates all learnUsers corresponding to given throneCompany
     * 
     * @param throneCompany
     * @return
     */
    @Transactional
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
        
        learnUserService.deactivateAllLearnUsers(throneCompany); // THR-3932
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
     * This function creates active or inactive company name based on configurationId
     * 
     * @param company
     * @return
     */
    public static String getLearnCompanyNameByConfigurationId(Company company) {
        String learnCompanyName = null;
        if (company.getConfigurationId() != null && company.getConfigurationId() != CONFIGURATION_ID_FOR_INACTIVE) {
            learnCompanyName = company.getCompanyName(); // Active company
        } else {
            learnCompanyName = LearnCompanyService.generateCompanyNameForInactive(company.getCompanyName(),
                    company.getBroker(), company.getCompanyId()); // Inactive company name
        }
        return learnCompanyName;
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
        if (throneCompany == null || throneCompany.getCompanyId() == null || companyKey == null) {
            return false;
        }


        LearnCompany learnCompany = learnCompanyRepository.findFirstByCompanyIdAndCompanyKey(
                throneCompany.getCompanyId(),
                companyKey);

        if (learnCompany == null) {
            //Skip this as no learnCompany for throneCompany is found
            return true;
        }

        learnCompany.setCompanyName(throneCompany.getCompanyName());

        learnCompanyRepository.save(learnCompany);

        learnUserService.activateAllLearnUsers(throneCompany); // THR-3932
        return true;
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
        return learnFileDataRepository.saveLearnCompanyRecord(modelConvertor.getColumnsForInsert(throneCompany), pkgId);

    }
}


