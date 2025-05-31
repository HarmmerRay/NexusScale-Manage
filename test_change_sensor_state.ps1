# 修改设备状态接口测试脚本
Write-Host "=== 修改设备状态接口测试 ===" -ForegroundColor Green

$baseUrl = "http://localhost:8080/device"

# 测试参数
$testDeviceId = 1
$testStates = @(0, 1)  # 测试关闭和开启状态

# 首先查询现有设备，获取实际的设备ID
Write-Host "`n获取现有设备列表..." -ForegroundColor Yellow
try {
    $devicesResponse = Invoke-RestMethod -Uri "$baseUrl/all_devices?user_id=1" -Method Get
    if ($devicesResponse.state -eq 200 -and $devicesResponse.data.Count -gt 0) {
        $firstDevice = $devicesResponse.data[0]
        $testDeviceId = $firstDevice.deviceId
        Write-Host "使用设备ID: $testDeviceId (设备名: $($firstDevice.deviceName))" -ForegroundColor Cyan
        Write-Host "当前状态: $($firstDevice.state)" -ForegroundColor Cyan
    } else {
        Write-Host "警告: 没有找到测试设备，使用默认设备ID: $testDeviceId" -ForegroundColor Yellow
    }
} catch {
    Write-Host "警告: 无法获取设备列表，使用默认设备ID: $testDeviceId" -ForegroundColor Yellow
}

# 测试不同的状态值
foreach ($state in $testStates) {
    Write-Host "`n--- 测试设置状态为 $state ---" -ForegroundColor Yellow
    
    # 构建请求参数
    $params = @{
        state = $state
        deviceId = $testDeviceId
    }
    
    try {
        # 发送POST请求
        $body = ($params.GetEnumerator() | ForEach-Object { "$($_.Key)=$($_.Value)" }) -join "&"
        $response = Invoke-RestMethod -Uri "$baseUrl/change_sensor_state" -Method Post -Body $body -ContentType "application/x-www-form-urlencoded"
        
        Write-Host "响应状态: $($response.state)" -ForegroundColor Cyan
        Write-Host "响应消息: $($response.msg)" -ForegroundColor Cyan
        
        if ($response.state -eq 200) {
            Write-Host "✓ 状态修改成功" -ForegroundColor Green
        } else {
            Write-Host "✗ 状态修改失败" -ForegroundColor Red
        }
        
        # 等待一秒后验证状态是否更新
        Start-Sleep -Seconds 1
        
        # 验证状态是否已更新
        Write-Host "验证状态更新..." -ForegroundColor Gray
        $verifyResponse = Invoke-RestMethod -Uri "$baseUrl/all_devices?user_id=1" -Method Get
        if ($verifyResponse.state -eq 200) {
            $updatedDevice = $verifyResponse.data | Where-Object { $_.deviceId -eq $testDeviceId }
            if ($updatedDevice) {
                Write-Host "数据库中的当前状态: $($updatedDevice.state)" -ForegroundColor Gray
                if ($updatedDevice.state -eq $state) {
                    Write-Host "✓ 状态验证成功" -ForegroundColor Green
                } else {
                    Write-Host "✗ 状态验证失败，期望: $state，实际: $($updatedDevice.state)" -ForegroundColor Red
                }
            }
        }
        
    } catch {
        Write-Host "错误: $($_.Exception.Message)" -ForegroundColor Red
        if ($_.Exception.Response) {
            Write-Host "HTTP状态码: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
        }
    }
}

# 测试无效设备ID
Write-Host "`n--- 测试无效设备ID ---" -ForegroundColor Yellow
try {
    $invalidParams = @{
        state = 1
        deviceId = 99999  # 不存在的设备ID
    }
    $body = ($invalidParams.GetEnumerator() | ForEach-Object { "$($_.Key)=$($_.Value)" }) -join "&"
    $response = Invoke-RestMethod -Uri "$baseUrl/change_sensor_state" -Method Post -Body $body -ContentType "application/x-www-form-urlencoded"
    
    Write-Host "响应状态: $($response.state)" -ForegroundColor Cyan
    Write-Host "响应消息: $($response.msg)" -ForegroundColor Cyan
    
    if ($response.state -ne 200) {
        Write-Host "✓ 正确处理了无效设备ID" -ForegroundColor Green
    } else {
        Write-Host "✗ 应该拒绝无效设备ID" -ForegroundColor Red
    }
    
} catch {
    Write-Host "错误: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n=== 测试完成 ===" -ForegroundColor Green 