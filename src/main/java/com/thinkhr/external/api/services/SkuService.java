package com.thinkhr.external.api.services;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.thinkhr.external.api.exception.APIErrorCodes;
import com.thinkhr.external.api.exception.ApplicationException;

/**
 *
 * Provides a collection of all services related with Skus
 * database object

 * @author Surabhi Bhawsar
 * @Since 2018-02-15
 *
 * 
 */
@Service
public class SkuService extends CommonService {
    
    public void validateSkus(Collection<Integer> skuIds) {
        List<Integer> allSkuIds = skuRepository.findAllSkus();

        skuIds.removeAll(allSkuIds);

        if (!CollectionUtils.isEmpty(skuIds)) {
            throw ApplicationException.createBadRequest(APIErrorCodes.SKU_IDS_NOT_EXISTS,
                    StringUtils.join(skuIds, ","));
        }
    }
    
}
