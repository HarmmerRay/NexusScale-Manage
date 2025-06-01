# 测试设备数据获取接口的PowerShell脚本

Write-Host "===========================================" -ForegroundColor Green
Write-Host "设备数据获取接口测试" -ForegroundColor Green
Write-Host "===========================================" -ForegroundColor Green

# 配置参数
$baseUrl = "http://localhost:8080"
$endpoint = "/device/get_device_data"

# 测试用例
$testCases = @(
    @{
        name = "测试1: 24小时数据，30分钟采样"
        deviceId = 13
        timePeriodHours = 24
        samplingIntervalMinutes = 30
    },
    @{
        name = "测试2: 6小时数据，10分钟采样"
        deviceId = 13
        timePeriodHours = 6
        samplingIntervalMinutes = 10
    },
    @{
        name = "测试3: 48小时数据，60分钟采样"
        deviceId = 13
        timePeriodHours = 48
        samplingIntervalMinutes = 60
    },
    @{
        name = "测试4: 所有数据（不采样）"
        deviceId = 13
        timePeriodHours = 12
        samplingIntervalMinutes = 0
    }
)

foreach ($testCase in $testCases) {
    Write-Host "`n----------------------------------------" -ForegroundColor Yellow
    Write-Host $testCase.name -ForegroundColor Yellow
    Write-Host "----------------------------------------" -ForegroundColor Yellow
    
    # 构造请求URL
    $url = "$baseUrl$endpoint" + 
           "?deviceId=$($testCase.deviceId)" +
           "&timePeriodHours=$($testCase.timePeriodHours)" +
           "&samplingIntervalMinutes=$($testCase.samplingIntervalMinutes)"
    
    Write-Host "请求URL: $url" -ForegroundColor Cyan
    
    try {
        # 发送GET请求
        $response = Invoke-RestMethod -Uri $url -Method Get -ContentType "application/json"
        
        # 检查响应
        if ($response.success -eq $true) {
            Write-Host "✓ 请求成功" -ForegroundColor Green
            
            if ($response.data.times -and $response.data.values) {
                $dataCount = $response.data.times.Count
                Write-Host "✓ 获取到 $dataCount 个数据点" -ForegroundColor Green
                
                # 显示前5个数据点
                $displayCount = [Math]::Min(5, $dataCount)
                Write-Host "前$displayCount个数据点:" -ForegroundColor White
                
                for ($i = 0; $i -lt $displayCount; $i++) {
                    $time = $response.data.times[$i]
                    $value = $response.data.values[$i]
                    Write-Host "  [$($i+1)] 时间: $time, 值: $value" -ForegroundColor White
                }
                
                if ($dataCount -gt 5) {
                    Write-Host "  ... 还有 $($dataCount - 5) 个数据点" -ForegroundColor Gray
                }
                
                # 分析时间格式
                if ($dataCount -gt 0) {
                    $firstTime = $response.data.times[0]
                    if ($firstTime -match "^\d{2}:\d{2}$") {
                        Write-Host "✓ 时间格式: HH:mm (短时间范围)" -ForegroundColor Green
                    } elseif ($firstTime -match "^\d{2}-\d{2} \d{2}:\d{2}$") {
                        Write-Host "✓ 时间格式: MM-dd HH:mm (长时间范围)" -ForegroundColor Green
                    } else {
                        Write-Host "⚠ 时间格式: $firstTime (其他格式)" -ForegroundColor Yellow
                    }
                }
            } else {
                Write-Host "⚠ 响应中没有数据" -ForegroundColor Yellow
            }
        } else {
            Write-Host "✗ 请求失败: $($response.message)" -ForegroundColor Red
        }
        
    } catch {
        Write-Host "✗ 请求异常: $($_.Exception.Message)" -ForegroundColor Red
        
        # 如果是网络错误，提供帮助信息
        if ($_.Exception.Message -like "*拒绝连接*" -or $_.Exception.Message -like "*无法连接*") {
            Write-Host "提示: 请确保Spring Boot应用正在运行在 $baseUrl" -ForegroundColor Yellow
        }
    }
}

Write-Host "`n===========================================" -ForegroundColor Green
Write-Host "测试完成" -ForegroundColor Green
Write-Host "===========================================" -ForegroundColor Green

# 提供额外的测试说明
Write-Host "`n测试说明:" -ForegroundColor Cyan
Write-Host "1. 确保Spring Boot应用运行在 http://localhost:8080" -ForegroundColor White
Write-Host "2. 确保HBase服务正在运行并包含测试数据" -ForegroundColor White
Write-Host "3. 确保iot_sensor_data表中有设备ID为13的数据" -ForegroundColor White
Write-Host "4. 时间格式会根据时间范围自动调整:" -ForegroundColor White
Write-Host "   - ≤12小时: HH:mm 格式" -ForegroundColor Gray
Write-Host "   - >12小时: MM-dd HH:mm 格式" -ForegroundColor Gray

Write-Host "`n如需手动测试，可使用以下curl命令:" -ForegroundColor Cyan
Write-Host "curl `"$baseUrl$endpoint`?deviceId=13&timePeriodHours=24&samplingIntervalMinutes=30`"" -ForegroundColor Gray 