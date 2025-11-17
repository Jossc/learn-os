# Mermaid格式架构图和时序图

本目录包含推荐系统的所有架构图和时序图，提供两种格式：

## 📁 文件清单

### PlantUML格式 (.puml)
1. **architecture.puml** - 整体架构图
2. **class-diagram.puml** - 类图（策略模式）
3. **sequence-auto-selection.puml** - 自动策略选择时序图
4. **sequence-fallback.puml** - 策略降级流程时序图
5. **sequence-hybrid.puml** - 混合推荐策略时序图

### Mermaid格式 (.mmd)
6. **architecture.mmd** - 整体架构图（Mermaid版）
7. **sequence-overview.mmd** - 推荐流程总览

## 🔧 如何使用

### 方式1：在线预览（推荐）

#### PlantUML
1. 访问 [PlantUML Online Editor](http://www.plantuml.com/plantuml/uml/)
2. 复制 .puml 文件内容粘贴到编辑器
3. 自动生成图表

#### Mermaid
1. 访问 [Mermaid Live Editor](https://mermaid.live/)
2. 复制 .mmd 文件内容粘贴到编辑器
3. 自动生成图表

### 方式2：在Markdown中嵌入

#### GitHub/GitLab
```markdown
```mermaid
[粘贴mermaid代码]
\`\`\`
```

#### IDEA/VSCode
安装插件：
- VSCode: `Markdown Preview Mermaid Support`
- IDEA: `Mermaid` 插件

### 方式3：导出图片

#### PlantUML导出
```bash
# 安装PlantUML
brew install plantuml  # macOS
# 或下载jar: http://plantuml.com/download

# 生成PNG
plantuml architecture.puml

# 生成SVG（矢量图）
plantuml -tsvg architecture.puml
```

#### Mermaid导出
使用在线编辑器的导出功能，或安装mermaid-cli：
```bash
npm install -g @mermaid-js/mermaid-cli
mmdc -i architecture.mmd -o architecture.png
```

## 📊 图表说明

### 1. 整体架构图 (architecture.puml/mmd)
- 展示系统分层架构
- 各层职责和交互关系
- 数据流向

### 2. 类图 (class-diagram.puml)
- 策略模式设计
- 类之间的继承和依赖关系
- 核心方法和属性

### 3. 自动策略选择时序图 (sequence-auto-selection.puml)
- 正常推荐流程
- 策略自动选择逻辑
- 数据查询过程

### 4. 策略降级时序图 (sequence-fallback.puml)
- 策略失败处理
- 自动降级机制
- 兜底策略执行

### 5. 混合推荐时序图 (sequence-hybrid.puml)
- 多策略并行执行
- 结果聚合算法
- 综合评分计算

## 🎨 自定义主题

### PlantUML主题
```plantuml
!theme cerulean-outline
' 或其他主题: plain, sketchy, etc.
```

### Mermaid主题
```javascript
%%{init: {'theme':'base', 'themeVariables': { 'primaryColor':'#ff0000'}}}%%
```

## 📚 参考资源

- [PlantUML官方文档](https://plantuml.com/)
- [Mermaid官方文档](https://mermaid-js.github.io/)
- [UML类图教程](https://www.visual-paradigm.com/guide/uml-unified-modeling-language/uml-class-diagram-tutorial/)
- [时序图最佳实践](https://www.ibm.com/docs/en/rational-soft-arch/9.7.0?topic=diagrams-sequence)

## 💡 提示

1. **在线编辑器最方便**：无需安装工具即可查看和修改
2. **导出SVG格式**：矢量图可无损缩放，适合文档
3. **版本控制**：图表源码可以用Git管理，方便协作
4. **自动生成**：可配置CI/CD自动从源码生成图片

---

**维护说明**：
- 图表与代码同步更新
- 重大架构变更需更新相关图表
- 图表文件遵循命名规范

