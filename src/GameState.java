package src;

import java.io.Serializable;

public class GameState implements Serializable {
    public int[][] tileState; // 0: unopened, 1: opened, 2: flag
    public int[][] tileNumber;
    public int tilesClicked;
    public int numOfPlantedFlags;
    public boolean gameOver;

    public GameState(Minesweeper ms) {
        int rows = Level.getNumRows(), cols = Level.getNumCols();
        tileState = new int[rows][cols];
        tileNumber = new int[rows][cols];
        for (int r = 0; r < rows; ++r) {
            for (int c = 0; c < cols; ++c) {
                MineTile t = ms.mainBoard[r][c];
                if (!t.isEnabled()) {
                    tileState[r][c] = 1; // opened
                } else if (t.getIcon() == Display.flagIcon) {
                    tileState[r][c] = 2; // flag
                } else {
                    tileState[r][c] = 0; // unopened
                }
                int number = -1;
                for (int i = 1; i <= 8; ++i) {
                    if (t.getIcon() == Display.numberIcons[i]) {
                        number = i;
                        break;
                    }
                }
                tileNumber[r][c] = number;
            }
        }
        tilesClicked = ms.tilesClicked;
        numOfPlantedFlags = ms.numOfPlantedFlags;
        gameOver = ms.gameOver;
    }

    public void restore(Minesweeper ms) {
        int rows = Level.getNumRows(), cols = Level.getNumCols();
        for (int r = 0; r < rows; ++r) {
            for (int c = 0; c < cols; ++c) {
                MineTile t = ms.mainBoard[r][c];
                if (tileState[r][c] == 1) { // opened
                    t.setEnabled(false);
                    int number = tileNumber[r][c];
                    if (number > 0) {
                        t.setIcon(Display.numberIcons[number]);
                        t.setDisabledIcon(Display.numberIcons[number]);
                    } else {
                        t.setIcon(Display.nullIcon);
                        t.setDisabledIcon(Display.nullIcon);
                    }
                } else if (tileState[r][c] == 2) { // flag
                    t.setEnabled(true);
                    t.setIcon(Display.flagIcon);
                } else { // unopened
                    t.setEnabled(true);
                    t.setIcon(Display.unclickedIcon);
                }
            }
        }
        ms.tilesClicked = tilesClicked;
        ms.numOfPlantedFlags = numOfPlantedFlags;
        ms.gameOver = gameOver;
        ms.textUpdate();
    }
}
