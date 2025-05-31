# 设备搜索API - search_devices_by_userid

## 接口说明

### GET /device/search_devices_by_userid

**功能**: 根据用户ID和关键字搜索设备，先根据用户ID筛选，再根据关键字搜索该用户的设备信息。

**请求参数**:
- `userId` (String, 必需): 用户ID
- `searchKey` (String, 可选): 搜索关键字，用于匹配设备名称或MAC地址

## 请求示例

### 1. 搜索用户的所有设备
```bash
GET http://localhost:8080/device/search_devices_by_userid?userId=user123
```

### 2. 根据关键字搜索用户的设备
```bash
GET http://localhost:8080/device/search_devices_by_userid?userId=user123&searchKey=温度
```

### 3. 根据MAC地址搜索用户的设备
```bash
GET http://localhost:8080/device/search_devices_by_userid?userId=user123&searchKey=AA:BB:CC
```

## 响应格式

### 成功响应
```json
{
    "state": 200,
    "msg": "成功",
    "data": [
        {
            "deviceId": 1,
            "deviceMac": "AA:BB:CC:DD:EE:FF",
            "deviceName": "温度传感器1",
            "state": 1,
            "userId": "user123",
            "createTime": "2024-01-01 10:00:00",
            "updateTime": "2024-01-01 10:00:00"
        },
        {
            "deviceId": 2,
            "deviceMac": "11:22:33:44:55:66",
            "deviceName": "温度传感器2",
            "state": 0,
            "userId": "user123",
            "createTime": "2024-01-01 11:00:00",
            "updateTime": "2024-01-01 11:00:00"
        }
    ]
}
```

### 错误响应
```json
{
    "state": 500,
    "msg": "用户ID不能为空"
}
```

## 搜索逻辑

1. **用户ID验证**: 首先验证userId参数是否为空
2. **无关键字**: 如果没有提供searchKey，返回该用户的所有设备
3. **有关键字**: 如果提供了searchKey，则：
   - 必须匹配指定的userId
   - 在设备名称(device_name)中进行模糊查询
   - 在设备MAC地址(device_mac)中进行模糊查询
   - 返回同时满足用户ID和关键字条件的设备

## 使用场景

### 1. 用户设备管理
```javascript
// 获取用户所有设备
fetch('/device/search_devices_by_userid?userId=user123')
    .then(response => response.json())
    .then(data => {
        console.log('用户所有设备:', data.data);
    });
```

### 2. 设备名称搜索
```javascript
// 搜索用户的温度相关设备
fetch('/device/search_devices_by_userid?userId=user123&searchKey=温度')
    .then(response => response.json())
    .then(data => {
        console.log('用户的温度设备:', data.data);
    });
```

### 3. MAC地址搜索
```javascript
// 根据MAC地址片段搜索设备
fetch('/device/search_devices_by_userid?userId=user123&searchKey=AA:BB')
    .then(response => response.json())
    .then(data => {
        console.log('匹配的设备:', data.data);
    });
```

## PowerShell测试命令

### 测试1: 获取用户所有设备
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/device/search_devices_by_userid?userId=test123" -Method Get
```

### 测试2: 搜索设备名称
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/device/search_devices_by_userid?userId=test123&searchKey=温度" -Method Get
```

### 测试3: 错误情况测试
```powershell
# 测试空用户ID
Invoke-RestMethod -Uri "http://localhost:8080/device/search_devices_by_userid?userId=" -Method Get
```

## 与现有接口的区别

| 接口 | 功能 | 参数 | 搜索范围 |
|------|------|------|----------|
| `/device/all_devices` | 获取用户所有设备 | userId | 特定用户的所有设备 |
| `/device/search_device` | 全局搜索设备 | searchKey | 所有用户的设备 |
| `/device/search_devices_by_userid` | 用户范围内搜索 | userId + searchKey | 特定用户的匹配设备 |

## 数据库查询逻辑

```sql
-- 当只有userId时
SELECT * FROM device WHERE user_id = 'user123';

-- 当有userId和searchKey时
SELECT * FROM device 
WHERE user_id = 'user123' 
AND (device_name LIKE '%关键字%' OR device_mac LIKE '%关键字%');
```

这个接口为前端提供了更精准的设备搜索功能，既保证了数据安全（只能搜索自己的设备），又提供了灵活的搜索能力。 