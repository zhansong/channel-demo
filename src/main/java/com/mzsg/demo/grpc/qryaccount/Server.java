package com.mzsg.demo.grpc.qryaccount;

import java.io.IOException;

public class Server {

    private static int port = 8883;
    private static io.grpc.Server server;

    public void run() {
        QryAccountServiceGrpc.QryAccountServiceImplBase modifyAccountServiceImpl = new QryAccountServiceImpl();
        server = io.grpc.ServerBuilder.forPort(port).addService(modifyAccountServiceImpl).build();
        try {
            server.start();
            System.out.println("Server start success on port:" + port);
            server.awaitTermination();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.run();
    }
}
