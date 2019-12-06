import java.util.*;
import java.io.*;

public class GameOfLife {

    public static void main(String[] args) throws FileNotFoundException {

        if (args.length == 0) {
            System.out.println("Please specify a file to read: java GameOfLife /path/to/file");
            return;
        }

        ArrayList<ArrayList<Integer>> map = new ArrayList<>();

        Scanner sc = new Scanner(new BufferedReader(new FileReader(args[0])));

        while (sc.hasNextLine()) { // Need to check grid is square
            List<String> string_row = Arrays.asList(sc.nextLine().split(" "));
            ArrayList<Integer> int_row = new ArrayList<>();
            for (String cell : string_row) {
                int_row.add(Integer.parseInt(cell));
            }
            map.add(int_row);
        }

        System.out.println("Initial map:\n");
        printMap(map);
    }

    public static void printMap(ArrayList<ArrayList<Integer>> map) {

        int rows = map.size();
        int cols = map.get(0).size();

        if (rows == 0 || cols == 0)
            return;

        // Draw top
        for (int i = 0; i < cols + 1; i++) {
            System.out.print("0|");
        }
        System.out.print("0");
        System.out.println("");


        // Draw map body
        for (ArrayList<Integer> row : map) {
            // System.out.println(row.toString());
            for (int i = 0; i < cols + 2; i++) {
                System.out.print("--");
            }
            System.out.println("");

            System.out.print("0");
            boolean wasLive = false;
            for (Integer cell : row) {
                System.out.print("|" + Integer.toString(cell));
                wasLive = (cell == 1) ? true : false;
            }
            System.out.print("|");
            System.out.print("0");
            System.out.println("");
        }

        for (int i = 0; i < cols + 2; i++) {
            System.out.print("--");
        }
        System.out.println("");


        for (int i = 0; i < cols + 1; i++) {
            System.out.print("0|");
        }
        System.out.print("0");
        System.out.println("");
    }

}
