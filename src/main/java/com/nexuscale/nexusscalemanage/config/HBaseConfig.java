package com.nexuscale.nexusscalemanage.config;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * HBase配置类
 */
@Component
@ConfigurationProperties(prefix = "hbase")
public class HBaseConfig {

    private String zkQuorum = "192.168.56.11";
    private String zkPort = "2181";
    private String zkZnodeParent = "/hbase";
    private String tableName = "iot_sensor_data";
    private String columnFamily = "cf1";

    public String getZkQuorum() {
        return zkQuorum;
    }

    public void setZkQuorum(String zkQuorum) {
        this.zkQuorum = zkQuorum;
    }

    public String getZkPort() {
        return zkPort;
    }

    public void setZkPort(String zkPort) {
        this.zkPort = zkPort;
    }

    public String getZkZnodeParent() {
        return zkZnodeParent;
    }

    public void setZkZnodeParent(String zkZnodeParent) {
        this.zkZnodeParent = zkZnodeParent;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getColumnFamily() {
        return columnFamily;
    }

    public void setColumnFamily(String columnFamily) {
        this.columnFamily = columnFamily;
    }

    @Bean
    public Configuration hbaseConfiguration() {
        Configuration config = HBaseConfiguration.create();
        config.set("hbase.zookeeper.quorum", zkQuorum);
        config.set("hbase.zookeeper.property.clientPort", zkPort);
        config.set("zookeeper.znode.parent", zkZnodeParent);
        // 设置HBase客户端连接超时时间
        config.set("hbase.client.operation.timeout", "30000");
        config.set("hbase.client.scanner.timeout.period", "60000");
        return config;
    }

    @Bean
    public Connection hbaseConnection() throws IOException {
        return ConnectionFactory.createConnection(hbaseConfiguration());
    }
} 