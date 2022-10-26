package org.union.sbp.springbase.adaptor.io;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.union.sbp.springbase.utils.SpringContextUtil;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class UnitResourceWrapper extends ClassPathResource {

    private Resource resource;
    public UnitResourceWrapper(String path, @Nullable ClassLoader classLoader) {
        super(path,classLoader);
        resource = this;
    }
    public UnitResourceWrapper(Resource resource) throws IOException {
        super(((ClassPathResource)resource).getPath(),Thread.currentThread().getContextClassLoader());
        this.resource = resource;
    }
    @Nullable
    protected URL resolveURL() {
        URL url = super.resolveURL();
        if(null == url){
            // 再用根context加载
            try {
                url = SpringContextUtil.getApplicationContext().getParent().getResource(this.getPath()).getURL();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return url;
    }
    @Override
    public InputStream getInputStream() throws IOException {
        InputStream is = null;
        try{
           is = super.getInputStream();
           if(null != is){
               return is;
           }
       }catch (FileNotFoundException fileNotFoundException){
            // System.out.println("资源不存在:"+this.getPath()+",classloader:"+getClassLoader());
            URL url = Thread.currentThread().getContextClassLoader().getResource(this.getPath());
            if(null == url){
                url = SpringContextUtil.getApplicationContext().getParent().getResource(this.getPath()).getURL();
            }
            if(null != url){
                is = url.openStream();
            }else{
                System.out.println("无法获得输入流:"+this.getPath());
            }
        }catch (Exception e){
            throw e;
       }
       return is;
    }
}
