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
@TableName("device_template")
@ApiModel(value = "DeviceTemplate对象", description = "设备模板表")
public class DeviceTemplate {
    @ApiModelProperty("模板主键")
    @TableId(value = "dt_id", type = IdType.AUTO)
    private Integer dtId;

    @ApiModelProperty("显示名称")
    @TableField("show_name")
    private String showName;

    @ApiModelProperty("英文名称")
    @TableField("en_name")
    private String enName;

    @ApiModelProperty("模板配置JSON")
    @TableField("template")
    private String template;

    @ApiModelProperty("创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @ApiModelProperty("MAC地址前缀")
    @TableField("mac_pre")
    private String macPre;
} 