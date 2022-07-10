import java.io.*;
import java.util.Scanner;
public class TopScores {


    private static final String FILE_NAME = "top2048.txt";
    public static final int MAX_SCORES = 5;
    public Score[] table = new Score[MAX_SCORES];
    public int rows = 0;
    public TopScores() { restoreFromFile(); }


    public Score getRow(int idx) { return table[idx]; }

    public void getNumOfRows() {
        for (int i = 0; i < MAX_SCORES; ++i) {
            if (table[i] != null)
                ++rows;
        }
    }

    public boolean canAdd(int score) {
        if(rows < MAX_SCORES) return true;
        for(int i=0; i <= rows ;++i)
            return (score>table[i].points);
        return false;
    }

    public void addRow(String name, int score) {
        for (; rows > 0 && score < table[rows-1].points ;--rows) {
            table[rows] = table[rows - 1];
            Panel.updateBestRow(rows, table[rows].name, table[rows].points);
        }  table[rows] = new Score(name,score);
        Panel.updateBestRow(1, table[rows].name, table[rows].points);
        ++rows;
    }

    private void restoreFromFile() {
        try {
            Scanner in = new Scanner(new FileReader(FILE_NAME));
            for (; in.hasNextLine(); ++rows) {
                int points = in.nextInt();
                String name = in.nextLine().trim();
                table[rows] = new Score(name, points);
            }
            in.close();
        } catch (Exception e) {
            System.out.println("Error reading file " + FILE_NAME);
        }
    }

    public void saveToFile() {
        try {
            PrintWriter out = new PrintWriter(new FileWriter(FILE_NAME));
            for (int i = 0; i < rows; i++) {
                out.println(table[i].points + " " + table[i].name);
            }
            out.close();
        } catch (Exception e) {
            System.out.println("Error writing file " + FILE_NAME);
        }
    }
}