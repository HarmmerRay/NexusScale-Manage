# NexusScale-Manage

此为管理端。

# hadoop集群服务器配置信息
## Hadoop配置

格式化：hdfs namenode -format 
如果修改了配置文件需要重新格式化，记得将所有机器上的datanode和namenode、tmp目录里面的东西都给删除，再在master机器上执行格式化命令。否则会报，clusterId不匹配。

测试： echo "hello world!" > input.txt中，创建这样一个文本文件
hdfs dfs -mkdir -p /test
hdfs dfs -put input.txt /test
hadoop jar hadoop-mapreduce-examples-3.1.3.jar wordcount /test /test/output



## Prometheus+Grafana配置

准备包: prometheus-3.2.1.linux-amd64.tar.gz   grafana-enterprise-11.3.3.linux-amd64.tar.gz  

​	node_exporter-1.9.0.linux-amd64.tar.gz  

四台虚拟机上 : 

```
tar -zxvf node_exporter-1.9.0.linux-amd64.tar.gz

cd node_exporter-1.9.0.linux-amd64

node_exporter &
```



Master机器上:

tar -zxvf prometheus-3.2.1.linux-amd64  

cd prometheus-3.2.1.linux-amd64

vim prometheus.yml  修改文件内容scrape_configs，添加一个job_name，告知其集群的信息

```
scrape_configs:
  # The job name is added as a label `job=<job_name>` to any timeseries scraped from this config.
  - job_name: "prometheus"

    # metrics_path defaults to '/metrics'
    # scheme defaults to 'http'.

    static_configs:
      - targets: ["master:9090"]
  - job_name: 'node_exporter'
    static_configs:
      - targets: ['slave1:9100','slave2:9100','slave3:9100']
```

./prometheus --config.file=prometheus.yml &



tar -zxvf grafana-enterprise-11.3.3.linux-amd64.tar.gz 

cd grafana-enterprise-11.3.3.linux-amd64

grafana-server web &

# hadoop集群各组件启动信息

## 启动hadoop

master单机执行 start-dfs.sh ,start-yarn.sh
访问hadoop: 192.168.56.10:9870
访问yarn: 192.168.56.10:8088

## 启动ZooKeeper(只在三台slave上部署)

三台slave机器执行 zkServer.sh start
zkServer.sh status 查看启动状态

## 启动hbase(先启动ZK)

master单机执行 start-hbase.sh
访问hbase: 192.168.56.10:16010

## 启动Kafka(只在三台slave上，无Kraft)

三台slave机器执行：进入到kafka_2.13-3.0.0:目录下执行 
        kafka-server-start.sh -daemon config/server.properties
测试：
创建topic:
kafka-topics.sh --create --bootstrap-server slave1:9092,slave2:9092,slave3:9092 --topic topicName --partitions 3 --replication-factor 2

列出topic：
kafka-topics.sh --list --bootstrap-server slave1:9092,slave2:9092,slave3:9092

## 启动hive(依赖mysql)

配置好后执行此命令初始化一次（只用首次启动时）：schematool -dbType mysql -initSchema
弄好后 hive 即可使用hive-cli连接上hive了
## 启动Prometheus+Grafana

只在master机器上 ： grafana-server web &

​					./prometheus --config.file=prometheus.yml &

所有机器上运行 node_export &


## 启动ElasticSearch+Kibana
四台机器 cd elastisearch/bin     ./elasticsearch
一台机器 cd kibana/bin ./kibana






