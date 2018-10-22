
# create cluster
tar -xvf redis-5.0.0.tar.gz
cd redis-5.0.0
make
cd ..
redis-5.0.0/src/redis-server cluster-test/7000/redis.conf &
redis-5.0.0/src/redis-server cluster-test/7001/redis.conf &
redis-5.0.0/src/redis-server cluster-test/7002/redis.conf &
redis-5.0.0/src/redis-server cluster-test/7003/redis.conf &
#give response as yes
redis-5.0.0/src/redis-cli --cluster create 127.0.0.1:7000 127.0.0.1:7001 127.0.0.1:7002 127.0.0.1:7003

#inserting some values
for i in {1..100000}; do redis-5.0.0/src/redis-cli -c -p 7000 set $i 1; done


redis-5.0.0/src/redis-cli -p 7000 dbsize
redis-5.0.0/src/redis-cli -p 7001 dbsize
redis-5.0.0/src/redis-cli -p 7002 dbsize
redis-5.0.0/src/redis-cli -p 7003 dbsize
#../redis-5.0.0/src/redis-cli --cluster add-node 127.0.0.1:7003 127.0.0.1:7000
#../redis-5.0.0/src/redis-cli -p 7003 cluster nodes
#../redis-5.0.0/src/redis-cli --cluster reshard 127.0.0.1:7000
#../redis-5.0.0/src/redis-cli -p 7000 dbsize
#../redis-5.0.0/src/redis-cli -p 7001 dbsize
#../redis-5.0.0/src/redis-cli -p 7002 dbsize
#../redis-5.0.0/src/redis-cli -p 7003 dbsize


#twemproxy

tar -xvf nutcracker-0.4.0.tar.gz
cd nutcracker-0.4.0
./configure
make
cd ..
#starting redis for twem test
redis-5.0.0/src/redis-server twem-test/6000/redis.conf &
redis-5.0.0/src/redis-server twem-test/6001/redis.conf &
redis-5.0.0/src/redis-server twem-test/6002/redis.conf &
redis-5.0.0/src/redis-server twem-test/6003/redis.conf &
#starting twem proxies
nutcracker-0.4.0/src/nutcracker -c twem-test/nc/test.yml -s 22222 &
nutcracker-0.4.0/src/nutcracker -c twem-test/nc/test2.yml -s 22223 &
nutcracker-0.4.0/src/nutcracker -c twem-test/nc/test3.yml -s 22224 &
nutcracker-0.4.0/src/nutcracker -c twem-test/nc/test4.yml -s 22225 &
#inserting some values using twemproxy
for i in {1..100000}; do redis-5.0.0/src/redis-cli -p 5000 set $i 1; done

redis-5.0.0/src/redis-cli -p 6000 dbsize
redis-5.0.0/src/redis-cli -p 6001 dbsize
redis-5.0.0/src/redis-cli -p 6002 dbsize
redis-5.0.0/src/redis-cli -p 6003 dbsize

# jedis test

#build package
mvn package
cd target
java -cp redistest-1.0-SNAPSHOT-jar-with-dependencies.jar test.RedisClusterWithCustomPipeline 10 10
java -cp redistest-1.0-SNAPSHOT-jar-with-dependencies.jar test.TwemproxyWithPipeline 10 10
java -cp redistest-1.0-SNAPSHOT-jar-with-dependencies.jar test.Twemproxy 10 10
java -cp redistest-1.0-SNAPSHOT-jar-with-dependencies.jar test.RedisCluster 10 10








