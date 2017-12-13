package com.thinkhr.external.api.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.thinkhr.external.api.db.entities.Company;
import com.thinkhr.external.api.db.learn.entities.LearnCompany;
import com.thinkhr.external.api.learn.repositories.LearnCompanyRepository;

@Service
public class LearnCompanyService {
    @Autowired
    LearnCompanyRepository learnCompanyRepository;
    private Logger logger = LoggerFactory.getLogger(LearnCompanyService.class);

    /**
     * 
     * @param learnCompany
     * @return
     */
    public LearnCompany addLearnCompany(LearnCompany learnCompany) {
        return learnCompanyRepository.save(learnCompany);
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

        LearnCompany learnCompany = learnCompanyRepository.findFirstByThrCompanyIdAndCompanyKey(
                throneCompany.getCompanyId(),
                companyKey);

        if (learnCompany == null) {
            //TODO :  What to do ?
        }

        String companyName = generateCompanyNameForInactive(throneCompany.getCompanyName());
        learnCompany.setCompanyName(companyName);

        learnCompanyRepository.save(learnCompany);
        isDeactivated = true;
        return isDeactivated;
    }

    /**
     * 
     * @param companyName
     * @return
     */
    public String generateCompanyNameForInactive(String companyName) {
        if (companyName == null) {
            return null;
        }

        // TODO : Code to generate inactiveCompanyName from companyName 
        return "ABC_INACT";
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


        LearnCompany learnCompany = learnCompanyRepository.findFirstByThrCompanyIdAndCompanyKey(
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


