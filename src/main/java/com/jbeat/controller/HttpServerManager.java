package com.jbeat.controller;
import com.sun.net.httpserver.*;
import java.io.*;
import java.net.InetSocketAddress;

public class HttpServerManager {
    public void startServer(int port) throws IOException {
        String portStr = System.getenv("PORT");
        port = (portStr != null) ? Integer.parseInt(portStr) : 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", port), 0);
        ApiRouter.route(server);
        server.setExecutor(null);
        server.start();
        System.out.println("J-Beat 서버가 포트 " + port + "에서 시작되었습니다.");
    }
}

