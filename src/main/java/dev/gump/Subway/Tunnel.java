package dev.gump.Subway;

import com.github.simplenet.Client;
import com.github.simplenet.packet.Packet;
import dev.gump.Subway.interfaces.SubwayConfig;
import dev.gump.Subway.interfaces.TunnelConfig;
import dev.gump.Subway.interfaces.TunnelResponse;

import java.io.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class Tunnel {
    private Client client;
    private final TunnelConfig config;
    private final HashMap<String, TunnelResponse> callbacks = new HashMap<>();

    private boolean connected;

    public Tunnel(TunnelConfig config){
        this.config = config;
        startClient();
    }
    public Tunnel(SubwayConfig config){
        this.config = new TunnelConfig(config.getHost(), config.getPort(), config.getPassword(), config.isDebug());
        startClient();
    }

    void startClient(){
        client = new Client();
        client.onConnect(() -> {
            connected = true;
            client.readByteAlways(opcode->{
                switch (opcode) {
                    case 1 -> sendError("Forbbiden");
                    case 2 -> {
                        client.readString(hash->{
                            if(!callbacks.containsKey(hash)) return;
                            client.readInt(size->{
                                client.readBytes(size, bytes -> {
                                    callbacks.get(hash).onResponse(bytes);
                                });
                            });
                        });
                    }
                    case 4 -> sendError("Class or function not recognized by server");
                }
            });

            client.preDisconnect(() -> {
                connected = false;
            });

            Packet.builder().putByte(1).putString(config.getPassword()).queueAndFlush(client);
        });

        client.connect(config.getIp(), config.getPort());
    }


    //Fazer sistema de reconnect
    public <T,E extends Train<T>> void sendTrain(E train, TunnelResponse callback) throws Exception {
        if(!connected) {
            if(config.getReconnectTime() != 0) {
                log("Trying to reconnect...");
                client.connect(config.getIp(), config.getPort());
                Thread.sleep(config.getReconnectTime());
            }
            if(!connected)
                throw new Exception("Client is disconnected");
            else
                log("Reconnected to the Server!");
        }

        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream out = new ObjectOutputStream(bos)) {
            out.writeObject(train);
            out.flush();

            byte[] bytes = bos.toByteArray();

            callbacks.put(train.getHash(), callback);
            Packet.builder().putByte(2).putString(train.getClass().getName()).putInt(bytes.length).putBytes(bytes).queueAndFlush(client);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> Future<T> send(String action, String ...args) throws Exception {
        if(!connected) {
            if(config.getReconnectTime() != 0) {
                log("Trying to reconnect...");
                client.connect(config.getIp(), config.getPort());
                Thread.sleep(config.getReconnectTime());
            }
            if(!connected)
                throw new Exception("Client is disconnected");
            else
                log("Reconnected to the Server!");
        }

        CompletableFuture<T> completableFuture = new CompletableFuture<>();
        String hash = UUID.randomUUID().toString();
        callbacks.put(hash, response -> {
            try (ByteArrayInputStream bis = new ByteArrayInputStream(response);
                 ObjectInputStream in = new ObjectInputStream(bis)) {
                completableFuture.complete((T) in.readObject());
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });

        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream out = new ObjectOutputStream(bos)) {
            out.writeObject(args);
            out.flush();

            byte[] bytes = bos.toByteArray();
            Packet packet = Packet.builder().putByte(3).putString(action).putString(hash).putInt(bytes.length).putBytes(bytes);
            packet.queueAndFlush(client);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return completableFuture;
    }

    public Future<Long> ping() throws Exception {
        if(!connected) {
            if(config.getReconnectTime() != 0) {
                log("Trying to reconnect...");
                client.connect(config.getIp(), config.getPort());
                Thread.sleep(config.getReconnectTime());
            }
            if(!connected)
                throw new Exception("Client is disconnected");
            else
                log("Reconnected to the Server!");
        }

        CompletableFuture<Long> completableFuture = new CompletableFuture<>();
        String hash = UUID.randomUUID().toString();
        callbacks.put(hash, response -> {
            try (ByteArrayInputStream bis = new ByteArrayInputStream(response);
                 ObjectInputStream in = new ObjectInputStream(bis)) {
                Long that = (Long) in.readObject();
                Date date = new Date();
                completableFuture.complete(date.getTime() - that);
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });

        Date date = new Date();
        Packet.builder().putByte(5).putString(hash).putLong(date.getTime()).queueAndFlush(client);
        return completableFuture;
    }

    public void close(){
        client.close();
    }

    void sendError(String msg){
        throw new Error(msg);
    }

    void log(String msg){
        if(config.isDebug())
            System.out.println("[Tunnel Client] " + msg);
    }

    public TunnelConfig getConfig() {
        return config;
    }
}
