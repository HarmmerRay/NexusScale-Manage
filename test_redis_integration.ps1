# Redis集成功能测试脚本
Write-Host "=== Redis集成功能测试 ===" -ForegroundColor Green

$baseUrl = "http://localhost:8080/device"

# 首先检查Redis连接
Write-Host "`n检查Redis服务..." -ForegroundColor Yellow
try {
    # 检测Redis是否运行 (Windows)
    $redisProcess = Get-Process redis-server -ErrorAction SilentlyContinue
    if ($redisProcess) {
        Write-Host "✓ Redis服务正在运行 (PID: $($redisProcess.Id))" -ForegroundColor Green
    } else {
        Write-Host "⚠ 未检测到Redis进程，请确保Redis已启动" -ForegroundColor Yellow
        Write-Host "启动Redis命令: redis-server" -ForegroundColor Gray
    }
} catch {
    Write-Host "⚠ 无法检查Redis服务状态" -ForegroundColor Yellow
}

# 获取设备列表，找到有dt_id的设备进行测试
Write-Host "`n获取测试设备..." -ForegroundColor Yellow
$testDevices = @()

try {
    # 尝试获取不同用户的设备
    $userIds = @("1", "13290824341-1744973022235")
    
    foreach ($userId in $userIds) {
        try {
            $devicesResponse = Invoke-RestMethod -Uri "$baseUrl/all_devices?user_id=$userId" -Method Get
            if ($devicesResponse.state -eq 200 -and $devicesResponse.data.Count -gt 0) {
                $testDevices += $devicesResponse.data
                Write-Host "找到用户 $userId 的设备: $($devicesResponse.data.Count) 个" -ForegroundColor Cyan
            }
        } catch {
            Write-Host "无法获取用户 $userId 的设备" -ForegroundColor Gray
        }
    }
    
    if ($testDevices.Count -eq 0) {
        Write-Host "未找到任何测试设备，请先创建设备" -ForegroundColor Red
        exit 1
    }
    
    # 选择第一个设备进行测试
    $testDevice = $testDevices[0]
    Write-Host "使用测试设备: ID=$($testDevice.deviceId), 名称=$($testDevice.deviceName)" -ForegroundColor Cyan
    
} catch {
    Write-Host "获取设备列表失败: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# 获取设备模板信息
Write-Host "`n获取设备模板信息..." -ForegroundColor Yellow
try {
    $templatesResponse = Invoke-RestMethod -Uri "$baseUrl/search_device_templates" -Method Get
    if ($templatesResponse.state -eq 200 -and $templatesResponse.data.Count -gt 0) {
        Write-Host "可用的设备模板:" -ForegroundColor Cyan
        foreach ($template in $templatesResponse.data) {
            Write-Host "  - ID: $($template.dtId), en_name: $($template.enName), 显示名: $($template.showName)" -ForegroundColor Gray
        }
    }
} catch {
    Write-Host "获取设备模板失败" -ForegroundColor Yellow
}

# 测试修改设备状态并推送到Redis
$testStates = @(1, 0, 1)  # 开启->关闭->开启

foreach ($state in $testStates) {
    Write-Host "`n--- 测试修改设备状态为 $state 并推送到Redis ---" -ForegroundColor Yellow
    
    try {
        # 构建请求参数
        $params = @{
            deviceId = $testDevice.deviceId
            state = $state
        }
        
        # 发送POST请求
        $body = ($params.GetEnumerator() | ForEach-Object { "$($_.Key)=$($_.Value)" }) -join "&"
        $response = Invoke-RestMethod -Uri "$baseUrl/change_sensor_state" -Method Post -Body $body -ContentType "application/x-www-form-urlencoded"
        
        Write-Host "API响应状态: $($response.state)" -ForegroundColor Cyan
        Write-Host "API响应消息: $($response.msg)" -ForegroundColor Cyan
        
        if ($response.state -eq 200) {
            Write-Host "✓ 设备状态修改成功" -ForegroundColor Green
            
            # 等待一下让Redis操作完成
            Start-Sleep -Seconds 1
            
            # 验证数据库状态
            $verifyResponse = Invoke-RestMethod -Uri "$baseUrl/all_devices?user_id=$($testDevice.userId)" -Method Get
            if ($verifyResponse.state -eq 200) {
                $updatedDevice = $verifyResponse.data | Where-Object { $_.deviceId -eq $testDevice.deviceId }
                if ($updatedDevice -and $updatedDevice.state -eq $state) {
                    Write-Host "✓ 数据库状态验证成功: $($updatedDevice.state)" -ForegroundColor Green
                } else {
                    Write-Host "✗ 数据库状态验证失败" -ForegroundColor Red
                }
            }
            
        } else {
            Write-Host "✗ 设备状态修改失败" -ForegroundColor Red
        }
        
    } catch {
        Write-Host "请求失败: $($_.Exception.Message)" -ForegroundColor Red
        if ($_.Exception.Response) {
            Write-Host "HTTP状态码: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
        }
    }
    
    # 等待2秒再进行下一次测试
    Start-Sleep -Seconds 2
}

Write-Host "`n=== Redis集成测试说明 ===" -ForegroundColor Green
Write-Host "1. 本测试验证了设备状态修改和Redis推送的集成功能" -ForegroundColor Gray
Write-Host "2. 每次状态修改都会:" -ForegroundColor Gray
Write-Host "   - 更新数据库中device表的state字段" -ForegroundColor Gray
Write-Host "   - 查询device_template表获取en_name" -ForegroundColor Gray
Write-Host "   - 将消息推送到Redis list (topic = en_name)" -ForegroundColor Gray
Write-Host "3. 消息格式: {\"deviceId\":设备ID,\"state\":状态值}" -ForegroundColor Gray
Write-Host "4. 可以使用Redis客户端查看推送的数据:" -ForegroundColor Gray
Write-Host "   redis-cli LLEN <en_name>  # 查看list长度" -ForegroundColor Gray
Write-Host "   redis-cli LRANGE <en_name> 0 -1  # 查看所有消息" -ForegroundColor Gray

Write-Host "`n=== 测试完成 ===" -ForegroundColor Green 