import isel.leic.pg.Console;
import java.awt.event.KeyEvent;
import static isel.leic.pg.Console.sleep;

public class Game2048 {

    static final int LINES = 4, COLS = 4;
    static final int MIN_VALUE = 2, MAX_VALUE = 2048;
    private static TopScores top = new TopScores();
    private static int moves = 0, key;
    public static int score = 0, resp;
    private static boolean exit = false, canMove;
    private static final int[][] grid = new int[LINES][COLS];
    private static final boolean[][] addedGrid = new boolean[LINES][COLS];
    public static String name;
    public static void main(String[] args) {


        Panel.open();
        init();
        System.out.println(top.rows);
        for (;;) {
            key = Console.waitKeyPressed(0);
            if (!processKey(key)) break;
            while (Console.isKeyPressed(key)) ;
            endgame();
        }
        Panel.close();
        top.saveToFile();
    }

    private static void scoreUpdate(int value) {
        score = score + value;
        Panel.updateScore(score);
    }

    private static void allFalse() {
        for (int i = 0; i < LINES; ++i) {
            for (int j = 0; j < COLS; ++j) {
                addedGrid[i][j] = false;
            }
        }
    }

    private static void randomPiece() {
        int li, co, randomValue = (Math.random() > 0.9 ? 4 : 2);
        do {
            li = (int) (Math.random() * LINES);
            co = (int) (Math.random() * COLS);
        } while (!isEmptyTile(li, co));
        Panel.showTile(li, co, randomValue);
        grid[li][co] = randomValue;
    }

    private static boolean isEmptyTile(int lin, int col) {
        return (grid[lin][col] == 0);
    }

    private static boolean possibleMoves() {
        for (int i = 0; i < LINES; i++) {
            for (int j = 0; j < COLS; j++) {
                if (j < COLS - 1) {
                    if (grid[i][j] == grid[i][j + 1]) {
                        return true;
                    }
                }
                if (i < LINES - 1) {
                    if (grid[i][j] == grid[i + 1][j]) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    private static void endgame() {
        int counter = 0;
        for (int i = 0; i < LINES; ++i) {
            for (int j = 0; j < COLS; ++j) {
                if (grid[i][j] != 0) ++counter;
                if (grid[i][j] == MAX_VALUE || counter == (LINES * COLS) && !possibleMoves()) {
                    Panel.showMessage(grid[i][j] == MAX_VALUE ? "YOU WIN Score= " + score : "YOU LOSE Score= " + score);
                    sleep(3000);
                    restartNewGame();
                }
            }
        }
    }


    private static void quitGame() {
        int resp = Panel.questionChar("Exit game (Y/N)");
        if (resp == 'Y')
            exit = true;
        askName();
        key = 0;
    }

    private static void askName() {
        if (top.canAdd(score)) {
            name = Panel.questionName("Name");
            top.addRow(name, score);
        }
    }

    private static void restart() {
        int resp = Panel.questionChar("New Game?(Y/N)");
        if (resp == 'Y') {
            for (int i = 0; i < LINES; ++i) {
                for (int j = 0; j < COLS; ++j) {
                    grid[i][j] = 0;
                    Panel.hideTile(i, j);
                }
            }
            askName();
            init();
        }

    }


    private static void restartNewGame() {
        resp = Panel.questionChar("New Game?(Y/N)");
        if (resp == 'Y') {
            for (int i = 0; i < LINES; ++i) {
                for (int j = 0; j < COLS; ++j) {
                    grid[i][j] = 0;
                    Panel.hideTile(i, j);
                }
            }
            askName();
            init();
        }else Console.close();
    }



    private static void init() {
       /* for (int i = 1; i <= top.getNumOfRows(); ++i){
            Panel.updateBestRow(i, top.table[i-1].name, top.table[i-1].points);
        }*/
        randomPiece();
        randomPiece();
        Panel.showMessage("Use cursor keys to play");
        moves = 0;
        score=0;
        Panel.updateMoves(moves);
        Panel.updateScore(score);
    }


    private static boolean processKey(int key) {
        switch (key) {
            case KeyEvent.VK_RIGHT: { slideRight(); update(); } break;
            case KeyEvent.VK_LEFT: { slideLeft(); update();} break;
            case KeyEvent.VK_UP: { slideUp(); update(); } break;
            case KeyEvent.VK_DOWN: { slideDown(); update(); } break;
            case KeyEvent.VK_ESCAPE:
            case 'Q': quitGame(); break;
            case 'N': restart(); break;
        }
        return !exit;
    }

    /*  Os métodos slide procuram os lugares do tabuleiro que não estão vazios para as poder deslizar.
     *  Os métodos move movem as peças escolhidas pelos métodos slide na direção escolhida pelo utilizador
     */

    private static void update() {
        if (canMove) {
            randomPiece();
            Panel.updateMoves(++moves);
        }
    }

    private static void slideRight() {
        canMove = false;
        allFalse();
        for (int c = COLS - 2; c >= 0; --c) {
            for (int l = 0; l < LINES; ++l) {
                int dcol;
                if (grid[l][c] != 0) {
                    dcol = c + 1;
                    moveVertical(l, c, dcol);
                }
            }
        }
    }

    private static void slideLeft() {
        canMove = false;
        allFalse();
        for (int c = 1; c <= COLS - 1; ++c) {
            for (int l = 0; l < LINES; ++l) {
                int dcol;
                if (grid[l][c] != 0) {
                    dcol = c - 1;
                    moveVertical(l, c, dcol);
                }
            }
        }
    }

    private static void moveVertical(int l, int c, int dcol) {
        int col = c; // variável que localiza o valor inicial da peça
        do {
            sleep(40);
            if (grid[l][dcol] == 0) {
                grid[l][dcol] = grid[l][col];
                grid[l][col] = 0;
                Panel.hideTile(l, col);
                Panel.showTile(l, dcol, grid[l][dcol]);
                canMove = true;
            } else if (grid[l][col] == grid[l][dcol]) {
                if (!addedGrid[l][dcol]) {
                    grid[l][dcol] = grid[l][dcol] * 2;
                    scoreUpdate(grid[l][dcol]);
                    grid[l][col] = 0;
                    Panel.hideTile(l, col);
                    Panel.showTile(l, dcol, grid[l][dcol]);
                    canMove = true;
                    addedGrid[l][dcol] = true;
                    break;
                }else break;
            }else if (grid[l][dcol] != 0 && grid[l][col] != grid[l][dcol]) break;
            col = key == KeyEvent.VK_LEFT?  col-1 : col+1;
            dcol = key == KeyEvent.VK_LEFT?  dcol-1 : dcol+1;
        } while (key == KeyEvent.VK_LEFT? dcol >= 0 : dcol <= 3);
    }


    private static void slideUp() {
        canMove = false;
        allFalse();
        for (int c = 0; c < COLS; ++c) {
            for (int l = 1; l <= LINES - 1; ++l) {
                int dlin;
                if (grid[l][c] != 0) {
                    dlin = l - 1;
                    moveHorizontal(l, c, dlin);
                }
            }
        }
    }

    private static void slideDown () {
        canMove = false;
        allFalse();
        for (int c = 0; c < COLS; ++c) {
            for (int l = LINES - 2; l >= 0; --l) {
                int dlin;
                if (grid[l][c] != 0) {
                    dlin = l + 1;
                    moveHorizontal(l, c, dlin);
                }
            }
        }
    }

    private static void moveHorizontal(int l, int c, int dlin) {
        int lin = l; // variável que localiza o valor inicial da peça
        do {
            sleep(40);
            if (grid[dlin][c] == 0) {
                grid[dlin][c] = grid[lin][c];
                grid[lin][c] = 0;
                Panel.hideTile(lin, c);
                Panel.showTile(dlin, c, grid[dlin][c]);
                canMove = true;
            } else if (grid[lin][c] == grid[dlin][c]) {
                if (!addedGrid[dlin][c]) {
                    grid[dlin][c] = grid[lin][c] * 2;
                    scoreUpdate(grid[dlin][c]);
                    Panel.hideTile(lin, c);
                    Panel.showTile(dlin, c, grid[dlin][c]);
                    grid[lin][c] = 0;
                    canMove = true;
                    addedGrid[dlin][c] = true;
                    break;
                }else break;
            } else if (grid[dlin][c] != 0 && grid[lin][c] != grid[dlin][c]) break;
            lin = key == KeyEvent.VK_UP? lin-1 : lin+1;
            dlin = key == KeyEvent.VK_UP? dlin - 1 : dlin + 1;
        } while (key == KeyEvent.VK_UP? dlin >= 0 : dlin <= 3);
    }
}