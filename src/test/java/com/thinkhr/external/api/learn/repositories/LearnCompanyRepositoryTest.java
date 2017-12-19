package com.thinkhr.external.api.learn.repositories;

import static com.thinkhr.external.api.utils.ApiTestDataUtil.createLearnCompany;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.thinkhr.external.api.ApiApplication;
import com.thinkhr.external.api.config.LearnDBTestConfig;
import com.thinkhr.external.api.db.learn.entities.LearnCompany;
import com.thinkhr.external.api.utils.ApiTestDataUtil;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { ApiApplication.class, LearnDBTestConfig.class })
@ActiveProfiles("testLearn")
public class LearnCompanyRepositoryTest {

    @Autowired
    private LearnCompanyRepository learnCompanyRepository;

    /**
     * Test o verfiy learnCompanyRepository.save method when adding company.
     */
    @Test
    public void testSaveForAdd() {
        LearnCompany company = createLearnCompany(null, 10, "Pepcus", "Software");

        LearnCompany companySaved = learnCompanyRepository.save(company);

        assertNotNull(companySaved);
        assertNotNull(company.getId());// As company is saved successfully.
        assertEquals(company.getCompanyId(), companySaved.getCompanyId());
        assertEquals(company.getCompanyName(), companySaved.getCompanyName());
        assertEquals(company.getCompanyType(), companySaved.getCompanyType());

    }

    /**
     * Test to verify when learnCompany is found if companyId and companyKey are
     * given.
     */
    @Test
    public void testFindFirstByCompanyIdAndCompanyKey() {
        Integer companyId = 10;
        String companyKey = "ABC";
        LearnCompany company = ApiTestDataUtil.createLearnCompanyWithCompanyKey(null, companyId, "Pepcus", "Software",
                companyKey);

        LearnCompany companySaved = learnCompanyRepository.save(company);
        LearnCompany foundCompany = learnCompanyRepository.findFirstByCompanyIdAndCompanyKey(companyId, companyKey);

        assertNotNull(foundCompany);
        assertNotNull(foundCompany.getId());// As company is saved successfully.
        assertEquals(foundCompany.getCompanyId(), companySaved.getCompanyId());
        assertEquals(foundCompany.getCompanyName(), companySaved.getCompanyName());
        assertEquals(foundCompany.getCompanyType(), companySaved.getCompanyType());

    }

}
