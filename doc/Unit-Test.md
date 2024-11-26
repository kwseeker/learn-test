# 单元测试

究竟应该如何优雅地为项目添加单元测试。

可以参考开源项目都是怎么做单元测试的。

主要内容： 

+ 单元测试框架选择
  + Junit-Jupiter
  
+ Mock框架选择
  + Mockito
  
+ 测试用例编写与执行

  + 编写
    
    + 代码同样要整洁、层次清晰
    + 不需要追求很高的覆盖率，但是应该覆盖所有场景
    + 单元测试需要纳入 CI/CD 流程
    + 确保测试用例不依赖外部变化或不确定的因素，这些因素最好使用Mock实现
    + 单元测试必须通过断言判断结果正确性
    + 需要验证边界和异常

  + 执行

    + 开发环境
    + 部署
  
+ 单元测试覆盖率
  + [JaCoCo](https://www.jacoco.org/jacoco/trunk/doc/)
  
    报告解析：[JaCoCo-Report.md](./JaCoCo-Report.md)
  
  + Emma
