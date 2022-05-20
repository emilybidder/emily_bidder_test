public class OverlappingLines {
    /**
     * Takes as input two lines on the x-axis (x1, x2) and (x3, x4) and returns whether the two lines overlap.
     * @param x1
     * @param x2
     * @param x3
     * @param x4
     * @return
     */
    public static boolean isOverlapping(double x1, double x2, double x3, double x4) {
        double min1; // the minimum point on (x1, x2)
        double max1; // the maximum point on (x1, x2)
        double min2; // the minimum point on (x3, x4)
        double max2; // the maximum point on (x3, x4)

        // set min1 and max1
        if (x1 <= x2) {
            min1 = x1;
            max1 = x2;
        } else {
            min1 = x2;
            max1 = x1;
        }

        // set min2 and max2
        if (x3 <= x4) {
            min2 = x3;
            max2 = x4;
        } else {
            min2 = x4;
            max2 = x3;
        }

        return min1 <= max2 && min2 <= max1;
    }

    public static void main(String[] args) {
        // some driver code to demonstrate

        System.out.println(isOverlapping(1, 5, 2, 6));
        System.out.println(isOverlapping(1, 5, 6, 8));

        System.out.println(isOverlapping(2, 6, 1, 5));
        System.out.println(isOverlapping(6, 8, 1, 5));

        // also works if the line is given "backwards", i.e. x2 >= x1 or x3 >= x4
        System.out.println(isOverlapping(5, 1, 2, 6));
    }
}
