### 1、使用的AI工具

- 项目分析阶段，主要使用deepseek网页端，规划各个模式对应包的创立以及具体各个包内部的类与接口的设计。
- 在开发阶段，主要使用deepseek、chatgpt、gemini网页端。
- 开发完成后，主要使用IDEA插件CC GUI配合step-3.7-flash模型，完成对代码注释的美化，使得其符合javadoc。

### 2、向 AI 提的主要的 Prompt (提示词)。

对话次数较多，以下按开发阶段和代码模块分类列举部分 Prompt。

#### (1) 项目分析与架构设计阶段

- "给我讲讲工厂模式，最好用 Java 举个简单例子。"
- "策略模式是啥？怎么用才能让数据清洗的逻辑可以随时切换？"
- "命令模式里，Command、Invoker、Receiver 这三个角色各负责什么？"

#### (2) 工厂模式开发

- "抽象类 Rover 和它的三个子类该怎么设计？executeCommand 是声明成抽象方法好，还是给个默认实现？"
- "怎么在加载 CSV 的时候就根据 RoverType 创建对应的 Rover 子类，并注册到 CommandCenter，免得后面危机时拿到默认的 TrackedRover？"
- "TelemetryRecord 的 equals() 和 hashCode() 应该用哪些字段？只用 roverId 还是把 timestamp 也加进去？"

#### (3) 策略模式开发

- "SmartFillStrategy 里三种传感器的默认值（-50.0 / 100.0 / 95.0）和物理范围，怎么组织比较好？是提取成常量还是直接在 switch 里写死？"
- "SmartFillStrategy 除了 null，还需要处理 NaN 和 Infinite 吗？"
- "DropInvalidStrategy 里，'Value 为空'和'Status 为 ERROR'是两个独立条件，应该用 && 合并还是分开判断？"

#### (4) 观察者模式开发

- "DataAnalyzer.scan() 里怎么判断同一 Rover 连续两次 Radiation > 400.0？用什么数据结构记上一次的辐射值？"
- "如果触发了危机，当前这条记录本身还要不要参与下一次判断？要是不更新 Map，逻辑上会不会有问题？"
- "notifyObservers() 里如果某个观察者抛异常了，怎么保证其他观察者还能继续执行？"

#### (5) 命令模式开发

- "CommandCenterObserver 收到告警后要下发两个指令（ShieldCommand 和 MoveCommand），执行顺序有影响吗？"
- "指令日志是记录在 Rover 对象内部好，还是放在 CommandCenter 里？两种方式各有什么优缺点？"
- "菜单选项 4 的日志格式是 [R-02] 接收指令: ShieldCommand... 执行成功: 探测车 R-02 已开启电磁防护罩。，怎么从日志字符串里提取出指令类型名？"
- "每次重新运行选项 3 之前，需不需要清空旧日志？clearLogs() 应该什么时候调用？"

#### (6) 工具类开发

- "用 Jackson 的 ObjectMapper 怎么把 List<TelemetryRecord> 序列化成标准的 JSON 数组文件？"
- "Jackson 的 INDENT_OUTPUT 和 WRITE_DATES_AS_TIMESTAMPS 这两个配置分别管什么的？需不需要额外设置？"
- "CSV 解析时 split(",", -1) 里的 -1 参数是什么意思？为什么不能直接用 split(",")？"
- "CSV 里 Value 字段如果是空字符串，parseDouble() 是返回 null 还是抛异常？策略层应该处理哪种情况？"
- "CsvDataLoader 加载 CSV 的同时还要注册 Rover 到 CommandCenter，这个职责是不是耦合太重了？怎么解耦比较好？"
- "JSON 导出时如果输出目录不存在，要自动创建父目录吗？"

### 3、AI 帮助下解决了哪些具体问题。

#### (1) 工厂模式 (Factory Pattern)

- 工厂类类型映射逻辑：在 RoverFactory.create() 中，需要根据 CSV 的 RoverType 字段精确映射到三个具体子类（TrackedRover/HoverRover/WalkerRover）。AI 帮助设计了使用 Java switch 表达式（JDK 17+ 语法）的映射结构，并加入了 isValidType() 辅助方法用于 CsvDataLoader 在注册探测车前做前置校验，避免非法类型传入工厂导致运行时异常。
- TelemetryRecord 不可变对象设计：AI 帮助设计了 withValueAndStatus() 方法，使 SmartFillStrategy 在修正数据时无需修改原对象，而是返回新的 TelemetryRecord 实例。同时指导了 equals() 和 hashCode() 的字段选择策略（基于 timestamp + roverId + sensorType 三重主键）。

#### (2) 策略模式 (Strategy Pattern)

- SmartFillStrategy 的物理极限判断与默认值映射：需求要求对三种传感器类型（Temperature / Radiation / Atmosphere）分别设定不同的默认值和范围上下界。AI 帮助设计了用 switch 语句统一处理三种传感器类型的结构，并将默认值和边界值提取为 private static final 常量，避免魔法数字散落在代码中。同时指导了 isNaN() 和 isInfinite() 的异常数值检查逻辑。
- DropInvalidStrategy 的校验逻辑分层：AI 帮助明确了"值非空"与"状态非 ERROR"是两个独立的校验条件，应分开判断而非合并为一条条件语句，便于后续维护和扩展。

#### (3) 观察者模式 (Observer Pattern)

- DataAnalyzer.scan() 的核心扫描算法：需求要求"同一台探测车连续 2 次传回 Radiation > 400.0 时触发危机"。AI 帮助设计了用 Map<String, Double> 记录每个 Rover 上一次辐射值的算法结构：遍历有效记录 → 过滤 Radiation 类型 → 用 Map 查上一次值 → 同时满足两次 > 400.0 则触发 → 更新 Map。特别解决了"危机触发后如何防止该条记录被重复计入"的问题（触发后仍更新 Map，仅对下一次记录做判断，保证连续性的语义正确）。
- 观察者通知的异常隔离：在 notifyObservers() 中，AI 帮助设计了 try-catch 包裹每个观察者的调用，确保单个观察者（如 LogFileObserver 文件写入失败）不会影响其他观察者（如 ConsoleAlertObserver 控制台输出）的正常执行。
- LogFileObserver 文件追加写入：AI 帮助确定了使用 PrintWriter 包装 FileWriter(file, true) 实现追加模式，确保多次运行程序不会覆盖历史告警记录，并指导了 flush() 的调用时机以保证数据立即落盘。

#### (4) 命令模式 (Command Pattern)

- CommandCenterObserver 的双指令顺序执行：需求要求危机触发时自动下发 ShieldCommand 和 MoveCommand 两个指令。AI 帮助明确了执行顺序（先 Shield 后 Move）的设计意图，并指导了通过 Rover.executeCommand() 方法将指令执行与日志记录封装在 Rover 内部，使 CommandCenterObserver 只需负责"创建并下发指令"，无需关心日志存储细节。
- CommandCenter 的日志聚合逻辑：菜单选项 4 要求展示格式为 [R-02] 接收指令: ShieldCommand... 执行成功: ... 的日志。AI 帮助设计了按 RoverID 排序输出、通过关键词匹配提取指令类型名称的 extractCommandName() 方法，以及 clearLogs() 在每次分析扫描前清空旧日志的设计，避免多次运行选项 3 导致日志累积。

#### (5) 数据加载与工具类

- CsvDataLoader 的 CSV 解析与异常处理：AI 帮助设计了 split(",", -1) 保留尾部空字段的分割方式，防止 CSV 末尾字段为空时数据错位。同时指导了 parseDouble() 中"空字符串返回 null、解析失败也返回 null"的统一异常值处理策略，使策略层无需关心解析细节，只需处理 null 值即可。
- CsvDataLoader 与 CommandCenter 的解耦：AI 帮助设计了 registerRover() 静态方法，在 CSV 加载阶段就根据 RoverType 创建正确的 Rover 子类并注册到 CommandCenter，使后续危机指令下发时能获取到正确的 Rover 实例（而非默认的 TrackedRover 兜底实例）。
- JsonUtil 的 Jackson 配置：AI 帮助确定了 Jackson ObjectMapper 的配置项（INDENT_OUTPUT 美化输出、WRITE_DATES_AS_TIMESTAMPS 禁用时间戳化），并指导了输出目录不存在的自动创建逻辑。

#### (6) 主程序架构与 CLI 交互

- 状态栏动态更新机制：AI 帮助设计了用 static 字段（cleanedRecords、currentStrategy）在 Main 类中维护系统状态，printHeader() 每次调用时实时读取这些字段来渲染状态栏，无需额外的状态管理类，保持架构简洁。
- 输入防呆与错误处理：AI 帮助设计了 isDataLoaded() 统一校验方法，在选项 3 和 5 执行前检查数据是否已加载，避免 NullPointerException。同时指导了 Scanner 输入时对非数字输入的 NumberFormatException 捕获处理。
- Javadoc 注释规范化：开发完成后，使用 IDEA 插件 CC GUI 配合 step-3.7-flash 模型，对所有类的 Javadoc 注释进行了统一美化。