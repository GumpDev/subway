package Tests;

import dev.gump.Subway.Train;

import java.util.Random;

public class TestTrain extends Train<String> {

    int id = 0;

    public TestTrain(int s){
        id = s;
    }

    @Override
    public String process(){
        Random rnd = new Random();
        return this.getClass().getName() + " " + id + " " + rnd.nextInt(10);
    }
}
