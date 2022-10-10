package org.union.sbp.springbase.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import springfox.documentation.service.Documentation;
import springfox.documentation.spring.web.plugins.DocumentationPluginsBootstrapper;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * swagger动态处理.
 * @author youg
 * @since jdk1.8
 */
public class SpringSwaggerUtil {
    private final static Logger log = LoggerFactory.getLogger(SpringSwaggerUtil.class);

    /**
     * 获得可以编绎的现有主context中 DocumentationMap
     * @return
     */
    private static Map<String, Documentation> getRootContextDocumentationMap(){
        final DocumentationPluginsBootstrapper rootDocumentationPluginsBootstrapper = SpringContextUtil.getApplicationContext().getBean(DocumentationPluginsBootstrapper.class);
        //由于DocumentationCache没有提供按key清除，所以先保存现有的，再清除指定key后再设回。
        final Map<String, Documentation> documentationMap = new LinkedHashMap<>();
        documentationMap.putAll(rootDocumentationPluginsBootstrapper.getScanned().all());
        return documentationMap;
    }
    /**
     * 将指定的documentation map设置到主context中.
     * @param documentationMap 要设置的map
     * @return
     */
    private static void getRootContextDocumentationMap(final Map<String, Documentation> documentationMap){
        final DocumentationPluginsBootstrapper rootDocumentationPluginsBootstrapper = SpringContextUtil.getApplicationContext().getBean(DocumentationPluginsBootstrapper.class);
        //清除原有的Map.
        rootDocumentationPluginsBootstrapper.getScanned().clear();
        //再将处理后的map加到原来的Map。
        documentationMap.forEach((key,docu)->{
            rootDocumentationPluginsBootstrapper.getScanned().addDocumentation(docu);
        });
    }

}
