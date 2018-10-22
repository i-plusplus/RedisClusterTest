package redis.clients.jedis;

import javafx.util.Pair;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import java.util.Set;

/**
 * Created by paras.mal on 21/10/18.
 */
public class JedisClusterWithPipeline extends JedisCluster {

    public JedisClusterWithPipeline(Set<HostAndPort> nodes, GenericObjectPoolConfig genericObjectPoolConfig) {
        super(nodes,genericObjectPoolConfig);
    }

    public JedisPool getJedisPoolFromSlot(int slot){
        return this.connectionHandler.cache.getSlotPool(slot);
    }
    public Pair<JedisPool,Jedis> getConnectionFromSlot(int slot)
    {
        JedisPool connectionPool = this.connectionHandler.cache.getSlotPool(slot);
        if(connectionPool != null) {
            return new Pair<>(connectionPool,connectionPool.getResource());
        } else {
            this.connectionHandler.renewSlotCache();
            connectionPool = this.connectionHandler.cache.getSlotPool(slot);
            return new Pair<>(connectionPool,connectionPool != null?connectionPool.getResource():this.connectionHandler.getConnection());
        }
    }



    public Pipeline pipelined(){
        MyPipeline pipeline = new MyPipeline();
        pipeline.setJedisClusterWithPipeline(this);
        return pipeline;
    }
}
