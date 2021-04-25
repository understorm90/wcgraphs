package it.unifi.simonesantarsiero.wcgraphs.newsumsweep;

/**
 * This class contains some routines that are useful for many different parts of the code.
 */
public class Utilities {

    /**
     * Returns the value of k such that (a[k], b[k]) is lexicographically maximal among those pairs such that both a[i] and b[i] are positive.
     *
     * @param a the first array
     * @param b the second array (must be at least as long as the first).
     * @return the value of k required
     */
    public static int argMax(int[] a, int[] b) {
        int max = -1;

        for (int k = 0; k < a.length; k++) {
            if (a[k] >= 0 && b[k] >= 0 && (max == -1 || a[k] > a[max] || (a[k] == a[max] && b[k] > b[max]))) {
                max = k;
            }
        }
        return max;
    }

    /**
     * Returns the value of k such that a[k] is maximal.
     *
     * @param a the array
     * @return the value of k required
     */
    public static int argMax(double[] a) {
        int max = 0;

        for (int k = 1; k < a.length; k++) {
            if (a[k] > a[max]) {
                max = k;
            }
        }
        return max;
    }

    /**
     * Returns the value of k such that (a[k], b[k]) is lexicographically minimal
     * among those pairs such that both a[i] and b[i] are positive.
     *
     * @param a the first array
     * @param b the second array (must be at least as long as the first).
     * @return the value of k required
     */
    public static int argMin(int[] a, int[] b) {
        int min = -1;
        for (int k = 0; k < a.length; k++) {
            if (a[k] >= 0 && b[k] >= 0 && (min == -1 || a[k] < a[min] || (a[k] == a[min] && b[k] < b[min]))) {
                min = k;
            }
        }
        return min;
    }
}
