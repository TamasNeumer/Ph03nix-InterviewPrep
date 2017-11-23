# How To Code at Interview
[Full Example](https://github.com/junit-team/junit4/wiki/getting-started)

#### Set Up Hello World
- Compile with `javac -cp . filename.java`
- Run with `java -cp . Main`

```java
public class Calculator {
  public static void main(String[] args){
    System.out.println("Hello World!");
  }

  public int evaluate(String expression) {
    int sum = 0;
    for (String summand: expression.split("\\+"))
      sum += Integer.valueOf(summand);
    return sum;
  }
}
```

#### Adding tests
- Get [Current release jar](https://github.com/junit-team/junit4/releases) and [Hamcrest](http://search.maven.org/remotecontent?filepath=org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar) and copy it to your current working folder.
- Compile with `javac -cp .:junit-4.XX.jar:hamcrest-core-1.3.jar CalculatorTest.java`
- Run with `java -cp .:junit-4.XX.jar:hamcrest-core-1.3.jar org.junit.runner.JUnitCore CalculatorTest`
