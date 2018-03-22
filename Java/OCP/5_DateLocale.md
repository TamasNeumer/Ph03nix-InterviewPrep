# Date, Strings and Localization

#### Dates and Times

- Package: `import java.time.*;`
- The types are **immutable!**
- **Creating Dates and Times**
  - `LocalDate` Contains just a date — no time and no time zone.
  - `LocalTime` Contains just a time — no date and no time zone.
  - `LocalDate` Time Contains both a date and time but no time zone.
  - `ZonedDateTime` Contains a date, time, and time zone.
    - 2015–06–20T07:50 GMT-04:00 // GMT 2015–06–20 11:50
    - 2015–06–20T07:50+02:00[Europe/Paris] // GMT 2015–06–20 5:50
    - It always shows the local time. If you want the GMT you have to calculate it for yourself.
  - Each of this classes have the static `now()` method.
  - Another option is to use the static `of()` funcition.
    - Date
      - `public static LocalDate of(int year, int month, int dayOfMonth)`
      - `public static LocalDate of(int year, Month month, int dayOfMonth)` -- Month is an Enum in this case.
      - **IMPORTANT** java normally counts enums from 0, however for `Month` there is an exception!
    -Time
      `public static LocalTime of(int hour, int minute, int second, int nanos)`
        - hour and minute are obligatory, the rest are optional.
    - DateTime
      - Lot of method signatures.
      - `public static LocalDateTime of(LocalDate date, LocalTime time)` - "merging" date and time
      - `public static LocalDateTime of(int year, int month,int dayOfMonth, int hour, int minute, int second, int nanos)`
        - Obligatory until minute. Then seconds and nanoseconds are optional.
    - Zones
      - `ZoneId zone = ZoneId.of("US/Eastern");`
      - `ZoneId.systemDefault()` - delivers your own timezone
      - `ZonedDateTime zoned1 = ZonedDateTime.of(2015, 1, 20, 6, 15, 30, 200, zone);`
      - `public static ZonedDateTime of(LocalDate date, LocalTime time, ZoneId zone)`
      - `public static ZonedDateTime of(LocalDateTime dateTime, ZoneId zone)`
      - Note that you can't pass the month as an enum here... Maybe a bug that will be fixed later.
- **Manipulating Dates and Times**
  - `plusDays, plusMonths, minusDays, minusMinutes` etc. Note the plural!
  - Java knows leap years!!!
- **Periods**
  - `Period period = Period.ofMonths(1);` Then using this to:  `ldInstance = ldInstance.plus(period);`
  - Note the plurals! (ofMonths, ofYears, ofMinutes etc.)
  - `static Period of(int years, int months, int days)`
  - **The period notation**
    - P1Y2M3D = `System.out.printIn(Period.of)1,2,3));`
    - `System.out.println(Period.ofWeeks(3));` --> P21D --> Week is **not** one of the units a Period stores.
  - Watch out. You can't add periods of days or month to a LocalTime instance. This will result in `UnsupportedTemporalTypeException`.
- **Durations**
  - Duration is supposed to be used with objects that have time.
  - Duration is output beginning with `PT`, which you can think of as a period of time. A Duration is stored in hours, minutes, and seconds.
  - `ofDays, ofHours, ofSeconds` etc.
  - Duration **doesn’t have a constructor that takes multiple units like Period does**. If you want something to happen every hour and a half, you would specify 90 minutes.
  - Remember you can't use `Periods` with `LocalTime`, as it doesn't store days. Also you can't use duration with `LocalDate` as it doesn't store the time!
- **Instants**
  - The `Instant` class represents a specific moment in time **in the GMT time zone**.
  - `Instant now = Instant.now();`
  - The `ZonedDateTime` includes a time zone. The `Instant` gets rid of the time zone and turns it into an Instant of time in GMT.
  - You cannot convert a LocalDateTime to an Instant. (LTD doesn't have timezone)
- **Daylight Savings**
  - Java accounts for these. Be aware of.

#### String class review

- Final and immutable.
- String literals are cached in the string pool.
- Comparing with `==` vs `equals()`. Know the difference.
- `+` operator is overloaded on the stings.
- Position counting starts from 0.
- StringBuilder is **not** thread safe!

#### Adding Internationalization and Localization

- *Internationalization* is the process of designing your program so it can be adapted. This involves placing strings in a property  le and using classes like DateFormat so that the right format is used based on user preferences.
- *Localization* means actually supporting multiple locales.
- **Picking a Locale**
  - Package: `java.util`
  - `Locale locale = Locale.getDefault();` -- find the current locale.
    - Language only: (fr, hu etc.)
    - Language and country: en_US, en_GB etc. The language must come first and after that the region, separated by an underscore.
  - Use the builder design pattern:

    ```java
    Locale l1 = new Locale.Builder()
      .setLanguage("en")
      .setRegion("US")
      .build();
    ```

  - For testing purposes it is often worth to change the locale: `Locale.setDefault(locale);`
- **Resource bundle**
  - A *resource bundle* contains the local specific objects to be used by a program.
  - A *property file* is a  le in a specific format with key/value pairs.
  - If we don’t have a country-specific resource bundle, Java will use a language-specific one.

    ```java
    Zoo_en.properties
      hello=Hello
      open=The zoo is open.
    Zoo_fr.properties
      hello=Bonjour
      open=Le zoo est ouvert

    // Usage
    Locale us = new Locale("en", "US");
    Locale france = new Locale("fr", "FR");

    public static void printProperties(Locale locale) {
      ResourceBundle rb = ResourceBundle.getBundle("Zoo", locale);
      System.out.println(rb.getString("hello"));
      System.out.println(rb.getString("open"));
    }
    ```

    - Notice how much is happening behind the scenes here. Java uses the name of the bundle (Zoo) and looks for the relevant property file.
  - **Property file format**
    - `animal=dolphin`, `animal:dolphin`, `animal dolphin` are all correct.
    - If a line begins with # or !, it is a comment.
    - Spaces before or after the separator character are ignored.
    - Spaces at the beginning/end of a line are ignored.
    - End a line with a backslash if you want to break the line for readability.
    - You can use normal Java escape characters like \t and \n.
  - Properties has some additional features, including being able to pass a default.
    - With properties you can do `getProperty("key", "default")`
- **Creating a Java Class Resource Bundle**
  - Most of the time, a property file resource bundle is enough to meet the program’s needs. It does have a limitation in that **only String values are allowed.** Java class resource bundles **allow any Java type as the value**. Keys are strings regardless.

    ```java
    import java.util.*;
    public class Zoo_en extends ListResourceBundle {
      protected Object[][] getContents() { return new Object[][] {
      { "hello", "Hello" },
      { "open", "The zoo is open" } }; } }
    ```

  - There are two main advantages of using a Java class instead of a property  le for a resource bundle:
    - You can use a value type that is not a String.
    - You can create the values of the properties at runtime.
- **Determining witch boundle to use**
  - `ResourceBundle.getBundle("name");` // uses default locale
  - `ResourceBundle.getBundle("name", locale);`
- **Formatting Numbers**
  - For each version there is a no-args constructor and one that accepts a locale.
    - `NumberFormat.getInstance()`
    - `NumberFormat.getNumberInstance()`
    - `NumberFormat.getCurrencyInstance()`
    - `NumberFormat.getPercentInstance()`
    - `NumberFormat.getIntegerInstance()`
    - Once you have the `NumberFormat` instance, you can call `format()` to turn a number into a String and `parse()` to turn a String into a number.

      ```java
      int attendeesPerYear = 3_200_000;
      int attendeesPerMonth = attendeesPerYear / 12;
      NumberFormat us = NumberFormat.getInstance(Locale.US); 
      System.out.println(us.format(attendeesPerMonth)); // 266,666
      NumberFormat g = NumberFormat.getInstance(Locale.GERMANY); 
      System.out.println(g.format(attendeesPerMonth)); // 266.666
      NumberFormat ca = NumberFormat.getInstance(Locale.CANADA_FRENCH); 
      System.out.println(ca.format(attendeesPerMonth)); 266 666
      ```

    - If using get currency instance the double 48.00 will be formatted as $48.00
- **Formatting Date and Time**
  - `System.out.println(date.format(DateTimeFormatter.ISO_LOCAL_DATE));` this is known from OCA

      ```java
      DateTimeFormatter shortDateTime = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);
      System.out.println(shortDateTime.format(date)); // 1/20/20
      ```

#### Learnings

- Locale is created using the constructor. Language comes as the first argument, the region as the second.
- Java looks into parent bundles if a key is not found.  Java will look at Props.properties if Props_en.properties does not contain the requested key.
- Since Class Resources can contain non-string values, all property files can be turned into class resources but not vice versa.
- `Locale.setDefault(new Locale("en", "US")); ResourceBundle b = ResourceBundle.getBundle("Dolphins");` -->  Java will first look for the most specific matches it can find, starting with `Dolphins_en_US.java` and then `Dolphins_en_US.properties`.
- Once a bundle is chosen, only resources in that hierarchy are allowed.
- Java throws an exception if invalid date values are passed to the `of` factory methods of the Date/Time classes.
- `Period` does not allow chaining. Only the last `Period` method called counts.
- The `Properties` class defines a `get()` method that does not allow for a default value. It also has a `getProperty()` method, which returns the default value if the key is not provided.