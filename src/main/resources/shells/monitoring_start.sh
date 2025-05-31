#!/bin/bash
source /etc/profile

# 获取用户和用户组
USER="zy"
GROUP="zy"

# 检查是否为主机
if [ "$HOST_NAME" = "master" ]; then
    # 检查 grafana-server 是否正在运行，若运行则关闭
    if pgrep -f "/home/zy/下载/grafana-v11.3.3" > /dev/null; then
        sudo -u $USER -g $GROUP pkill -f "/home/zy/下载/grafana-v11.3.3"
        sleep 2  # 等待服务关闭
    fi
    # 启动 grafana-server
    sudo -u $USER -g $GROUP bash -c '/home/zy/下载/grafana-v11.3.3/bin/grafana-server --homepath /home/zy/下载/grafana-v11.3.3 web &'

    # 检查 prometheus 是否正在运行，若运行则关闭
    if pgrep -f "/home/zy/下载/prometheus-3.2.1.linux-amd64/prometheus" > /dev/null; then
        sudo -u $USER -g $GROUP pkill -f "/home/zy/下载/prometheus-3.2.1.linux-amd64/prometheus"
        sleep 2  # 等待服务关闭
    fi
    # 启动 prometheus
    sudo -u $USER -g $GROUP bash -c '/home/zy/下载/prometheus-3.2.1.linux-amd64/prometheus --config.file=/home/zy/下载/prometheus-3.2.1.linux-amd64/prometheus.yml &'
fi

# 检查 node_exporter 是否正在运行，若运行则关闭
if pgrep -f "/home/zy/下载/node_exporter-1.9.0.linux-amd64/node_exporter" > /dev/null; then
    sudo -u $USER -g $GROUP pkill -f "/home/zy/下载/node_exporter-1.9.0.linux-amd64/node_exporter"
    sleep 2  # 等待服务关闭
fi
# 在所有机器上启动 node_exporter
sudo -u $USER -g $GROUP bash -c '/home/zy/下载/node_exporter-1.9.0.linux-amd64/node_exporter &'
