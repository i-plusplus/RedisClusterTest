package test;

import javafx.util.Pair;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.*;
import redis.clients.util.JedisClusterCRC16;

import java.util.*;

/**
 * Created by paras.mal on 18/10/18.
 */
public class RedisClusterWithPipeline implements Runnable{

    public void run(){
        JedisClusterWithPipeline jedisCluster = new JedisClusterWithPipeline(
                new HashSet<HostAndPort>(){{add(new HostAndPort("127.0.0.1", 7001));}}
        , new GenericObjectPoolConfig(){{setMaxTotal(200);setMaxIdle(200);}});
        Random random = new Random();
        long l = System.currentTimeMillis();

        for(int i = 0;i<1000000;i++) {
            Map<JedisPool, Pair<Jedis,Pipeline>> map = new HashMap<>();
            for(int j = i+10;i<=j;i++) {
                String v = String.valueOf(random.nextInt(10000));

                int k = JedisClusterCRC16.getSlot(v);
                JedisPool jedisPool = jedisCluster.getJedisPoolFromSlot(k);
                if(jedisPool == null || !map.containsKey(jedisPool)) {
                    Pair<JedisPool, Jedis> jedis = jedisCluster.getConnectionFromSlot(k);
                    if(!map.containsKey(jedis.getKey())){
                        map.put(jedis.getKey(), new Pair<>(jedis.getValue(), jedis.getValue().pipelined()));
                    }
                    jedisPool = jedis.getKey();
                }
                map.get(jedisPool).getValue().get(v);
            }
            i--;
            List<Object> list = new ArrayList<>(10);
            for(Pair<Jedis,Pipeline> jedis : map.values()){
                list.addAll(jedis.getValue().syncAndReturnAll());
                jedis.getKey().close();
            }
        }
        System.out.println(System.currentTimeMillis() - l);
    }

    public static void main(String s[]) throws Exception{
        int n = 1;
        if(s.length>1){
            n = Integer.parseInt(s[1]);
        }
        for(int k = 0;k<n;k++)
            new Thread(new RedisClusterWithPipeline()).start();
    }
}


