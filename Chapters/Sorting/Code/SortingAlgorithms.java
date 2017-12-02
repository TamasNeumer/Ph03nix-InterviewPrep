import java.util.Arrays;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.List;

class Bubble {
	public static void main(String args[]) {
		/*Bubble sort*/
		/*int[] array = new int[] {9, 8, 7, 6, 5, 4, 3, 2, 1};
		int[] sorted = bubbleSort(array);
		System.out.println(Arrays.toString(sorted));*/

		/*Insertion Sort*/
		List<Integer> array = Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1);
		List<Integer> sorted = quickSort(array);
		System.out.println(Arrays.toString(sorted.toArray()));
	}

	public static int[] bubbleSort(int[] array) {
		boolean swapped;
		do {
			swapped = false;
			for (int i = 1; i < array.length; i++) {
				if (array[i] < array[i - 1]) {
					int temp = array[i];
					array[i] = array[i - 1];
					array[i - 1] = temp;
					swapped = true;
				}
			}
		} while (swapped);
		return array;
	}

	public static List<Integer> insertionSort(List<Integer> numbers) {
		final LinkedList<Integer> sortedList = new LinkedList<>();

		originalList:
		for (Integer number : numbers) {
			for (int i = 0; i < sortedList.size(); i++) {
				sortedList.add(i, number);
				continue originalList;
			}
			sortedList.add(sortedList.size(), number);
		}

		return sortedList;
	}

	public static List<Integer> quickSort(List<Integer> list) {
		if (list.size() < 2)
			return list;

		Integer pivot = list.get(0);
		List<Integer> smaller = new ArrayList<>();
		List<Integer> larger = new ArrayList<>();

		for (int i = 1; i < list.size(); i++) {
			if (list.get(i) < pivot) {
				smaller.add(list.get(i));
			} else {
				larger.add(list.get(i));
			}
		}

		List<Integer> sorted = quickSort(smaller);
		sorted.add(pivot);
		sorted.addAll(quickSort(larger));
		return  sorted;
	}


	public static List<Integer> quickSortInPlace(int[] array,
	        int leftBoundaryIndex, int rightBoundaryIndex) {

		int pivot = array[0];
		int leftPtr = leftBoundaryIndex;
		int rightPtr = rightBoundaryIndex;

		while (leftPtr <= rightPtr) {
			while (array[leftPtr] < pivot) leftPtr++;
			while (array[rightPtr] > pivot) rightPtr--;
			if (leftPtr <= rightPtr) {
				int temp = array[leftPtr];
				array[leftPtr] = array[rightPtr];
				array[rightPtr] = temp;
				leftPtr++;
				rightPtr--;
			}
		}

		if (left < j) quickSort(arr, left, j);
		if (i < right) quickSort(arr, i, right);
	}

	/*MERGE SORT*/
	public static List<Integer> mergeSort(List<Integer> list) {
		if (list.size() < 2)
			return list;

		List<Integer> leftHalf = list.subList(0, list.size() / 2);
		List<Integer> rightHalf = list.subList(list.size() / 2, list.size());

		return  merge(mergeSort(leftHalf), mergeSort(rightHalf));
	}

	public static List<Integer> merge(List<Integer> l1, List<Integer> l2) {
		int leftPtr = 0;
		int rightPtr = 0;
		final List<Integer> merged = new ArrayList<>(left.size() + right.size());

		while (leftPtr < left.size() && rightPtr < right.size()) {
			if (left.get(leftPtr) < right.get(rightPtr)) {
				merged.add(left.get(leftPtr));
				leftPtr++;
			} else {
				merged.add(right.get(rightPtr));
				rightPtr++;
			}
		}

		while (leftPtr < left.size()) {
			merged.add(left.get(leftPtr));
			leftPtr++;
		}

		while (rightPtr < right.size()) {
			merged.add(right.get(rightPtr));
			rightPtr++;
		}
		return merged;
	}

	public static boolean binarySearch(final List<Integer> numbers,
	                                   final Integer value) {
		if (numbers == null || _numbers.isEmpty()) {
			return false;
		}
		final Integer comparison = numbers.get(numbers.size() / 2);
		if (value.equals(comparison)) {
			return true;
		}
		if (value < comparison) {
			return binarySearch(
			           numbers.subList(0, numbers.size() / 2),
			           value);
		} else {
			return binarySearch(
			           numbers.subList(numbers.size() / 2 + 1, numbers.size()),
			           value);
		}
	}
}