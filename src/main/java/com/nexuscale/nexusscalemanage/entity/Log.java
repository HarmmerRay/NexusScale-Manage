package com.nexuscale.nexusscalemanage.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@TableName("log")
@ApiModel(value = "Log对象", description = "日志表")
public class Log {
    @ApiModelProperty("操作时间")
    @TableField(value = "operation_time", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime operationTime;
    @ApiModelProperty("请求路径")
    @TableField("path")
    private String path;
    @ApiModelProperty("请求参数")
    @TableField("params")
    private String params;
    @ApiModelProperty("用户ID")
    @TableField("user_id")
    private String userId;
    @ApiModelProperty("FromIp")
    @TableField("ip")
    private String ip;
    @ApiModelProperty("请求方式")
    @TableField("method")
    private String method;

    public Map<String,Object> toDict(){
        Map<String,Object> map = new HashMap<>();
        map.put("operation_time",operationTime);
        map.put("path",path);
        map.put("params",params);
        map.put("userId",userId);
        map.put("ip",ip);
        map.put("method",method);
        return map;
    }
    public Log(LocalDateTime operationTime){
        this.operationTime = operationTime;
    }
}
