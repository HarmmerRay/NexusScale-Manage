package com.nexuscale.nexusscalemanage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceStateMessage {
    private Integer deviceId;
    private Integer state;
} 