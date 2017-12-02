import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;



/*
* The readAllLines is not AutoCloseable!
* You need to add the "Comparator.comparingInt" (you can't simply write String::length)
*/
public class Main {
    public static void main(String[] argv) {
        try{
            List<String> lines = Files.readAllLines(Paths.get("/home/puppy/Desktop/skills_1_to_5.txt"));
            lines.sort(Comparator.comparingInt(String::length).reversed());
            System.out.println(lines.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
