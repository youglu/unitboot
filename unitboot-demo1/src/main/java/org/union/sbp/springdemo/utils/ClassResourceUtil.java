package org.union.sbp.springdemo.utils;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassResourceUtil {

    private final static String RESOURCE_PATTERN = "/**/*.class";
    /**
     * 用于获得指定路径下的所有资源.
     */
    private final static ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
    /**
     * MetadataReader 的工厂类,用于读取类信息
     */
    private final static  MetadataReaderFactory readerfactory = new CachingMetadataReaderFactory(resourcePatternResolver);
    /**
     * 通过包名获取项目包下所有类
     * @param basePackage 包名
     * @return
     */
    public static Map<String, List<Class>> getObject(final String basePackage, final String annotationClassName) {
        try {
            //将包名转换为路径名.
            String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                    ClassUtils.convertClassNameToResourcePath(basePackage) +
                    RESOURCE_PATTERN;
            Resource[] resources = resourcePatternResolver.getResources(pattern);
            if(null == resources ||resources.length <= 0){
                return null;
            }
            Map<String, List<Class>> classListMap = new HashMap<String, List<Class>>();
            List<Class> classList = new ArrayList<>();
            for (Resource resource : resources) {
                //用于读取类信息
                MetadataReader reader = readerfactory.getMetadataReader(resource);
                //扫描到的class
                String classname = reader.getClassMetadata().getClassName();
                Class<?> clazz = Class.forName(classname);
                Class annatationClass = Class.forName(annotationClassName);
                Annotation annotation = clazz.getAnnotation(annatationClass);
               if (null != annotation) {
                   classList.add(clazz);
               }
            }
            classListMap.put(annotationClassName, classList);
            return classListMap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
    	//传入类的包路径和类注解的值
        Map<String, List<Class>> map = ClassResourceUtil.getObject("org.union.sbp.springdemo", RestController.class.getName());
        System.out.println(map);
    }
}