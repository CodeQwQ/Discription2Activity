# UCMeta到活动图转换系统

这个项目实现了从UCMeta模型到UML活动图的自动转换，基于论文中定义的转换规则。

## 项目结构

```
src/
├── project/
│   ├── ucmeta/                 # UCMeta数据模型
│   │   ├── UCMetaModel.java    # UCMeta核心数据结构
│   │   └── UCMetaParser.java   # UCMeta解析器和示例数据
│   ├── rules/                  # 转换规则实现
│   │   ├── ActivityDiagramManager.java      # 活动图管理器
│   │   ├── UCMetaToActivityTransformer.java # 转换引擎
│   │   ├── ActivityDiagramGenerator.java    # 原始测试代码
│   │   └── transformByRules.java           # 原始规则实现
│   ├── main/                   # 主程序
│   │   └── UCMetaToActivityMain.java       # 主转换程序
│   └── test/                   # 测试程序
│       └── SimpleTransformTest.java        # 简单测试
└── knoledge/                   # 知识库
    └── UCmeta到活动图规则.txt   # 转换规则文档
```

## 实现的转换规则

### 基础规则
- **规则1c**: 为每个用例生成一个活动图实例
- **规则1.1a**: 简单句 → CallOperationAction
- **规则1.5a**: 前置条件 → Constraint
- **规则1.6a**: 后置条件 → Constraint

### 复合句规则 (规则1.2c)
- **规则1.2.1c**: 条件检查句 → CallOperationAction + DecisionNode
- **规则1.2.2c**: 条件句 → DecisionNode + 分支处理
- **规则1.2.3c**: 并行句 → ForkNode + JoinNode
- **规则1.2.4c**: 迭代句 → DecisionNode + 循环结构

### 特殊句规则 (规则1.3c)
- **规则1.3.1a**: 包含句 → CallBehaviorAction
- **规则1.3.2a**: 扩展句 → CallBehaviorAction
- **规则1.3.3a**: 中止句 → FlowFinalNode
- **规则1.3.4a**: 恢复步骤句 → ControlFlow到指定步骤

### 高级规则
- **规则1.4c**: 全局备选流 → AcceptEventAction + InterruptibleActivityRegion
- **规则2c**: 数据流附加 → InputPin/OutputPin

## 核心特性

### 1. 精确的元素定位
- 使用自定义ID系统进行元素定位
- 支持节点注册表快速查找
- 支持复杂的图形操作（插入、连接、修改）

### 2. 双模式生成
- **详细活动图**: 递归处理所有子句
- **概览活动图**: 使用CallBehaviorAction引用交互

### 3. 完整的UML支持
- 支持所有必要的UML活动图元素
- 支持数据流（InputPin/OutputPin）
- 支持约束（前置/后置条件）

## 使用方法

### 1. 运行主程序
```bash
java -cp "jar/*;src" project.main.UCMetaToActivityMain
```

### 2. 运行简单测试
```bash
java -cp "jar/*;src" project.test.SimpleTransformTest
```

### 3. 编程方式使用

```java
// 创建UCMeta用例
UseCase useCase = UCMetaParser.createLoginUseCase();

// 创建转换器
UCMetaToActivityTransformer transformer = new UCMetaToActivityTransformer(true);

// 执行转换
ActivityDiagramManager manager = transformer.transformUseCase(useCase);

// 保存活动图
manager.saveToFile("output.uml");
```

## 示例用例

### 1. 用户登录用例
- 包含简单句、条件检查句
- 演示数据流处理
- 包含全局备选流

### 2. 在线购物用例
- 包含条件句、并行句、迭代句
- 演示复杂控制结构
- 包含用例包含关系

### 3. 文件上传用例
- 演示恢复步骤功能
- 包含错误处理流程

## 生成的文件

运行程序后会生成以下UML文件：
- `login_activity_detailed.uml` - 详细登录活动图
- `login_activity_overview.uml` - 概览登录活动图
- `shopping_activity_detailed.uml` - 详细购物活动图
- `shopping_activity_overview.uml` - 概览购物活动图
- `upload_activity_detailed.uml` - 详细上传活动图

## 扩展指南

### 添加新的句子类型
1. 在`UCMetaModel.java`中定义新的句子类
2. 在`SentenceType`枚举中添加新类型
3. 在`UCMetaToActivityTransformer.java`中实现处理方法

### 添加新的UML元素
1. 在`ActivityDiagramManager.java`中添加创建方法
2. 更新转换器以使用新元素

### 自定义转换规则
1. 继承`UCMetaToActivityTransformer`类
2. 重写相应的处理方法
3. 实现自定义逻辑

## 技术细节

### 依赖项
- Eclipse UML2 5.5.0
- Eclipse EMF Core
- Java 8+

### 关键设计模式
- **策略模式**: 不同句子类型的处理策略
- **建造者模式**: 活动图的构建过程
- **注册表模式**: 元素ID到对象的映射

### 性能考虑
- 使用HashMap进行快速元素查找
- 延迟创建UML对象
- 批量处理控制流创建

## 故障排除

### 常见问题
1. **编译错误**: 确保jar文件路径正确
2. **运行时错误**: 检查Java版本兼容性
3. **文件保存失败**: 确保有写入权限

### 调试技巧
- 使用`printNodeInfo()`查看生成的节点
- 检查控制台输出的详细信息
- 验证生成的UML文件结构

## 贡献指南

1. Fork项目
2. 创建特性分支
3. 提交更改
4. 创建Pull Request

## 许可证

本项目采用MIT许可证。
