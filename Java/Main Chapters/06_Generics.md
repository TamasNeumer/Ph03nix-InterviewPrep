# Generics

#### 1. Generics in Java
- Intent: Hinder run-time exceptions thrown while cating objects.
- Before:

  ```java
  List list = new ArrayList();
  list.add("abc");
  list.add(new Integer(5)); //OK

  for(Object obj : list){
	//type casting leading to ClassCastException at runtime
    String str=(String) obj;
  }
  ```

- After:
  ```java
  List<String> list1 = new ArrayList<>();
  list1.add("abc");
  //list1.add(new Integer(5)); //compiler error
  ```

#### 2. Generic classes
- In order to use templatized parameters in a class, you have to append the `<T>` to the name of the class.
- `public class GenericsType<T>{...}`

#### 3. Generic methods
- If you don't want the whole class generalized you can "templatize" methods as well:
- `public static <T> boolean isEqual(GenericsType<T> g1, GenericsType<T> g2)`

#### 4. The ? wildchar
- Use the ? if you don't know the type that is passed:
  - This is useful if you are expecting two types to be passed (Integer / Double) that share a common interface.
  ```java
  public static double sum(List<? extends Number> list){
    double sum = 0;
    for(Number n : list){
      sum += n.doubleValue();
    }
    return sum;
  }
  ```

#### 5. Naming
- E – Element (used extensively by the Java Collections Framework, for example ArrayList, Set etc.)
K – Key (Used in Map)
N – Number
T – Type
V – Value (Used in Map)
S,U,V etc. – 2nd, 3rd, 4th types
