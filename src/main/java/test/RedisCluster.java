package test;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.util.HashSet;
import java.util.Random;

/**
 * Created by paras.mal on 18/10/18.
 */
public class RedisCluster implements Runnable{

    public void run(){
        JedisCluster jedisCluster = new JedisCluster(
                new HashSet<HostAndPort>(){{add(new HostAndPort("127.0.0.1", 7001));}}
                , new GenericObjectPoolConfig(){{setMaxTotal(200);setMaxIdle(200);}});
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
        for(int k = 0;k<n;k++)
            new Thread(new RedisCluster()).start();
    }
}


