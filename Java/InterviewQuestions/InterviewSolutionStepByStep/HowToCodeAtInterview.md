# How To Code at Interview
[Full Example](https://github.com/junit-team/junit4/wiki/getting-started)

## Natively
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
  - Note: The -classpath option expects a semicolon (``;``) when running on Windows and a colon (``:``) otherwise.
  - Don't forget to upadte XX to your version. (Currntly 12)

```java
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class CalculatorTest {
	@Test
	public void evaluatesExpression() {
		Calculator calculator = new Calculator();
		int sum = calculator.evaluate("1+2+3");
		assertEquals(6, sum);
	}
}
```

## IntelliJ (Native)
- Use the same class.
- Press `Ctrl+Shift+T` on the method name and click add test. (TestClass is created next to the other one in the src folder.)
- Use the same test (copy text)
- Now you have to add the dependencies (junit) to your project.
  - 1) Before creating the tests IntelliJ offers you to a) copy the libs to your ./libs folder or include it. I have chosen copying it to my project's folder.
  - 2) Then press `Ctrl+Alt+Shift+S` go to libraries and add `./libs` with the two jars to your project dependencies.
- It should work then.

## Maven
