package com.thinkhr.external.api.services;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.thinkhr.external.api.db.entities.Company;
import com.thinkhr.external.api.model.CompanyModel;
import com.thinkhr.external.api.repositories.CompanyRepository;

/**
*
* Provides a collection of all services related with Company
* database object
*
* @author Surabhi Bhawsar
* @Since 2017-11-04
*
* 
*/

@Service
public class CompanyService  extends CommonService {
	
    @Autowired
    private CompanyRepository companyRepository;

    /**
     * Fetch all companies from database
     * 
     * @return List<Company> object 
     */
    public List<CompanyModel> getAllCompany() {
		List<Company> companies = new ArrayList<Company>();
		companyRepository.findAll().forEach(c -> companies.add(c));
		return companies.stream().map(c -> { return (CompanyModel) convert(c, CompanyModel.class); }).collect(Collectors.toList());
	}
    
    /**
     * Fetch specific company from database
     * 
     * @param companyId
     * @return Company object 
     */
    public CompanyModel getCompany(Integer companyId) {
    	Company company = null;
    	company = companyRepository.findOne(companyId);
    	return (null != company ? (CompanyModel) convert(company, CompanyModel.class) : null);    
    }
    
    /**
     * Add a company in database
     * 
     * @param CompanyModel object
     */
    public CompanyModel addCompany(CompanyModel companyModel)  {
		Company company = (Company)convert(companyModel, Company.class);
    	companyRepository.save(company);
    	return (CompanyModel) convert(company, CompanyModel.class);
    }
    
    /**
     * Update a company in database
     * 
     * @param CompanyModel object
     */
    public CompanyModel updateCompany(CompanyModel companyModel)  {
		Company company = (Company)convert(companyModel, Company.class);
    	companyRepository.save(company);
    	return (CompanyModel) convert(company, CompanyModel.class);

    }
    
    /**
     * Delete specific company from database
     * 
     * @param companyId
     */
    public int deleteCompany(int companyId)  {
    	companyRepository.delete(companyId);
    	return companyId;
    }
}