# [TestNG](https://testng.org/doc/documentation-main.html)

测试案例：Github上找吧，找几个对比下，看看人家怎么用的，直接在人家的代码上实验比自己写高效多了。



## 1 介绍

TestNG 是一个测试框架，可用于从单元测试（单个类）到集成测试（测试多个类、多个包、几个外部框架搭建的系统）广泛的测试需求。

编写测试的步骤：

+ 写业务测试逻辑代码，添加 TestNG annotations
+ 往  testng.xml 或 build.xml 中添加测试信息
+ 执行测试

几个重要概念及关系

```
suite (xml文件, <suite> tag)
└── test (<test> tag)
    └── testng class (<class> tag，这里还有个group的概念)
        └── test method (@Test)
```



## 2 使用

项目中怎么使用？

其实制约开发人员使用单元测试的不是单元测试框架怎么用而是项目代码的可测试性，需要研究下怎么写出可测试的代码？TDD是一种参考规范。

[单元测试，及如何写出可测试的代码](https://www.jianshu.com/p/b2c35b0ffb5e), 翻译自 [Unit Tests, How to Write Testable Code and Why it Matters](https://www.toptal.com/qa/how-to-write-testable-code-and-why-it-matters)，举了一些小例子说明了什么是可测试和不可测试代码。

总结有以下几点：

+ **单一职责**

+ **去除状态（纯函数）**

  或者状态不可变也行。

+ **IoC控制反转**

+ **去耦合，分离关注点**



## 3 文档

 ### 3.1 注解&属性

+ Before & After 注解族

  可继承，同级按继承顺序执行。

+ @DataProvider

  定义数据源方法，为测试方法提供参数，返回 `Object[][]`。

+ @Factory

  标记测试类工厂。

+ @Listeners

  在测试类上定义监听器，做什么的？

+ @Parameters

  为测试方法传递参数。

+ @Test

  标记一个类或方法作为测试的一部分。

  

### 3.2 testng.xml

[DTD](https://testng.org/testng-1.0.dtd)

可以零活配置测试案例。



### 3.3 执行TestNG

TestNG的调用方式：

+ **使用 Ant、Maven执行**

  应该是适用于集成测试。

  Ant已经很少人用了，研究下怎么用Maven执行TestNG测试吧。

  即 `mvn test`执行TestNG测试用例。

  Maven官网有教怎么配置: [Using TestNG](http://maven.apache.org/surefire/maven-surefire-plugin/examples/testng.html)

  １）首先引入[maven-surefire-plugin](http://maven.apache.org/surefire/maven-surefire-plugin/index.html)插件，指定一个Suite XML文件；

  ２）拓展配置：如：传递参数，可以使用@Parameters以String[]的方式传递参数，也可以通过surefire插件配置传递系统属性；......

  ```xml
  
  ```

  

+ **命令行执行**

  应该是适用于集成测试。

  可以`-cp`指定依赖（包括TestNG Jar），将测试代码编译后的class、testng.xml放在一起，然后用java命令运行；

  ```shell
  java org.testng.TestNG testng1.xml [testng2.xml testng3.xml ...]
  # 比如：
  # 会报ClassNotFoundException，因为testng jar包并不是完整的可执行的jar
  java -cp  ~/.m2/repository/org/testng/testng/7.4.0/testng-7.4.0.jar org.testng.TestNG suite.xml
  ```

  另外使用Maven maven-assembly-plugin 把所有测试依赖打包到一起，使用java -jar 执行测试也是可行的。

  ```xml
  <build>
      <plugins>
          <!-- Maven Assembly Plugin -->
          <plugin>
              <groupId>org.apache.maven.plugins</groupId>
              <artifactId>maven-assembly-plugin</artifactId>
              <version>2.4.1</version>
              <configuration>
                  <!-- get all project dependencies -->
                  <descriptorRefs>
                      <descriptorRef>jar-with-dependencies</descriptorRef>
                  </descriptorRefs>
                  <!-- MainClass in mainfest make a executable jar -->
                  <archive>
                      <manifest>
                          <!-- 这里配置为TestNG主类 -->
                          <mainClass>org.testng.TestNG</mainClass>
                      </manifest>
                  </archive>
              </configuration>
              <executions>
                  <execution>
                      <id>make-assembly</id>
                      <phase>package</phase>
                      <goals>
                          <goal>single</goal>
                      </goals>
                  </execution>
              </executions>
          </plugin>
      </plugins>
  </build>
  ```

  执行

  ```shell
  java -jar Chapter5-1.0-SNAPSHOT-jar-with-dependencies.jar suite.xml
  # 执行测试结果
  Before Suite 运行了
  beforeTest
  淘宝登录成功
  afterTest
  beforeTest
  支付宝支付成功
  afterTest
  After Suite 运行了
  
  ===============================================
  test
  Total tests run: 2, Passes: 2, Failures: 0, Skips: 0
  ===============================================
  ```

  

+ **作为IDE插件执行**

  应该是适用于开发时的单元测试和集成测试。

  可以执行单个方法、类或在testng.xml上右键运行。

  

### 3.4 测试方法、类、分组

+ **嵌套分组**

+ **排除分组**

+ **局部分组**

+ **传参**

  + 来源于testng.xml
  + 来源于DataProviders
  + 参数报告

+ **依赖**

  即测试执行顺序。

  + 方法依赖
  + 分组依赖
  + xml中定义依赖

+ **工厂**

  用于动态创建测试，比如多次使用不同的值访问一个web页面。

+ **类级别注解**（注解放到类上）

+ **忽略测试**

+ **并行和超时设置**

+ **重试失败的测试**

+ **执行JUnit测试**

  testng支持JUnit测试。

+ **以编程的方式执行测试**

+ **BeanShell & 高级分组选取**

  可以通过beanshell语法自定义复杂的分组选取逻辑。

+ **注解转换器**

+ **方法拦截器**

+ **TestNG Listeners**

  + SPI加载Listeners

+ **依赖注入**

+ **监听方法调用**

+ **覆写测试方法**

+ **改变suites(或) tests**

  

### 3.5 测试结果处理

+ **断言**
+ **日志记录和结果**
  + 日志监听器
  + 日志上报器
  + JUnitReports
  + Reporter API
  + XML Reports
  + TestNG返回码



### 3.6 YAML

使用YAML替代XML定义suite文件。



### 3.7 测试试运行

查看会执行哪些测试方法，但是并不真正地执行测试, 使用 `-Dtestng.mode.dryrun=true` 这个JVM参数。



### 3.8 TestNG JVM 参数

