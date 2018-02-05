package com.thinkhr.external.api.services;

import static com.thinkhr.external.api.services.utils.EntitySearchUtil.convertToDate;
import static com.thinkhr.external.api.services.utils.EntitySearchUtil.isDateField;
import static com.thinkhr.external.api.services.utils.EntitySearchUtil.isStringField;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import com.thinkhr.external.api.db.entities.SearchableEntity;

import lombok.Data;
/**
 * Extends Specification specific to SearchableEntity
 * 
 * @author Surabhi Bhawsar
 * @since 2017-11-09
 *
 */
@Data
public class EntitySearchSpecification<T extends SearchableEntity> implements Specification<T>{

    private String searchSpec ;
    private Map<String, String> searchParameters;
    private T t ;

    /**
     * Constructor to create Entity Search Specification 
     * 
     * @param searchSpec
     * @param entity
     */
    public EntitySearchSpecification(String searchSpec,T entity) {
        super();
        this.searchSpec =  searchSpec;
        this.t =  entity;
    }

    /**
     * Constructor to create Entity Search Specification 
     * 
     * @param searchParams
     * @param entity
     */
    public EntitySearchSpecification (Map<String, String> searchParams, T entity) {
        super();
        this.searchParameters = searchParams;
        this.t = entity;
    }

    @Override
    public Predicate toPredicate(Root<T> from, CriteriaQuery<?> criteria, CriteriaBuilder criteriaBuilder) {

        List<Predicate> predicates = new ArrayList<Predicate>();


        if (searchParameters != null && !searchParameters.isEmpty()) {
            Predicate filterPredicate =  criteriaBuilder.conjunction();
            searchParameters.entrySet().forEach( searchParam -> { 
                if (isStringField(t.getClass(), searchParam.getKey())) { 
                    filterPredicate.getExpressions().
                    add(criteriaBuilder.like(from.get(searchParam.getKey()), "%" + searchParam.getValue() + "%")); 
                } else if (isDateField(t.getClass(), searchParam.getKey())) {
                    Date date = convertToDate(searchParam.getValue(), searchParam.getKey());
                    filterPredicate.getExpressions().
                    add(criteriaBuilder.equal(from.get(searchParam.getKey()), date)); 
                } else {
                    filterPredicate.getExpressions().
                    add(criteriaBuilder.equal(from.get(searchParam.getKey()), searchParam.getValue())); 
                }
            }
                    );
            predicates.add(filterPredicate);
        }

        //WHEN search spec is not null
        if (StringUtils.isNotBlank(searchSpec)) {
            Predicate searchPredicate = criteriaBuilder.disjunction();
            List<String> searchColumns =  t.getSearchFields();
            searchColumns.stream().forEach(column -> searchPredicate.getExpressions().add(criteriaBuilder.like(from.get(column), "%" + searchSpec +"%")));
            predicates.add(searchPredicate);
        } 

        Predicate[] pr = new Predicate[predicates.size()];
        predicates.toArray(pr);
        return criteriaBuilder.and(pr);
    }

}
