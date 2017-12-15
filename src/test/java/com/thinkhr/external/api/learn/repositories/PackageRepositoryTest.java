package com.thinkhr.external.api.learn.repositories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.thinkhr.external.api.ApiApplication;
import com.thinkhr.external.api.config.LearnDBTestConfig;
import com.thinkhr.external.api.db.learn.entities.Package;
import com.thinkhr.external.api.utils.ApiTestDataUtil;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { ApiApplication.class, LearnDBTestConfig.class })
@ActiveProfiles("testLearn")
public class PackageRepositoryTest {

    @Autowired
    private PackageRepository packageRepository;

    @Value("${com.thinkhr.external.api.learn.default.package}")
    private String defaultCompanyPackage;

    /**
     * Test to verify save method in repository.
     */
    @Test
    public void testAdd() {
        Package package1 = ApiTestDataUtil.createPacakge(1L, 1, defaultCompanyPackage);

        Package savedPackage = packageRepository.save(package1);

        assertNotNull(savedPackage);
        assertNotNull(savedPackage.getId());// As company is saved successfully.
        assertEquals(1, savedPackage.getCategoryId().intValue());
        assertEquals(defaultCompanyPackage, savedPackage.getName());

    }

    /**
     * Test to verify findFirstByName method in repository.
     */
    @Test
    public void testFindFirstByName() {
        Package package1 = ApiTestDataUtil.createPacakge(1L, 1, defaultCompanyPackage);

        Package savedPackage = packageRepository.save(package1);
        Package found = packageRepository.findFirstByName(defaultCompanyPackage);

        assertNotNull(found);
        assertNotNull(found.getId());// As company is saved successfully.
        assertEquals(1, found.getCategoryId().intValue());
        assertEquals(defaultCompanyPackage, found.getName());

    }

}
