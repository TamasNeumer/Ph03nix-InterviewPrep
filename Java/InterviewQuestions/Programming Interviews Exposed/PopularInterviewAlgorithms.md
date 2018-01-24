## Popular interview Algorithms

#### 1. FizzBuzz
- Bit overcomplicated solution using HashMap to store Interger-String pairs. Scales well tho + clean code.
```java
class FizBuzz {
    private Map<Integer, String> knownWords = new HashMap<>();

    List<String> fizBuz(int n){
        List<String> solution = new ArrayList<>();

        for(int i = 1; i <= n; i++){
            StringBuilder sb = new StringBuilder();
            for(Map.Entry<Integer, String> entry : knownWords.entrySet()){
                if(i % entry.getKey() == 0){
                    sb.append(entry.getValue());
                }
            }
            if(sb.toString().isEmpty()) solution.add(Integer.toString(i));
            else {
                solution.add(sb.toString());
            }
        }
        return solution;
    }

    void addPair(Integer num, String word){
        knownWords.put(num, word);
    }
}
```

#### 2. Write a method that returns a Fibonacci sequence from 1 to n.
```java
class Fibo {
     List<Integer> fibo(int n){
        if(n <= 0){
            throw new IllegalArgumentException("n must be greater than 0");
        }
        if (n == 1){
            return Arrays.asList(0);
        }
        if( n == 2)
            return Arrays.asList(0,1);

        // Since we know the exact number we want to store, specify size.
        List<Integer> solution = new ArrayList<>(n);
        solution.add(0);
        solution.add(1);
        n = n-2;

        while(n > 0){
            Integer one = solution.get(solution.size()-1);
            Integer two = solution.get(solution.size()-2);
            solution.add(one + two);
            n = n-1;
        }

        return solution;
    }
}
```

#### 3. Write a method that returns the nth value of the Fibonacci sequence.
```java
class Fibo {
    private Map<Integer, Integer> fibCache = new HashMap<>();
    public int cachedFibN(int n) {
        if (n < 0) {
            throw new IllegalArgumentException(
                    "n must not be less than zero");
        }
        fibCache.put(0, 0);
        fibCache.put(1, 1);
        return recursiveCachedFibN(n);
    }
    private int recursiveCachedFibN(int n) {
        if (fibCache.containsKey(n)) {
            return fibCache.get(n);
        }
        int value = recursiveCachedFibN(n - 1) + recursiveCachedFibN(n - 2);
        fibCache.put(n, value);
        return value;
    }
}
```

#### 4. Write a factorial implementation that does not use recursion.
```java
public int factorial(int n){
  if (n < 1) {
    throw new IllegalArgumentException("n must be greater than zero");
  }

  // NOTE THE LONG!
  long toReturn = 1;
  for (int i = 1; i <= n; i++) {
    toReturn *= i;
  }
  return toReturn;
}
```

#### 5.
