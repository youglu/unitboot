package org.union.sbp.springbase.adaptor.web.swagger;

import org.springframework.util.StringUtils;
import springfox.documentation.service.*;

import java.util.*;

/**
 * 单元swagger ApiListing包状类
 * @author youg
 * @since JDK1.8
 */
public class UnitApiListingWrapper extends ApiListing{
    private String unitName;
    public UnitApiListingWrapper(ApiListing apiListing,final String unitName){
        super(apiListing.getApiVersion(),
                apiListing.getBasePath(),
                apiListing.getResourcePath(),
                apiListing.getProduces(),
                apiListing.getConsumes(),
                apiListing.getHost(),
                apiListing.getProtocols(),
                apiListing.getSecurityReferences(),
                apiListing.getApis(),
                apiListing.getModels(),
                apiListing.getModelSpecifications(),
                apiListing.getModelNamesRegistry(),
                apiListing.getDescription(),
                apiListing.getPosition(),
                apiListing.getTags());
        this.unitName = unitName;
    }
    @Override
    public List<ApiDescription> getApis() {
        final List<ApiDescription> apiList = super.getApis();
        if(!StringUtils.isEmpty(unitName) && null != apiList && !apiList.isEmpty()){
            if(apiList.get(0) instanceof UnitApiDescriptionWrapper){
                return apiList;
            }
            for(int i=0,L=apiList.size();i<L;i++){
                apiList.set(i,new UnitApiDescriptionWrapper(apiList.get(i),unitName));
            }
        }
        return apiList;
    }
}
