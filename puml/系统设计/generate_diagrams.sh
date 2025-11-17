#!/bin/bash

# PlantUML 生成脚本
# 使用方法: ./generate_diagrams.sh [文件名.puml]

PLANTUML_JAR="./plantuml.jar"

if [ ! -f "$PLANTUML_JAR" ]; then
    echo "正在下载 PlantUML JAR 文件..."
    curl -L -o plantuml.jar https://github.com/plantuml/plantuml/releases/download/v1.2024.6/plantuml-1.2024.6.jar
fi

if [ $# -eq 0 ]; then
    echo "生成当前目录下所有 .puml 文件的图表..."
    java -jar "$PLANTUML_JAR" *.puml
else
    echo "生成指定文件: $1"
    java -jar "$PLANTUML_JAR" "$1"
fi

echo "生成完成！"
echo "生成的图片文件："
ls -la *.png
