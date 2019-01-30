package Spider;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Scrawl {
    public static void main(String args[]){
        ExecutorService servicePool= Executors.newFixedThreadPool(10);
        try {
            while(true) {
                servicePool.execute(new spider());
                Thread.sleep(9999999);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
