import java.util.*;
import java.io.*;
import java.awt.Point;
import java.math.BigInteger;

public class GameOfLife {

    public static void main(String[] args) throws FileNotFoundException {

        if (args.length != 2) {
            System.out.println("Please specify a file to read and a number of iterations: java GameOfLife /path/to/file n");
            return;
        }

        ArrayList<ArrayList<String>> map = new ArrayList<>();

        Scanner sc = new Scanner(new BufferedReader(new FileReader(args[0])));

        while (sc.hasNextLine()) { // Need to check grid is square
            ArrayList<String> string_row = new ArrayList<>(Arrays.asList(sc.nextLine().split(" ")));
            map.add(string_row);
        }

        map = resizeMap(map).getSecond();

        System.out.println("Initial map:\n");
        printMap(map);

        BigInteger n = BigInteger.valueOf(Integer.parseInt(args[1]));
        n = n.mod(choose((map.size() - 2) * (map.get(0).size() - 2), 2));
        System.out.println(n);
        System.out.println(choose((map.size() - 2) * (map.get(0).size() - 2), 2));



        driver(map, n);



    }

    public static void driver(ArrayList<ArrayList<String>> map, BigInteger n) {
        BigInteger i = BigInteger.ZERO;
        int comparison = i.compareTo(n);

        while (comparison == -1) {
            i = i.add(BigInteger.ONE);
            comparison = i.compareTo(n);
            ArrayList<ArrayList<String>> newMap = generateNextState(map);
            if (newMap.equals(map)) {
                break;
            } else {
                map = generateNextState(map);
            }

            printMap(map);
        }

        System.out.println("Final map:");
        printMap(resizeMap(map).getSecond());
    }


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

            System.out.println("");
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

        if (liveCells.size() != 0) {
            if (minI == 0 || maxI == rows - 1 || minJ == 0 || maxJ == cols - 1) {
                map = padMap(map);
                for (Point p : liveCells) {
                    p.translate(1, 1);
                }
            } else if (minI > 1 && maxI < rows - 2 && minJ > 1 && maxJ < cols - 2) {
                System.out.println(minI);
                System.out.println(maxI);
                System.out.println("");
                System.out.println(rows);
                System.out.println(cols);
                System.out.println("");
                System.out.println(minJ);
                System.out.println(maxJ);
                int n = Math.min(Math.min(minI - 1, rows - maxI - 2), Math.min(minJ - 1, cols - maxJ - 2));
                System.out.println("======" + Integer.toString(n));
                map = shrinkMap(map, n);
                for (Point p : liveCells) {
                    p.translate(-n, -n);
                }
            }
        }


        return new Pair(liveCells, map);
    }


    // choose function taken from https://stackoverflow.com/a/2929897
    public static BigInteger choose(int N, int K) {
        BigInteger ret = BigInteger.ONE;
        for (int k = 0; k < K; k++) {
            ret = ret.multiply(BigInteger.valueOf(N-k))
                     .divide(BigInteger.valueOf(k+1));
        }
        return ret;
    }


    // heterogenous Pair container class used for resizeMap method
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
