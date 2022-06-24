package dev.gump.Subway;

import dev.gump.Subway.interfaces.TrainInterface;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;


public class Train<T> implements Serializable, TrainInterface<T> {

    public String hash;

    public Train(){
        hash = UUID.randomUUID().toString();
    }

    public Future<T> send(Tunnel tunnel) throws Exception {
        CompletableFuture<T> completableFuture = new CompletableFuture<>();
        tunnel.sendTrain(this, response -> {
            try (ByteArrayInputStream bis = new ByteArrayInputStream(response);
                 ObjectInputStream in = new ObjectInputStream(bis)) {
                completableFuture.complete((T) in.readObject());
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
        return completableFuture;
    }

    public String getHash() {
        return hash;
    }

    @Override
    public T process() {
        return null;
    }
}
