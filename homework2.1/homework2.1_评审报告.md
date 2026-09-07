# homework2.1 类图评审与优化报告

> 评审对象：`homework2.1.puml`（初始版，未改动）
> 评审标准：`uml-skills.md`（四个维度：需求完整性 / 关系精准度 / SOLID 与扩展性 / 属性与行为封装）
> 优化结果：`homework2.1_reviewed.puml`（新版，排版精美、注释清晰、可直接渲染）

---

## 一、评审意见（按 uml-skills.md 四个维度）

### ✅ 闪光点

- **继承关系准确**：`User` 抽象类 → `Employee` / `ExternalClient`，符合两种用户共享基础信息的需求。
- **生命周期绑定判断精准**：大楼→楼层→会议室用**组合** `*--`（大楼拆除则全部消亡），设备↔会议室用**普通关联**（可动态搬离），这是最容易出错的地方，做得对。
- **策略模式**：计费规则抽象为 `BillingStrategy` 接口 + 两种实现，为易变的计费规则留出了扩展点。
- **周期预订**：`ReservationGroup` 组合管理多个 `Reservation`，且提供了 `cancelSpecificOccurrence`，满足了"整体管理 + 单独取消某一周"的需求。

### 🔴 核心缺陷（致命程度排序）

**1. 需求完整性缺口：预订没有关联"设备"，外部客户计费无法计算**
外部客户费用 =（时租费 + **室内附加设备租赁费**）× 时长 × 1.2，但 `Reservation` 与 `Equipment` 之间没有任何连线。`ExternalBillingStrategy.calculateFee(equipments)` 的参数从哪来？语义上应是"本次预订实际占用的设备快照"（而非会议室当前持有的全部设备），两者是有区别的。

**2. 违约逻辑硬编码在实体里，违反开闭原则**
`Penalty` 实体背上 `calculatePenalty` 方法，把"24 小时免罚窗口 + 30% 费率"两个易变业务规则写死在实体中。明天规则改成"48 小时 / 20%"，就要改实体；而且 `Penalty` 是违约事实记录（应含 `penaltyAmount, reason` 的历史快照），计算规则与事实记录是两个职责，混在一起成了迷你"上帝类"。应把规则抽象为 `PenaltyRule` 策略，`Penalty` 只负责沉淀事实。

**3. 贫血模型 / 值对象缺失**
- `startTime` + `endTime` 是一对必须同时出现的字段，原图中裸散落在 `Reservation` 里，应提炼为 `TimeSlot` 值对象（可自带 `overlapsWith()` 冲突判断）。
- `ReservationGroup.recurrenceRule: String` 用裸字符串描述周期规则（"每周一 9:00-10:00"），无法校验、无法自描述行为，应提炼 `RecurrenceRule` 值对象。
- `Invoice.status: String` 应使用枚举 `InvoiceStatus`，否则"已支付/待支付/已退款"三态在字符串里等于没有约束。

### ⚠️ 次要问题

- `Employee --> Department` 语义上是弱整体-部分（部门解散员工归属待定），建议用聚合 `o--`。
- `Room.isAvailable()` 需要查预订历史才能回答，放进 `Room` 会让会议室实体依赖预订数据；冲突判断放 `ReservationService` 更合理（已在原图中体现）。

---

## 二、优化要点（新文件具体改动）

| 维度 | 改动 |
|---|---|
| 排版 | `!theme plain`、连线加标签（`A "1" *-- "0..*" B : 包含 >`）、note 解释模式与业务约束 |
| 需求完整性 | 新增 `Reservation "1" --> "0..*" Equipment : uses at booking time`；补 `InvoiceStatus` 枚举 |
| 关系精准度 | `Employee o-- Department` 改聚合；`ExternalBillingStrategy.premiumFactor = 1.2` 显式化 |
| SOLID | 违约规则抽象出 `PenaltyRule` 策略 + `StandardPenaltyRule`（`rate=0.30, freeCancelHours=24`）；`Penalty` 还原为事实记录 |
| 封装 | 提炼 `RecurrenceRule` 值对象（替代裸 String，含 `nextOccurrence()` 行为）；标注 `<<Value Object>>` 与注释 |

---

## 三、架构设计总结

1. **领域分层**：实体（User/Department/Building/…）承载业务身份与生命周期；值对象（RecurrenceRule）承载无身份的不可变数据与自描述行为；服务（ReservationService/BillingService）编排跨实体的用例流程。
2. **策略模式×2**：计费（按角色分发 Internal/External）与违约（StandardPenaltyRule）两条易变规则线各自抽象，未来新增 VIP 客户折扣、动态违约金档位只增新类、不改核心——满足开闭原则。
3. **生命周期纪律**：组合只用于"共存亡"（楼层/会议室、预订组/预订），可搬离的设备、可重组的部门关系一律弱绑定，与需求中的"装修搬离""拆除消亡"语义一一对应。
4. **事实快照不可变**：Invoice、Penalty 一经生成即固化当时金额，避免历史账单随日后规则调整而回溯变化。