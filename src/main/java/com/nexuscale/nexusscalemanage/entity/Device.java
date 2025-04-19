package com.nexuscale.nexusscalemanage.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@TableName("device")
@ApiModel(value = "Device对象", description = "设备表")
public class Device {
    @ApiModelProperty("设备主键")
    @TableId(value = "device_id",type = IdType.AUTO)
    private Integer deviceId;

    @ApiModelProperty("设备标识号")
    @TableField("device_mac")
    private String deviceMac;

    @ApiModelProperty("设备名字")
    @TableField("device_name")
    private String deviceName;

    @ApiModelProperty("设备状态")
    @TableField("state")
    private String state;

    @ApiModelProperty("用户外键")
    @TableField("user_id")
    private String userId;

    @ApiModelProperty("创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
