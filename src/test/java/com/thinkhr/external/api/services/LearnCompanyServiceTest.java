package com.thinkhr.external.api.services;

import static com.thinkhr.external.api.utils.ApiTestDataUtil.createCompany;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.thinkhr.external.api.ApiApplication;
import com.thinkhr.external.api.db.entities.Company;
import com.thinkhr.external.api.db.learn.entities.LearnCompany;
import com.thinkhr.external.api.db.learn.entities.LearnPackageMaster;
import com.thinkhr.external.api.exception.ApplicationException;
import com.thinkhr.external.api.helpers.ModelConvertor;
import com.thinkhr.external.api.learn.repositories.LearnCompanyRepository;
import com.thinkhr.external.api.learn.repositories.PackageRepository;
import com.thinkhr.external.api.utils.ApiTestDataUtil;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ApiApplication.class)
@SpringBootTest
public class LearnCompanyServiceTest {

    @InjectMocks
    private LearnCompanyService learnService;

    @Mock
    private LearnCompanyRepository learnCompanyRepository;

    @Mock
    private PackageRepository packageRepository;

    @Mock
    private ModelConvertor modelConvertor;

    @Value("${com.thinkhr.external.api.learn.default.package}")
    private String defaultCompanyPackage;

    /**
     * Test to verify if learnCompany is saved when learn company object is
     * given.
     * 
     */
    @Test
    public void testAddLearnCompany_ForLearnCompany() {
        LearnCompany learnCompany = ApiTestDataUtil.createLearnCompany(1L, 1, "Pepcus", "IT");

        when(learnCompanyRepository.save(learnCompany)).thenReturn(learnCompany);

        LearnCompany actual = learnService.addLearnCompany(learnCompany);

        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertEquals(learnCompany.getCompanyId(), actual.getCompanyId());
        assertEquals(learnCompany.getCompanyName(), actual.getCompanyName());
        assertEquals(learnCompany.getCompanyType(), actual.getCompanyType());
    }

    /**
     * Test to verify if learnCompany is saved when throne company object is
     * given.
     * 
     */
    @Test
    public void testAddLearnCompany_ForCompany() {
        Company company = ApiTestDataUtil.createCompany();
        LearnCompany learnCompany = ApiTestDataUtil.createLearnCompany(1L, 1, "Pepcus", "Software");
        LearnPackageMaster package1 = ApiTestDataUtil.createPacakge(1L, 10, defaultCompanyPackage);

        when(modelConvertor.convert(company)).thenReturn(learnCompany);
        when(packageRepository.findFirstByName(defaultCompanyPackage)).thenReturn(package1);
        when(learnCompanyRepository.save(learnCompany)).thenReturn(learnCompany);

        LearnCompany actual = learnService.addLearnCompany(company);

        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertEquals(company.getCompanyId(), actual.getCompanyId());
        assertNotEquals(company.getCompanyName(), actual.getCompanyName());
        assertEquals(company.getCompanyType(), actual.getCompanyType());
    }

    /**
     * Test to verify learnPackage is saved.
     * 
     */
    @Test
    public void testAddPackage() {
        LearnPackageMaster package1 = ApiTestDataUtil.createPacakge(1L, 10, defaultCompanyPackage);
        LearnCompany learnCompany = ApiTestDataUtil.createLearnCompany(1L, 1, "Pepcus", "Software");

        when(packageRepository.findFirstByName(defaultCompanyPackage)).thenReturn(package1);

        LearnPackageMaster actual = learnService.addPackage(learnCompany, defaultCompanyPackage);

        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertEquals(package1.getId(), actual.getId());
        assertEquals(package1.getName(), actual.getName());
    }

    /**
     * Test to verify when learnCompany is updated.
     * 
     */
    @Test
    public void testUpdateLearnCompany() {
        Long companyId = 1L;

        LearnCompany company = ApiTestDataUtil.createLearnCompany(companyId, 1, "Pepcus", "Software");

        when(learnCompanyRepository.save(company)).thenReturn(company);
        when(learnCompanyRepository.findOne(companyId)).thenReturn(company);

        // Updating company name 
        company.setCompanyName("Pepcus - Updated");

        LearnCompany companyUpdated = null;
        try {
            companyUpdated = learnService.updateLearnCompany(company);
        } catch (ApplicationException e) {
            fail("Not expecting application exception for a valid test case");
        }
        assertEquals("Pepcus - Updated", companyUpdated.getCompanyName());
    }

    /**
     * Test to verify if learnCompany is deactivated or not.
     * 
     */
    @Test
    public void testDeactivateLearnCompany() {
        Integer companyId = 1;
        Company company = createCompany(companyId, "Pepcus", "Software", "PEP", new Date(), "PepcusNotes",
                "PepcusHelp");
        String companyKey = "ABC";

        LearnCompany learnCompany = ApiTestDataUtil.createLearnCompany(1L, companyId, "Pepcus", "Software");

        when(learnCompanyRepository.findFirstByCompanyIdAndCompanyKey(companyId, companyKey)).thenReturn(learnCompany);
        when(learnCompanyRepository.save(learnCompany)).thenReturn(learnCompany);

        boolean isDeactivate = learnService.deactivateLearnCompany(company);

        assertTrue(isDeactivate);

    }

    /**
     * Test to verify if learnCompany is activated or not.
     * 
     */
    @Test
    public void testActivateLearnCompany() {
        Integer companyId = 1;
        Company company = createCompany(companyId, "Pepcus", "Software", "PEP", new Date(), "PepcusNotes",
                "PepcusHelp");
        String companyKey = "ABC";

        LearnCompany learnCompany = ApiTestDataUtil.createLearnCompany(1L, companyId, "Pepcus", "Software");

        when(learnCompanyRepository.findFirstByCompanyIdAndCompanyKey(companyId, companyKey)).thenReturn(learnCompany);
        when(learnCompanyRepository.save(learnCompany)).thenReturn(learnCompany);

        boolean isAactivate = learnService.activateLearnCompany(company, companyKey);

        assertTrue(isAactivate);

    }
}
