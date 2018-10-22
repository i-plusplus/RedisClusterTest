package redis.clients.jedis;

import javafx.util.Pair;
import redis.clients.jedis.exceptions.JedisDataException;
import redis.clients.util.JedisClusterCRC16;

import java.util.*;

/**
 * Created by paras.mal on 22/10/18.
 */
public class MyPipeline extends Pipeline {

    protected JedisClusterWithPipeline jedisClusterWithPipeline;
    protected Map<JedisPool, Jedis> map = new HashMap<>();
    public void setJedisClusterWithPipeline(JedisClusterWithPipeline jedisClusterWithPipeline) {
        this.jedisClusterWithPipeline = jedisClusterWithPipeline;
    }

    protected Client getClient(String key) {
        int k = JedisClusterCRC16.getSlot(key);

        JedisPool jedisPool = jedisClusterWithPipeline.getJedisPoolFromSlot(k);
        if(jedisPool == null || !map.containsKey(jedisPool)) {
            Pair<JedisPool, Jedis> jedis = jedisClusterWithPipeline.getConnectionFromSlot(k);
            if(!map.containsKey(jedis.getKey())){
                map.put(jedis.getKey(), jedis.getValue());
            }
            jedisPool = jedis.getKey();
        }

        return map.get(jedisPool).getClient();
    }

    public void sync() {
        try{
        if(this.getPipelinedResponseLength() > 0) {
            List unformatted = new LinkedList<>();
            for(Jedis j : map.values()){
                unformatted.addAll(j.getClient().getAll());
            }
            Iterator var2 = unformatted.iterator();

            while(var2.hasNext()) {
                Object o = var2.next();
                this.generateResponse(o);
            }
        }
    }finally {
        returnResource();
    }

    }

    public List<Object> syncAndReturnAll() {
        try {
            if (this.getPipelinedResponseLength() <= 0) {
                return Collections.emptyList();
            } else {
                List unformatted = new LinkedList<>();
                for (Jedis j : map.values()) {
                    unformatted.addAll(j.getClient().getAll());
                }
                ArrayList formatted = new ArrayList();
                Iterator var3 = unformatted.iterator();

                while (var3.hasNext()) {
                    Object o = var3.next();

                    try {
                        formatted.add(this.generateResponse(o).get());
                    } catch (JedisDataException var6) {
                        formatted.add(var6);
                    }
                }

                return formatted;
            }
        }finally {
            returnResource();
        }
    }

    protected void returnResource(){
        for(Jedis jedis : map.values()){
            jedis.close();
        }
    }
}
