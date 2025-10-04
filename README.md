# 混沌点插件 (Hundundian)

[![Minecraft](https://img.shields.io/badge/Minecraft-1.21-green.svg)](https://www.minecraft.net/)
[![Spigot](https://img.shields.io/badge/Spigot-1.21-orange.svg)](https://www.spigotmc.org/)
[![Language](https://img.shields.io/badge/Language-Kotlin-purple.svg)](https://kotlinlang.org/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

一个为 Minecraft 服务器设计的混沌点系统插件，提供动态的 PvE 挑战体验。管理员可以设置混沌点，玩家使用羽毛探测器寻找并挑战。

## ✨ 特性

### 🎯 混沌点系统
- 管理员可在任意位置设置混沌点
- 自动生成炙热裂缝提示（3次广播，间隔3秒）
- 混沌点会持续发出信标激活音效（50格内可听到）
- 距离越近音量越大的动态音效系统

### 🪶 羽毛探测器
- 类似末影之眼的探测机制
- 右键使用后飞向混沌点方向
- 火焰粒子轨迹效果
- 使用后消耗物品

### 👹 怪物生成
- 玩家进入15格范围自动刷新僵尸猪灵
- 每15秒刷新一次
- 怪物不掉落任何物品和经验
- 附带火焰粒子生成效果

### 🔊 音效系统
- 信标激活音效（范围50格）
- 基于距离的动态音量调节
- 混沌点关闭时自动停止

## 📋 指令

所有指令需要管理员权限（`hundundian.admin`，默认 OP）

| 指令 | 描述 | 权限 |
|------|------|------|
| `/hundundian` | 显示所有指令帮助 | `hundundian.admin` |
| `/setpoint` | 在当前位置设置混沌点 | `hundundian.admin` |
| `/closepoint` | 关闭当前的混沌点 | `hundundian.admin` |
| `/pointhint` | 发送混沌点坐标提示 | `hundundian.admin` |
| `/broadcast` | 手动广播混沌点开启提示 | `hundundian.admin` |
| `/detector` | 获得羽毛探测器 | `hundundian.admin` |

**注意**：玩家使用羽毛探测器不需要任何权限，只要拥有物品即可使用。

## 🎮 使用方法

### 管理员
1. 使用 `/setpoint` 在当前位置创建混沌点
2. 使用 `/detector` 获取羽毛探测器并分发给玩家
3. 使用 `/pointhint` 向玩家提示混沌点坐标
4. 使用 `/broadcast` 再次提醒玩家混沌点存在
5. 使用 `/closepoint` 关闭混沌点

### 玩家
1. 从管理员处获得羽毛探测器
2. 右键使用探测器，它会飞向混沌点方向
3. 跟随探测器指引寻找混沌点
4. 到达混沌点附近会遭遇僵尸猪灵攻击
5. 在50格内可以听到信标音效，越近越响

## 🔧 安装

### 要求
- Minecraft 1.21 或更高版本
- Spigot/Paper 服务器
- Java 21 或更高版本

### 步骤
1. 下载最新版本的 `hundundian-1.0-SNAPSHOT.jar`
2. 将 jar 文件放入服务器的 `plugins` 文件夹
3. 重启服务器或使用 `/reload` 命令
4. 插件将自动加载并可以使用

## ⚙️ 配置

插件配置参数（在代码中定义）：

```kotlin
MOB_SPAWN_RADIUS = 15.0         // 怪物生成范围（格）
MOB_SPAWN_INTERVAL = 300L       // 怪物刷新间隔（15秒）
SOUND_MAX_DISTANCE = 50.0       // 音效最大距离（格）
SOUND_INTERVAL = 60L            // 音效播放间隔（3秒）
ANNOUNCE_TIMES = 3              // 广播次数
ANNOUNCE_INTERVAL = 60L         // 广播间隔（3秒）
```

## 🎨 游戏机制

### 探测器机制
- 类似末影之眼，使用后消耗一个
- 飞行约3秒后破碎
- 带有火焰粒子轨迹
- 指向混沌点方向

### 怪物刷新
- 范围：混沌点周围15格
- 频率：每15秒
- 类型：僵尸猪灵
- 特性：不掉落战利品

### 音效系统
- 类型：信标激活音效
- 范围：50格
- 频率：每3秒
- 音量：根据距离动态调整

## 🛠️ 构建

### 从源码构建

```bash
# 克隆仓库
git clone https://github.com/ydxc20091/hundundian.git
cd hundundian

# 使用 Gradle 构建
./gradlew shadowJar

# 构建产物位于
# build/libs/hundundian-1.0-SNAPSHOT.jar
```

### 开发环境
- Kotlin 2.2.20
- Gradle 8.x
- Spigot API 1.21.1

## 📝 权限

| 权限节点 | 描述 | 默认 |
|---------|------|------|
| `hundundian.admin` | 使用所有管理命令 | OP |

## 🤝 贡献

欢迎提交 Issue 和 Pull Request！

1. Fork 本仓库
2. 创建你的特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交你的更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启一个 Pull Request

## 📄 开源协议

本项目使用 MIT 协议开源 - 查看 [LICENSE](LICENSE) 文件了解详情


