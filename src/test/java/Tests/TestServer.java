package Tests;

import dev.gump.Subway.Subway;
import dev.gump.Subway.interfaces.SubwayActions;
import dev.gump.Subway.interfaces.SubwayConfig;

import java.io.IOException;

public class TestServer {
    public static SubwayConfig config = new SubwayConfig("localhost", 33990, "password", true);
    public static void main(String[] args) throws IOException {
        Subway.registerClass(TestTrain.class);
        Subway.registerAction("teste", new SubwayActions<String>() {
            @Override
            public String onRequest(String... args) {
                return args[0];
            }
        });
        Subway.init(config);
    }
}
