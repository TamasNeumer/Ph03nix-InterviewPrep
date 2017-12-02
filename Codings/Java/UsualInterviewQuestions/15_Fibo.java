import com.sun.javaws.exceptions.InvalidArgumentException;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        try {
            List<Integer> result = fiboSequenceUntil(18);
            System.out.println(result.toString());
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
        }
    }

    private static List<Integer> fiboSequenceUntil(int n) throws InvalidArgumentException {
        List<Integer> result = new ArrayList<>();
        if (n <= 0)
            throw new IllegalArgumentException("n must be greater than zero!");

        result.add(1);
        if (n == 1)
            return result;
        result.add(1);
        if (n == 2)
            return result;

        while (n > 2) {
            result.add(result.get(result.size() - 1) + result.get(result.size() - 2));
            n--;
        }
        return result;
    }
}
