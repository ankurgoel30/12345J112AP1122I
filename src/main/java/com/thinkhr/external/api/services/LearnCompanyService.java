package com.thinkhr.external.api.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.thinkhr.external.api.db.entities.Company;
import com.thinkhr.external.api.db.learn.entities.LearnCompany;
import com.thinkhr.external.api.helpers.ModelConvertor;
import com.thinkhr.external.api.learn.repositories.LearnCompanyRepository;

@Service
public class LearnCompanyService {
    @Autowired
    LearnCompanyRepository learnCompanyRepository;

    @Autowired
    protected ModelConvertor modelConvertor;

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
        return this.addLearnCompany(learnCompany);
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
     * 
     * @param companyName
     * @return
     */
    public String generateCompanyNameForInactive(String companyName,Integer brokerId, Integer companyId) {
        return companyName + "_" + brokerId + companyId + "_inact";
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


