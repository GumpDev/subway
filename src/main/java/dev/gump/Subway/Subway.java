package dev.gump.Subway;

import com.github.simplenet.Client;
import com.github.simplenet.Server;
import com.github.simplenet.packet.Packet;
import dev.gump.Subway.interfaces.SubwayActions;
import dev.gump.Subway.interfaces.SubwayConfig;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Subway {
    private static Server server;
    private static SubwayConfig config;

    private static List<Client> connected = new ArrayList<>();

    private static HashMap<String, Class<? extends Train>> registryClass = new HashMap<>();
    private static HashMap<String, SubwayActions> registryActions = new HashMap<>();

    public static void init(SubwayConfig _config) throws IOException {
        config = _config;
        startServer();
    }

    static void startServer(){
        server = new Server();
        server.onConnect(client -> {
            log(client + " has connected!");

            client.readByteAlways(opcode -> {
                if(opcode != 1 && !connected.contains(client)){
                    client.close();
                    return;
                }
                switch (opcode) {
                    case 1 -> client.readString(message -> {
                        if (config.getPassword().equals(message) || Objects.equals(config.getPassword(), "")) {
                            connected.add(client);
                            log(client + " has authenticated!");
                        }
                    });
                    case 2 -> client.readString(registry -> {
                        if(!registryClass.containsKey(registry)) {
                            Packet.builder().putByte(4).queueAndFlush(client);
                            return;
                        }
                        client.readInt(size -> {
                            client.readBytes(size, bytes -> {
                                try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                                     ObjectInputStream in = new ObjectInputStream(bis)) {
                                    Class<? extends Train> aClass = registryClass.get(registry);

                                    try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
                                         ObjectOutputStream out = new ObjectOutputStream(bos)) {
                                        var obj = aClass.cast(in.readObject());
                                        log(client + ": " + registry + " - " + obj.getHash());
                                        out.writeObject(obj.process());
                                        out.flush();
                                        byte[] bytesResponse = bos.toByteArray();
                                        Packet.builder().putByte(2).putString(obj.getHash()).putInt(bytesResponse.length).putBytes(bytesResponse).queueAndFlush(client);
                                    }
                                } catch (IOException | ClassNotFoundException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                        });
                    });
                    case 3 -> client.readString(registry -> {
                        if(!registryActions.containsKey(registry)) {
                            Packet.builder().putByte(4).queueAndFlush(client);
                            return;
                        }
                        client.readString(hash -> {
                            client.readInt(size -> {
                                client.readBytes(size, bytes -> {
                                    try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                                         ObjectInputStream in = new ObjectInputStream(bis)) {
                                        String[] args = (String[]) in.readObject();
                                        var obj = registryActions.get(registry).onRequest(args);
                                        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
                                             ObjectOutputStream out = new ObjectOutputStream(bos)) {

                                            log(client + ": " + registry + " - " + hash);
                                            out.writeObject(obj);
                                            out.flush();

                                            byte[] bytesResponse = bos.toByteArray();

                                            Packet.builder().putByte(2).putString(hash).putInt(bytesResponse.length).putBytes(bytesResponse).queueAndFlush(client);
                                        }
                                    } catch (IOException | ClassNotFoundException e) {
                                        throw new RuntimeException(e);
                                    }
                                });
                            });
                        });
                    });
                    case 5 -> client.readString(hash -> {
                        client.readLong(time -> {
                            try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
                                 ObjectOutputStream out = new ObjectOutputStream(bos)) {

                                log(client + ": " + "ping - " + hash);
                                out.writeObject(time);


                                byte[] bytesResponse = bos.toByteArray();
                                Packet.builder().putByte(2).putString(hash).putInt(bytesResponse.length).putBytes(bytesResponse).queueAndFlush(client);
                            }catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });
                    });
                }
            });

            client.preDisconnect(() -> {
                connected.remove(client);
            });
        });

        if(config.getThreads() != 0)
            server.bind(config.getHost(), config.getPort(), config.getThreads());
        else
            server.bind(config.getHost(), config.getPort());
        log("Subway server started at " + config.getHost() + ":" + config.getPort());
    }

    static void log(String msg){
        if(config.isDebug())
            System.out.println("[Subway server] " + msg);
    }

    public static void registerClass(Class<? extends Train> train){
        registryClass.put(train.getName(), train);
    }
    public static void registerAction(String action, SubwayActions callback){
        registryActions.put(action, callback);
    }

    public static SubwayConfig getConfig() {
        return config;
    }
}
