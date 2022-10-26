package org.union.sbp.springbase.adaptor.web.swagger;

import org.springframework.util.StringUtils;
import springfox.documentation.service.ApiDescription;
import springfox.documentation.service.ApiListing;

/**
 * 单元swagger ApiDescription包状类
 * @author youg
 * @since JDK1.8
 */
public class UnitApiDescriptionWrapper extends ApiDescription {
    private String unitName;
    public UnitApiDescriptionWrapper(ApiDescription apiDescription, final String unitName){
        super(apiDescription.getDescription(),
                apiDescription.getPath(),
                apiDescription.getSummary(),
                apiDescription.getDescription(),
                apiDescription.getOperations(),
                apiDescription.isHidden());
        this.unitName = unitName;
        if(!StringUtils.isEmpty(unitName) && !unitName.startsWith("/")){
            this.unitName = "/"+this.unitName;
        }
    }
    public String getPath() {
        if(!StringUtils.isEmpty(unitName)){
            return unitName+super.getPath();
        }
        return super.getPath();
    }
}
