package com.thinkhr.external.api.learn.repositories;

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
import com.thinkhr.external.api.db.learn.entities.LearnUser;
import com.thinkhr.external.api.utils.ApiTestDataUtil;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { ApiApplication.class, LearnDBTestConfig.class })
@ActiveProfiles("testLearn")
public class LearnUserRepositoryTest {
    
    @Autowired
    private LearnUserRepository learnUserRepository;
    
    /**
     * Test to verify learnUserRepository.save method when adding user.
     */
    @Test
    public void testSaveForAdd() {
        LearnUser learnUser = ApiTestDataUtil.createLearnUser(null, 10, "Ajay", "Jain", "ajain", "",
                "ajay.jain@pepcus.com", "9009876543");
        LearnUser userSaved = learnUserRepository.save(learnUser);

        assertNotNull(userSaved);
        assertNotNull(userSaved.getId());// As company is saved successfully.
        assertEquals(learnUser.getCompanyId(), userSaved.getCompanyId());
        assertEquals(learnUser.getFirstName(), userSaved.getFirstName());
        assertEquals(learnUser.getLastName(), userSaved.getLastName());
        assertEquals(learnUser.getUserName(), userSaved.getUserName());
        assertEquals(learnUser.getEmail(), userSaved.getEmail());
        assertEquals(learnUser.getPhone1(), userSaved.getPhone1());
    }

    /**
     * Test to verify when learnUser is found if thrUserId is given.
     * 
     */
    @Test
    public void testFindFirstByThrUserId() {
        Integer thrUserId = 10;
        LearnUser learnUser = ApiTestDataUtil.createLearnUser(null, 10, "Ajay", "Jain", "ajain", "",
                "ajay.jain@pepcus.com", "9009876543");

        LearnUser userSaved = learnUserRepository.save(learnUser);
        LearnUser foundUser = learnUserRepository.findFirstByThrUserId(thrUserId);

        assertNotNull(foundUser);
        assertNotNull(foundUser.getId());// As company is saved successfully.
        assertEquals(learnUser.getCompanyId(), foundUser.getCompanyId());
        assertEquals(learnUser.getFirstName(), foundUser.getFirstName());
        assertEquals(learnUser.getLastName(), foundUser.getLastName());
        assertEquals(learnUser.getEmail(), foundUser.getEmail());
        assertEquals(learnUser.getUserName(), foundUser.getUserName());
        assertEquals(learnUser.getPhone1(), foundUser.getPhone1());
    }

}
