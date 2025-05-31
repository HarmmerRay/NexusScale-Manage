#!/bin/bash

# 定义机器列表
MASTER="zy@192.168.56.10"
SLAVES=("zy@slave1" "zy@slave2" "zy@slave3")
# 启动 ZooKeeper
echo "启动 ZooKeeper..."
for slave in "${SLAVES[@]}"; do
    ssh $slave 'bash -l -c "zkServer.sh start"'
    ssh $slave 'bash -l -c "zkServer.sh status"'
done
echo "ZooKeeper 启动完成。"

# 启动 Hadoop
echo "启动 Hadoop..."
#ssh $MASTER "start-dfs.sh && start-yarn.sh"
start-dfs.sh
start-yarn.sh
echo "Hadoop 启动完成。可以通过 http://$(echo $MASTER | cut -d'@' -f2):9870 访问 Hadoop，通过 http://$(echo $MASTER | cut -d'@' -f2):8088 访问 YARN。"

# 启动 HBase
echo "启动 HBase..."
#ssh $MASTER "start-hbase.sh"
start-hbase.sh
echo "HBase 启动完成。可以通过 http://$(echo $MASTER | cut -d'@' -f2):16010 访问 HBase。"

# 启动 Kafka
echo "启动 Kafka..."
#KAFKA_DIR="kafka_2.13-3.0.0"
for slave in "${SLAVES[@]}"; do
    ssh $slave 'bash -l -c "kafka-server-start.sh -daemon /home/zy/下载/kafka_2.13-3.0.0/config/server.properties"'
done
echo "Kafka 启动完成。"

# 测试 Kafka，创建并列出 topic
#echo "测试 Kafka，创建并列出 topic..."
#bootstrap_servers="${SLAVES[0]#*@}:9092,${SLAVES[1]#*@}:9092,${SLAVES[2]#*@}:9092"
#ssh $MASTER "kafka-topics.sh --create --bootstrap-server $bootstrap_servers --topic topicName --partitions 3 --replication-factor 2"
#ssh $MASTER "kafka-topics.sh --list --bootstrap-server $bootstrap_servers"

# 启动 Hive
echo "启动 Hive..."
# 首次启动时初始化
#ssh $MASTER "schematool -dbType mysql -initSchema"
#ssh $MASTER "hive"
echo "Hive 启动完成。"

echo "所有组件启动完成。"
    
