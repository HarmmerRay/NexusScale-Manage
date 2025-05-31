# 修改设备状态API - change_sensor_state

## 接口说明

### POST /device/change_sensor_state

**功能**: 根据设备ID修改MySQL数据库中指定设备的状态值。

**请求参数**:
- `deviceId` (int, 必需): 设备ID
- `state` (int, 必需): 设备状态值 (0=关闭, 1=开启)

## 请求示例

### 开启设备
```bash
POST http://localhost:8080/device/change_sensor_state
Content-Type: application/x-www-form-urlencoded

deviceId=1&state=1
```

### 关闭设备
```bash
POST http://localhost:8080/device/change_sensor_state
Content-Type: application/x-www-form-urlencoded

deviceId=1&state=0
```

## 响应格式

### 成功响应
```json
{
    "state": 200,
    "msg": "设备状态修改成功，当前状态开启",
    "data": "设备状态修改成功，当前状态开启"
}
```

### 失败响应（设备不存在）
```json
{
    "state": 500,
    "msg": "设备状态修改失败，设备不存在或更新失败"
}
```

### 错误响应（异常情况）
```json
{
    "state": 500,
    "msg": "设备状态修改失败：具体错误信息"
}
```

## 状态值说明

| 状态值 | 说明 | 返回消息 |
|--------|------|----------|
| 0 | 关闭 | "设备状态修改成功，当前状态关闭" |
| 1 | 开启 | "设备状态修改成功，当前状态开启" |

## 数据库操作

### 更新的表和字段
- **表**: `device`
- **字段**: `state` (tinyint(4))
- **条件**: `device_id = {deviceId}`

### SQL示例
```sql
UPDATE device SET state = ? WHERE device_id = ?
```

## 业务逻辑

1. **接收参数**: 获取前端传递的 `deviceId` 和 `state` 参数
2. **参数验证**: 验证参数的有效性
3. **数据库更新**: 调用 `DeviceService.updateDeviceState()` 方法更新设备状态
4. **联查设备模板**: 根据 `deviceId` 联查 `device` 和 `device_template` 表获取 `en_name`
5. **Redis推送**: 使用 `en_name` 作为Redis list的topic，推送设备状态消息
6. **结果返回**: 根据更新结果返回相应的响应消息

## 实现细节

### DeviceService.updateDeviceState()
```java
@Override
public boolean updateDeviceState(int deviceId, int state) {
    // 构建更新条件，只更新指定设备的状态
    LambdaQueryWrapper<Device> wrapper = new LambdaQueryWrapper<>();
    wrapper.eq(Device::getDeviceId, deviceId);
    
    // 创建要更新的设备对象，只设置状态字段
    Device device = new Device();
    device.setState(state);
    
    // 执行更新操作
    int result = deviceMapper.update(device, wrapper);
    return result > 0;
}
```

### Controller处理逻辑
```java
@PostMapping("/change_sensor_state")
public Map<String, Object> changeSensorState(@RequestParam int state, @RequestParam int deviceId) {
    try {
        // 1. 调用服务层更新设备状态
        boolean success = deviceService.updateDeviceState(deviceId, state);
        
        if (success) {
            // 2. 查询设备模板信息获取en_name作为Redis topic
            DeviceTemplate deviceTemplate = deviceService.getDeviceTemplateByDeviceId(deviceId);
            
            if (deviceTemplate != null && deviceTemplate.getEnName() != null) {
                // 3. 创建设备状态消息对象
                DeviceStateMessage message = new DeviceStateMessage(deviceId, state);
                
                // 4. 推送消息到Redis list
                String topic = deviceTemplate.getEnName();
                redisService.pushDeviceStateMessage(topic, message);
            }
            
            String statusMsg = (state == 1) ? "开启" : "关闭";
            return ApiResponse.success("设备状态修改成功，当前状态" + statusMsg);
        } else {
            return ApiResponse.fail("设备状态修改失败，设备不存在或更新失败");
        }
    } catch (Exception e) {
        return ApiResponse.fail("设备状态修改失败：" + e.getMessage());
    }
}
```

## 测试方法

### PowerShell测试
```powershell
# 开启设备
$params = @{
    deviceId = 1
    state = 1
}
$body = ($params.GetEnumerator() | ForEach-Object { "$($_.Key)=$($_.Value)" }) -join "&"
Invoke-RestMethod -Uri "http://localhost:8080/device/change_sensor_state" -Method Post -Body $body -ContentType "application/x-www-form-urlencoded"

# 关闭设备
$params = @{
    deviceId = 1
    state = 0
}
$body = ($params.GetEnumerator() | ForEach-Object { "$($_.Key)=$($_.Value)" }) -join "&"
Invoke-RestMethod -Uri "http://localhost:8080/device/change_sensor_state" -Method Post -Body $body -ContentType "application/x-www-form-urlencoded"
```

### 运行测试脚本
```powershell
# 运行完整测试脚本
.\test_change_sensor_state.ps1

# 运行Redis集成测试脚本
.\test_redis_integration.ps1
```

### cURL测试
```bash
# 开启设备
curl -X POST "http://localhost:8080/device/change_sensor_state" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "deviceId=1&state=1"

# 关闭设备
curl -X POST "http://localhost:8080/device/change_sensor_state" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "deviceId=1&state=0"
```

## 前端集成示例

### JavaScript/AJAX
```javascript
function changeDeviceState(deviceId, state) {
    const formData = new FormData();
    formData.append('deviceId', deviceId);
    formData.append('state', state);
    
    fetch('/device/change_sensor_state', {
        method: 'POST',
        body: formData
    })
    .then(response => response.json())
    .then(data => {
        if (data.state === 200) {
            console.log('设备状态修改成功:', data.msg);
            alert(data.msg);
        } else {
            console.error('设备状态修改失败:', data.msg);
            alert('修改失败: ' + data.msg);
        }
    })
    .catch(error => {
        console.error('请求错误:', error);
        alert('网络错误，请稍后重试');
    });
}

// 使用示例
changeDeviceState(1, 1); // 开启设备ID为1的设备
changeDeviceState(1, 0); // 关闭设备ID为1的设备
```

### Vue.js示例
```vue
<template>
  <div>
    <button @click="toggleDeviceState(device)" 
            :class="device.state ? 'btn-success' : 'btn-danger'">
      {{ device.state ? '关闭' : '开启' }} 设备
    </button>
  </div>
</template>

<script>
export default {
  methods: {
    async toggleDeviceState(device) {
      const newState = device.state ? 0 : 1;
      
      try {
        const formData = new FormData();
        formData.append('deviceId', device.deviceId);
        formData.append('state', newState);
        
        const response = await fetch('/device/change_sensor_state', {
          method: 'POST',
          body: formData
        });
        
        const result = await response.json();
        
        if (result.state === 200) {
          device.state = newState; // 更新本地状态
          this.$message.success(result.msg);
        } else {
          this.$message.error(result.msg);
        }
      } catch (error) {
        this.$message.error('网络错误，请稍后重试');
      }
    }
  }
}
</script>
```

## 错误处理

### 常见错误情况
1. **设备不存在**: 当提供的 `deviceId` 在数据库中不存在时
2. **数据库连接错误**: 数据库连接失败或SQL执行异常
3. **参数缺失**: 未提供必需的 `deviceId` 或 `state` 参数
4. **参数类型错误**: 参数类型不匹配（如传递字符串而非数字）

### 异常日志
系统会在控制台输出详细的错误信息：
```
接收到的状态: 1
设备ID: 1
更新设备状态时发生异常: [具体异常信息]
```

## 安全考虑

1. **参数验证**: 确保 `deviceId` 和 `state` 参数的有效性
2. **权限控制**: 建议在实际使用中添加用户权限验证，确保用户只能修改自己的设备
3. **SQL注入防护**: 使用MyBatis-Plus的参数化查询避免SQL注入
4. **状态值限制**: 可以在业务层面限制状态值的范围（如只允许0和1）

## 与其他接口的关系

| 功能 | 相关接口 |
|------|----------|
| 查看设备列表 | `/device/all_devices` |
| 搜索用户设备 | `/device/search_devices_by_userid` |
| 修改设备状态 | `/device/change_sensor_state` |
| 更新设备信息 | `/device/update_device_name` |

## Redis集成功能

### Redis数据流
1. **联查数据库**: 根据 `deviceId` 联查 `device` 表和 `device_template` 表
2. **获取topic**: 使用 `device_template.en_name` 作为Redis list的topic名称
3. **数据推送**: 向Redis list推送JSON格式的设备状态消息

### Redis消息格式
```json
{
    "deviceId": 1,
    "state": 1
}
```

### Redis操作
- **数据结构**: List
- **Topic命名**: 使用 `device_template.en_name` (如: "temperature", "humidity", "air_component" 等)
- **操作类型**: RPUSH (右侧推入)

### Redis命令验证
```bash
# 查看某个topic的消息数量
redis-cli LLEN temperature

# 查看某个topic的所有消息
redis-cli LRANGE temperature 0 -1

# 弹出最早的消息 (LPOP)
redis-cli LPOP temperature

# 实时监控Redis操作
redis-cli MONITOR
```

### 配置说明
Redis连接配置位于 `application.properties`:
```properties
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.data.redis.password=
spring.data.redis.database=0
```

### 错误处理
- 如果设备不存在或没有关联的设备模板，会跳过Redis推送
- 如果Redis连接失败，会抛出运行时异常但不影响数据库更新
- 控制台会输出详细的Redis操作日志

这个接口为物联网设备管理系统提供了核心的设备状态控制功能，可以实现远程开关设备的操作，并将状态变化实时推送到Redis消息队列。 