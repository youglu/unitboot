package org.union.sbp.springbase.adaptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.union.sbp.springbase.utils.SpringContextUtil;
import org.union.sbp.springbase.utils.SpringSwaggerUtil;
import springfox.documentation.service.Documentation;
import springfox.documentation.spring.web.plugins.DocumentationPluginsBootstrapper;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * swagger适配
 * @author youg
 * @since jdk1.8
 */
public class SwaggerAdaptor {
    private final static Logger log = LoggerFactory.getLogger(SwaggerAdaptor.class);
    /**
     * 将子单元的swagger增加到主context documentcache中.
     */
    public static void addUnitSwagger(ApplicationContext unitApplicationContext){
        DocumentationPluginsBootstrapper unitDocumentationPluginsBootstrapper = unitApplicationContext.getBean(DocumentationPluginsBootstrapper.class);
        DocumentationPluginsBootstrapper rootDocumentationPluginsBootstrapper = SpringContextUtil.getApplicationContext().getBean(DocumentationPluginsBootstrapper.class);

        //由于DocumentationCache没有提供按key清除，所以先保存现有的，再清除指定key后再设回。
        final Map<String, Documentation> documentationMap = new LinkedHashMap<>();
        documentationMap.putAll(rootDocumentationPluginsBootstrapper.getScanned().all());

        log.info("停止主Context DocumentationPluginsBootstrapper");
        rootDocumentationPluginsBootstrapper.stop();
        log.info("启动主Context DocumentationPluginsBootstrapper");
        rootDocumentationPluginsBootstrapper.start();

        log.info("将子context的swagger docket添加到主swagger documentcache中");
        // 重新扫描一次
        unitDocumentationPluginsBootstrapper.stop();
        unitDocumentationPluginsBootstrapper.start();

        documentationMap.putAll(unitDocumentationPluginsBootstrapper.getScanned().all());
        // 将子单元的swagger Document加到主单元中的document缓存中
        documentationMap.forEach((key,docu)->{
            rootDocumentationPluginsBootstrapper.getScanned().addDocumentation(docu);
        });


    }
    /**
     * 在停止子单元后，需要将子单元的swagger缓存信息从主context中移除.
     */
    public static void removeUnitSwagger(final ApplicationContext unitApplicationContext){
        final DocumentationPluginsBootstrapper unitDocumentationPluginsBootstrapper = unitApplicationContext.getBean(DocumentationPluginsBootstrapper.class);
        final DocumentationPluginsBootstrapper rootDocumentationPluginsBootstrapper = SpringContextUtil.getApplicationContext().getBean(DocumentationPluginsBootstrapper.class);
        //由于DocumentationCache没有提供按key清除，所以先保存现有的，再清除指定key后再设回。
        final Map<String, Documentation> documentationMap = new LinkedHashMap<>();
        documentationMap.putAll(rootDocumentationPluginsBootstrapper.getScanned().all());
        //在临时的map中清除指定key。
        unitDocumentationPluginsBootstrapper.getScanned().all().forEach((key,docu)->{
            documentationMap.remove(key);
        });
        //清除原有的Map.
        rootDocumentationPluginsBootstrapper.getScanned().clear();
        //再将处理后的map加到原来的Map。
        documentationMap.forEach((key,docu)->{
            rootDocumentationPluginsBootstrapper.getScanned().addDocumentation(docu);
        });
    }
}
