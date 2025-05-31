# 设备模板接口测试脚本
Write-Host "=== 设备模板接口测试 ===" -ForegroundColor Green

$baseUrl = "http://localhost:8080/device"

Write-Host "`n测试获取所有设备模板..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/search_device_templates" -Method Get
    Write-Host "响应状态: $($response.state)" -ForegroundColor Cyan
    Write-Host "响应消息: $($response.msg)" -ForegroundColor Cyan
    Write-Host "模板数量: $($response.data.Count)" -ForegroundColor Cyan
    
    if ($response.data.Count -gt 0) {
        Write-Host "`n设备模板列表:" -ForegroundColor Cyan
        foreach ($template in $response.data) {
            Write-Host "  - ID: $($template.dtId)" -ForegroundColor Gray
            Write-Host "    显示名称: $($template.showName)" -ForegroundColor Gray
            Write-Host "    英文名称: $($template.enName)" -ForegroundColor Gray
            Write-Host "    MAC前缀: $($template.macPre)" -ForegroundColor Gray
            Write-Host "    模板配置: $($template.template)" -ForegroundColor Gray
            Write-Host "    创建时间: $($template.createTime)" -ForegroundColor Gray
            Write-Host "    ---" -ForegroundColor DarkGray
        }
    } else {
        Write-Host "没有找到设备模板数据" -ForegroundColor Yellow
    }
    
} catch {
    Write-Host "错误: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "详细错误信息:" -ForegroundColor Red
    Write-Host $_.Exception -ForegroundColor Red
}

Write-Host "`n=== 测试完成 ===" -ForegroundColor Green 