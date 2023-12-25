# [JMH](https://github.com/openjdk/jmh) 

JMH（Java Microbenchmark Harness, Java微基准测试套件），用于构建、运行和分析用Java和其他JVM语言编写的Nano/Micro/Milli/Macro基准测试。



## 基础知识

+ **基准测试定义**

  基准测试（benchmarking）是一种测量和评估软件性能指标的活动。你可以在某个时候通过基准测试建立一个已知的性能水平（称为基准线），当系统的软硬件环境发生变化之后再进行一次基准测试以确定那些变化对性能的影响。这是基准测试最常见的用途。其他用途包括测定某种负载水平下的性能极限、管理系统或环境的变化、发现可能导致性能问题的条件，等等。

  基准测试的具体做法是：在系统上运行一系列测试程序并把性能计数器的结果保存起来。这些结构称为“性能指标”。性能指标通常都保存或归档，并在系统环境的描述中进行注解。比如说，有经验的数据库专业人员会把基准测试的结果以及当时的系统配置和环境一起存入他们的档案。这可以让他们对系统过去和现在的性能表现进行对照比较，确认系统或环境的所有变化。

  基准测试通常都是些功能测试，即测试系统的某个功能是否达到了预期的要求。有些性能测试工具可以对系统几乎所有的方面（从最常见的操作到最复杂的操作，从小负载到中等负载到大负载）进行测试。

+ **JVM 预热**(Warm-up)

  由于JVM的懒加载及JIT机制等原因，”首次“执行会有额外耗时。

  测试中JVM预热是为了排除上面因素影响，让测试结果更接近被测代码逻辑本身。

  业务中也有用到预热（不单单是JVM预热），多在一些低延迟要求较高的应用中，比如大促等活动。

  > 首次不一定是第一次，还得看JVM对代码具体处理逻辑，比如热点代码需要看计数器阈值。



## 使用方法

 参考：

+ [jmh-samples](https://github.com/openjdk/jmh/tree/master/jmh-samples) 
+ [基准测试神器JMH —— 详解36个官方例子](https://heapdump.cn/article/2985869)
+ [Understanding Java Microbenchmark Harness or JMH Tool](https://medium.com/javarevisited/understanding-java-microbenchmark-harness-or-jmh-tool-5b9b90ccbe8d)

### 引入依赖

```xml
<dependencies>     
    <dependency>
        <groupId>org.openjdk.jmh</groupId>
        <artifactId>jmh-core</artifactId>
        <version>1.35</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.openjdk.jmh</groupId>
        <artifactId>jmh-generator-annprocess</artifactId>
        <version>1.35</version>
    </dependency>
</dependencies>

<build>
    <plugins>
        <plugin>
            <groupId>org.codehaus.mojo</groupId>
            <artifactId>exec-maven-plugin</artifactId>
            <executions>
                <execution>
                    <id>run-benchmarks</id>
                    <phase>integration-test</phase>
                    <goals>
                        <goal>exec</goal>
                    </goals>
                    <configuration>
                        <classpathScope>test</classpathScope>
                        <executable>java</executable>
                        <arguments>
                            <argument>-classpath</argument>
                            <classpath />
                            <argument>org.openjdk.jmh.Main</argument>
                            <argument>.*</argument>
                        </arguments>
                    </configuration>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

### 安装IDE插件

[idea-jmh-plugin](https://github.com/artyushov/idea-jmh-plugin)

直接搜索jmh插件安装即可。

### 创建基准测试类

#### 常用注解

+ **@Benchmark**

  用于标注待分析的方法。

+ **@BenchmarkMode**

  基准测试模式，可以设置多个模式，即每个模式执行一遍测试。

  + Throughput：吞吐量测试，即每秒执行了多少次调用，单位为 `ops/time`

  + AverageTime：操作平均用时测试，单位为 `time/op`

  + SampleTime：随机取样，最后输出取样结果的分布

  + SingleShotTime：只运行一次，往往同时把 Warmup 次数设为 0，用于测试冷启动时的性能

  + All：上面的所有模式都会执行一次

+ **@CompilerControl**

  控制方法的编译行为，可注解于类或者方法。

  有6种模式：

  + BREAK
  + PRINT
  + EXCLUDE：标记的方法将被排除在编译的优化之外（JIT）
  + INLINE：启用对标记的方法的内联优化
  + DONT_INLINE：禁用对标记的方法的内联优化
  + COMPILE_ONLY 

+ **@Fork**

  Fork JVM进程配置。

  配置选项：

  + value：默认值-1（-1即表示未设置，后面同义），经测试**默认会Fork 5个进程**（但是是依次执行的，并不是5个进程同时执行，怪不得有些文档说此参数是配置测试执行次数）

    ```jmh
    # Fork: 1 of 5
    # Fork: 2 of 5
    # Fork: 3 of 5
    # Fork: 4 of 5
    # Fork: 5 of 5
    ```

  + warmups：从日志行为上看和value类似，也会Fork JVM进程并依次执行，默认值-1，经测试**默认不会Fork JVM进程**

    设置@Fork(warmups = 3)的效果：

    ```jmh
    # Warmup Fork: 1 of 3
    # Warmup Fork: 2 of 3
    # Warmup Fork: 3 of 3
    ```

  + jvm

  + jvmArgs

  + jvmArgsPrepend

  + jvmArgsAppend

  > **@Fork 和 @Measurement、@Warmup的关系**：
  >
  > @Fork的value和warmups指示**测试的轮数**，比如设置value=2, warmups=2会测试4轮(前两轮显示`Warmup Fork`，后两轮显示`Fork`)，每轮Fork一个JVM进程，顺序执行；
  >
  > @Measurement的iterations指示每轮测试中测试方法的迭代次数，比如设置5即每轮会迭代执行5次；
  >
  > @Warmup的iterations指示每轮预热中测试方法的迭代次数，比如设置5即每轮会迭代预热5次；
  >
  > 结合上面的配置就是2×(5+5) + 2×(5+5)=40，即最终测试方法会迭代40次，假设每次迭代测试方法执行N次，本次测试测试方法总共执行40×N次。

+ **@Measurement**

  测试配置，可注解于类或者方法。

  选项：

  + iterations: 测试迭代的次数

    注意**测试迭代次数不是测试方法执行次数**。

  + time: 每次迭代的时间

    即每次迭代会不断地执行测试方法，直到超过time设置的时间，才算这次迭代测试结束。

  + timeUnit: 默认秒

  + batchSize: 批处理大小

    关于这个参数，搜了很多资料都没有找到讲的清楚或正确的，为此个人做了个测试，最终估计此参数是**规定执行几次测试方法作为一次操作，更准确描述的是执行几次测试方法统计一次操作时间**，比如batchSize=3就是执行3次测试方法作为一次操作统计一次执行时间；

    最初以为此参数的含义是：

    ”估计每次迭代都是分批执行的，比如假设每次迭代执行1秒，测试方法每次执行耗时0.1s， 

    batchSize = 3 的话，每次迭代会执行4批，每次迭代测试方法一共会执行12次；

    batchSize = 8 的话，每次迭代会执行2批，每次迭代测试方法一共会执行16次；”

    但是经过测试发现，不管设置成3或8, 日志显示方法都是执行10次，说明上面假设错误；

    然后**发现每次迭代报告 ms/op 都不一样，batchSize = 1 时， 100.293ms/op,  batchSize = 3 时， 302.275ms/op, batchSize = 8 时， 803.758ms/op，即每次操作用时和batchSize成正比**；

    比如 batchSize = 8 时，JMH 报告：

    ```jmh
    # Run progress: 0.00% complete, ETA 00:00:01
    # Fork: 1 of 1
    Iteration   1: 1
    2
    3
    4
    5
    6
    7
    8
    9
    10
    803.758 ms/op	#这里与batchSize成正比
    ```

    测试DEMO:

    ```java
    @BenchmarkMode(Mode.AverageTime)
    @Fork(value = 1)   //一共会Fork 1个JVM进程（0 * Warmup Fork + 1 * Fork）
    //@Measurement(iterations = 1, time = 1, batchSize = 1)
    //@Measurement(iterations = 1, time = 1, batchSize = 3)
    @Measurement(iterations = 1, time = 1, batchSize = 8)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)  //以毫秒为单位
    @State(Scope.Thread)            //每个测试线程分配一个实例
    @Threads(1)                     //每个进程只创建一个测试线程
    @Warmup(iterations = 0)         //不预热
    public class JMHUsageTest {
    
        private final AtomicInteger counter = new AtomicInteger(0);
    
        @Benchmark
        public void testMeasurementBatchSize() {
            try {
                Thread.sleep(100);
                System.out.println(counter.incrementAndGet());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
    ```

+ **@OperationsPerInvocation**

  其实是将一次基准测试方法调用视为一批操作（包含多次操作），最终计算耗时是按每个操作平均耗时计算；

  比如设置10，最终结果是基准测试方法耗时除以10；适合处理测试方法中包含循环的情况。

  > 但是基准测试中尽量不要用循环，因为 JIT 对循环也有一系列优化，比如循环展开、循环无关代码外提、循环剥离等等，多次测试每次循环本身耗时可能相差很大；
  >
  > 不过也不是说一定不能用循环，如果循环内部操作耗时远远大于循环本身耗时，循环不管是否优化都对测试结果没什么影响，那就无所谓了。

  官方注释给了个例子，如下一次基准测试方法调用内实际包含了10次循环，想要测试每次循环的耗时：

  ```java
  //For example, a benchmark which uses the internal loop to have multiple operations, may want to 
  // measure the performance of a single operation:
  @Benchmark
  @OperationsPerInvocation(10)
  public void test() {
      for (int i = 0; i < 10; i++) {
          // do something
      }
  }
  ```

  再比如下面例子，测试结果是 80 ms/op 左右（100 × 8 / 10）。

  ```java
  @Benchmark
  @Measurement(iterations = 1, time = 1, batchSize = 8)
  @OperationsPerInvocation(10)
  public void testOperationsPerInvocation() {
      try {
          Thread.sleep(100);
      } catch (InterruptedException e) {
          throw new RuntimeException(e);
      }
  }
  ```

+ **@OutputTimeUnit**

  统计结果的时间单位，可注解于类或者方法。

+ **@Param** 

  设置参数用例，只注解于字段上，需要定义 @State 注解。

  每个参数会执行一次测试。

+ **@Setup @TearDown**

  分别用于基准测试前的初始化动作和基准测试后的动作。

  选项：

  + level

    Trial：默认级别，每个实例会执行一次。 

    Iteration：每次迭代都会运行。 

    Invocation：每次方法调用都会运行，这个是粒度最细的。

+ **@State**

  指定类对象的作用范围，@State 可以被继承。

  + Scope.Benchmark：所有测试线程共享一个实例

  + Scope.Group：在同一个线程组的测试线程共享一个实例

  + Scope.Thread：默认值，每个测试线程分配一个实例

+ **@Threads**

  指定每个进程中的测试线程数量，默认只有一个线程，可注解于类或者方法。

+ **@Timeout**

  测试发现迭代超时并不会中断测试，参考：[JMH Timeout doesn't interrupt](https://stackoverflow.com/questions/50003967/jmh-timeout-doesnt-interrupt)，没看懂具体什么意思，也没搜到哪里有具体讲这个问题，有空了看看源码吧。

  选项：

  + time: 每个迭代的超时时间

  + timeUnit

+ **@Warmup**

  预热配置，可注解于类或者方法。

  选项（同 @Measurement）：

  + iterations: 预热迭代次数，默认5次
  + time: 每次预热的时间
  + timeUnit: 默认秒
  + batchSize: 批处理大小（执行几次测试方法统计一次操作时间）

#### Blackhole

工作模式：

+ COMPILER
+ FULL_DONTINLINE
+ FULL

### JMH使用时的一些测试陷阱

理解这部分需要对JIT中的一些优化策略有一定的理解，参考总结的JVM文档。正是因为这些优化手段可能导致对基准测试产生无法忽略的影响。

参考：

+ 《深入理解Java虚拟机》C11.3 编译优化技术

+ [java-jmh](https://www.cnkirito.moe/java-jmh/)
+ [JMH](https://nanova.me/posts/JMH)

#### 死码消除 (Dead Code Elimination)

死码指被注释的代码、不可达的代码块、可达但不被使用的代码等等；JIT会自动删除死码；

此处陷阱就是指因为测试实现不规范被JIT自动删除。

防止测试代码被当死码消除的两种方法：

+ 将执行结果返回
+ 使用 Blockhole 接口消费执行结果

#### 常量折叠 (Constant Folding) & 常量传播 (Constant Propagation)

**常量折叠**是在编译时期简化常数的过程，可以被常量折叠的代码比如`i = 320 * 200 * 32;`。

防止常量折叠的方法：

+ 将常量通过变量传递

**常数传播**是替代表达式中已知常数的过程，也是在编译时期完成。

比如

```java
int x = 14;
int y = 7 - x / 2;
return y * (28 / x + 2);
```

会被优化为

```java
int x = 14;
int y = 0;
return 0;
```

#### 循环优化

JIT对循环有多种优化，《深入理解Java虚拟机》11.3 列举了一些对循环的优化方法，统称为循环变换（loop transformations）：

+ 循环展开（loop unrolling）
+ 循环剥离（loop peeling）
+ 安全点消除（safepoint elimination）
+ 迭代范围分离（iteration range splitting）
+ 范围检查消除（range check elimination）
+ 循环向量化（loop vectorization）

不过基本找不到什么详细的资料，英文版维基百科还好一些，比如在英文版维基百科搜索 “Loop unrolling”。

参考博客中建议不要在JMH中使用循环，不过个人不太赞同，还是需要看循环优化对关注的代码性能的相对影响，如果循环耗时远低于关注的业务代码耗时，循环优化的影响就可以被忽略。

#### Fork(0)运行多个测试导致互相影响

Fork(0) 即使用同一个JVM跑多个测试，默认情况下每个测试会依次Fork 5个JVM进程跑测试。

在同一个JVM跑多个测试会导致各个测试互相影响，产生异常的测试数据。每个测试都应该运行在单独的JVM进程。

#### 方法内联

方法内联就是把目标方法的代码“复制”到发起调用的方法之中。

Java 中，无法手动编写内联方法，但 JVM 会自动识别热点方法，并对它们使用方法内联优化，一段代码需要执行多少次才会触发 JIT 优化通常这个值由 
`-XX:CompileThreshold` 参数进行设置：

+ 使用 client 编译器时，默认为 1500；
+ 使用 server 编译器时，默认为 10000；

但是一个方法就算被 JVM 标注成为热点方法，JVM 仍然不一定会对它做方法内联优化。其中有个比较常见的原因就是这个方法体太大了，分为两种情况。

+ 如果方法是经常执行的，默认情况下，方法大小小于 325 字节的都会进行内联（可以通过 -XX:MaxFreqInlineSize=N 来设置这个大小）
+ 如果方法不是经常执行的，默认情况下，方法大小小于 35 字节才会进行内联（可以通过 -XX:MaxInlineSize=N 来设置这个大小）

为了排除方法内联带来的影响，应该使用 `@CompilerControl(CompilerControl.Mode.Xxx)`控制总是开启内联或禁用内联。

#### 伪共享与缓存行

关于伪共享参考JVM的文档。

JMH 官方示例: JMHSample_22_FalseSharing。

解决方案：通过填充 padding 数据，实现变量对缓存行的独占，使用 @Contented 注解应该也是可以的。

#### 分支预测

维基百科：[Branch predictor](https://en.wikipedia.org/wiki/Branch_predictor) 介绍了一些分支预测的实现方案。

JMH 官方示例: JMHSample_36_BranchPrediction

但是并没有哪里有给出怎么消除分支预测带来的影响。

### JMH报告可视化

输出报告：

```java
public static void main(String[] args) throws RunnerException {
    Options opt = new OptionsBuilder()
        .resultFormat(ResultFormatType.JSON)
        .build();
    new Runner(opt).run();
}
```

JMH支持以下5种格式的结果：

- TEXT 导出文本文件。
- CSV 导出csv格式文件。
- SCSV 导出scsv等格式的文件。
- JSON 导出成json文件。
- LATEX 导出到latex，一种基于ΤΕΧ的排版系统。

在线工具：

+ [JMH Visualizer](https://jmh.morethan.io/)

+ [jmh-visual-chart](http://deepoove.com/jmh-visual-chart)



## 实现原理



## 参考

+ [jmh-samples](https://github.com/openjdk/jmh/tree/master/jmh-samples)

+ [fastjson2](https://github.com/alibaba/fastjson2)

  阿里fastjson2中有用JMH做基准测试，可以参考下如何合理设置基准测试参数值。

+ [Java Microbenchmark Harness: The Lesser of Two Evils](https://www.youtube.com/watch?v=VaWgOCDBxYw)

  JMH作者的视频讲解。
