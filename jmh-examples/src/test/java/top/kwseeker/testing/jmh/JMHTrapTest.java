package top.kwseeker.testing.jmh;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

/**
 * JMH使用中的一些测试陷阱
 */
@BenchmarkMode(Mode.AverageTime)
@Fork(value = 1)
@Measurement(iterations = 3, time = 1)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
@Warmup(iterations = 3, time = 1)
public class JMHTrapTest {

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public void baseline() {
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public void baselineBh(Blackhole blackhole) {
        blackhole.consume(1);
    }

    // 死码消除 ------------------------------------------------------------------

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public void testDeadCodeElimination1() {
        new Object();
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public void testSolveDeadCodeElimination1(Blackhole blackhole) {
        blackhole.consume(new Object());
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public void testDeadCodeElimination2() {
        //TODO 为何不会被当死码清除？
        new HeavyObject();
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void testDeadCodeElimination3() {
        //TODO 为何不会被当死码清除？
        loop();
    }

    private void loop() {
        int count = 0;
        for (int i = 0; i < 1000; i++) {
            for (int j = 0; j < 1000; j++) {
                for (int k = 0; k < 100; k++) {
                    count ++;
                }
            }
        }
        //return count;
    }

    static class HeavyObject {
        public HeavyObject() {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
