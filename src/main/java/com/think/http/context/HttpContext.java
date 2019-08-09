package com.think.http.context;

import com.think.http.Router;
import com.think.http.handler.RouterHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Http context object.
 *
 * @author veione 2019/08/07
 */
public class HttpContext {
    private final HttpRequest request;
    private final ChannelHandlerContext ctx;
    private String uri;
    private String endPoint;
    private HttpMethod method;
    private Map<String, String> parameters;
    private Map<String, String> pathParameters;

    public HttpContext(ChannelHandlerContext ctx, HttpRequest request) {
        this.request = request;
        this.ctx = ctx;
        this.parameters = new HashMap<>();
        this.pathParameters = new HashMap<>();
    }

    /**
     * Parse client request
     *
     * @param request
     * @throws IOException
     */
    public void parse(HttpRequest request) throws IOException {
        this.uri = request.uri();
        this.method = request.method();
        if (this.method == HttpMethod.POST) {
            HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(request);
            List<InterfaceHttpData> paramList = decoder.getBodyHttpDatas();
            for (InterfaceHttpData param : paramList) {
                Attribute data = (Attribute) param;
                parameters.put(data.getName(), data.getValue());
            }
        } else {
            new QueryStringDecoder(this.uri).parameters().entrySet().forEach(e -> parameters.put(e.getKey(), e.getValue().get(0)));
        }

        String endpoint = uri.split("\\?")[0];
        if (endpoint.endsWith("/")) {
            endpoint = endpoint.substring(0, endpoint.length());
        }
        this.uri = endpoint;

        Set<Map.Entry<String, Router>> set = RouterHandler.getRouterMap().entrySet();
        for (Map.Entry<String, Router> entry : set) {
            Router router = entry.getValue();
            Pattern pattern = Pattern.compile("^" + router.getRegex() + "$");
            Matcher matcher = pattern.matcher(endpoint);
            if (matcher.find()) {
                this.endPoint = router.getEndPoint();
                if (matcher.groupCount() > 0) {
                    for (int i = 0; i < matcher.groupCount(); i++) {
                        addPathParameter(router.getParameterNames().get(i), matcher.group(i + 1));
                    }
                }
                break;
            }
        }
    }

    private void addPathParameter(String key, String param) {
        this.pathParameters.put(key, param);
    }

    /**
     * 获取给定键对应的value值
     *
     * @param key
     * @return
     */
    public String getParameter(String key) {
        return parameters.get(key);
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public HttpRequest getRequest() {
        return request;
    }

    public String getUri() {
        return uri;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public HttpMethod getHttpMethod() {
        return method;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public Map<String, String> getPathParameters() {
        return pathParameters;
    }
}
