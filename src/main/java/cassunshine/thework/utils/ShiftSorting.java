package cassunshine.thework.utils;

import java.util.ArrayList;
import java.util.function.Function;

/**
 * Used to sort lists of objects so that they end up with the same configuration given multiple inputs.
 */
public class ShiftSorting {

    /**
     * Generates an index of how much an array needs to rotate to end up 'alphabetically' the best array. All rotations will end up with the same result.
     */
    public static <T> int findShiftValue(T[] array, Function<T, Integer> indexer) {
        int[] indexes = new int[array.length];

        for (int i = 0; i < indexes.length; i++)
            indexes[i] = indexer.apply(array[i]);

        return performShiftSort(indexes);
    }

    /**
     * Generates an index of how much a list needs to rotate to end up 'alphabetically' the best array. All rotations will end up with the same result.
     */
    public static <T> int findShiftValue(ArrayList<T> list, Function<T, Integer> indexer) {
        int[] indexes = new int[list.size()];

        for (int i = 0; i < indexes.length; i++)
            indexes[i] = indexer.apply(list.get(i));

        return performShiftSort(indexes);
    }

    private static <T> int performShiftSort(int[] indexes) {
        int[] bestSort = new int[indexes.length];
        int[] currentSort = new int[indexes.length];

        System.arraycopy(indexes, 0, bestSort, 0, indexes.length);
        System.arraycopy(indexes, 0, currentSort, 0, indexes.length);

        int bestSortOffset = 0;

        //Generate shifted arrays, and compare. Keep only the best.
        for (int i = 1; i < indexes.length; i++) {
            //Rotate the current array 1 time.
            rotateArray(currentSort, 1);

            int compare = compareArrays(bestSort, currentSort);

            if (compare > 0) {
                bestSortOffset = i;

                //Copy current sort into best for future comparisons.
                System.arraycopy(currentSort, 0, bestSort, 0, bestSort.length);
            }
        }

        return bestSortOffset;
    }


    public static <T> void rotateArray(T[] array, int amount) {
        for (int i = 0; i < amount; i++) {
            //Steal last element from array.
            var last = array[array.length - 1];
            //Move each element up one.
            for (int j = array.length - 1; j > 0; j--)
                array[j] = array[j - 1];
            //Put lost element back into array as the first entry.
            array[0] = last;
        }
    }

    public static void rotateArray(int[] array, int amount) {
        for (int i = 0; i < amount; i++) {
            //Steal last element from array.
            var last = array[array.length - 1];
            //Move each element up one.
            for (int j = array.length - 1; j > 0; j--)
                array[j] = array[j - 1];
            //Put lost element back into array as the first entry.
            array[0] = last;
        }
    }

    private static int compareArrays(int[] a, int[] b) {
        for (int i = 0; i < a.length && i < b.length; i++) {
            var num = a[i];
            var numOther = b[i];

            if (num != numOther)
                return num - numOther;
        }

        return a.length - b.length;
    }

    private static class SortConfiguration<T> {
        public final int[] entries;

        public SortConfiguration(T[] array, Function<T, Integer> indexer, int offset) {
            entries = new int[array.length];

            for (int i = 0; i < array.length; i++) {
                int realIndex = (i + offset) % array.length;
                entries[realIndex] = indexer.apply(array[i]);
            }
        }
    }
}
