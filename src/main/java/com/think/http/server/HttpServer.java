package com.think.http.server;

import com.think.http.constant.Constants;
import com.think.http.util.Config;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Http server
 *
 * @author veione
 */
public class HttpServer extends Thread {
    private Logger logger = LoggerFactory.getLogger(HttpServer.class);
    private Channel channel;

    public HttpServer() {
        setName("Think-Http");
    }

    @Override
    public void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.option(ChannelOption.SO_BACKLOG, 1024);
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .option(ChannelOption.SO_REUSEADDR, true)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new HttpServerChannelFactory());

            int listenPort = Config.getInt("http.port");
            if (listenPort == 0) {
                listenPort = Constants.DEFAULT_HTTP_SERVER_PORT;
            }
            this.channel = b.bind(listenPort).sync().channel();
            logger.info("Think http server startup successfully, listen on port: [{}]", listenPort);
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            logger.error("Start http server fail", e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            if (channel.isOpen()) {
                channel.close();
            }
        }
    }
}
