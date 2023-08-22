# AssertJ

源码：https://github.com/assertj/assertj

文档：https://assertj.github.io/doc/#assertj-core

这个测试库是在看ProjectReactor源码看到的。

AssertJ提供了一个**丰富而直观的强类型断言集**，用于单元测试，可以与JUnit、TestNG或任何其他测试框架一起使用。

它对标的是各个测试框架里面的断言。



## AssertJ的特点

+ 支持的断言类型很丰富且直观（主要）

  比如还支持Java集合类、异常、Class类型等的断言。

+ 流式接口方便易用且易读

  流式接口（fluent）支持一条断言语句设置多个断言校验点。代码也会更简洁。

+ 易拓展，可以方便地实现自己的类的断言

+ 还提供了对一些常用编程模块的断言支持（Exception、Iterable、JodaTime、Guava、[Neo4j](https://neo4j.com/who-uses-neo4j/)、甚至是对数据库数据的断言）

  > Neo4j 是图数据库，用于存储知识图谱，基于图的数据结构，知识图谱简单理解就是研究实体间的关联关系。属于大数据数据挖掘领域。
  >
  > Neo4j推荐的一些书籍：https://neo4j.com/books/。



## 搭配Junit TestNG等测试框架使用

引入AssertJ断言基本没有额外成本。

关于使用细节，直接看官方文档即可。



## AssertJ的“性能”问题

在Reactor测试中运行某单元测试发现首次执行断言竟然耗时50-100ms。不过估计AssertJ首次执行断言时才加载自己的组件，后续的断言执行效率都很高。执行单个单元测试时，需要注意这个问题，可能会影响性能测试结果。可以测试开始时先执行一次断言”预热下“。

```java
@Test
public void testAssertJ() {
    AtomicLong counter = new AtomicLong();
    System.out.println("assert: " + System.currentTimeMillis());
    assertThat(counter.intValue()).isEqualTo(0);      //Assert断言首次执行比较耗时
    System.out.println("assert: " + System.currentTimeMillis());
    assertEquals(0, counter.intValue());
    System.out.println("assert: " + System.currentTimeMillis());
    assertThat(counter.intValue()).isEqualTo(0);      //再次执行很快
    System.out.println("assert: " + System.currentTimeMillis());
}
//assert: 1692694054325
//assert: 1692694054446
//assert: 1692694054446
//assert: 1692694054446
```

