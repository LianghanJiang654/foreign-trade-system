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