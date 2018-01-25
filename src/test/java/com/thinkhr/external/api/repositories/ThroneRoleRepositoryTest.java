package com.thinkhr.external.api.repositories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.thinkhr.external.api.db.entities.ThroneRole;
import com.thinkhr.external.api.utils.ApiTestDataUtil;

/**
 * Junit to verify methods of ThroneRoleRepository with use of H2 database
 * 
 * 
 */
@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.AUTO_CONFIGURED)
public class ThroneRoleRepositoryTest {

    @Autowired
    private ThroneRoleRepository roleRepository;

    /**
     * Test to verify findByNameAndCompanyId method.
     */
    @Test
    public void testFindByNameAndType() {

        ThroneRole role1 = ApiTestDataUtil.createThroneRole(null, "Admin", "thinkhr");

        // Saving record into H2 DB
        roleRepository.save(role1);

        ThroneRole role2 = ApiTestDataUtil.createThroneRole(null, "Broker Admin", "broker");

        // Saving record into H2 DB
        roleRepository.save(role2);

        ThroneRole role3 = ApiTestDataUtil.createThroneRole(null, "Student", "re");

        // Saving record into H2 DB
        roleRepository.save(role3);

        ThroneRole foundRole = roleRepository.findFirstByNameAndType("Broker Admin", "broker");

        assertNotNull(foundRole);
        assertEquals(role2.getType(), foundRole.getType());
        assertEquals(role2.getName(), foundRole.getName());
    }

}
