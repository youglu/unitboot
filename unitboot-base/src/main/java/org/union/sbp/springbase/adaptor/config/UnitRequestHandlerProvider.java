package org.union.sbp.springbase.adaptor.config;

import com.fasterxml.classmate.TypeResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.BuilderDefaults;
import springfox.documentation.spi.service.RequestHandlerProvider;
import springfox.documentation.spi.service.contexts.Orderings;
import springfox.documentation.spring.web.WebMvcRequestHandler;
import springfox.documentation.spring.web.readers.operation.HandlerMethodResolver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * 单元swagger requesthandler提供者.
 */
public class UnitRequestHandlerProvider implements RequestHandlerProvider {
    private RequestMappingHandlerMapping handlerMapping;
    private HandlerMethodResolver methodResolver = new HandlerMethodResolver(new TypeResolver());
    public UnitRequestHandlerProvider(RequestMappingHandlerMapping handlerMapping){
        this.handlerMapping = handlerMapping;
    }
    @Override
    public List<RequestHandler> requestHandlers() {
        List<RequestMappingInfoHandlerMapping> handlerMappings = new ArrayList<RequestMappingInfoHandlerMapping>();
        handlerMappings.add(handlerMapping);
        return (List) BuilderDefaults.nullToEmptyList(handlerMappings).stream()
                .map(this.toMappingEntries()).flatMap((entries) -> {
                    return StreamSupport.stream(entries.spliterator(), false);
                }).map(this.toRequestHandler()).sorted(Orderings.byPatternsCondition()).collect(Collectors.toList());
    }
    private Function<RequestMappingInfoHandlerMapping, Iterable<Map.Entry<RequestMappingInfo, HandlerMethod>>> toMappingEntries() {
        return (input) -> {
            return input.getHandlerMethods().entrySet();
        };
    }
    private Function<Map.Entry<RequestMappingInfo, HandlerMethod>, RequestHandler> toRequestHandler() {
        return (input) -> {
            return new WebMvcRequestHandler("", this.methodResolver, (RequestMappingInfo)input.getKey(), (HandlerMethod)input.getValue());
        };
    }
}
