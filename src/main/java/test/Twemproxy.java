package test;

import redis.clients.jedis.Jedis;

import java.util.Random;

/**
 * Created by paras.mal on 18/10/18.
 */
public class Twemproxy implements Runnable {
    int port;
    public Twemproxy(int port){
        this.port = port;
    }
    public void run(){
        Jedis jedisCluster = new Jedis("127.0.0.1", port);
        Random random = new Random();
        long l = System.currentTimeMillis();
        for(int i = 0;i<1000000;i++) {

            jedisCluster.get(String.valueOf(random.nextInt(10000)));
        }
        System.out.println(System.currentTimeMillis()-l);
    }

    public static void main(String s[]) throws Exception{
        int n = 200;
        if(s.length>1){
            n = Integer.parseInt(s[1]);
        }
        for(int k = 0;k<n/4;k++)
            new Thread(new Twemproxy(5000)).start();
        for(int k = n/4;k<n/2;k++)
            new Thread(new Twemproxy(5001)).start();
        for(int k = n/2;k<(int)(n*3.0/4);k++)
            new Thread(new Twemproxy(5002)).start();
        for(int k = (int)(n*3.0/4);k<n;k++)
            new Thread(new Twemproxy(5003)).start();
    }
}


