package com.nexuscale.nexusscalemanage.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@TableName("messages")
@ApiModel(value = "Message对象", description = "消息表")
public class Message {
    @ApiModelProperty("消息主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("用户ID")
    @TableField("user_id")
    private String userId;

    @ApiModelProperty("是否来自客户端")
    @TableField("from_client")
    private Boolean fromClient;

    @ApiModelProperty("消息内容")
    @TableField("message")
    private String message;
} 