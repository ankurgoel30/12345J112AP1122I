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
import com.thinkhr.external.api.db.learn.entities.LearnRole;
import com.thinkhr.external.api.utils.ApiTestDataUtil;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { ApiApplication.class, LearnDBTestConfig.class })
@ActiveProfiles("testLearn")
public class LearnRoleRepositoryTest {

    @Autowired
    private LearnRoleRepository roleRepository;

    /**
     * Test to verify findFirstByShortName method.
     */
    @Test
    public void testFindFirstByShortName() {
        String shortName = "broker";
        LearnRole role = ApiTestDataUtil.createLearnRole(null, "Broker", "broker");

        LearnRole learnRole = roleRepository.save(role);
        LearnRole foundRole = roleRepository.findFirstByShortName(shortName);

        assertNotNull(foundRole.getId());
        assertEquals("Broker", foundRole.getName());
        assertEquals("broker", foundRole.getShortName());
    }

}
