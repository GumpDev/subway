package Tests;

import dev.gump.Subway.Tunnel;

public class TestClient {
    public static void main(String[] args) throws Exception {
        Tunnel tunnel = new Tunnel(TestServer.config);
        TestTrain testTrain = new TestTrain(1);
        String response = testTrain.send(tunnel).get();
        System.out.println(response);

        System.out.println(tunnel.send("teste", "a").get());
        System.out.println(tunnel.ping().get() + "ms");
    }
}
