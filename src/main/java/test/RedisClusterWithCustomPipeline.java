package test;

import javafx.util.Pair;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.*;
import redis.clients.util.JedisClusterCRC16;

import java.util.*;

/**
 * Created by paras.mal on 18/10/18.
 */
public class RedisClusterWithCustomPipeline implements Runnable{

    public void run(){
        JedisClusterWithPipeline jedisCluster = new JedisClusterWithPipeline(
                new HashSet<HostAndPort>(){{add(new HostAndPort("127.0.0.1", 7001));}}
                , new GenericObjectPoolConfig(){{setMaxTotal(200);setMaxIdle(200);}});
        Random random = new Random();
        long l = System.currentTimeMillis();

        for(int i = 0;i<1000000;i++) {

            Pipeline pipeline = jedisCluster.pipelined();
            for(int j = i+10;i<=j;i++) {
                pipeline.get(String.valueOf(random.nextInt(10000)));
            }
            i--;
            pipeline.syncAndReturnAll().size();

            //List<Object> list = new ArrayList<>(10);

           // System.out.println( i);
        }
        System.out.println(System.currentTimeMillis() - l);
    }

    public static void main(String s[]) throws Exception{
        int n = 1;
        if(s.length>1){
            n = Integer.parseInt(s[1]);
        }
        for(int k = 0;k<n;k++)
            new Thread(new RedisClusterWithCustomPipeline()).start();
    }
}


