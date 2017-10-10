# Interfaces and Abstractions in Java

#### Intro
- Having switch statements is messy. --> They might change because you add a new product, thus you have to introduce a new case. Or if any of the calculation methods used inside a switch body, you have to change it again etc. Doesn't scale well...

**Abstract class**
A class that has unimplemented (abstract) methods, and hence cannot be instantiated.

**Interface**
A named set of methods. A class implements an interface, meaning that it has those methods.

Abstract class | Interface
--|--
Methods without keywords (might) have bodies  |  Methods without keywords don't have bodies
*abstract* keyword lets you to remove body  | *default* keyowrd lets you add a body  
Methods can be public, private, protected, or package-private (by default)  | All methods are public  


```java
// Abstract class and usage as extension
abstract Class RevenueCalculator{}
class Hourly extends RevenueCalculator{}

interface RevenueCalculator{}
class Hourly implements RevenueCalculator{}
```

**Abstractions**
