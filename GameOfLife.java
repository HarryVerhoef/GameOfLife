import java.util.*;
import java.io.*;
import java.awt.Point;
import java.math.BigInteger;

public class GameOfLife {

    // Entry point of the program, takes 2 arguments: filename and n
    public static void main(String[] args) throws FileNotFoundException {

        if (args.length != 2) {
            System.out.println("Please specify a file to read and a number of iterations: java GameOfLife /path/to/file n");
            return;
        }

        ArrayList<ArrayList<String>> map = new ArrayList<>();

        Scanner sc = new Scanner(new BufferedReader(new FileReader(args[0])));

        while (sc.hasNextLine()) {
            ArrayList<String> string_row = new ArrayList<>(Arrays.asList(sc.nextLine().split(" ")));
            map.add(string_row);
        }

        map = resizeMap(map).getSecond();

        System.out.println("Initial map:\n");
        printMap(map);
        System.out.println("\n\n");

        long n = Long.parseLong(args[1]);

        driver(map, n);



    }

    // The iterative method that controls the main flow of the program, also
    // checks for and mitigates cycles using a HashMap
    public static void driver(ArrayList<ArrayList<String>> map, long n) {
        HashMap<String, Long> hm = new HashMap<>();
        hm.put(encodeMap(map), 0L);
        boolean cycleFound = false;
        long totalIterations = 0L;

        for (long i = 1; i <= n; i++) {
            totalIterations++;
            System.out.println("iteration " + Long.toString(totalIterations) + ":");
            map = resizeMap(generateNextState(map)).getSecond();
            printMap(map);
            String primNewMap = encodeMap(map);

            long cycleCheck = hm.getOrDefault(primNewMap, 0L);
            if (cycleCheck == 0L) {
                hm.put(primNewMap, i);
            } else if (!cycleFound) {
                System.out.println("Cycle detected: iteration " + cycleCheck + " to " + i);
                n = (n - i) % (i - cycleCheck);
                i = 0;
                System.out.println("New n: " + Long.toString(n));
                cycleFound = true;
            }
        }

        System.out.println("FINISHED:");
        System.out.println("Total iterations: " + Long.toString(totalIterations));

        printMap(resizeMap(map).getSecond());

    }

    // A method to output the map to stdout
    public static void printMap(ArrayList<ArrayList<String>> map) {

        int rows = map.size();
        int cols = map.get(0).size();

        if (rows == 0 || cols == 0)
            return;

        // Draw map body
        for (ArrayList<String> row : map) {
            for (String cell : row) {
                System.out.print(cell + "  ");
            }
            System.out.println("");
        }

        System.out.println("\n");
    }

    // Performs the main calculation: Calculating the new map
    public static ArrayList<ArrayList<String>> generateNextState(ArrayList<ArrayList<String>> map) {

        Pair<ArrayList<Point>, ArrayList<ArrayList<String>>> resizedPair = resizeMap(map);
        ArrayList<Point> liveCells = resizedPair.getFirst();
        map = resizedPair.getSecond();
        int rows = map.size();
        int cols = map.get(0).size();


        ArrayList<ArrayList<Integer>> adjacentMap = new ArrayList<>();
        ArrayList<Integer> zeroRow = new ArrayList<Integer>();



        for (int j = 0; j < cols; j++) {
            zeroRow.add(0);
        }

        for (int i = 0; i < rows; i++) {
            adjacentMap.add(new ArrayList<>(zeroRow));
        }

        // Increment adjacent map values according to live cell location
        for (Point cell : liveCells) {
            int x = (int) cell.getX();
            int y = (int) cell.getY();

            adjacentMap.get(y).set(x + 1, adjacentMap.get(y).get(x + 1) + 1);
            adjacentMap.get(y + 1).set(x + 1, adjacentMap.get(y + 1).get(x + 1) + 1);
            adjacentMap.get(y + 1).set(x, adjacentMap.get(y + 1).get(x) + 1);
            adjacentMap.get(y + 1).set(x - 1, adjacentMap.get(y + 1).get(x - 1) + 1);
            adjacentMap.get(y).set(x - 1, adjacentMap.get(y).get(x - 1) + 1);
            adjacentMap.get(y - 1).set(x - 1, adjacentMap.get(y - 1).get(x - 1) + 1);
            adjacentMap.get(y - 1).set(x, adjacentMap.get(y - 1).get(x) + 1);
            adjacentMap.get(y - 1).set(x + 1, adjacentMap.get(y - 1).get(x + 1) + 1);

        }



        ArrayList<ArrayList<String>> newMap = new ArrayList<>(map);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int numIntersections = adjacentMap.get(i).get(j);
                if (map.get(i).get(j).equals("1")) {
                    if (numIntersections == 0 || numIntersections == 1 || numIntersections > 3) {
                        newMap.get(i).set(j, "0");
                    }
                } else if (numIntersections == 3) {
                    newMap.get(i).set(j, "1");
                }
            }
        }



        return newMap;

    }

    // Pads the map with 1 layer of inactive cells
    public static ArrayList<ArrayList<String>> padMap(ArrayList<ArrayList<String>> map) {
        System.out.println("Padding map...");
        int rows = map.size();
        int cols = map.get(0).size();

        // Add row of 0s to top of map
        ArrayList<String> newRow = new ArrayList<>();
        for (int i = 0; i < cols; i++) {
            newRow.add("0");
        }
        map.add(0, newRow);

        // Add row of 0s to bottom of map
        map.add(new ArrayList<String>(newRow));

        // Wrap each row in 0s
        for (int i = 0; i < rows + 2; i++) {
            map.get(i).add(0, "0");
            map.get(i).add("0");
        }

        return map;
    }

    // Method to remove border of inactive cells from map (of thickness n)
    public static ArrayList<ArrayList<String>> shrinkMap(ArrayList<ArrayList<String>> map, int n) {
        System.out.println("Shrinking map...");


        for (int i = 0; i < n; i++) {
            // Remove first and last row
            map.remove(0);
            map.remove(map.size() - 1);

            // Remove 0s on side of body
            for (ArrayList<String> row : map) {
                row.remove(0);
                row.remove(row.size() - 1);
            }
        }

        return map;
    }

    // Method to resize map and contain location of active cells
    public static Pair<ArrayList<Point>, ArrayList<ArrayList<String>>> resizeMap(ArrayList<ArrayList<String>> map) {
        int rows = map.size();
        int cols = map.get(0).size();
        ArrayList<Point> liveCells = new ArrayList<>();


        // Scan map and populate liveCells ArrayList
        int minI = cols - 1;
        int maxI = 0;
        int minJ = rows - 1;
        int maxJ = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (map.get(i).get(j).equals("1")) {
                    minI = Math.min(minI, i);
                    maxI = Math.max(maxI, i);
                    minJ = Math.min(minJ, j);
                    maxJ = Math.max(maxJ, j);
                    liveCells.add(new Point(j, i));
                }
            }
        }

        // Determine if map resizing (padding/shrinking) is required and if so, do so
        if (liveCells.size() != 0) {
            if (minI == 0 || maxI == rows - 1 || minJ == 0 || maxJ == cols - 1) {
                map = padMap(map);
                for (Point p : liveCells) {
                    p.translate(1, 1);
                }
            } else if (minI > 1 && maxI < rows - 2 && minJ > 1 && maxJ < cols - 2) {
                int n = Math.min(Math.min(minI - 1, rows - maxI - 2), Math.min(minJ - 1, cols - maxJ - 2));
                map = shrinkMap(map, n);
                for (Point p : liveCells) {
                    p.translate(-n, -n);
                }
            }
        }


        return new Pair(liveCells, map);
    }

    // Encodes map to a unique string representation so that the map representation is immutable
    public static String encodeMap(ArrayList<ArrayList<String>> map) {
        StringBuilder sb = new StringBuilder();
        for (ArrayList<String> row : map) {
            sb.append(row.toString());
        }
        return sb.toString();
    }


    // Heterogenous Pair container class used for resizeMap method
    private static class Pair<T, U> {
        T first;
        U second;

        Pair(T first, U second) {
            this.first = first;
            this.second = second;
        }

        T getFirst() {
            return this.first;
        }

        U getSecond() {
            return this.second;
        }
    }



}
