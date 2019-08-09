package com.think.http;

import com.think.http.constant.StatusCode;
import com.think.http.context.HttpContext;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Router {
    private HttpContext context;
    private Class<?> clazz;
    private Method invoker;
    private HttpMethod httpMethod;
    private String endPoint;
    private Class<?> returnType;
    private List<String> parameterNames;
    private String regex;

    public Router() {
        this.parameterNames = new ArrayList<>();
    }

    public void setContext(HttpContext context) {
        this.context = context;
    }

    public Object doHandle() throws Exception {
        Object[] params = resolveParameter(invoker, parameterNames, context);
        Object result = invoker.invoke(clazz.newInstance(), params);
        if (result  == null) {
            return StatusCode.SUCCESS;
        }
        return result;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public void setInvoker(Method invoker) {
        this.invoker = invoker;
        this.returnType = invoker.getReturnType();
    }

    public void setHttpMethod(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;

        String[] strings = endPoint.split("/");
        StringBuilder strBuilder = new StringBuilder();

        for (int i = 0, len = strings.length; i < len; i++) {
            if (strings[i].length() == 0) {
                continue;
            }
            strBuilder.append("/");
            if (strings[i].startsWith("{")) {
                parameterNames.add(strings[i].substring(1, strings[i].length() - 1));
                strBuilder.append("([^/]+)");
            } else {
                strBuilder.append(strings[i]);
            }
        }

        this.regex = strBuilder.toString();
    }

    public String getEndPoint() {
        return endPoint;
    }

    public String getRegex() {
        return regex;
    }

    public List<String> getParameterNames() {
        return parameterNames;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    private static Object[] resolveParameter(Method method, List<String> params, HttpContext context) {
        int paramCount = method.getParameterCount();
        Object[] values = new Object[paramCount];
        Parameter[] parameters = method.getParameters();
        Map<String, String> valueMap = context.getPathParameters();
        for (int i = 0; i < paramCount; i++) {
            Parameter p = parameters[i];
            // ugly code：TODO
            if (p.getType().equals(Long.class)) {
                values[i] = Long.parseLong(valueMap.get(params.get(i)));
            } else if (p.getType().equals(Integer.class)) {
                values[i] = Integer.parseInt(valueMap.get(params.get(i)));
            } else if (p.getType().equals(String.class)) {
                values[i] = valueMap.get(params.get(i));
            } else if (p.getType().equals(HttpContext.class)) {
                values[i] = context;
            }
        }
        return values;
    }
}
