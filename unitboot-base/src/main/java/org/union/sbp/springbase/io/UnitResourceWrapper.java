package org.union.sbp.springbase.io;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.lang.Nullable;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class UnitResourceWrapper extends ClassPathResource {

    public UnitResourceWrapper(String path, @Nullable ClassLoader classLoader) {
        super(path,classLoader);//Thread.currentThread().getContextClassLoader());//path,classLoader);//
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
