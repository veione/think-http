package com.think.http.result;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.think.http.constant.StatusCode;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;

import static io.netty.handler.codec.http.HttpHeaderNames.*;
import static io.netty.handler.codec.http.HttpHeaderValues.APPLICATION_JSON;
import static io.netty.handler.codec.http.HttpHeaderValues.CHUNKED;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Json result implementation
 *
 * @author veione 2019/08/07
 */
public class JsonResult implements Result {

    private byte[] data;

    @Override
    public void handle(Object result) {
        if (result instanceof StatusCode) {
            StatusCode statusCode = (StatusCode) result;
            JSONObject data = new JSONObject(2);
            data.put("msg", statusCode.msg);
            data.put("code", statusCode.code);
            this.data = JSON.toJSONString(data).getBytes();
        } else {
            this.data = JSON.toJSONString(result).getBytes();
        }
    }

    @Override
    public FullHttpResponse getResponse() {
        ByteBuf buf = Unpooled.wrappedBuffer(this.data);
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, buf);

        response.headers().set(CONTENT_TYPE, APPLICATION_JSON);
        response.headers().set(TRANSFER_ENCODING, CHUNKED);
        response.headers().set(CONTENT_LENGTH, buf.readableBytes());
        return response;
    }
}
