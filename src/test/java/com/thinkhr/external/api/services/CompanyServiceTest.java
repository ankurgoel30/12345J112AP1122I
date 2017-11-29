package com.thinkhr.external.api.services;

import static com.thinkhr.external.api.ApplicationConstants.DEFAULT_SORT_BY_COMPANY_NAME;
import static com.thinkhr.external.api.services.utils.EntitySearchUtil.getPageable;
import static com.thinkhr.external.api.utils.ApiTestDataUtil.createCompany;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;

import com.thinkhr.external.api.db.entities.Company;
import com.thinkhr.external.api.exception.APIErrorCodes;
import com.thinkhr.external.api.exception.ApplicationException;
import com.thinkhr.external.api.repositories.CompanyRepository;

/**
 * Junit to test all the methods of CompanyService.
 * 
 * @author Surabhi Bhawsar
 * @since 2017-11-06
 *
 */

@RunWith(SpringRunner.class)
public class CompanyServiceTest {

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private CompanyService companyService;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
    }

    /**
     * To verify getAllCompany method. 
     * 
     */
    @Test
    public void testGetAllCompany(){
        List<Company> companyList = new ArrayList<Company>();
        companyList.add(createCompany(1, "Pepcus", "Software", "PEP", new Date(), "PepcusNotes", "PepcusHelp"));
        companyList.add(createCompany(2, "ThinkHR", "Service Provider", "THR", new Date(), "THRNotes", "THRHelp"));
        companyList.add(createCompany(3, "ICICI", "Banking", "ICICI", new Date(), "ICICINotes", "ICICIHelp"));
        Pageable pageable = getPageable(null, null, null, DEFAULT_SORT_BY_COMPANY_NAME);

        when(companyRepository.findAll(null, pageable)).thenReturn(new PageImpl<Company>(companyList, pageable, companyList.size()));

        try {
            List<Company> result =  companyService.getAllCompany(null, null, null, null, null);
            assertEquals(3, result.size());
        } catch (ApplicationException ex) {
            fail("Not expected exception");
        }

    }

    /**
     * To verify getAllCompany method specifically for pageable.
     * 
     */
    @Test
    public void testGetAllToVerifyPageable(){

        List<Company> companyList = new ArrayList<Company>();
        companyList.add(createCompany(1, "Pepcus", "Software", "PEP", new Date(), "PepcusNotes", "PepcusHelp"));
        companyList.add(createCompany(2, "ThinkHR", "Service Provider", "THR", new Date(), "THRNotes", "THRHelp"));
        companyList.add(createCompany(3, "ICICI", "Banking", "ICICI", new Date(), "ICICINotes", "ICICIHelp"));

        companyService.getAllCompany(null, null, null, null, null);

        Pageable pageable = getPageable(null, null, null, DEFAULT_SORT_BY_COMPANY_NAME);

        //Verifying that internally pageable arguments is passed to companyRepository's findAll method
        verify(companyRepository, times(1)).findAll(null, pageable);
    }

    /**
     * To verify createCompany method
     * 
     */
    @Test
    public void testGetCompany() {
        Integer companyId = 1;
        Company company = createCompany(companyId, "Pepcus", "Software", "PEP", new Date(), "PepcusNotes", "PepcusHelp");
        when(companyRepository.findOne(companyId)).thenReturn(company);
        Company result = companyService.getCompany(companyId);
        assertEquals(companyId, result.getCompanyId());
        assertEquals("Pepcus", result.getCompanyName());
        assertEquals("Software", result.getCompanyType());
        assertEquals("PEP", result.getDisplayName());
    }

    /**
     * To verify createCompany method
     * 
     */
    @Test(expected=com.thinkhr.external.api.exception.ApplicationException.class)
    public void testGetCompanyNotExists() {
        Integer companyId = 1;
        when(companyRepository.findOne(companyId)).thenReturn(null);
        Company result = companyService.getCompany(companyId);
    }

    /**
     * To verify addCompany method
     * 
     */
    @Test
    public void testAddCompany() {
        //When all data is correct, it should assert true 
        Integer companyId = 1;
        Company company = createCompany(companyId, "Pepcus", "Software", "PEP", new Date(), "PepcusNotes", "PepcusHelp");

        when(companyRepository.save(company)).thenReturn(company);

        Company result = companyService.addCompany(company);
        assertEquals(companyId, result.getCompanyId());
        assertEquals("Pepcus", result.getCompanyName());
        assertEquals("Software", result.getCompanyType());
        assertEquals("PEP", result.getDisplayName());
    }

    /**
     * To verify updateCompany method
     * 
     */

    @Test
    public void testUpdateCompany(){
        Integer companyId = 1;

        Company company = createCompany(companyId, "Pepcus", "Software", "PEP", new Date(), "PepcusNotes", "PepcusHelp");

        when(companyRepository.save(company)).thenReturn(company);
        when(companyRepository.findOne(companyId)).thenReturn(company);

        // Updating company name 
        company.setCompanyName("Pepcus - Updated");

        Company companyUpdated = null;
        try {
            companyUpdated = companyService.updateCompany(company);
        } catch (ApplicationException e) {
            fail("Not expecting application exception for a valid test case");
        }
        assertEquals("Pepcus - Updated", companyUpdated.getCompanyName());
    }

    /**
     * To verify updateCompany method when companyRepository doesn't find a match for given companyId.
     * 
     * 
     */

    @Test
    public void testUpdateCompanyForEntityNotFound(){
        Integer companyId = 1;
        Company company = createCompany(companyId, "Pepcus", "Software", "PEP", new Date(), "PepcusNotes", "PepcusHelp");
        when(companyRepository.findOne(companyId)).thenReturn(null);
        try {
            companyService.updateCompany(company);
        } catch (ApplicationException e) {
            assertEquals(APIErrorCodes.ENTITY_NOT_FOUND, e.getApiErrorCode());
        }
    }


    /**
     * To verify deleteCompany method
     * 
     */
    @Test
    public void testDeleteCompany() {
        Integer companyId = 1;

        when(companyRepository.findOne(companyId)).thenReturn(createCompany());
        try {
            companyService.deleteCompany(companyId);
        } catch (ApplicationException e) {
            fail("Should be executed properly without any error");
        }
        //Verifying that internally companyRepository's delete method executed
        verify(companyRepository, times(1)).softDelete(companyId);
    }

    /**
     * To verify deleteCompany method throws ApplicationException when internally companyRepository.delete method throws exception.
     * 
     */
    @Test(expected=com.thinkhr.external.api.exception.ApplicationException.class)
    public void testDeleteCompanyForEntityNotFound() {
        int companyId = 1 ;
        when(companyRepository.findOne(companyId)).thenReturn(null);
        companyService.deleteCompany(companyId);
    }

}
