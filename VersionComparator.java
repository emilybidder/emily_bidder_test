import java.util.*;

public class VersionComparator implements Comparator<String> {

    @Override
    /**
     * Compares two version numbers. A version number consists of substrings separated by period (.) characters.
     *
     * The comparison is based on the comparison of each substring, similarly to how you would sort alphabetically.
     * (e.g. "2.1" is greater than "1.2")
     *
     * Each substring is compared first by the value of any integer at the start of the substring
     * (e.g. "1.10" is greater than "1.2")
     *
     * If one substring does not start with an integer, and the other does, then the string that does not start with an
     * integer is always considered greater.
     * (e.g. "1.a10" is greater than "1.10")
     *
     * If neither substring starts with an integer, then the substrings are compared using Java's built-in
     * String.compareTo method. This means that numbers following non-number characters will not be compared as integers,
     * but rather as strings. However, this is a very niche edge case unlikely to occur in the real world, so not
     * accounting for this case reduces complexity within the method, improving efficiency and reducing the likelihood
     * of bugs in the program.
     * (e.g. "1.b1" is greater than "1.a1",
     *       "1.a2" is greater than "1.a10")
     *
     * If there is no index at which the two version strings differ, then the longer version string is considered greater.
     * (e.g. "1.0.0" is greater than "1.0")
     * @param v1: a string representing a version number.
     * @param v2: a string representing a version number.
     * @return A negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater
     *         than the second.
     */
    public int compare(String v1, String v2) {
        if (v1.equals(v2)) { // if the strings are equal then the versions are the same
            return 0; // saves us computation
        }

        // split each string into an array of substrings, where each substring is separated by a period in the original string.
        // for example: "1.2.3" becomes {"1", "2", "3"}
        String[] v1Split = v1.split("\\.");
        String[] v2Split = v2.split("\\.");

        // convert each substring after splitting into the format returned by versionSplitNumbers
        // (see that function's documentation below)
        String[][] v1SplitNums = new String[v1Split.length][2];
        String[][] v2SplitNums = new String[v2Split.length][2];

        for (int i = 0; i < v1Split.length; i++) {
            v1SplitNums[i] = versionSplitNumbers(v1Split[i]);
        }

        for (int i = 0; i < v2Split.length; i++) {
            v2SplitNums[i] = versionSplitNumbers(v2Split[i]);
        }

        int len = Math.min(v1Split.length, v2Split.length); // length of the shorter array of substrings

        for (int i = 0; i < len; i++) {
            if (v1SplitNums[i][0].equals(v2SplitNums[i][0])) { // if both substrings start with the same number, or have no number
                int comparison = v1SplitNums[i][1].compareTo(v2SplitNums[i][1]); // compare the letters alphanumerically
                if (comparison == 0) // if the letters are the same we have to continue to the next substring
                    continue;
                else
                    return comparison;
            }
            else if (v1SplitNums[i][0].isEmpty()) { // if the substring in v1 contains numbers at the start
                                                    // and the one in v2 doesn't
                return 1; // always collate numbers before letters
            }
            else if (v2SplitNums[i][0].isEmpty()) { // if the substring in v2 contains numbers at the start
                                                    // and the one in v1 doesn't
                return -1; // always collate numbers before letters
            }
            else { // if the substrings in both v1 and v2 contain numbers
                // we need to compare the numbers
                int n1 = Integer.parseInt(v1SplitNums[i][0]);
                int n2 = Integer.parseInt(v2SplitNums[i][0]);

                int comparison = Integer.compare(n1, n2);

                if (comparison == 0) { // if the numbers are equal we have to move to the next substring
                    continue;
                }
                else
                    return comparison;
            }

        }


        // at this point each version string is equal until the end of the shortest string.
        // we consider the longer string to be greater

        return Integer.compare(v1.length(), v2.length());

    }


    /**
     * Splits substring of a version string (i.e. a part between periods) into an array, the first entry consisting of
     * any numbers at the start of the input string, and the second entry consisting of the rest of the input string.
     * For example:
     * versionSplitNumbers("12abc34") will return an array {"12", "abc34"}
     * versionSplitNumbers("10") will return an array {"10", ""}
     * versionSplitNumbers("a129") will return an array {"", "a129"}
     * @param s: a string representing a substring in a version string
     * @return an array where the first entry is either empty or a string of an integer, and the second entry is another
     *         string representing the rest of the input string (may contain digits or be empty)
     */
    private static String[] versionSplitNumbers(String s) {
        char[] charArr = s.toCharArray(); // convert the string to a list of chars

        String[] result = new String[2];

        for (int i = 0; i < charArr.length; i++) {
            if (charArr[i] < '0' || '9' < charArr[i]) { // if the current character is not a number
                result[0] = s.substring(0, i);
                result[1] = s.substring(i);
                return result;
            }
        }

        result[0] = s;
        result [1] = "";
        return result;
    }

    public static void main (String[] args) {
        // some driver code to demonstrate
        Comparator<String> c = new VersionComparator();

        String[] ar = {"0.0", "1.1", "1.10", "10", "2.0", "1.10a", "1.a10", "1.b97", "1.10b", "1.2", "1.1.0", "1.1.1"};
        List<String> l = Arrays.asList(ar);

        Collections.sort(l, c);

        for (String s: l)
            System.out.println(s);
    }
}
