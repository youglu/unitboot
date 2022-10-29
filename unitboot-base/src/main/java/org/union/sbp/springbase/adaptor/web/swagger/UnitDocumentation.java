package org.union.sbp.springbase.adaptor.web.swagger;

import org.springframework.util.StringUtils;
import springfox.documentation.service.ApiListing;
import springfox.documentation.service.Documentation;

import java.util.*;

/**
 * 单元swagger Documentation包状类
 * @author youg
 * @since JDK1.8
 */
public class UnitDocumentation extends Documentation {
    private String unitName;
    public UnitDocumentation(Documentation originalDocumentation,final String unitName) {
        super(originalDocumentation.getGroupName(),
                originalDocumentation.getBasePath(),
                originalDocumentation.getTags(),
                originalDocumentation.getApiListings(),
                originalDocumentation.getResourceListing(),
                new HashSet<>(originalDocumentation.getProduces()),
                new HashSet<>(originalDocumentation.getConsumes()),
                originalDocumentation.getHost(),
                new HashSet<>(originalDocumentation.getSchemes()),
                originalDocumentation.getServers(),
                originalDocumentation.getExternalDocumentation(),
                originalDocumentation.getVendorExtensions());
        this.unitName = unitName;
    }
    /**
     * 增加/unitName作为前缀，此前缀为server.servlet.contextPath配置，但在
     * 整合中没法获得，导至需要这样处理.
     * @return
     */
    @Override
    public Map<String, List<ApiListing>> getApiListings() {
        final Map<String, List<ApiListing>> apiListings = super.getApiListings();
        if(!StringUtils.isEmpty(unitName) && null != apiListings && !apiListings.isEmpty()){
            final Map<String, List<ApiListing>> unitApiListings = new HashMap<>();//super.getApiListings();
            apiListings.forEach((key,apilist)->{
                final List<ApiListing> unitApis = new ArrayList<>();
                apilist.forEach(api->{
                    unitApis.add(new UnitApiListingWrapper(api,unitName));
                });
                unitApiListings.put(key,unitApis);
            });
            // 考虑在这里更换父类中的类型.
            return unitApiListings;
        }
        return apiListings;
    }
}
