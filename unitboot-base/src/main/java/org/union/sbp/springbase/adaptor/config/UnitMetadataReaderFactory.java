package org.union.sbp.springbase.adaptor.config;

import org.springframework.boot.type.classreading.ConcurrentReferenceCachingMetadataReaderFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.classreading.MetadataReader;
import org.union.sbp.springbase.adaptor.io.UnitResourceWrapper;

import java.io.IOException;

/**
 * 暂时不需要了
 * @deprecated 由启动器提供统一类与资源访问hook实现.
 */
@Deprecated
public class UnitMetadataReaderFactory extends ConcurrentReferenceCachingMetadataReaderFactory {
        public UnitMetadataReaderFactory(){
            super();
        }
        public UnitMetadataReaderFactory(ResourceLoader resourceLoader) {
            super(resourceLoader);
        }
        @Override
        public MetadataReader getMetadataReader(Resource resource) throws IOException {
            return super.getMetadataReader(new UnitResourceWrapper(resource));
        }
    }