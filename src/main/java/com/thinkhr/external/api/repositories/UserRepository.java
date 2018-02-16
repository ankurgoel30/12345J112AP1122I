package com.thinkhr.external.api.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.thinkhr.external.api.db.entities.User;

/**
 * User repository for user entity.
 *  
 * @author Surabhi Bhawsar
 * @since   2017-11-01 
 *
 */

public interface UserRepository extends PagingAndSortingRepository<User, Integer>, JpaSpecificationExecutor<User> {


    /**
     * 
     * @param userID
     */
    @Query("update User user set user.isActive=0 , user.deactivationDate=now() where user.userId = ?1")
    @Modifying
    @Transactional
    public void softDelete(int userID);
    
    /**
     * 
     * @param userName
     * @return
     */
    public User findByUserName(String userName);

    /**
     * 
     * @param companyId
     * @return
     */
    public List<User> findByCompanyId(Integer companyId);

    /**
     * 
     * @param jobId
     * @return
     */
    @Query(value = "select u.userId from User u where u.addedBy = :jobId")
    public List<Integer> findAllUsersByJobId(@Param("jobId") String jobId);
    
    /**
     * 
     * @param companyId
     * @return
     */
    @Query(value = "select u.userId from User u where u.companyId = :companyId")
    public List<Integer> findAllUsersByCompanyId(@Param("companyId") Integer companyId);
    
    /**
     * 
     * @param addedBy
     * @return
     */
    public List<User> findByAddedBy(String addedBy);

    /**
     * 
     * @param jobId
     */
    @Transactional
    public void deleteByAddedBy(String jobId);
    
    /**
     * 
     * @param companyIdList
     */
    @Transactional
    public void deleteByCompanyIdIn(List<Integer> companyIdList);
    
    /**
     * 
     * @param companyIdList
     */
    public List<User> findByCompanyIdIn(List<Integer> companyIdList);

}