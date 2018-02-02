package com.thinkhr.external.api.services.utils;

import static com.thinkhr.external.api.ApplicationConstants.ASCENDING;
import static com.thinkhr.external.api.ApplicationConstants.DEFAULT_LIMIT;
import static com.thinkhr.external.api.ApplicationConstants.DEFAULT_OFFSET;
import static com.thinkhr.external.api.ApplicationConstants.DESENDING;
import static com.thinkhr.external.api.ApplicationConstants.VALID_FORMAT_YYYY_MM_DD;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;

import com.thinkhr.external.api.db.entities.Configuration;
import com.thinkhr.external.api.db.entities.SearchableEntity;
import com.thinkhr.external.api.exception.APIErrorCodes;
import com.thinkhr.external.api.exception.ApplicationException;
import com.thinkhr.external.api.services.EntitySearchSpecification;
import com.thinkhr.external.api.services.OffsetPageRequest;

/**
 * Class is specific to keep utility methods for Entity Search feature. 
 * @author Surabhi Bhawsar
 * @since 2017-11-09
 *
 */
public class EntitySearchUtil {

    private static Logger logger = LoggerFactory.getLogger(EntitySearchUtil.class);

    /**
     * Get Pageable instance
     * @param offset
     * @param limit
     * @param sortedBy
     * @return
     */
    public static Pageable getPageable(Integer offset, Integer limit, String sortedBy, String defaultSortedBy) {

        OffsetPageRequest pageable = null;

        offset = offset == null ? DEFAULT_OFFSET : offset;
        limit = limit == null ? DEFAULT_LIMIT : limit;

        sortedBy = StringUtils.isBlank(sortedBy) ? defaultSortedBy : sortedBy.trim();

        Sort.Direction sortDirection = getSortDirection(sortedBy);

        sortedBy = extractSortDirection(sortedBy, sortDirection); // Extracted out + or - character from sortBy string

        Sort sort = new Sort(sortDirection, sortedBy);

        pageable = new OffsetPageRequest(offset/limit, limit, sort);

        pageable.setOffset(offset);

        return pageable;
    }

    /**
     * It will extract + or - character those stands for sort direction from column field name
     * @param sortedBy
     * @param sortDirection
     * @return
     */
    public static String extractSortDirection(String sortedBy, Sort.Direction sortDirection) {
        String directionIndicator = sortedBy.substring(0,1);
        if(directionIndicator.equals(ASCENDING) || directionIndicator.equals(DESENDING) ) {
            sortedBy = sortedBy.substring(1);
        } 

        return sortedBy.trim();
    }

    /**
     * Return formatted string to display like +-fieldName will be formatted as 
     * 
     * fieldName ASC or fieldName DESC
     */
    public static String getFormattedString(String sortBy) {
        if (StringUtils.isBlank(sortBy)) {
            return sortBy;
        }
        Sort.Direction sortDirection = getSortDirection(sortBy);
        String sortedBy = extractSortDirection(sortBy, sortDirection);

        return sortedBy + " " + sortDirection.name();
    }

    /**
     * Get the first character out from sortedBy value. 
     * like +companyName
     * @param sortedBy
     * @return
     */
    public static Direction getSortDirection(String sortedBy) {
        String sortDirection =  sortedBy.substring(0, 1);
        return DESENDING.equalsIgnoreCase(sortDirection) ? Direction.DESC : Direction.ASC;
    }

    /**
     * @return
     */
    public static Set<String> getSortAndLimitRequestParams() {
        //TODO: To externalize them
        String requestParams[] = {"offset", "limit", "sort", "searchSpec"};
        return new HashSet<String>(Arrays.asList(requestParams));
    }

    /**
     * To validate given Class has field with fieldName or not
     * @param <T>
     * 
     * @param kclass
     * @param fieldName
     * @return
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     */
    public static <T> boolean classHasField(Class<T> kclass, String fieldName) {
        try {
            Field field = kclass.getDeclaredField(fieldName);
            if (field == null) {
                return false;
            }
        } catch(NoSuchFieldException | SecurityException ex) {
            return false;
        }

        return true;
    }

    /**
     * To check type of field for given parameters and validate it is java.lang.String or not
     * 
     * @param <T>
     * @param kclass
     * @param fieldName
     * @param fieldType
     * @return
     */
    public static <T> boolean isFieldOfType(Class<T> kclass, String fieldName,  Class<?> fieldType) {
        Field field = null;
        try {
            field = kclass.getDeclaredField(fieldName);
        } catch(NoSuchFieldException | SecurityException ex) {
            return false;
        }
        if (field.getType().isAssignableFrom(fieldType)) {
            return true;
        }

        return false;
    }

    /**
     * To check type of field for given parameters and validate it is java.lang.String or not
     * 
     * @param <T>
     * @param kclass
     * @param fieldName
     * @return
     */
    public static <T> boolean isStringField(Class<T> kclass, String fieldName) {
        return isFieldOfType(kclass, fieldName, String.class);
    }

    /**
     * To check type of field for given parameters and validate it is java.util.Date or not
     * 
     * @param <T>
     * @param kclass
     * @param fieldName
     * @return
     */
    public static <T> boolean isDateField(Class<T> kclass, String fieldName) {
        return isFieldOfType(kclass, fieldName, Date.class);
    }

    /**
     * Get the date for a given string
     * @param dateStr
     * @param key
     * @return
     * @throws ApplicationException 
     */
    public static Date convertToDate(String value, String key) throws ApplicationException {

        SimpleDateFormat sdf = new SimpleDateFormat(VALID_FORMAT_YYYY_MM_DD);
        sdf.setLenient(false);
        try {
            return sdf.parse(value);
        } catch (ParseException e) {
            throw ApplicationException.createBadRequest(APIErrorCodes.INVALID_DATE_FORMAT, key, value);
        } catch (Exception e) {
            throw ApplicationException.createBadRequest(APIErrorCodes.INVALID_DATE_FORMAT, key, value);
        }
    }

    /**
     * To filter request parameters on field Name
     * @param <T>
     * 
     * @param allRequestParams
     * @param kclass
     * @return 
     */
    public static <T> Map<String, String> extractParametersForFilterRecords(Map<String, String> allRequestParams,
            Class<T> kclass) throws ApplicationException {

        Set<String> excludedParams = getSortAndLimitRequestParams();

        Map<String, String> filteredParameters = new HashMap<String, String>();

        for(Entry<String, String> entry : allRequestParams.entrySet()) { 
            if (excludedParams.contains(entry.getKey())) {
                continue;
            } else if (!classHasField(kclass, entry.getKey())) {
                throw ApplicationException.createBadRequest(APIErrorCodes.REQUEST_PARAM_INVALID, entry.getKey(), kclass.getName());
            } 
            filteredParameters.put(entry.getKey(), entry.getValue());
        }

        return filteredParameters;
    }

    /**
     * Create Entity Search Specification
     * It will give priority over requestParameters on searchSpec
     * 
     * @param searchSpec
     * @param requestParameters
     * @return
     * @throws ApplicationException 
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <T> Specification<T> getEntitySearchSpecification(String searchSpec,
            Map<String, String> requestParameters, Class<T> kclass, SearchableEntity entity) throws ApplicationException {

        //To get any other requestParameter like Company's fieldName to filter record on filterName with this case we will ignore searchSpec.
        if (requestParameters != null) {
            Map<String, String> requestParametersForFilterRecords = EntitySearchUtil.extractParametersForFilterRecords(requestParameters, kclass);
            if (requestParametersForFilterRecords != null && !requestParametersForFilterRecords.isEmpty()) {
                return new EntitySearchSpecification(requestParametersForFilterRecords, entity);
            } 
        }

        if (StringUtils.isNotBlank(searchSpec)) {
            if(logger.isDebugEnabled()) {
                logger.debug("Applying searchSpec filter on records " + searchSpec);
            }
            return new EntitySearchSpecification (searchSpec, entity);
        }

        return null;
    }

    /**
     * To add additional filters in search specification
     * @param <T>
     * @param spec
     * @param filterParamMap
     * @return 
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <T> Specification<Configuration> applyAdditionalFilter(
            Specification<T> spec, Map<String, String> filterParamMap, SearchableEntity entity) {

        if (spec == null) {
            return new EntitySearchSpecification(filterParamMap, entity);
        }
        
        EntitySearchSpecification eSpec = ((EntitySearchSpecification)spec);
        
        if (eSpec.getSearchParameters() == null || eSpec.getSearchParameters().isEmpty()) {
            eSpec.setSearchParameters(filterParamMap);
            return eSpec;
        }
        
        eSpec.getSearchParameters().putAll(filterParamMap);
        
        return eSpec ;
    }
    
}
