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

+ **使用 Ant**

  应该是适用于集成测试。

  Ant已经很少人用了，研究下怎么用Maven执行TestNG测试吧。

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

  应该是适用于开发时的单元测试。

  

### 3.4 测试方法、类、分组



### 3.5 测试结果处理



### 3.6 YAML



### 3.7 测试试运行



### 3.8 TestNG JVM 参数

