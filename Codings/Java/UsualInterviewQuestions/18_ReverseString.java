import java.util.Scanner;

public class Main {
    public static void main(String[] argv) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Please enter a string: ");
        String toReverse = sc.nextLine();

        StringBuilder sb = new StringBuilder();
        for(int j = toReverse.length() -1; j >= 0; j--){
            sb.append(toReverse.charAt(j));
        }
        System.out.println(sb.toString());
    }
}
