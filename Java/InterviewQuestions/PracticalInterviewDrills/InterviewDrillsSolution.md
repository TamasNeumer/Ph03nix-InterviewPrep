#### 1) Interfaces with default and static methods.
- `Testinterface.AppendZeroToText("a")` --> to use static method. Can NOT be overriden.
- You can use default method as if it was a method of the class.
```java
public interface TestInterface {
    default void PrintText(String text){
        System.out.println(text);
    }

    static String AppendZeroToText(String text){
        text += "Zero";
        return text;
    }
}

public class TestInterfaceImpl implements TestInterface {
    public TestInterfaceImpl() {
        PrintText("Now using default method of interface!");
        PrintText(TestInterface.AppendZeroToText("Text + "));
    }
}

public class Main {
    public static void main(String[] args) {
        TestInterfaceImpl obj =  new TestInterfaceImpl();
    }
}
```
