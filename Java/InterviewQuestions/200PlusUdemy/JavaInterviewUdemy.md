## Java Platform
#### 1. Why is Java so famous?
- Platform independence, Object oriented language

#### 2. What is platform independence?
-  Build once run anywhere
- Java code (.java) is compiled to byte code (.class) by your JDK's compiler
- Then the .class is JIT compiled to machine code.

#### 3. Look at JDK versus JVM versus JRE
- JDK: javac, jar, debugging tools etc.
- JRE: java, javaw, libraries, .jar + JVM: Just in time compiler (JIT)

#### 4. What are the essential contrasts amongst C++ and Java?
- Java is platform independent and JIT compiled
- Java is "more" Object-oriented. In C++ you can write completely functional code.
- No pointers, hence no direct access to memory.
- Memory management -> Garbage collection in Java.
- Multiple inheritance is supported in C++, while not in Java.
  - One reason why the Java programming language does not permit you to extend more than one class is to avoid the issues of multiple inheritance of state, which is the ability to inherit fields from multiple classes. For example, suppose that you are able to define a new class that extends multiple classes. When you create an object by instantiating that class, that object will inherit fields from all of the class's superclasses. What if methods or constructors from different superclasses instantiate the same field? Which method or constructor will take precedence? Because interfaces do not contain fields, you do not have to worry about problems that result from multiple inheritance of state. (Oracle docs.)
  - Tl;DR: You **can IMPLEMENT multiple** interfaces as these don't have field variables, however you **can't EXTEND multiple** classes.

#### 5. What is the part for a classloader in Java?
- Java uses the "ClassLoaders" to load the content of all the needed classes. Classes are searched for in the following order:
  - System Class Loader - loads all classes from CLASSPATH
  - Extension Class Loader - Loads all the classes from extension directory
  - Bootstrap Class Loader - Loads all the Java core files

## Wrapper Classes
#### 6. What are Wrapper classes?
- Wrapping classes around the primitive types, and it gives an object appearance.
- Boolean, Byte, Character, Float, Short, Integer, Long, Double.
- Why we do need them? To use them in collections. To allow "null" value (?!)

#### 7. What are the distinctive methods for making Wrapper class occasions?
- Using wrapper class constructor `new Integer(55)` or `new Integer("55")`
- ValueOf methods `Integer.valueOf("100")`

#### 8. What are differences in the two methods for making Wrapper classes?
- If you use the constructor you **always** create a new object, while using `valueOf()` static method it may return you a cached value with-in a range.
  - The cached values for long are between [-128 to 127] --> Hence using static method is better to save memory.
- BTW: Such wrapped objects are **IMMUTABLE**

#### 9. What is auto boxing?
- Automatic conversion of primitive types and their corresponding object wrapper classes.
- Autoboxing uses the **static valueOf** method

#### 10. What are the benefits of auto boxing?
- Memory saving by reusing already created Wrapper objects.

#### 11. What is casting?
- Converting one type to anohter
  - Implicit casting: Automatically done by the compiler. Automatic **widening** castings.
    - `int value = 100;` and `long number = value` --> implicit casting
  - Explicit casting: `int value = (int) longValue` --> Forcing **narrowing** casting.

## Strings
#### 12. Are all String's immutable?
- Yes they are.
- Thus for example the ``stringVar.concat("Value2")`` returns a new String instance.
  - toLowerCase, toUpperCase etc. **all create a new String**

#### 13. Where are String values stored in memory?
- **String constant pool** in the **heap memory**
- `String str1 = "value"; String str2 = "value"` --> same memory is used!

#### 13. Why would it be a good idea for you to be watchful about String concatenation(+) administrator in circles?
- Each concatenation creates a new object.
- Recommended to use a `StringBuilder` or `StringBuffer`

#### 14. How would you take care of above issue?
- ``StringBuffer`` is thread-safe, ``StringBuilder`` is not!
  - ``StringBuffer s3 = new StringBuffer("Value1"); s3.append("haha");``

#### 15. What are the differences between String and StringBuffer?
- Strings are immutable. StringBuffer is used to represent values taht can be modified.
- Both String and StringBuffer are thread-safe.
- StringBuffer is implemented usint eh `synchroznized` keyword on all methods.

#### 16. What are contrasts amongst StringBuilder and StringBuffer?
- StringBuffer is thread-safe, while StringBuilder is not!

## Object oriented programming nuts and bolts
#### 17. What is a class?
- A class is a template for creating multiple objects. A class defines state and behavior that an object can exhibit.

#### 18. What is an object? What is the state of an object? What is the behavior of an Object?
- An instance of a class.
- State = valued assigned to instance variables of an object.
- Behavior = methods supported by an object.

#### 19. What is the super class of each class in Java?
- Every class is a sub-class of the `Object `

#### 20. Explain the toString() method?
- Used to print the content of an object. (If not overridden the Object class' implementation is used.)
- `public String toString()` is the syntax!

#### 21. Equals method in Java?
- ``Obj1 == Obj2`` won't be true, unless they have the same reference.
- Steps: Override method using the correct Signature, Typecast, Comparison.

  ```java
  @Override
  public boolean equals(Object obj){
    Client other = (Client) obj;
    if(id != other.id)
      return false;
    return true;
  }
  ```

#### 22. What are the basic rules of equals method?
- Reflexive (equal with itself)
- Symmetric (a=b -> b=a)
- Transitive (a=b && b=c --> a = c)
- Consistent (return same result over and over with same conditions)
- For any non-null reference ``x.equals(null)`` should return ``false``.

  ```java
  @Override
  public boolean equals(Object obj){
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if(getClass() != obj.getClass())
      return fasle
    /*... Same logic as before ...*/
  }
  ```

#### 23. What is the hashCode method used for in Java?
- Hash codes are used in hashing to decide which group (or bucket) an object should be placed into.
- The quality/implementation decides the effectiveness.
- hashCode properties:
  - If two objects are equal, their hash has to be equal. However if they are not equal, it doesn't mean that hashCode must be different. Two unequal objects **might** have the same hashCode.
  - Consistency
- For proper implementation see [Effective Java](https://stackoverflow.com/questions/113511/best-implementation-for-hashcode-method)

## Inheritance
#### 24. What is inheritance?
- Reuse of class templates.

#### 25. What is method over-loading?
- Method with a same name taking different number/types of arguments.
- Overloading in **same class**
- Overloading in **sub-class**

#### 25. What is method overriding?
- Creating a Sub-class method with the same signature as of the Super Class', and changing behavior.
- Examples: ``HashMap public int size()`` overrides ``AbstractMap public int size()``

#### 26. Can super class reference variable hold an object of sub class?
- Yes.
  - `Object object = new Hero()` is allowed, however you can only use Object's methods.
- Or classic `Animal animal = new Cat()`

#### 27. Is multiple inheritance allowed in Java?
- Inheritance yes, extension no!
- You can create extension chain.

#### 28. What is an interface?

38 . How would you characterize an interface?

39 . How would you execute an interface?

40 . Can you clarify a couple of dubious things about interfaces?

41 . Can you augment an interface?

42 . Could a class develop different interfaces?

43 . What is a theoretical class?

44 . At the point when do you utilize a theoretical class?

45 . How would you characterize a dynamic technique?

46 . Think about unique class versus interface?

47 . What is a constructor?

48 . What is a default constructor?

49 . Will this code incorporate?

50 . How would you call a super class constructor from a constructor?

51 . Will this code incorporate?

52 . What is the utilization of this()?

53 . Will a constructor be called specifically from a strategy?

54 . Is a super class constructor called notwithstanding when there is no express call from a sub class constructor?

Advanced Object Oriented Ideas:

55 . What is polymorphism?

56 . What is the utilization of instanceof administrator in Java?

57 . What is coupling?

58 . What is attachment?

59 . What is exemplification?

60 . What is an inward class?

61 . What is a static inward class?

62 . Can you make an internal class inside a strategy?

63 . What is a mysterious class?

Modifiers

64 . What is default class modifier?

65 . What is private access modifier?

66 . What is default or bundle access modifier?

67 . What is ensured access modifier?

68 . What is community modifier?

69 . What access sorts of variables can be gotten to from a class in same bundle?

70 . What access sorts of variables can be gotten to from a class in various bundle?

71 . What access sorts of variables can be gotten to from a sub class in same bundle?

72 . What access sorts of variables can be gotten to from a sub class in various bundle?

73 . What is the utilization of a last modifier on a class?

74 . What is the utilization of a last modifier on a strategy?

75 . What is a last variable?

76 . What is a last contention?

77 . What happens when a variable is set apart as unpredictable?

78 . What is a static variable?

Conditions and Loops

79 . Why would it be advisable for you to dependably utilize obstructs around if proclamation?

80 . Surmise the yield

81 . Surmise the yield

82 . Surmise the yield of this switch square .

83 . Surmise the yield of this switch square?

84 . Should default be the last case in a switch proclamation?

85 . Can a switch proclamation be utilized around a String

86 . Surmise the yield of this for circle

87 . What is an upgraded for circle?

88 . What is the yield of the for circle beneath?

89 . What is the yield of the project beneath?

90 . What is the yield of the system beneath?

Exemption taking care of

91 . Why is exemption taking care of critical?

92 . What design example is utilized to execute special case taking care of elements in many dialects?

93 . What is the requirement for at long last piece?

94 . In what situations is code in at last not executed?

95 . Will at last be executed in the system beneath?

96 . Is attempt without a catch is permitted?

97 . Is attempt without catch lastly permitted?

98 . Will you clarify the chain of command of special case taking care of classes?

99 . What is the distinction amongst mistake and special case?

100 . What is the distinction between checked special cases and unchecked exemptions?

101 . How would you toss a special case from a strategy?

102 . What happens when you toss a checked special case from a technique?

103 . What are the alternatives you need to wipe out gathering blunders when taking care of checked special cases?

104 . How would you make a custom special case?

105 . How would you handle various special case sorts with same exemption taking care of square?

106 . Could you clarify about attempt with assets?

107 . How does attempt with assets work?

108 . Could you clarify a couple special case taking care of best practices?

Various topics

109 . What are the default values in an exhibit?

110 . How would you circle around an exhibit utilizing upgraded for circle?

111 . How would you print the substance of an exhibit?

112 . How would you think about two exhibits?

113 . What is an enum?

114 . Can you utilize a switch articulation around an enum?

115 . What are variable contentions or varargs?

116 . What are attests utilized for?

117 . At the point when ought to affirms be utilized?

118 . What is trash accumulation?

119 . Could you clarify junk accumulation with a case?

120 . At the point when is waste accumulation run?

121 . What are best practices on junk gathering?

122 . What are introduction squares?

123 . What is a static initializer?

124 . What is an occasion initializer piece?

125 . What is tokenizing?

126 . Will you give a case of tokenizing?

127 . What is serialization?

128 . How would you serialize an article utilizing serializable interface?

129 . How would you de-serialize in Java?

130 . What do you do if just parts of the item must be serialized?

131 . How would you serialize a chain of importance of articles?

132 . Are the constructors in an item summoned when it is de-serialized?

133 . Are the estimations of static variables put away when an article is serialized?

Collections

134 . Why do we require accumulations in Java?

135 . What are the critical interfaces in the gathering progressive system?

136 . What are the essential techniques that are pronounced in the accumulation interface?

137 . Will you clarify quickly about the List interface?

138 . Clarify about ArrayList with an illustration?

139 . Will an ArrayList have copy components?

140 . How would you repeat around an ArrayList utilizing iterator?

141 . How would you sort an ArrayList?

142 . How would you sort components in an ArrayList utilizing practically identical interface?

143 . How would you sort components in an ArrayList utilizing comparator interface?

144 . What is vector class? How is it unique in relation to an ArrayList?

145 . What is linkedList? What interfaces does it actualize? How is it not the same as an ArrayList?

146 . Will you quickly clarify about the Set interface?

147 . What are the vital interfaces identified with the Set interface?

148 . What is the contrast amongst Set and sortedSet interfaces?

149 . Can you give case of classes that execute the Set interface?

150 . What is a HashSet?

151 . What is a linkedHashSet? How is not quite the same as a HashSet?

152 . What is a TreeSet? How is not the same as a HashSet?

153 . Will you give case of usage of navigableSet?

154 . Clarify quickly about Queue interface?

155 . What are the vital interfaces identified with the Queue interface?

156 . Clarify about the Deque interface?

157 . Clarify the BlockingQueue interface?

158 . What is a priorityQueue?

159 . Could you give illustration executions of the BlockingQueue interface?

160 . Could you quickly clarify about the Map interface?

161 . What is contrast amongst Map and sortedMap?

162 . What is a HashMap?

163 . What are the distinctive techniques in a Hash Map?

164 . What is a TreeMap? How is not the same as a HashMap?

165 . Can you give a case of execution of navigableMap interface?

166 . What are the static techniques present in the accumulations class?

Advanced accumulations

167 . What is the contrast amongst synchronized and simultaneous accumulations in Java?

168 . Clarify about the new simultaneous accumulations in Java?

169 . Clarify about copyonwrite simultaneous accumulations approach?

170 . What is compareandswap approach?

171 . What is a lock? How is it not quite the same as utilizing synchronized methodology?

172 . What is starting limit of a Java gathering?

173 . What is burden component?

174 . At the point when does a Java gathering toss UnsupportedOperationException?

175 . What is distinction between safeguard and come up short quick iterators?

176 . What are nuclear operations in Java?

177 . What is BlockingQueue in Java?

Generics

178 . What are Generics?

179 . Why do we require Generics? Could you give a case of how Generics make a system more adaptable?

180 . How would you pronounce a nonexclusive class?

181 . What are the confinements in utilizing bland sort that is pronounced in a class assertion?

182 . In what capacity would we be able to confine Generics to a subclass of specific cl
