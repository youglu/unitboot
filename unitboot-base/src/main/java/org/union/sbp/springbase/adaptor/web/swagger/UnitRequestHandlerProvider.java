package org.union.sbp.springbase.adaptor.web.swagger;

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
 * 用于子单元将子单元中的controller信息初始化到swagger.
 * @author youg
 * @since JDK1.8
 */
public class UnitRequestHandlerProvider implements RequestHandlerProvider {
    /**
     * controller的映射信息.
     */
    private RequestMappingHandlerMapping handlerMapping;
    /**
     * 类型处理器.
     */
    private HandlerMethodResolver methodResolver = new HandlerMethodResolver(new TypeResolver());

    /**
     * 构造函数.
     * @param handlerMapping 从外面传处controller的映射对象.
     */
    public UnitRequestHandlerProvider(RequestMappingHandlerMapping handlerMapping){
        this.handlerMapping = handlerMapping;
    }

    /**
     * 重写获得request处理者的方法。
     * @return
     */
    @Override
    public List<RequestHandler> requestHandlers() {
        List<RequestMappingInfoHandlerMapping> handlerMappings = new ArrayList<RequestMappingInfoHandlerMapping>();
        handlerMappings.add(handlerMapping);
        return BuilderDefaults.nullToEmptyList(handlerMappings).stream()
                .map(this.toMappingEntries()).flatMap((entries) -> StreamSupport.stream(entries.spliterator(), false)).map(this.toRequestHandler()).sorted(Orderings.byPatternsCondition()).collect(Collectors.toList());
    }
    private Function<RequestMappingInfoHandlerMapping, Iterable<Map.Entry<RequestMappingInfo, HandlerMethod>>> toMappingEntries() {
        return (input) -> input.getHandlerMethods().entrySet();
    }
    private Function<Map.Entry<RequestMappingInfo, HandlerMethod>, RequestHandler> toRequestHandler() {
        return (input) -> new WebMvcRequestHandler("", this.methodResolver, (RequestMappingInfo)input.getKey(), (HandlerMethod)input.getValue());
    }
}
