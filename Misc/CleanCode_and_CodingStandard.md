# Clean code

## Meaningful Names
- If a name requires a comment, then the name does not reveal its intent. We
should choose a name that specifies what is being measured and the unit of that measurement.
```cpp
int d; // elapsed time in days
int elapsedTimeInDays;

/* Or another example */
public List<int[]> getThem() {
  List<int[]> list1 = new ArrayList<int[]>();
  for (int[] x : theList)
    if (x[0] == 4)
      list1.add(x);
  return list1;
}

public List<Cell> getFlaggedCells() {
  List<Cell> flaggedCells = new ArrayList<Cell>();
  for (Cell cell : gameBoard)
    if (cell.isFlagged())
      flaggedCells.add(cell);
  return flaggedCells;
}
```

- Avoid Disinformation
  - Do not refer to a grouping of accounts as an accountList unless it’s actually a List.
  - Avoid similar names (XYZControllerForEfficientHandlingOfStrings vs XYZControllerForEfficientStorageOfStrings?)
- User pronounceable names (so that it is easier to talk about it)
- Use searchable names
  - Finding and replacing the character `i` is going to be a tough job. "The length of a name should correspond to the size of its scope."
- Avoid encodings (Hungarian notation and co.)
- Avoid member prefixes (m_var, var_ etc.)
- Leave the interface notation off. (IFactory etc.)
- Classes and objects should have noun or noun phrase names like Customer, WikiPage, Account, and AddressParser. Avoid words like Manager, Processor, Data, or Info in the name of a class. A class name should not be a verb.
- Methods should have verb or verb phrase names like postPayment, deletePage, or save.
- Pick one word per concept: Pick one word for one abstract concept and stick with it. For instance, it’s confusing to have fetch, retrieve, and get as equivalent methods of different classes.

## Functions
- Functions should be small, usually smaller than 20 lines.
- FUNCTIONS SHOULD DO ONE THING. THEY SHOULD DO IT WELL.THEY SHOULD DO IT ONLY.
- Using the "TO" paragraph we can check, whether our function has more then one responsibility:
  - "TO *RenderPageWithSetupsAndTeardowns*, we check to see whether the page is a test page and if so, we include the setups and teardowns. In either case we render the page in HTML."
- Switch -> Factory
  - By their nature, switch statements always do N things.
  - Switch Statements violate several principles (SRP, OCP etc.)
```java
public Money calculatePay(Employee e)
throws InvalidEmployeeType {
  switch (e.type) {
    case COMMISSIONED:
      return calculateCommissionedPay(e);
    case HOURLY:
      return calculateHourlyPay(e);
    case SALARIED:
      return calculateSalariedPay(e);
    default:
      throw new InvalidEmployeeType(e.type);
    }
}

public abstract class Employee {
  public abstract boolean isPayday();
  public abstract Money calculatePay();
  public abstract void deliverPay(Money pay);
}
-----------------
public interface EmployeeFactory {
  public Employee makeEmployee(EmployeeRecord r) throws InvalidEmployeeType;
}
-----------------
public class EmployeeFactoryImpl implements EmployeeFactory {
  public Employee makeEmployee(EmployeeRecord r) throws InvalidEmployeeType {
    switch (r.type) {
      case COMMISSIONED:
        return new CommissionedEmployee(r) ;
      case HOURLY:
        return new HourlyEmployee(r);
      case SALARIED:
        return new SalariedEmploye(r);
      default:
        throw new InvalidEmployeeType(r.type);
    }
  }
}
```
- Function arguments: try to create functions that use max one or two arguments, as functions taking more arguments are harder to test.
- Common monadic forms: There are two very common reasons to pass a single argument into a function. You may be asking a question about that argument, as in boolean fileExists(“MyFile”). Or you may be operating on that argument, transforming it into something else and returning it.
- Avoid boolean flag arguments. Flag arguments are ugly. Passing a boolean into a function is a truly terrible practice. It immediately complicates the signature of the method, loudly proclaiming that this function does more than one thing. It does one thing if the flag is true and another if the flag is false!
- If you need more than 3 arguments try to put them into a struct and pass it as an argument. (e.g.: Instead of passing name, postalCode, streetName, houseNumber, pass an Address struct.)
- Command Query Separation: Functions should either do something or answer something, but not both. --> change state of the object (void) or return some information (bool isEnabled()) but not both.
- Avoid using error codes:
```cpp
if (deletePage(page) == E_OK) {
  if (registry.deleteReference(page.name) == E_OK) {
    if (configKeys.deleteKey(page.name.makeKey()) == E_OK){
      logger.log("page deleted");
    } else {
      logger.log("configKey not deleted");
    }
  } else {
    logger.log("deleteReference from registry failed");
  }
} else {
  logger.log("delete failed");
  return E_ERROR;
}

Insead:

try {
  deletePage(page);
  registry.deleteReference(page.name);
  configKeys.deleteKey(page.name.makeKey());
}
catch (Exception e) {
  logger.log(e.getMessage());
}
```
- One input, one output for functions (Dijkstra). Martin says that violating this is not a problem when making small functions. (People tend to lose the overview when working with long functions.)

## Comments
- The proper use of comments is to compensate for our failure to express ourself in code. Note that I used the word failure. I meant it. --> Less shitty comments, more quality code.
```cpp
// Check to see if the employee is eligible for full benefits
if ((employee.flags & HOURLY_FLAG) &&
(employee.age > 65))

if (employee.isEligibleForFullBenefits())
```
- TODO Comments: It is sometimes reasonable to leave “To do” notes in the form of //TODO comments. It is not an excuse to leave bad code in the system. If you have extra time fix them!
- Comments are usually less maintained than code, thus they often lie. --> Write less comment and write better code!
- Don't comment on functions that are obvious. ("Constructor for XYZ", "Returns the day of the month")
- Delete the commented out code.

## Formatting
- Declare the variables as close  as possible to their actual use.
- Maximum number of chars in a line should be limited to 80 but max 120.
- "Space" should be added to operations that have lower "rank" (+,-), but no space to operations that have higher rank ( * , / )
- Use an auto-formatter. (clang / google style for C++)
