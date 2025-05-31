#!/bin/bash

# 定义机器列表
MASTER="zy@192.168.56.10"
SLAVES=("zy@slave1" "zy@slave2" "zy@slave3")

# 停止 Hadoop
echo "停止 Hadoop..."
stop-yarn.sh
stop-dfs.sh
echo "Hadoop 停止完成。"

# 停止 HBase
echo "停止 HBase..."
stop-hbase.sh
echo "HBase 停止完成。"

# 停止 Kafka
echo "停止 Kafka..."
for slave in "${SLAVES[@]}"; do
    ssh $slave 'bash -l -c "kafka-server-stop.sh /home/zy/下载/kafka_2.13-3.0.0/config/server.properties"'
done
echo "Kafka 停止完成。"

# 停止 Hive 相关服务（Hive 本身没有显式的停止命令，一般停止相关的 metastore 等服务）
# 如果有启动 metastore 服务，可以在这里添加停止命令
echo "Hive 相关服务停止完成。"

echo "所有组件停止完成。"

# 停止 ZooKeeper
echo "停止 ZooKeeper..."
for slave in "${SLAVES[@]}"; do
    ssh $slave 'bash -l -c "zkServer.sh stop"'
    ssh $slave 'bash -l -c "zkServer.sh status"'
done
echo "ZooKeeper 停止完成。"




