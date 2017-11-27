
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

import Test.Country;

public class Main {
    public static void main(String[] argv) {
        class Coordinate {
            int x;
            int y;

            Coordinate(int x, int y){
                this.x = x;
                this.y = y;
            }

            @Override
            public String toString() {
                return "x: '" + this.x + "', y: '" + this.y + '\n';
            }

            public int getX() {
                return x;
            }

            public int getY() {
                return y;
            }
        }

        try (Stream<String> stream = Files.lines(Paths.get("/home/puppy/Desktop/JavaKata.txt"))) {
            // Read
            List<Coordinate> coordinates = new ArrayList<>();
            stream.forEach(s -> {
                String[] pair_coordinates = s.split(",");
                if(pair_coordinates.length == 2){
                    Coordinate coordinate = new Coordinate(Integer.parseInt(pair_coordinates[0]),
                            Integer.parseInt(pair_coordinates[1]));
                    coordinates.add(coordinate);
                }
            });
            System.out.println(coordinates);

            // Sort based on x
            // ComparingInt: Accepts a function that extracts an int sort key from a type T,
            // and returns a Comparator<T> that compares by that sort key.

            coordinates.sort(Comparator.comparingInt(Coordinate::getX));
            System.out.println(coordinates);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
