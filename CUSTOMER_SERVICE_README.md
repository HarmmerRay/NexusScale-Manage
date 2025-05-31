# NexusScale 客服系统使用说明

## 概述
本系统实现了一个基于Ollama本地AI模型的智能客服功能，前端可以通过API接口与部署在本地的deepseek-r1:1.5b模型进行对话。

## 系统架构

```
前端 → Java Spring Boot → Ollama API → deepseek-r1:1.5b模型
                    ↓
                MySQL数据库 (messages表)
```

## API接口

### POST /user/customer_service

**功能**: 处理用户客服请求，调用AI模型生成回复

**请求参数**:
- `userId` (String): 用户ID
- `message` (String): 用户输入的消息

**请求示例**:
```
POST http://localhost:8080/user/customer_service
Content-Type: application/x-www-form-urlencoded

userId=user123&message=你好，我需要帮助
```

**响应格式**:
```json
{
    "state": 200,
    "msg": "成功",
    "data": "您好！我是AI助手，很高兴为您服务。请问有什么可以帮助您的吗？"
}
```

**错误响应**:
```json
{
    "state": 500,
    "msg": "客服服务暂时不可用，请稍后重试"
}
```

## 数据库存储

所有对话消息都会自动保存到`messages`表中：

| 字段 | 类型 | 说明 |
|------|------|------|
| id | int | 消息ID (自增主键) |
| user_id | varchar(255) | 用户ID |
| from_client | tinyint(1) | 是否来自客户端 (1=用户消息, 0=AI回复) |
| message | text | 消息内容 |

## 前端集成示例

### JavaScript/AJAX
```javascript
function sendMessage(userId, message) {
    $.ajax({
        url: '/user/customer_service',
        type: 'POST',
        data: {
            userId: userId,
            message: message
        },
        success: function(response) {
            if (response.state === 200) {
                console.log('AI回复:', response.data);
                // 在聊天界面显示AI回复
                displayMessage(response.data, 'ai');
            } else {
                console.error('错误:', response.msg);
            }
        },
        error: function(xhr, status, error) {
            console.error('请求失败:', error);
        }
    });
}
```

### Vue.js示例
```javascript
methods: {
    async sendMessage() {
        try {
            const response = await this.$http.post('/user/customer_service', {
                userId: this.userId,
                message: this.inputMessage
            });
            
            if (response.data.state === 200) {
                this.messages.push({
                    type: 'user',
                    content: this.inputMessage,
                    timestamp: new Date()
                });
                this.messages.push({
                    type: 'ai',
                    content: response.data.data,
                    timestamp: new Date()
                });
                this.inputMessage = '';
            }
        } catch (error) {
            console.error('发送消息失败:', error);
        }
    }
}
```

## 环境要求

### 1. Ollama环境
- 确保Ollama在localhost:11434端口运行
- 已安装deepseek-r1:1.5b模型

**安装步骤**:
```bash
# 1. 安装Ollama
curl -fsSL https://ollama.ai/install.sh | sh

# 2. 拉取模型
ollama pull deepseek-r1:1.5b

# 3. 启动服务
ollama serve
```

### 2. 数据库配置
- MySQL数据库名称: `nexuscale`
- 用户名: `root`
- 密码: `111111`
- 端口: `3306`

### 3. Java配置
- JDK 17+
- Spring Boot 3.4.4
- Maven 3.6+

## 测试方法

### 1. 启动应用
```bash
mvn spring-boot:run
```

### 2. 测试API
```bash
curl -X POST http://localhost:8080/user/customer_service \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "userId=test123&message=你好"
```

### 3. 查看数据库记录
```sql
SELECT * FROM messages WHERE user_id = 'test123' ORDER BY id DESC;
```

## 注意事项

1. **超时设置**: Ollama调用设置了120秒读取超时，适合处理较长的AI响应
2. **错误处理**: 系统会自动处理网络错误、JSON解析错误等异常情况
3. **日志记录**: 所有请求和响应都会记录在日志中，便于调试
4. **消息存储**: 用户消息和AI回复都会分别存储，便于后续分析和改进
5. **内容过滤**: 系统会自动过滤AI响应中的特殊字符、Unicode转义和思考标记，确保只返回纯净的中文内容
6. **JSON转义**: 使用Jackson ObjectMapper自动处理用户输入中的特殊字符（换行符、引号等），确保JSON格式正确

## 扩展功能

可以基于现有架构扩展以下功能：
- 对话历史查询
- 用户满意度评价
- 多模型切换
- 流式响应支持
- 上下文记忆功能

## 常见问题

**Q: Ollama无法连接怎么办？**
A: 检查Ollama服务是否运行在localhost:11434，使用`ollama serve`启动服务。

**Q: 模型响应很慢？**
A: deepseek-r1:1.5b是较小的模型，如果依然很慢，检查系统资源使用情况。

**Q: 数据库连接失败？**
A: 检查MySQL服务是否启动，数据库连接配置是否正确。

**Q: 输入中文时报JSON错误？**
A: 已修复！系统现在使用Jackson ObjectMapper自动处理特殊字符转义，支持中文、换行符、引号等特殊字符输入。 