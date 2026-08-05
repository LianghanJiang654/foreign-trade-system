---

## 2026-07-23
- Studied: JPA主键类型映射(INT vs BIGINT对应Integer vs Long)、@Entity注解的作用、
Controller层写法(@RestController/@GetMapping/@PostMapping/@RequestBody)
- Understood: 数据库字段类型和Java类型必须严格对应(INT→Integer, BIGINT→Long)，
Entity和Repository的泛型参数是联动的，改一处必须跟着改另一处；
@Entity和@Table要配套使用，漏了@Entity会导致关联它的其他Entity报错
"which is not an @Entity type"
- Stuck on: 一开始不理解为什么建表用INT但Java默认写Long会报错，
后来才明白Hibernate的ddl-auto=validate会严格核对两边类型完全一致
- Next: 给四个Controller配套加Service层，学习依赖注入(@Autowired)和分层架构

## 2026-07-24
- Studied: Service层重构,@RequestBody注解的作用
- Understood: @RequestBody负责把请求Body的JSON转换成Java对象；
重构代码时手滑漏掉这个注解，参数会变成一堆null，但不会编译报错，
只有实际测试POST请求时才会在运行时暴露(NOT NULL约束报错)
- Stuck on: 一开始以为是Service层逻辑写错了，实际上是Controller参数注解漏了，
排查时学会了对比"重构前能跑、重构后不能跑"这个线索来缩小范围
- Next: 库存校验功能(创建OrderItem时检查库存够不够)

## 2026-07-24 (续)
- Studied: Service层业务逻辑实现(库存校验+扣减)、@ExceptionHandler统一异常处理
- Understood: 关联对象传进来时只有id是真实的，其他字段是"半成品"，
需要用id重新查一次完整数据；throw new RuntimeException()可以主动中断方法执行；
@ExceptionHandler能把丑陋的500堆栈，转换成干净的错误信息返回给前端
- Stuck on: 一开始分不清orderItem.getProduct()和从数据库查出来的product的区别，
后来理解"传进来的是半成品，必须用id重新查一次才是完整的"
- Next: 考虑要不要给Client、Product也加类似的校验(比如sku重复检查)，
或者转向准备软考/面试八股文复习

## [今天日期]
- Studied: 乐观锁(@Version)实现库存并发保护
- Understood: 乐观锁思路是"先都读写,保存时检查version有没有被别人改过"，
  和synchronized(先锁住排队)是完全不同的思路；
  给已有数据的表加version列时，老数据可能是null不是默认值，需要手动UPDATE补上；
  从JSON转换出来的关联对象只有id、其他字段(包括version)是null，
  如果直接拿去save会因为"version是null"报错，必须替换成真正查出来的完整对象
- Stuck on: 一开始没意识到orderItem里的product和查出来的product是两个不同实例，
  只更新了查出来的那个，orderItem里挂的还是半成品，导致最后save时报错
- Next: 可以准备面试时讲"乐观锁vs悲观锁"这个经典对比题，或者继续别的功能

     ## [今天日期]
- Studied: JUnit + Mockito单元测试
- Understood: 单元测试和集成测试(Postman)的区别——单元测试不碰真实数据库，
  用Mock(假对象)代替Repository；@Mock造假对象，@InjectMocks自动把假对象
  注入到被测试的类里；when().thenReturn()预设假对象的行为；
  assertThrows专门用来验证"应该抛出异常"的场景
- Stuck on: 一开始用手动set的方式给Service注入Mock，因为字段是private
  导致setter调不到，后来换成@InjectMocks让Mockito自动处理，更标准
- Next: 可以再补1-2个测试场景(比如库存正好等于购买数量的边界情况)，
  或者转向整理"面试怎么讲乐观锁"这段话术

   - Studied: 复盘之前学过的8个Java/Spring核心概念(HashMap、ArrayList扩容、
  进程线程、堆栈、synchronized、GC、IOC、AOP)
- Understood: 这些概念其实都还记得，只是"感觉忘了"和"真的忘了"不是一回事，
  靠主动回忆(而不是被动重读)能验证真实掌握程度；
  进程/线程和堆/栈这两组类比容易搞混(工厂工人 vs 仓库便利贴)，
  需要专门区分记忆
- Next: 继续巩固@Transactional、单元测试这两块比较新的内容，
  或者开始练习脱稿讲那三段面试话术(乐观锁/Service层/快照价格)

## 7.28
- Studied: 线程池核心思想
- Understood: 线程池通过复用线程避免频繁创建销毁的开销；
核心线程→队列→最大线程数→拒绝，这是任务处理的完整链路；
Spring Boot的Tomcat本身就是用线程池处理请求的，日志里nio-8080-exec-N
就是线程池工作线程的名字
- Stuck on: (自己填)
- Next: MySQL索引原理，或者事务隔离级别

## [7.28]
- Studied: 线程池深度追问(核心/最大线程数设置公式、拒绝策略、Executors的坑)
- Understood: CPU密集型用核心数+1，IO密集型用核心数×2；
四种拒绝策略要分场景选，不是背下来就行；
newFixedThreadPool风险在队列无界(堆积任务撑爆内存)，
newCachedThreadPool风险在线程数无界(线程暴增撑爆资源)，
两者是不同的失控方式，不能混为一谈
- Stuck on: 一开始把两个Executors方法的风险点搞反了，
后来分清楚"线程数有限/无限"和"队列有限/无限"是两个独立维度
- Next: 用同样的深挖方式，回头补索引、HashMap这些之前讲得比较表面的知识点

## [7.28]
- Studied: 事务隔离级别深度(三大并发问题、四种级别、MVCC)
- Understood: 脏读/不可重复读/幻读是并发场景下的三种数据一致性问题；
隔离级别越高越安全但并发性能代价越大；
MySQL默认可重复读，靠MVCC机制在不用完全排队执行的情况下
也能防住大部分幻读，是安全性和性能的平衡点
- Stuck on: 一开始只答出了"Serializable性能差"，没答出MySQL用什么机制
让可重复读能兼顾安全和性能，后来补充了MVCC这个关键点
- Next: 按之前定的顺序，接下来可以按你说的第3步——找一份真实面经，
看看这几个知识点的准备程度够不够真实面试的要求

## [7.28]
- Studied: 联合索引、最左前缀原则、覆盖索引
- Understood: 联合索引是"一个索引"按多列顺序排序，不是多个独立索引；
最左前缀原则——查询必须从索引最左列开始用，跳过就用不上索引排序优势；
覆盖索引——查询字段全部包含在索引里就不用回表，否则必须回表
- Stuck on: 第一次把"最左前缀失效"和"函数运算导致索引失效"这两个不同原因
搞混了，后来分清楚前者是"顺序被打破"，后者是"索引列被计算过导致无法匹配"；
覆盖索引第一次表述成"没有完整数据也能用"，后来纠正成"查询字段都在索引里"
- Next: MySQL这块基础打得差不多了，可以转向复习项目"为什么"话术，
或者按之前排的优先级看看还有没有其他高频知识点要补

## [今天日期]
- Studied: Spring IOC深挖(单例Bean、循环依赖)、AOP深挖(动态代理、CGLIB)
- Understood: Bean默认单例，整个应用共用同一实例；
循环依赖是两个对象互相等待对方先准备好，形成死循环；
@Transactional靠代理对象实现，不是业务代码自己开启/提交事务；
CGLIB生成继承目标类的子类，重写方法，前后插入通用逻辑，
用super调用真正业务代码，所以不需要接口
- Stuck on: 第一次讲循环依赖和AOP完全没听懂，换成"两人传纸条"
和"政务大厅迎宾员"这两个生活化类比后才理解，说明抽象概念需要
先建立直觉再回到代码
- Next: 这几个高频知识点(线程池/索引/事务隔离/IOC-AOP)基本都补深了，
可以转向练习项目"为什么"话术，或者做一次模拟面试串联所有知识点