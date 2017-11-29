import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] argv) {
        List<String> resumt = alternativeFizzBuzz(21);
        System.out.println(resumt.toString());
    }

    public static List<String> alternativeFizzBuzz(final int n) {
        final List<String> toReturn = new ArrayList<>(n);
        for (int i = 1; i <= n; i++) {
            final String word =
                    toWord(3, i, "Fizz") + toWord(5, i, "Buzz");
            if(word.isEmpty()) {
                toReturn.add(Integer.toString(i));
            }
            else {
                toReturn.add(word);
            }
        }
        return toReturn;
    }

    private static String toWord(final int divisor,
                                 final int value,
                                 final String word) {
        return value % divisor == 0 ? word : "";
    }
}
