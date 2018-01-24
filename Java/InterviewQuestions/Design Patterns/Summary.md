# Summary
Most of the info were taken from [journaldev](https://www.journaldev.com/1827/java-design-patterns-example-tutorial) and [github](https://github.com/iluwatar/java-design-patterns)

#### Structural
- **Adapter**
  - Convert the interface of a class into another interface the clients expect.
- **Composite**
  - Treat a group of objects the same way, as you would treat individual objects.
    - (Folder / File) - both have "open" method (implemented differently). Also one
- **Proxy**
  - Provide a surrogate or placeholder for another object to control access to it.
  - Implement the same interface, but with methods that "filter" the input / restirct the action based on the privileges.
- **Flyweight**
  - Create a factory that stores an object once created. If the client requests an object of a type, that was created before, the factory returns a reference to the already existing object.
  - It is used to minimize memory usage or computational expenses by sharing as much as possible with similar objects.
- **Facade**
  - Provide a simplified interface to a complex subsystem.
- **Bridge**
  - Prefer composition over inheritance, by pushing from a hierarchy to another object with a separate hierarchy.
- **Decorator**
  - Decorator design pattern is used to modify the functionality of an object at runtime.
  - Child class has a reference to superclass. Child class "decorates" a method and calls super's method. (super.getAttackPower() + 10) --> +10 is the decoration.

#### Creational
- **Singleton**
  - You want to have one instance of a class.
  - Example: logger
  - Remember to synchronize if you have multiple threads. (race condition)
- **Factory**
  - You want to instantiate sub-classes based on a value (i.e. switch-case structure) and return different implementations based on this input value. --> Factory Method lets a class defer instantiation to subclasses.
  - Or if you have a constructor with the same arguments, however you want to have different implementations. (radian vs carthesian problem.)
- **Abstract factory**
  - You want to create group related/dependent factories together without specifying their concrete classes. --> You create an interface for the related factories.
    - (NYPizzaFactory, ChichagoFactory, LAFactory -> under an interface)
    - `getPizza(new NYPizzafactory());` calling
  - You want to enable DI on the factory itself as well.
- **Builder Pattern**
  - Avoid constructor pollution (constructor with 4 args + 2 optional --> at least 4 constructor versions)
  - Create objects by "chaining the arguments" to it via setters and finally verify the object with a `build` command.
  - In the end: `Computer comp = new Computer.ComputerBuilder( "500 GB", "2 GB").setBluetoothEnabled(true).setGraphicsCardEnabled(true).build();`
- **Prototype**
  - If object creation is expensive (e.g. due to DB query), copy your existing objects.
  - Implement the `Clonable` interface's `clone()` method. Whether shallow copy or deep copy is your call.
  - Instead of creating objects with new ClassName(dbQuery().....) use the objects `clone()` method.

#### Behavioral
- **Teamplate**
  - Create a procedure consisting of multiple steps (function calls) and let the sub-classes implement the individual calls themselves.
    - Have a House calss and define the building steps (buildWall, buildDoor, buildRoof), however the individual sub-classes will override these methods ("building wooden-wall, building stone-wall")
- **Mediator**
  - Create a "mediator / forwarder" that forwards a given message to a group of people, by calling "receive" on each registered object.
- **Chain of Responsibility**
  - Avoid coupling the sender of a request to its receiver by giving more than one object a chance to handle the request. Chain the receiving objects and pass the request along the chain until an object handles it.
  - Each object has a reference to the next element, and calls the function.
- **Observer**
  - Avoid coupling the sender of a request to its receiver by giving more than one object a chance to handle the request. Chain the receiving objects and pass the request along the chain until an object handles it.
- **Strategy**
  - Have an interface that defines a strategy. The concrete strategies implement this interface. Our class has a reference to this interface, meaning that the concrete strategies can be replaces at run-time.
- **State**
  - Allow an object to alter its behavior when its internal state changes. The object will appear to change its class.
- **Visitor**
  - An object of class (i.e. Book) will have its `calculatePrice` method delegated to another class, i.e. the object passes itself and the other class does the operation implementation.
