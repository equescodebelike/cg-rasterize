package com.cgvsu.rasterization;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;


public class Rasterization {

    public static void drawRectangle(
            final GraphicsContext graphicsContext,
            final int x, final int y,
            final int width, final int height,
            final Color color) {
        final PixelWriter pixelWriter = graphicsContext.getPixelWriter();

        for (int row = y; row < y + height; ++row)
            for (int col = x; col < x + width; ++col)
                pixelWriter.setColor(col, row, color);
    }

    public static void drawCircle(
            final GraphicsContext graphicsContext,
            int centx, int centy,
            int radius,
            final Color color) {

        final PixelWriter pixelWriter = graphicsContext.getPixelWriter();

        int x = 0;
        int y = radius;
        int delta = calculateStartDelta(radius);

        while (y >= x) {
            pixelWriter.setColor(centx + x, centy + y, color);
            pixelWriter.setColor(centx + x, centy - y, color);
            pixelWriter.setColor(centx - x, centy + y, color);
            pixelWriter.setColor(centx - x, centy - y, color);

            pixelWriter.setColor(centx + y, centy + x, color);
            pixelWriter.setColor(centx - y, centy + x, color);
            pixelWriter.setColor(centx + y, centy - x, color);
            pixelWriter.setColor(centx - y, centy - x, color);

            if (delta < 0) {
                delta = calculateDeltaForHorizontalPixel(delta, x);
            } else {
                delta = calculateDeltaForDiagonalPixel(delta, x, y);
                y--;
            }
            x++;
        }

        /* for (int j = -radius; j < radius; j++) {
            int hh = (int) Math.sqrt(radius * radius - j * j);
            int rx = centx + j;
            int ph = centy + hh;

            for (int i = centy - hh; i < ph; i++)
                pixelWriter.setColor(rx, i, color);

        } */
    }

    public static void drawSectorInt(
            final GraphicsContext graphicsContext,
            int centx, int centy,
            int sectstartx, int sectstarty,
            int sectendx, int sectendy,
            int radius,
            Color c1, Color c2) {

        int startR = (int) c1.getRed();
        int startG = (int) c1.getGreen();
        int startB = (int) c1.getBlue();
        int endR = (int) c2.getRed();
        int endG = (int) c2.getGreen();
        int endB = (int) c2.getBlue();

        final PixelWriter pixelWriter = graphicsContext.getPixelWriter();

        double path = (int) Math.sqrt(Math.pow(sectendx - sectstartx, 2) + Math.pow(sectendy - sectstarty, 2));
        double pathpassed;
        double pathRatio;
        Color currColor = c1;

        for (int j = -radius; j < radius; j++) {
            int hh = (int) Math.sqrt(radius * radius - j * j);
            int rx = centx + j;
            int ph = centy + hh;

            for (int i = centy - hh; i <= ph; i++)
                if (isInsideSector(rx, i, centx, centy, sectstartx, sectstarty, sectendx, sectendy, radius)) {
                    pixelWriter.setColor(rx, i, currColor);
                }

        }


    }

    public static void drawSector(
            final GraphicsContext graphicsContext,
            int centx, int centy,
            int sectstartx, int sectstarty,
            int sectendx, int sectendy,
            int radius,
            Color c1, Color c2, Color c3) {

        double r1 = c1.getRed();
        double g1 = c1.getGreen();
        double b1 = c1.getBlue();
        double r2 = c2.getRed();
        double g2 =  c2.getGreen();
        double b2 =  c2.getBlue();
        double r3 =  c3.getRed();
        double g3 =  c3.getGreen();
        double b3 =  c3.getBlue();

        final PixelWriter pixelWriter = graphicsContext.getPixelWriter();

       /* for (int i = 0; i < 8000; i++) {
            int px = (int) (Math.random() * 800);
            int py = (int) (Math.random() * 600);
            if (isInsideSector(px, py, centx, centy, sectstartx, sectstarty, sectendx, sectendy, radius)) {
                pixelWriter.setColor(px, py, color);
            }
        } */

        for (int j = -radius; j < radius; j++) {
            int hh = (int) Math.sqrt(radius * radius - j * j);
            int rx = centx + j;
            int ph = centy + hh;

            for (int i = centy - hh; i <= ph; i++)
                if (isInsideSector(rx, i, centx, centy, sectstartx, sectstarty, sectendx, sectendy, radius)) {
                    double detT = (sectstarty - sectendy) * (centx - sectendx) + (sectendx - sectstartx) * (centy - sectendy);
                    double alpha = ((sectstarty - sectendy) * (j - sectendx) + (sectendx - sectstartx) * (i - sectendy)) / detT;
                    double betta = ((sectendy - centy) * (j - sectendx) + (centx - sectendx) * (i - sectendy)) / detT;
                    double gamma = 1 - alpha - betta;
                    int r = (int) (alpha * r1 + betta * r2 + gamma * r3);
                    int g = (int) (alpha * g1 + betta * g2 + gamma * g3);
                    int b = (int) (alpha * b1 + betta * b2 + gamma * b3);
                    pixelWriter.setColor(rx, i, Color.rgb(r, g, b));
                }

        }


    }

    private static int calculateStartDelta(int radius) {
        return 3 - 2 * radius;
    }

    private static int calculateDeltaForHorizontalPixel(int oldDelta, int x) {
        return oldDelta + 4 * x + 6;
    }

    private static int calculateDeltaForDiagonalPixel(int oldDelta, int x, int y) {
        return oldDelta + 4 * (x - y) + 10;
    }

    private static boolean areClockwise(int v1x, int v1y, int v2x, int v2y) {
        return -v1x * v2y + v1y * v2x > 0;
    }

    private static boolean isWithinRadius(int x, int y, int radius) {
        return x * x + y * y <= radius * radius;
    }

    private static boolean isInsideSector(
            int px, int py,
            int centx, int centy,
            int sectstartx, int sectstarty,
            int sectendx, int sectendy,
            int radius) {

        int relpointx = px - centx;
        int relpointy = py - centy;

        return !areClockwise(sectstartx, sectstarty, relpointx, relpointy) && areClockwise(sectendx, sectendy, relpointx, relpointy) &&
                isWithinRadius(relpointx, relpointy, radius * radius);
    }

}
