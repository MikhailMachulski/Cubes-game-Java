package com.company;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;

public class Controller {
    private static final int SIZE_OF_SQUARE = 60;
    private static final int UNIT_X = 9;
    private static final int UNIT_Y = UNIT_X;
    private static final int RESOLUTION_X = UNIT_X * SIZE_OF_SQUARE;
    private static final int RESOLUTION_Y = UNIT_Y * SIZE_OF_SQUARE;
    private static final int NUMBER_OF_CUBES = 3;
    private static final Color[] COLORS = {Color.GREEN, Color.RED, Color.YELLOW, Color.BLUE, Color.CYAN, Color.PINK, Color.MAGENTA};

    private View view;
    private Graphics graphics;
    private Color[][] cells = new Color[UNIT_X][UNIT_Y];
    private Set<Point> winningCells = new HashSet<>();
    private Point selectedCell;

    public void start() {
        view.create(RESOLUTION_X, RESOLUTION_Y);
        generateCubes();
        renderImage();
    }

    public void handleMouseClick(int mouseX, int mouseY) {
        int x = mouseX / SIZE_OF_SQUARE;
        int y = mouseY / SIZE_OF_SQUARE;
        Color cell = cells[x][y];
        if (cell == null) {
            if (selectedCell == null) {
                return;
            }
            cells[x][y] = cells[selectedCell.x][selectedCell.y];
            cells[selectedCell.x][selectedCell.y] = null;
            selectedCell = null;
            checkAllField();
            if (winningCells.isEmpty()) {
                generateCubes();
                checkAllField();
            }
        } else {
            selectedCell = new Point(x, y);
        }
        renderImage();
    }

    private void checkAllField() {
        winningCells.clear();
        for (int x = 0; x <= UNIT_X - 2; x++) {
            for (int y = 0; y <= UNIT_Y - 2; y++) {
                checkOneSquare(x, y);
            }
        }
        clearWinningCells();
    }

    private void clearWinningCells() {
        for (Point winningCell : winningCells) {
            cells[winningCell.x][winningCell.y] = null;
        }
    }

    private void checkOneSquare(int x, int y) {
        Color color = cells[x][y];
        if (color == null) {
            return;
        }
        for (int i = x; i <= x + 1; i++) {
            for (int j = y; j <= y + 1; j++) {
                if (!color.equals(cells[i][j])) {
                    return;
                }
            }
        }
        for (int i = x; i <= x + 1; i++) {
            for (int j = y; j <= y + 1; j++) {
                winningCells.add(new Point(i, j));
            }
        }
    }

    private int random(int max) {
        return (int) (Math.random() * max);
    }

    private Color getRandomColor() {
        return COLORS[random(COLORS.length)];
    }

    private void placeCube() {
        int x;
        int y;
        do {
            x = random(UNIT_X);
            y = random(UNIT_Y);
        } while (cells[x][y] != null);
        cells[x][y] = getRandomColor();
    }

    private void generateCubes() {
        for (int i = 0; i < NUMBER_OF_CUBES; i++) {
            placeCube();
        }
    }

    private void draw(int x, int y, BufferedImage image) {
        graphics.drawImage(image, x * SIZE_OF_SQUARE, y * SIZE_OF_SQUARE, null);
    }

    private void drawField() {
        for (int x = 0; x < UNIT_X; x++) {
            for (int y = 0; y < UNIT_Y; y++) {
                draw(x, y, createSquare());
            }
        }
    }

    private void drawCubes() {
        for (int x = 0; x < UNIT_X; x++) {
            for (int y = 0; y < UNIT_Y; y++) {
                if (cells[x][y] != null) {
                    draw(x, y, createCube(cells[x][y]));
                }
            }
        }
    }

    private void drawHighlightedCell() {
        draw(selectedCell.x, selectedCell.y, createSelectionHighlight());
    }

    private void renderImage() {
        BufferedImage image = new BufferedImage(RESOLUTION_X, RESOLUTION_Y, BufferedImage.TYPE_INT_RGB);
        graphics = image.getGraphics();
        drawField();
        drawCubes();
        if (selectedCell != null) {
            drawHighlightedCell();
        }
        view.setImage(image);
    }

    private BufferedImage createSelectionHighlight() {
        BufferedImage image = new BufferedImage(SIZE_OF_SQUARE, SIZE_OF_SQUARE, BufferedImage.TYPE_INT_ARGB);
        Graphics graphics = image.getGraphics();
        graphics.drawRect(1, 1, SIZE_OF_SQUARE - 3, SIZE_OF_SQUARE - 3);
        graphics.drawRect(2, 2, SIZE_OF_SQUARE - 5, SIZE_OF_SQUARE - 5);
        return image;
    }

    private BufferedImage createSquare() {
        BufferedImage image = new BufferedImage(SIZE_OF_SQUARE, SIZE_OF_SQUARE, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = image.getGraphics();
        graphics.drawRect(0, 0, SIZE_OF_SQUARE - 1, SIZE_OF_SQUARE - 1);
        return image;
    }

    private BufferedImage createCube(Color color) {
        int shift = 4;
        int sizeOfCube = SIZE_OF_SQUARE - shift * 2;
        BufferedImage image = new BufferedImage(SIZE_OF_SQUARE, SIZE_OF_SQUARE, BufferedImage.TYPE_INT_ARGB);
        Graphics graphics = image.getGraphics();
        graphics.setColor(color);
        graphics.fillRect(shift, shift, sizeOfCube, sizeOfCube);
        return image;
    }

    public void setView(View view) {
        this.view = view;
    }
}
