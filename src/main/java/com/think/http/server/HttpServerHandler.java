package com.think.http.server;

import com.think.http.context.HttpContext;
import com.think.http.handler.RouterHandler;
import com.think.http.result.Result;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.util.concurrent.FastThreadLocal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.netty.handler.codec.http.HttpHeaderNames.*;
import static io.netty.handler.codec.http.HttpHeaderValues.KEEP_ALIVE;
import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * http message handler.
 *
 * @author veione
 */
public class HttpServerHandler extends SimpleChannelInboundHandler<Object> {
    private final Logger logger = LoggerFactory.getLogger(HttpServerHandler.class);
    private final FastThreadLocal<HttpContext> localHttpContext = new FastThreadLocal<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            if (msg instanceof HttpRequest) {
                HttpRequest req = (HttpRequest) msg;

                if (HttpUtil.is100ContinueExpected(req)) {
                    ctx.write(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE));
                }

                boolean keepAlive = HttpUtil.isKeepAlive(req);

                HttpContext context = new HttpContext(ctx, req);
                context.parse(req);
                localHttpContext.set(context);

                // execute handle logic
                Result result = RouterHandler.execute(context);
                FullHttpResponse response = result.getResponse();

                if (!keepAlive) {
                    ctx.write(response).addListener(ChannelFutureListener.CLOSE);
                } else {
                    response.headers().set(CONNECTION, KEEP_ALIVE);
                    ctx.write(response);
                }
            }
        } catch (Exception e) {
            logger.error("Occur exception when handle http message", e);
        } finally {
            localHttpContext.remove();
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        logger.error("Occur exception when handle http message", cause);
        ctx.close();
    }
}
