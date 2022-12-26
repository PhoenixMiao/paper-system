//package com.phoenix.paper.config;
//
//import com.phoenix.paper.util.ShuaiDatabaseUtils;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.io.IOException;
//import java.net.InetSocketAddress;
//import java.nio.ByteBuffer;
//import java.nio.channels.SocketChannel;
//
//@Configuration
//public class ShuaiDatabaseConfig {
//
//    public static int DEFAULT_PORT = 8888;
//
//    public static int BUFFER_SIZE = 100000;
//
//    @Bean
//    public ShuaiDatabaseUtils shuaiDatabaseInitializer() throws IOException {
//        SocketChannel client = SocketChannel.open(new InetSocketAddress(DEFAULT_PORT));
//        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
//        client.read(buffer);
//        buffer.clear();
//        return new ShuaiDatabaseUtils(client,buffer);
//    }
//}
