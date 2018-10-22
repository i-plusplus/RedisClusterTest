# RedisClusterTest
RedisCluster vs Twemproxy 

This project demostrate the difference between Twemproxy and RedisCluster when it comes to high performance large in memory datasets. 
RedisCluster solves all the issues originaly comes with Twemproxy along with providing all(most) the current features of twemproxy.

Twemproxy : responsible for sharding based on given configurations. We can start multiple Twemproxy servers with same configurations. Call first goes to Twemproxy and then Twemproxy calls Redis to fire the command, it support all type of commands. 

Architecture based on twemproxy has many issues : 
1. Data has to be on same host as twemproxy to avoid n/w call. So, Data cannot be sharded over different servers.
2. Twem proxy takes more CPU then actual Redis and add extra hop. This is specially a problem in AWS where redis machines have lesser CPU power then compute machines.
3. Adding another shard and re sharding is very difficult process.
4. Adding another twem is difficult because we have to add it to client code manually by production build. 

RedisCluster : It makes Twemproxy obsolete by adding sharding intelligence in client and Redis servers. It provide support for adding new shards by providing auto-sharding and It further auto-syncs b/w client and server when we add another shard. 

Advantages of using RedisCluster instead of Twemproxy. 
1. Data can be sharding in multiple hosts.
2. Logic of computing the correct shard is on the client itself. This does 2 thing.
      a. Removes extra hop
      b. Move the CPU requirements on different box. 
3. Easy to add new shard
4. Auto sync the shard information with client which means we don't have to do any code upload. 
5. Cutting Redis command time by 50%. 

Note : 
RedisCluster java client jedis doesn't provide pipeline support. I have written my own wrapper to support pipeline with jedis. 


Test Results : Running with 10 threads with 4 redis and 4 twemproxy as 1st setup and 4 redis servers in redis clusters as 2nd setup.

Setup	Call Type	Time Taken(Sec)	Redis CPU * 4	Client CPU	Twemproxy CPU * 4	Total CPU on given time	Total CPU cycles
Twemproxy
GET	140	22	90	50	378	52920
GET Pipeline	35	25	70	50	370	12950
RedisCluster
GET	67	44	190	0	366	24522
GET Pipeline	29	44	180	0	356	10324
 


