package com.think.http.result;

import io.netty.handler.codec.http.FullHttpResponse;

/**
 * 视图结果接口类
 *
 * @author veione 2019/08/07
 */
public interface Result {
    /**
     * 处理结果
     *
     * @param result
     */
    void handle(Object result);

    /**
     * 获取http响应消息
     *
     * @return
     */
    FullHttpResponse getResponse();
}
