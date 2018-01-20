import java.util.*;
import java.util.stream.Collectors;

public class Solutions {
    public int mostFrequentElementInArray(int[] array) {
        Map<Integer, Integer> occurrences = new HashMap<>();
        int maxValue = 0, maxOccurrence = 0;
        for (int i : array) {
            if (occurrences.containsKey(i)) {
                Integer value = occurrences.get(i);
                occurrences.put(i, value + 1);
            } else {
                occurrences.put(i, 1);
            }
            if (occurrences.get(i) > maxOccurrence) {
                {
                    maxOccurrence = occurrences.get(i);
                    maxValue = i;
                }
            }
        }
        return maxValue;
    }

    public Integer[] commonElementsInTwoArrays(int[] a, int[] b) {
        int ai = 0, bi = 0;
        List<Integer> solution = new ArrayList<>();
        while (ai < a.length && bi < b.length) {
            if (a[ai] < b[bi]) {
                ai++;
            } else if (a[ai] == b[bi]) {
                solution.add(a[ai]);
                ai++;
                bi++;
            } else {
                bi++;
            }
        }

        Integer[] resultInArray = new Integer[solution.size()];
        return solution.toArray(resultInArray);
    }

    public boolean isRotatons(int[] a, int[] b) {
        if (a.length != b.length) return false;
        if (a.length == 0) return true;
        int ai = 0, bi = -1;

        int a0 = a[ai];
        for(int i = 0; i < b.length; i++){
            if(a0 == b[i]){
                bi = i;
                break;
            }
        }

        if(bi == -1) return false;

        // More elegant: j = (bi + ap) % b.length;
        while(ai < a.length){
            if(bi == b.length)
                bi = 0;

            if(a[ai] != b[bi]) return false;
            bi++;
            ai++;
        }
        return true;
    }

    public Character non_repeating(String text){
        Hashtable<Character, Integer> ht = new Hashtable<>();
        for(Character c : text.toCharArray()){
            if(ht.containsKey(c)){
                Integer newValue = ht.get(c) + 1;
                ht.put(c, newValue);
            } else {
                ht.put(c,1);
            }
        }

        List<Character> c = new ArrayList<>();
        ht.forEach((k,v) -> {
            System.out.println("Item : " + k + " Count : " + v);
            if(v == 1){
                c.add(k);
            }
        });

        if(c.size() > 0) return c.get(0);
        return null;
    }

    public boolean is_one_away(String a, String b){
        if(a.length() > b.length()+1 && a.length() < b.length() -1) return false;

        if(a.length() < b.length()){
            if(!oneAway(a, b)) return false;
        } else if(b.length() < a.length()){
            if(!oneAway(b,a)) return false;
        } else {
            if(!oneChange(a,b)) return false;
        }

        return true;
    }

    public boolean oneAway(String a, String b){
        int ai = 0, bi = 0;
        boolean mismatchFound = false;
        while(ai < a.length()){
            if(a.charAt(ai) != b.charAt(bi)){
                if(mismatchFound) return false;
                mismatchFound = true;
                bi++;
            } else {
                ai++;
                bi++;
            }
        }
        return true;
    }

    public boolean oneChange(String a, String b){
        int ai = 0, bi = 0;
        boolean mismatchFound = false;
        while(ai < a.length()){
            if(a.charAt(ai) != b.charAt(bi)){
                if(mismatchFound) return false;
                mismatchFound = true;
                ai++;
                bi++;
            } else {
                ai++;
                bi++;
            }
        }
        return true;
    }

    public int[][] mineSweeper(int[][] bombs, int numRows, int numCols) {
    int[][] field = new int[numRows][numCols];
    for (int[] bomb: bombs) {
        int rowIndex = bomb[0];
        int colIndex = bomb[1];
        field[rowIndex][colIndex] = -1;
        for(int i = rowIndex - 1; i < rowIndex + 2; i++) {
            for (int j = colIndex - 1; j < colIndex + 2; j++) {
                if (0 <= i && i < numRows &&
                        0 <= j && j < numCols &&
                        field[i][j] != -1) {
                    field[i][j] += 1;
                }
            }
        }
    }
    return field;
}
}
