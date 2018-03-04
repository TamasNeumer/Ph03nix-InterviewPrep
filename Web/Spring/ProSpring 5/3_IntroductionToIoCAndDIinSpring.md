# Introducing IoC and DI in Spring

## Types of IoC
#### Dependency Pull
- In dependency pull, dependencies are pulled from a registry as required.
- Spring also offers dependency pull as a mechanism for retrieving the components that the framework manages. This is done by the `appContext.getBean("beanName", beanClass.class)` function.

#### Contextualized Dependency Lookup
- In CDL lookup is performed against the container that is managing the resource.
- In CDL the components implement an interface, (e.g. with method 'void performLookup(Container container);') So basically the component is aware of the container.
- When the container is ready to "hand over" the dependencies to the components, it calls the `performLookup` on these. In the implementation of the `performLookup` each component asks the container for its dependencies.

#### Constructor Dependency Injection
- Constructor dependency injection occurs when a component’s dependencies are provided to it in its constructor (or constructors).
- The component declares a constructor or a set of constructors, taking as arguments its dependencies, and the IoC container passes the dependencies to the component when instantiation occurs.
- Constructor-injection enforces the order of initialization and prevents circular dependencies. (*Hence favored lately by the Spring community*)
```java
public ConstructorInjection(Dependency dependency) {
  this.dependency = dependency;
}
```

#### Setter Dependency Injection
- A component’s setters expose the dependencies the IoC container can manage. (i.e. for the dependencies you create setter functions, that are called by the container when injecting instances.)
- An obvious consequence of using setter injection is that an object can be created without its dependencies, and they can be provided later by calling the setter.
- As a rule of thumb use *constructor injection for mandatory* dependencies and setter injection for optionals. Also if the *component is happy to provide its own defaults*, setter injection is usually the best way to accomplish this.
  - (Setter injection is often used with "configuration parameters", e.g. primitive types that are injected to the component to work properly. Such are username, password for the database etc.)
  - Finally setter injection also allows you to swap dependencies for a different implementation on the fly without creating a new instance of the parent component.

#### Field injection
- See at `@Autowired`

#### Conclusion
- The dependency pull must actively obtain a reference to the registry and interact with it to obtain the dependencies, and
using CDL requires your classes to implement a specific interface and look up all dependencies manually.
- You write substantially less code when you are using injection, and the code that you do write is simple and can, in general, be automated by a good IDE.
-  The "Injection" style is the most cleared and hance it is used by Spring.

## IoC in Spring
