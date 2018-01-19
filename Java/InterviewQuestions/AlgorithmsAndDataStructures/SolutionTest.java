import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class SolutionsTest {
    private Solutions obj = new Solutions();

    @Test
    public void mostFrequentElement() throws Exception {
        assertEquals(1, obj.mostFrequentElementInArray(new int[]{1, 3, 1, 3, 2, 1}));
    }

    @Test
    public void commonElementsInTwoArrays() throws Exception {
        assertArrayEquals(new Integer[]{1, 4, 9},
                obj.commonElementsInTwoArrays(
                        new int[]{1, 3, 4, 6, 7, 9},
                        new int[]{1, 2, 4, 5, 9, 10}));
    }

    @Test
    public void isRotations() throws Exception {
        assertEquals(true, obj.isRotatons(
                new int[] {1,2,3,4,5,6,7},
                new int[] {4,5,6,7,1,2,3}));
    }

    @Test
    public void non_repeating() throws Exception {
        assertEquals('c', obj.non_repeating("aabcb").charValue());
        assertEquals('y', obj.non_repeating("xxyz").charValue());
        assertEquals(null, obj.non_repeating("aabb"));
    }

    @Test
    public void is_one_away() throws Exception {
        assertEquals(true, obj.is_one_away("abcde", "abfde"));
        assertEquals(true, obj.is_one_away("abcde", "abde"));
        assertEquals(true, obj.is_one_away("xyz", "xyaz"));
    }

}
