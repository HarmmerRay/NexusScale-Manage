# 设备搜索接口测试脚本
Write-Host "=== 设备搜索接口测试 ===" -ForegroundColor Green

$baseUrl = "http://localhost:8080/device"
$testUserId = "13290824341-1744973022235"  # 从数据库中看到的真实用户ID

Write-Host "`n1. 测试获取用户所有设备..." -ForegroundColor Yellow
try {
    $response1 = Invoke-RestMethod -Uri "$baseUrl/search_devices_by_userid?userId=$testUserId" -Method Get
    Write-Host "响应状态: $($response1.state)" -ForegroundColor Cyan
    Write-Host "响应消息: $($response1.msg)" -ForegroundColor Cyan
    Write-Host "设备数量: $($response1.data.Count)" -ForegroundColor Cyan
    if ($response1.data.Count -gt 0) {
        Write-Host "设备列表:" -ForegroundColor Cyan
        foreach ($device in $response1.data) {
            Write-Host "  - ID: $($device.deviceId), 名称: $($device.deviceName), MAC: $($device.deviceMac)" -ForegroundColor Gray
        }
    }
} catch {
    Write-Host "错误: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n2. 测试根据关键字'温度'搜索设备..." -ForegroundColor Yellow
try {
    $response2 = Invoke-RestMethod -Uri "$baseUrl/search_devices_by_userid?userId=$testUserId&searchKey=温度" -Method Get
    Write-Host "响应状态: $($response2.state)" -ForegroundColor Cyan
    Write-Host "响应消息: $($response2.msg)" -ForegroundColor Cyan
    Write-Host "匹配设备数量: $($response2.data.Count)" -ForegroundColor Cyan
    if ($response2.data.Count -gt 0) {
        Write-Host "匹配的设备:" -ForegroundColor Cyan
        foreach ($device in $response2.data) {
            Write-Host "  - ID: $($device.deviceId), 名称: $($device.deviceName), MAC: $($device.deviceMac)" -ForegroundColor Gray
        }
    }
} catch {
    Write-Host "错误: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n3. 测试根据关键字'湿度'搜索设备..." -ForegroundColor Yellow
try {
    $response3 = Invoke-RestMethod -Uri "$baseUrl/search_devices_by_userid?userId=$testUserId&searchKey=湿度" -Method Get
    Write-Host "响应状态: $($response3.state)" -ForegroundColor Cyan
    Write-Host "响应消息: $($response3.msg)" -ForegroundColor Cyan
    Write-Host "匹配设备数量: $($response3.data.Count)" -ForegroundColor Cyan
    if ($response3.data.Count -gt 0) {
        Write-Host "匹配的设备:" -ForegroundColor Cyan
        foreach ($device in $response3.data) {
            Write-Host "  - ID: $($device.deviceId), 名称: $($device.deviceName), MAC: $($device.deviceMac)" -ForegroundColor Gray
        }
    }
} catch {
    Write-Host "错误: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n4. 测试根据MAC地址片段搜索设备..." -ForegroundColor Yellow
try {
    $response4 = Invoke-RestMethod -Uri "$baseUrl/search_devices_by_userid?userId=$testUserId&searchKey=HM" -Method Get
    Write-Host "响应状态: $($response4.state)" -ForegroundColor Cyan
    Write-Host "响应消息: $($response4.msg)" -ForegroundColor Cyan
    Write-Host "匹配设备数量: $($response4.data.Count)" -ForegroundColor Cyan
    if ($response4.data.Count -gt 0) {
        Write-Host "匹配的设备:" -ForegroundColor Cyan
        foreach ($device in $response4.data) {
            Write-Host "  - ID: $($device.deviceId), 名称: $($device.deviceName), MAC: $($device.deviceMac)" -ForegroundColor Gray
        }
    }
} catch {
    Write-Host "错误: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n5. 测试空用户ID错误情况..." -ForegroundColor Yellow
try {
    $response5 = Invoke-RestMethod -Uri "$baseUrl/search_devices_by_userid?userId=" -Method Get
    Write-Host "响应状态: $($response5.state)" -ForegroundColor Cyan
    Write-Host "响应消息: $($response5.msg)" -ForegroundColor Cyan
} catch {
    Write-Host "请求错误 (这是预期的): $($_.Exception.Message)" -ForegroundColor Orange
}

Write-Host "`n6. 测试不存在的用户ID..." -ForegroundColor Yellow
try {
    $response6 = Invoke-RestMethod -Uri "$baseUrl/search_devices_by_userid?userId=不存在的用户" -Method Get
    Write-Host "响应状态: $($response6.state)" -ForegroundColor Cyan
    Write-Host "响应消息: $($response6.msg)" -ForegroundColor Cyan
    Write-Host "设备数量: $($response6.data.Count)" -ForegroundColor Cyan
} catch {
    Write-Host "错误: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n=== 测试完成 ===" -ForegroundColor Green 