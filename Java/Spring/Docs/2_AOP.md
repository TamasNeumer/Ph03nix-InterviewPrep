# AOP

#### Introduction
-  The key unit of modularity in OOP is the class, whereas in AOP the unit of modularity is the *aspect*.
  - Aspects enable the modularization of concerns such as transaction management that cut across multiple types and objects.

#### Concepts
- **Aspect**: a modularization of a concern that cuts across multiple classes.  Transaction management or logging as examples. Aspects are implemented using regular classes (the schema-based approach) or regular classes annotated with the @Aspect annotation (the @AspectJ style).

- **Join point**: a point during the execution of a program. (Method execution, Exceoption handling)

- **Advice**: action taken by an aspect at a particular join point. Different types of advice include "around," "before" and "after" advice.
  - **Before advice**: Advice that executes before a join point, but which does not have the ability to prevent execution flow proceeding to the join point (unless it throws an exception).
  - **After returning advice**: Advice to be executed after a join point completes normally: for example, if a method returns without throwing an exception.
  - **After throwing advice**: Advice to be executed if a method exits by throwing an exception.
  - **After (finally) advice**: Advice to be executed regardless of the means by which a join point exits (normal or exceptional return).
  - **Around advice**: Advice that surrounds a join point such as a method invocation. This is the most powerful kind of advice. Around advice can perform custom behavior before and after the method invocation. It is also responsible for choosing whether to proceed to the join point or to shortcut the advised method execution by returning its own return value or throwing an exception.

- **Pointcut**: a predicate that matches join points. Advice is associated with a pointcut expression and runs at any join point matched by the pointcut (for example, the execution of a method with a certain name).

- **Target object**: object being advised by one or more aspects. Also referred to as the advised object.

- **AOP proxy (object)**: an object created by the AOP framework in order to implement the aspect contracts (advise method executions and so on). In the Spring Framework, an AOP proxy will be a JDK dynamic proxy or a CGLIB proxy.

- **Weaving**: linking aspects with other application types or objects to create an advised object. Spring AOP, like other pure Java AOP frameworks, performs weaving at runtime.

#### @AspectJ support
- To use @AspectJ aspects in a Spring configuration you need to enable Spring support for configuring Spring AOP based on @AspectJ aspects, and autoproxying beans based on whether or not they are advised by those aspects.

  ```java
  @Configuration
  @EnableAspectJAutoProxy
  public class AppConfig {

  }
  ```

**Declaring aspects**  
- To declare an aspect use the `@Aspect` annotation over the class definition.

  ```java
  @Aspect
  public class NotVeryUsefulAspect {

  }
  ```

**Declaring point cuts**  
- Recall that pointcuts determine join points of interest, and thus enable us to control when advice executes.
- Spring AOP only supports method execution join points for Spring beans, so you can think of a pointcut as matching the execution of methods on Spring beans.
- To declare a pointcut annote one of the class methods with `@Pointcut(/*expression*/)`
- The following example defines a pointcut named 'anyOldTransfer' that will match the execution of any method named 'transfer':

  ```java
  @Pointcut("execution(* transfer(..))")// the pointcut expression
  private void anyOldTransfer() {}// the pointcut signature
  ```

- Examples:
  - **execution**
    - `execution(public * *(..))` --> the execution of any public method
    - `execution(* set*(..))` --> the execution of any method with a name beginning with "set"
    - `execution(* com.xyz.service.AccountService.*(..))` --> the execution of any method defined by the AccountService interface:
  - **within**
    - We can use ``within`` in pointcut expression to apply advice to all the methods in the class.
    - `within(com.xyz.service.*)` --> any join point (method execution only in Spring AOP) within the service package.
  - **this**
    - `this(com.xyz.service.AccountService)` --> any join point (method execution only in Spring AOP) where the proxy implements the AccountService interface

**Declaring advice**
- **@Before** --> Before the function call
- **@AfterReturning** --> After returning advice runs when a matched method execution returns normally.
- **@AfterReturning** --> After successful return
- **@AfterThrowing** --> After function has thrown
- **@Around** --> It has the opportunity to do work both before and after the method executes, and to determine when, how, and even if, the method actually gets to execute at all.

**Accessing Joint Point information**
- The function that was marked as advice should have the arguments `(JoinPoint joinPoint)`.
- Then later you can use the jointPoint object to access information on the joint point:

  ```java
  log.info("Signature name : {}", joinPoint.getSignature().getName());
  log.info("Arguments : {}", Arrays.toString(joinPoint.getArgs()));

  /*Signature name : add
  Arguments : [1.0, 2.0]*/
  ```

**Prioritizing aspects**
- Assume that you have multiple `@Before` aspects and you want to set the execution order.
- Use the `@Order(0)` annotation. The smaller the number the higher the priority.

**Sharing pointcuts**
- Assume you want to reuse the already defined pointcuts. In this case create an empty advice as a public function. Also annotate this function with `@Pointcut`. Then Simply refer to the function via the class name. The class is not located in the same package as the aspect, you have to include the package name also. If the method is in the same class you can simply write the method name.

  ```java
  @Aspect
  public class CalculatorPointcuts {
    @Pointcut("execution(* *.*(..))")
    public void loggingOperation() {}
  }

  @Aspect
  public class CalculatorLoggingAspect {
    @Before("CalculatorPointcuts.loggingOperation()")
    public void logBefore(JoinPoint joinPoint) {
      //..
      }
  }
  ```

**Combining pointcut operations**
- You can combine pointcut operations with the arithmetic operatos (||, &&, !)
  - `@Pointcut("arithmeticOperation() || unitOperation()")`

**Around aspect**
- Around aspect to cut the method execution before and after. We can use it to control whether the advised method will execute or not. We can also inspect the returned value and change it. This is the most powerful advice and needs to be applied properly.
- Around advice are always required to have ``ProceedingJoinPoint`` as argument and we should use it’s ``proceed()`` method to invoke the target object advised method.
- If advised method is returning something, it’s advice responsibility to return it to the caller program. For void methods, advice method can return null.

  ```Java
  @Aspect
  public class EmployeeAroundAspect {

  	@Around("execution(* com.journaldev.spring.model.Employee.getName())")
  	public Object employeeAroundAdvice(ProceedingJoinPoint proceedingJoinPoint){
  		System.out.println("Before invoking getName() method");
  		Object value = null;
  		try {
  			value = proceedingJoinPoint.proceed();
  		} catch (Throwable e) {
  			e.printStackTrace();
  		}
  		System.out.println("After invoking getName() method. Return value="+value);
  		return value;
  	}
  }
  ```

**Spring Advice with Custom Annotation Pointcut**
- If you look at all the above advices pointcut expressions, there are chances that they get applied to some other beans where it’s not intended. For example, someone can define a new spring bean with getName() method and the advices will start getting applied to that even though it was not intended.
- An alternative approach is to create a custom annotation and annotate the methods where we want the advice to be applied.

```Java
// Enables to log methods as "loggable"
public @interface Loggable {

}

// Aspect definition
@Aspect
public class EmployeeAnnotationAspect {
	@Before("@annotation(com.journaldev.spring.aspect.Loggable)")
	public void myAdvice(){
		System.out.println("Executing myAdvice!!");
	}
}
```
