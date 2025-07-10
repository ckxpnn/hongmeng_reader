

---

# HarmonyOS Java 项目

本项目为基于鸿蒙（HarmonyOS）4.x，使用 Java 语言开发的标准 Stage 模型应用结构示例。

## 主要结构

- `entry/src/main/java/com/example/harmonyapp/`：主业务代码（MainAbility、MainAbilitySlice）
- `entry/src/main/resources/`：资源文件与配置（如config.json、string.json）
- `entry/build.gradle`：子模块构建脚本

## 说明

建议使用 DevEco Studio 打开本项目进行开发与调试。

## 书架功能

- 支持本地存储书籍信息（标题、路径、封面）
- 书架页面支持格子和列表两种视图切换
- 展示书籍封面（默认封面：src/main/resources/base/media/ic_default_cover.png）和标题
- 点击书籍可进入阅读页面（需后续实现）