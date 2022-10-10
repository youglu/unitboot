package com.union.springbase;

import com.union.springbase.ClientConfig;
import com.union.springbase.NamedHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * @author tianwen.yin
 */

public class NamedHttpClientConfiguration {

    public NamedHttpClientConfiguration(){
        System.out.println("init NamedHttpClientConfiguration,");
    }

    @Value("${http.client.name}")
    private String httpClientName;

    @Bean
    @ConditionalOnMissingBean
    public ClientConfig clientConfig(Environment env) {
        return new ClientConfig(httpClientName, env);
    }

    @Bean
    @ConditionalOnMissingBean
    public NamedHttpClient namedHttpClient(ClientConfig clientConfig) {
        return new NamedHttpClient(httpClientName, clientConfig);
    }

}
