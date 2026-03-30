# Atoms

Atoms 是一个面向移动端的习惯养成产品项目，核心目标是帮助用户建立微习惯、完成专注执行、查看行为进展，并通过反思和成长内容强化长期改变。

当前仓库中的产品判断主要来自 `picture/` 目录中的界面截图，现有文档已经将这些截图整理为一套可执行的产品与技术方案。

## 项目定位

Atoms 的核心产品闭环如下：

1. 创建习惯
2. 查看今天要完成的习惯
3. 执行并打卡
4. 如有需要进入专注计时
5. 查看连续天数、完成率和历史记录
6. 做简短反思并继续下一天

## 当前目录结构

```text
autom/
├─ AGENTS.md
├─ PLAN.md
├─ README.md
├─ SPEC.md
├─ picture/
├─ docs/
│  ├─ architecture.md
│  └─ api.md
└─ autom-front-end/
```

说明：

- `SPEC.md`：产品规格说明
- `AGENTS.md`：协作与执行规则
- `PLAN.md`：项目实施计划
- `docs/architecture.md`：系统架构说明
- `docs/api.md`：后端 API 设计文档
- `picture/`：界面参考截图
- `autom-front-end/`：uniapp 前端工程

## 技术方案

### 前端

- 框架：uniapp
- 语言：TypeScript
- 视图层：Vue 3
- 构建工具：Vite
- 首发平台：微信小程序

### 后端

- 语言：Java
- 框架：Spring Boot
- 架构：单体 REST API
- 数据库：MySQL

## MVP 范围

第一阶段先实现核心链路，不把内容与监督能力做成完整的一期交付。

一期重点包括：

- 微信登录
- 用户基础信息
- 习惯创建、编辑、归档
- 今日习惯展示
- 打卡记录
- 专注计时
- 连续天数与完成率
- 反思记录

二期预留：

- Mindset 内容接口
- 监督伙伴
- Hall of Fame

## 文档索引

- [产品规格](C:\Users\Administrator\Desktop\autom\SPEC.md)
- [协作规则](C:\Users\Administrator\Desktop\autom\AGENTS.md)
- [实施计划](C:\Users\Administrator\Desktop\autom\PLAN.md)
- [系统架构](C:\Users\Administrator\Desktop\autom\docs\architecture.md)
- [接口设计](C:\Users\Administrator\Desktop\autom\docs\api.md)

## 前端工程现状

当前仓库已经包含一个 uniapp 前端骨架工程：

- 路径：`autom-front-end/`
- 当前技术栈：`uniapp + Vue 3 + TypeScript + Vite`
- 当前页面入口：`src/pages/index/index`

## 本地开发

### 前端

进入前端目录后安装依赖：

```bash
cd autom-front-end
npm install
```

启动 H5 调试：

```bash
npm run dev:h5
```

启动微信小程序调试：

```bash
npm run dev:mp-weixin
```

类型检查：

```bash
npm run type-check
```

### 后端

后端工程尚未在仓库中落目录，建议后续新增：

```text
autom-back-end/
```

后端创建后，统一按 Spring Boot 单体项目组织，接口契约以 `docs/api.md` 为准。

## 推荐后续目录规划

```text
autom/
├─ autom-front-end/
├─ autom-back-end/
├─ docs/
├─ picture/
├─ README.md
├─ SPEC.md
├─ AGENTS.md
└─ PLAN.md
```

## 协作约定

- 产品需求以 `SPEC.md` 为准
- 执行优先级以 `PLAN.md` 为准
- 技术边界以 `docs/architecture.md` 为准
- 前后端联调契约以 `docs/api.md` 为准
- 如果截图理解与文档冲突，优先以文档中的明确规则为准

## 当前结论

这个仓库已经具备继续进入开发的基础文档条件。下一步最合理的工作顺序是：

1. 初始化 Spring Boot 后端工程
2. 按 `docs/api.md` 实现登录、习惯、打卡、专注、进度、反思接口
3. 在 `autom-front-end` 中落首页、进度、专注等核心页面
