package com.think.http.server;

import com.think.http.constant.Constants;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * Http server channel factory.
 *
 * @author veione 2019/08/07
 */
public class HttpServerChannelFactory extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();
        p.addLast("http-codec", new HttpServerCodec());
        p.addLast("compress", new HttpContentCompressor());
        p.addLast("objectAggregator", new HttpObjectAggregator(Constants.MAX_HTTP_CONTENT_LENGTH));
        p.addLast("chunkedWrite", new ChunkedWriteHandler());
        p.addLast("serverHandler", new HttpServerHandler());
    }
}
