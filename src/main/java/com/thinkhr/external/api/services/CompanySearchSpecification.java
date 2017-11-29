package com.thinkhr.external.api.services;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import com.thinkhr.external.api.db.entities.Company;

/**
 * Extends Specification specific to Company
 * @author Surabhi Bhawsar
 * @since 2017-11-09
 *
 */
public class CompanySearchSpecification implements Specification<Company>{

    private String searchSpec ;

    private static final List<String> searchColumns = new ArrayList<String>();

    static {
        searchColumns.add("searchHelp");
        searchColumns.add("companyName");
        searchColumns.add("companyPhone");
        searchColumns.add("website");
    }

    /**
     * @param searchSpec
     */
    public CompanySearchSpecification(String searchSpec) {
        super();
        this.searchSpec =  searchSpec;
    }

    @Override
    public Predicate toPredicate(Root<Company> from, CriteriaQuery<?> criteria, CriteriaBuilder criteriaBuilder) {
        Predicate p = criteriaBuilder.disjunction();
        if(StringUtils.isNotBlank(searchSpec)) {
            searchColumns.stream().forEach(column -> p.getExpressions().add(criteriaBuilder.like(from.get(column), "%" + searchSpec +"%")));
        }
        return p;
    }

}
