#!/bin/bash

# 业务指标监控演示启动脚本
# 使用方法：./run-demo.sh [模式]
# 模式:
#   standalone - 独立运行业务指标监控
#   with-skywalking - 与SkyWalking集成运行

echo "🚀 业务指标监控演示启动脚本"
echo "=================================================="

# 设置变量
AGENT_JAR="target/business-metrics-agent.jar"
DEMO_CLASS="com.cn.agent.demo.BusinessMetricsDemo"
MODE=${1:-standalone}

# 检查Agent JAR是否存在
if [ ! -f "$AGENT_JAR" ]; then
    echo "❌ Agent JAR不存在，正在构建..."
    mvn clean package -DskipTests
    if [ $? -ne 0 ]; then
        echo "❌ Maven构建失败"
        exit 1
    fi
fi

echo "✅ Agent JAR已准备就绪: $AGENT_JAR"

# 设置JVM参数
JVM_OPTS="-Xms512m -Xmx1024m"
AGENT_OPTS="-javaagent:$AGENT_JAR=business-metrics-demo"

# 根据模式设置不同的启动参数
case $MODE in
    "standalone")
        echo "🔧 启动模式: 独立运行"
        echo "监控功能: 业务指标收集和本地输出"
        ;;
    "with-skywalking")
        echo "🔧 启动模式: SkyWalking集成"
        echo "监控功能: 业务指标收集 + SkyWalking上报"

        # SkyWalking配置
        SKYWALKING_AGENT="/path/to/skywalking/agent/skywalking-agent.jar"
        SERVICE_NAME="business-metrics-demo"
        OAP_SERVER="127.0.0.1:11800"

        if [ -f "$SKYWALKING_AGENT" ]; then
            AGENT_OPTS="$AGENT_OPTS -javaagent:$SKYWALKING_AGENT"
            AGENT_OPTS="$AGENT_OPTS -Dskywalking.agent.service_name=$SERVICE_NAME"
            AGENT_OPTS="$AGENT_OPTS -Dskywalking.collector.backend_service=$OAP_SERVER"
            echo "✅ SkyWalking Agent已配置"
        else
            echo "⚠️  SkyWalking Agent未找到，请设置正确的路径"
            echo "提示: 请修改脚本中的 SKYWALKING_AGENT 变量"
        fi
        ;;
    *)
        echo "❌ 未知模式: $MODE"
        echo "支持的模式: standalone, with-skywalking"
        exit 1
        ;;
esac

echo "=================================================="
echo "🎯 启动参数:"
echo "   JVM选项: $JVM_OPTS"
echo "   Agent选项: $AGENT_OPTS"
echo "   主类: $DEMO_CLASS"
echo "=================================================="

# 启动应用
echo "🚀 正在启动业务指标监控演示..."
java $JVM_OPTS $AGENT_OPTS -cp target/classes:target/dependency/* $DEMO_CLASS

echo "👋 演示程序已退出"
