@echo off
echo ==========================================
echo HBase IoT传感器数据查询测试
echo ==========================================

echo.
echo 正在编译项目...
call mvn compile dependency:copy-dependencies

if %ERRORLEVEL% neq 0 (
    echo 编译失败！
    pause
    exit /b 1
)

echo.
echo 编译成功！正在运行独立测试...
echo.

java -cp "target/classes;target/dependency/*" HBaseSensorDataTestMain

echo.
echo 测试完成！
pause 