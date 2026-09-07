# 全格式媒体播放器集成系统 (All-Format Media Player Integration System)

## 项目简介

本系统实现了**全格式媒体播放器集成系统**，展示了**工厂方法模式 (Factory Method)** 与 **适配器模式 (Adapter)** 在实际项目中的协同应用。

## 背景

某公司现有的媒体播放器仅支持 MP3 格式。为了支持更多音频格式，公司购买了第三方闭源高级音频库（支持 FLAC 和 WAV），但该库的接口与公司现有标准接口不兼容。本项目通过设计模式优雅地解决了接口兼容问题和对象创建问题。

## 设计模式

### 1. 适配器模式 (Adapter Pattern)

- **问题**：第三方库 `AdvancedAudioPlayer` 接口（`playFlac` / `playWav`）与公司标准接口 `MediaPlayer`（`play(audioType, fileName)`）不兼容。
- **解决方案**：`MediaAdapter` 实现 `MediaPlayer` 接口，内部持有 `AdvancedAudioPlayer` 实例，将标准接口的调用适配委托给第三方接口的对应方法。

### 2. 工厂方法模式 (Factory Method Pattern)

- **问题**：客户端直接使用 `new` 创建具体播放器会导致代码紧耦合，难以维护和扩展。
- **解决方案**：`MediaPlayerFactory` 提供静态工厂方法 `getPlayer(String audioType)`，根据音频类型返回对应的播放器实例。客户端只需与工厂交互，无需关心具体类的实例化细节。

## 项目结构

```
src/main/java/org/example/mediaplayer/
├── MediaPlayer.java             # 标准媒体播放器接口
├── Mp3Player.java               # MP3 播放器（标准接口的直接实现）
├── MediaAdapter.java            # 适配器（将 AdvancedAudioPlayer 适配为 MediaPlayer）
├── MediaPlayerFactory.java      # 工厂类（根据音频类型创建播放器实例）
├── Main.java                    # 客户端测试类
└── advanced/
    ├── AdvancedAudioPlayer.java  # 第三方高级音频播放器接口（模拟闭源库）
    └── VlcPlayer.java            # VLC 播放器（第三方库的实现）
```

## 核心类说明

| 类名 | 角色 | 职责 |
|------|------|------|
| `MediaPlayer` | 目标接口 (Target) | 定义统一的 `play(audioType, fileName)` 方法 |
| `Mp3Player` | 具体目标类 | 原生支持 MP3 格式播放 |
| `AdvancedAudioPlayer` | 被适配接口 | 第三方库接口，提供 `playFlac` / `playWav` |
| `VlcPlayer` | 被适配类 | 实现高级音频播放能力 |
| `MediaAdapter` | 适配器 (Adapter) | 实现 `MediaPlayer`，将请求委托给 `AdvancedAudioPlayer` |
| `MediaPlayerFactory` | 工厂 (Factory) | 根据格式返回对应的播放器实例 |
| `Main` | 客户端 | 仅通过工厂获取播放器，调用 `play` 方法 |

## 运行方式

### 使用 Maven

```bash
mvn compile exec:java -Dexec.mainClass=org.example.mediaplayer.Main
```

### 直接编译运行

```bash
javac -d target/classes src/main/java/org/example/mediaplayer/*.java src/main/java/org/example/mediaplayer/advanced/*.java
java -cp target/classes org.example.mediaplayer.Main
```

## 测试结果预期

```
========================================
    全格式媒体播放器集成系统 - 测试
========================================

>>> 测试 1：播放 song.mp3
[Mp3Player] 正在播放 MP3 文件: song.mp3

>>> 测试 2：播放 lossless.flac
[MediaAdapter] → [VlcPlayer] 正在播放 FLAC 文件（原生支持）: lossless.flac

>>> 测试 3：播放 record.wav
[MediaAdapter] → [VlcPlayer] 正在播放 WAV 文件（原生支持）: record.wav

========================================
    所有测试完成！
========================================
```

## 设计原则体现

1. **开闭原则 (OCP)**：新增音频格式时，只需添加新适配器和工厂中的分支（或用策略模式进一步消除分支），无需修改已有代码。
2. **依赖倒置原则 (DIP)**：客户端依赖 `MediaPlayer` 抽象接口，而非具体实现类。
3. **单一职责原则 (SRP)**：每个类只负责一个功能领域（播放、适配、创建）。

## 技术栈

- Java 17
- Maven
