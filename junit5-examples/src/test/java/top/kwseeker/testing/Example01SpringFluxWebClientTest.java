package top.kwseeker.testing;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import top.kwseeker.testing.pojo.Person;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.stream.Stream;

/**
 * 来源于 Spring WebFlux 中 WebClient 测试
 */
public class Example01SpringFluxWebClientTest {

    /**
     * 这个注解仅仅是为了简化为带参数的测试方法添加注解
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @ParameterizedTest(name = "[{index}] {0}")  //测试报告格式：{index} 测试索引，{0}第一个参数序列化后的值
    @MethodSource("namedArguments")  //测试方法参数数据源
    @interface SourceParameterizedTest {
    }

    static Stream<Named<Person>> namedArguments() {
        return Stream.of(
                Named.named("Arvin", new Person("Arvin", "18")),
                Named.named("Bob", new Person("Bob", "19")),
                Named.named("Candy", new Person("Candy", "20"))
        );
    }

    @DisplayName("参数化测试-单个参数")
    @SourceParameterizedTest
    void testParameterizedMethod(Person person) {
        System.out.println(person);
    }

    static Stream<Person> arguments() {
        return Stream.of(
                new Person("Arvin", "18"),
                new Person("Bob", "19"),
                new Person("Candy", "20")
        );
    }

    @DisplayName("参数化测试-单个参数2")
    @ParameterizedTest(name = "[{index}] {0}")  //测试报告格式：{index} 测试索引，{0}第一个参数序列化后的值
    @MethodSource("arguments")  //测试方法参数数据源
    void testParameterizedMethod2(Person person) {
        System.out.println(person.toString());
    }

    @DisplayName("参数化测试-多个参数")
    //@ParameterizedTest(name = "[{index}] {0}")  //测试报告格式：{index} 测试索引，{0}第一个参数序列化后的值
    @ParameterizedTest(name = "[{index}] {arguments}")
    @CsvSource(useHeadersInDisplayName = true, textBlock = """
    FRUIT,         RANK
    apple,         1
    banana,        2
    'lemon, lime', 0xF1
    strawberry,    700_000
    """)
    void testParameterizedMethod2(String fruit, int rank2) {
        System.out.println(fruit + " -> " + rank2);
    }

    static Stream<Arguments> nameAndPersonProvider() {
        return Stream.of(
                Arguments.of("Arvin", new Person("Arvin", "18")),
                Arguments.of("Bob", new Person("Bob", "19")),
                Arguments.of("Candy", new Person("Candy", "20"))
        );
    }

    @DisplayName("参数化测试-多个参数2")
    @ParameterizedTest(name = "[{index}] {arguments}")
    @MethodSource("nameAndPersonProvider")
    void testParameterizedMethod2(String name, Person person) {
        System.out.println(name + " -> " + person);
    }
}
