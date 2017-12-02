import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void  main(String[] args) {
        Fibo obj = new Fibo();
        System.out.println(obj.cachedFibN(14));
    }
}

class Fibo{
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
