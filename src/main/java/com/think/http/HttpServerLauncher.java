package com.think.http;

import com.think.http.server.HttpServer;

/**
 * Hello world!
 *
 * @author veione 2019/08/07
 */
public class HttpServerLauncher {
    public static void main(String[] args) {
        HttpServer server = new HttpServer();
        server.start();
    }
}
