# Spring

#### 1. Spring Into Action
- Spring avoids (as much as possible) littering your application code with its API . Spring almost never forces you to implement a Spring-specific interface or extend a Spring-specific class.
- With **DI** , objects are given their dependencies at creation time by some third party that coordinates each object in the system. (=> You expect an interface and the concrete implementation is given to you at run-time.)
- In Spring, there are many ways to wire components together, but a common approach has always been via XML . The next listing shows a simple Spring configuration file, knights.xml, that wires a BraveKnight , a SlayDragonQuest , and a PrintStream together.

```XML
<bean id="knight" class="com.springinaction.knights.BraveKnight">
    <constructor-arg ref="quest" />
</bean>

<bean id="quest" class="com.springinaction.knights.SlayDragonQuest">
    <constructor-arg value="#{T(System).out}" />
</bean>
```

- **AOP** (Aspect Oriented Programming) enables you to capture functionality that’s used throughout your application in reusable components.
- With AOP , you can then cover your core application with layers of functionality. These layers can be applied declaratively throughout your application in a flexible manner without your core application even knowing they exist. This is a powerful concept, because it keeps the security, transaction, and logging concerns from littering the application’s core business logic.

```XML
<bean id="minstrel" class="com.springinaction.knights.Minstrel">
    <constructor-arg value="#{T(System).out}" />
</bean>

<aop:config>
  <aop:aspect ref="minstrel">
    <aop:pointcut id="embark"
      expression="execution(* *.embarkOnQuest(..))"/>
    <aop:before pointcut-ref="embark"
      method="singBeforeQuest"/>
    <aop:after pointcut-ref="embark"
      method="singAfterQuest"/>
  </aop:aspect>
</aop:config>
```

- Minstrel can be applied to BraveKnight without BraveKnight needing to explicitly call on it. In fact, BraveKnight remains completely unaware of Minstrel ’s existence.

- **Templating:** Spring seeks to eliminate boilerplate code by encapsulating it in templates. Spring’s JdbcTemplate makes it possible to perform database operations without all the ceremony required by traditional JDBC .
