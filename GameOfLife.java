import java.util.*;
import java.io.*;
import java.awt.Point;

public class GameOfLife {

    public static void main(String[] args) throws FileNotFoundException {

        if (args.length == 0) {
            System.out.println("Please specify a file to read: java GameOfLife /path/to/file");
            return;
        }

        ArrayList<ArrayList<String>> map = new ArrayList<>();

        Scanner sc = new Scanner(new BufferedReader(new FileReader(args[0])));

        while (sc.hasNextLine()) { // Need to check grid is square
            ArrayList<String> string_row = new ArrayList<>(Arrays.asList(sc.nextLine().split(" ")));
            map.add(string_row);
        }

        map = padMap(map);

        System.out.println("Initial map:\n");
        printMap(map);

        System.out.println("After parse 1:\n");
        map = generateNextState(map);
        printMap(map);
    }


    public static void printMap(ArrayList<ArrayList<String>> map) {

        int rows = map.size();
        int cols = map.get(0).size();

        if (rows == 0 || cols == 0)
            return;



        // Draw map top
        // System.out.print("0  ");
        // for (int i = 0; i < cols + 1; i++) {
        //     System.out.print("0  ");
        // }
        // System.out.println("");


        // Draw map body
        for (ArrayList<String> row : map) {
            // System.out.println(row.toString());


            // System.out.print("0  ");
            for (String cell : row) {
                System.out.print(cell + "  ");
            }
            // System.out.print("0");
            System.out.println("");
        }

        // Draw map bottom
        // System.out.print("0  ");
        // for (int i = 0; i < cols + 1; i++) {
        //     System.out.print("0  ");
        // }
        System.out.println("\n");
    }


    public static ArrayList<ArrayList<String>> generateNextState(ArrayList<ArrayList<String>> map) {
        int rows = map.size();
        int cols = map.get(0).size();
        System.out.println(rows);
        System.out.println(cols);
        boolean hasPaddedMap = false;
        ArrayList<Point> liveCells = new ArrayList<>();


        // Scan map and populate liveCells ArrayList
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                System.out.println(map.get(i).get(j));
                if (map.get(i).get(j).equals("1")) {
                    liveCells.add(new Point(j, i));
                    if (!hasPaddedMap && (i == 1 || i == cols - 1 || j == 1 || j == rows - 1)) {
                        map = padMap(map);
                        rows = map.size();
                        cols = map.get(0).size();
                        i = 0;
                        j = 0;
                        hasPaddedMap = true;
                        liveCells.clear();
                    }
                }
            }
        }
        for (Point p : liveCells) {
            System.out.println(p);
        }

        printMap(map);

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

            for (ArrayList<Integer> row : adjacentMap) {
                System.out.println(row);
            }
            System.out.println("");
        }



        ArrayList<ArrayList<String>> newMap = new ArrayList<>(map);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int numIntersections = adjacentMap.get(i).get(j);
                if (map.get(i).get(j) == "1") {
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
        System.out.println("padding map:");
        printMap(map);
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



}
