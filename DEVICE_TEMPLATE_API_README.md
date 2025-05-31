# 设备模板API - search_device_templates

## 接口说明

### GET /device/search_device_templates

**功能**: 获取所有设备模板信息，包括模板配置、显示名称、MAC前缀等信息。

**请求参数**: 无

## 请求示例

```bash
GET http://localhost:8080/device/search_device_templates
```

## 响应格式

### 成功响应
```json
{
    "state": 200,
    "msg": "成功",
    "data": [
        {
            "dtId": 1,
            "showName": "温度传感器",
            "enName": "temperature",
            "template": "{\"temperature\": {\"unit\": \"°C\", \"range\": \"-40~85\", \"accuracy\": \"±0.1\"}}",
            "createTime": "2025-05-31 15:08:16",
            "updateTime": "2025-05-31 15:08:16",
            "macPre": "TP"
        },
        {
            "dtId": 2,
            "showName": "湿度传感器",
            "enName": "humidity",
            "template": "{\"humidity\": {\"unit\": \"%RH\", \"range\": \"0~100\", \"accuracy\": \"±2%\"}}",
            "createTime": "2025-05-31 15:08:16",
            "updateTime": "2025-05-31 15:08:16",
            "macPre": "HM"
        }
    ]
}
```

### 错误响应
```json
{
    "state": 500,
    "msg": "获取设备模板失败"
}
```

## 数据字段说明

| 字段 | 类型 | 说明 |
|------|------|------|
| dtId | Integer | 模板ID (主键) |
| showName | String | 显示名称 (中文名称) |
| enName | String | 英文名称 |
| template | String | 模板配置JSON字符串 |
| createTime | String | 创建时间 |
| updateTime | String | 更新时间 |
| macPre | String | MAC地址前缀 |

## 模板配置JSON格式

模板配置是一个JSON字符串，包含设备的参数配置信息：

### 温度传感器模板
```json
{
    "temperature": {
        "unit": "°C",
        "range": "-40~85",
        "accuracy": "±0.1"
    }
}
```

### 湿度传感器模板
```json
{
    "humidity": {
        "unit": "%RH",
        "range": "0~100",
        "accuracy": "±2%"
    }
}
```

### 空气成分传感器模板
```json
{
    "co2": {"unit": "ppm", "range": "400~5000"},
    "o2": {"unit": "%", "range": "0~25"},
    "pm25": {"unit": "μg/m³", "range": "0~500"}
}
```

### 土壤氮磷钾传感器模板
```json
{
    "nitrogen": {"unit": "mg/kg", "range": "0~1999"},
    "phosphorus": {"unit": "mg/kg", "range": "0~1999"},
    "potassium": {"unit": "mg/kg", "range": "0~1999"}
}
```

## 使用场景

### 1. 设备创建页面
```javascript
// 获取所有设备模板，用于创建设备时选择模板
fetch('/device/search_device_templates')
    .then(response => response.json())
    .then(data => {
        if (data.state === 200) {
            console.log('设备模板列表:', data.data);
            // 渲染模板选择列表
            renderTemplateList(data.data);
        }
    });
```

### 2. 设备类型展示
```javascript
// 在设备管理页面展示可用的设备类型
function renderTemplateList(templates) {
    const templateContainer = document.getElementById('template-list');
    templates.forEach(template => {
        const templateCard = document.createElement('div');
        templateCard.innerHTML = `
            <div class="template-card" data-template-id="${template.dtId}">
                <h3>${template.showName}</h3>
                <p>英文名: ${template.enName}</p>
                <p>MAC前缀: ${template.macPre}</p>
                <p>配置: ${template.template}</p>
            </div>
        `;
        templateContainer.appendChild(templateCard);
    });
}
```

### 3. 根据MAC前缀识别设备类型
```javascript
// 根据设备MAC地址前缀确定设备类型
function getDeviceTypeByMac(macAddress, templates) {
    for (const template of templates) {
        if (macAddress.startsWith(template.macPre)) {
            return template;
        }
    }
    return null;
}
```

## PowerShell测试命令

### 基本测试
```powershell
# 获取所有设备模板
Invoke-RestMethod -Uri "http://localhost:8080/device/search_device_templates" -Method Get

# 格式化输出
$response = Invoke-RestMethod -Uri "http://localhost:8080/device/search_device_templates" -Method Get
$response.data | Format-Table dtId, showName, enName, macPre
```

### 运行测试脚本
```powershell
# 运行测试脚本
.\test_device_templates.ps1
```

## 数据库查询逻辑

```sql
-- 获取所有设备模板
SELECT * FROM device_template;
```

## 与其他接口的关系

| 接口用途 | 相关接口 |
|----------|----------|
| 获取设备模板 | `/device/search_device_templates` |
| 根据模板创建设备 | `/device/create_device` |
| 查询用户设备 | `/device/search_devices_by_userid` |

这个接口为前端提供了完整的设备模板信息，可以用于：
- 设备创建时的模板选择
- 设备类型识别
- 参数配置参考
- MAC地址前缀管理 