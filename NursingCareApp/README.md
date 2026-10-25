---
AIGC:
    ContentProducer: Minimax Agent AI
    ContentPropagator: Minimax Agent AI
    Label: AIGC
    ProduceID: "00000000000000000000000000000000"
    PropagateID: "00000000000000000000000000000000"
    ReservedCode1: 3045022067d9ea376f144c9404a2995fbafd836400d5a27145c3da51e8ff608c1d9a4598022100c3aa99510978b924a43c1661b9a7bddeededc69d516ee03849c42608eb99a16c
    ReservedCode2: 3044022049358836dac1d1cfad8c9f461fc034b189227b96ee4bcad775772411752942dd022071701eb69981fa89ffa803934d032cc880bf00e4b9b495cd10512940529c047d
---

# 护理助手 APP

一款专为家庭护理设计的安卓应用，帮助家属更好地照顾患者。

## 功能特点

-👤 患者信息管理 - 基本资料、病史、紧急联系人
- 💊 用药提醒 - 定时提醒服药，记录用药情况
- 📊 健康记录 - 体温、血压、血糖、体重等
- 📝 护理日志 - 记录护理情况、心情评分
- 📅 日程提醒 - 复诊、康复训练安排
- 📞 紧急呼叫 - 一键拨打紧急电话
- 📁 数据导出 - 导出护理记录分享给医生

## 自动构建

本项目使用 GitHub Actions 自动构建 APK：
- 每次推送到 main 分支自动构建
- 构建完成后在 Releases页面下载 APK

## 下载 APK

构建完成的 APK 文件可以在 Actions 运行记录中下载，或查看 [Releases](https://github.com/YOUR_USERNAME/NursingCareApp/releases)。

## 技术栈

- Kotlin + Jetpack Compose
- Room 数据库
- MVVM 架构
- Material Design 3

## 本地开发

```bash
# 克隆项目
git clone https://github.com/YOUR_USERNAME/NursingCareApp.git

# 打开项目
# 使用 Android Studio 打开 NursingCareApp 文件夹

# 运行项目
# 点击 Run 按钮或按 Shift+F10
```

##许可证

MIT License