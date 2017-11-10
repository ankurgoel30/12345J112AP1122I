package com.thinkhr.external.api.services;

import java.util.List;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import com.thinkhr.external.api.db.entities.SearchableEntity;
/**
 * Extends Specification specific to SearchableEntity
 * 
 * @author Surabhi Bhawsar
 * @since 2017-11-09
 *
 */
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
		
		Predicate p = criteriaBuilder.disjunction();
		
		if (searchParameters != null && !searchParameters.isEmpty()) {
			
			searchParameters.entrySet().forEach( searchParam -> p.getExpressions().
						add(criteriaBuilder.like(from.get(searchParam.getKey()), "%" + searchParam.getValue() + "%")));
		}
		
		//WHEN search spec is not null
		if (StringUtils.isNotBlank(searchSpec)) {
			List<String> searchColumns =  t.getSearchFields();
			searchColumns.stream().forEach(column -> p.getExpressions().add(criteriaBuilder.like(from.get(column), "%" + searchSpec +"%")));
		} 

		return p;
	}

}
