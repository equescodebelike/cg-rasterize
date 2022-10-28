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
            Color c0, Color c1) {

        final PixelWriter pixelWriter = graphicsContext.getPixelWriter();

        double startR = c0.getRed();
        double startG = c0.getGreen();
        double startB = c0.getBlue();
        double endR = c1.getRed();
        double endG = c1.getGreen();
        double endB = c1.getBlue();

        // double path = (int) Math.sqrt(Math.pow())


        // int rx;
        // int ry;

        for (int x = -radius; x <= radius; x++) {
            int height = (int) Math.sqrt(radius * radius - x * x);

            for (int y = -height; y <= height; y++)
                if (isInsideSector(x + centx, y + centy, centx, centy, sectstartx, sectstarty, sectendx, sectendy, radius)) {
                    int x1 = findCX(radius, centx, centy, x, y);
                    // pixelWriter.setColor(x + centx, y + centy, color);
                }
        }


    }

    public static int findCX(int radius, int x0, int y0, int xa, int ya) {
        return (int) (x0 + ((radius * (xa - x0)) / (Math.sqrt((int) (Math.pow(xa - x0, 2) + (int) Math.pow(ya - y0, 2))))));
    }

    public static int findCY(int radius, int x0, int y0, int xa, int ya) {
        return (int) (y0 + ((radius * (ya - y0)) / (Math.sqrt((int) (Math.pow(xa - x0, 2) + (int) Math.pow(ya - y0, 2))))));
    }

    public static void drawSector(
            final GraphicsContext graphicsContext,
            int centx, int centy,
            int sectstartx, int sectstarty,
            int sectendx, int sectendy,
            int radius,
            Color c0, Color c1) {

        final PixelWriter pixelWriter = graphicsContext.getPixelWriter();

       /* for (int i = 0; i < 8000; i++) {
            int px = (int) (Math.random() * 800);
            int py = (int) (Math.random() * 600);
            if (isInsideSector(px, py, centx, centy, sectstartx, sectstarty, sectendx, sectendy, radius)) {
                pixelWriter.setColor(px, py, color);
            }
        } */

        /*for (int j = -radius; j < radius; j++) {
            int hh = (int) Math.sqrt(radius * radius - j * j);
            int rx = centx + j;
            int ph = centy + hh;

            for (int i = centy - hh; i <= ph; i++)
                if (isInsideSector(rx, i, centx, centy, sectstartx, sectstarty, sectendx, sectendy, radius)) {
                    pixelWriter.setColor(rx, i, color);
                }

        }*/

        for (int x = -radius; x <= radius; x++) {
            int height = (int) Math.sqrt(radius * radius - x * x);

            for (int y = -height; y <= height; y++)
                if (!isInsideSector(x + centx, y + centy, centx, centy, sectstartx, sectstarty, sectendx, sectendy, radius)) {
                    //pixelWriter.setColor(x + centx, y + centy, c0);
                    int x1 = findCX(radius, centx, centy, x, y);
                    int y1 = findCY(radius, centx, centy, x, y);
                    Color color = calcColor(c0, c1, centx, centy, x1, y1, x + centx, y + centy);
                    pixelWriter.setColor(x + centx, y + centy, color);
                }
        }
    }

    private static Color calcColor(Color c0, Color c1, int x0, int y0, int x1, int y1, int x, int y) {
        double startR = c0.getRed();
        double startG = c0.getGreen();
        double startB = c0.getBlue();
        double endR = c1.getRed();
        double endG = c1.getGreen();
        double endB = c1.getBlue();

        double ratio = Math.sqrt((Math.pow((x - x0), 2) + Math.pow((y - y0), 2)) / (Math.pow((x1 - x0), 2) + Math.pow((y1 - y0), 2)));
        double r = startR + (endR - startR) * ratio;
        double g = startG + (endG - startG) * ratio;
        double b = startB + (endB - startB) * ratio;

        return Color.color(r, g, b);
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
        return -v1x * v2y + v1y * v2x > 0; // ==0
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

        if (areClockwise(sectstartx, sectstarty, sectendx, sectendy)) {
            return !(!areClockwise(sectstartx, sectstarty, relpointx, relpointy) &&
                    areClockwise(sectendx, sectendy, relpointx, relpointy)) &&
                    isWithinRadius(relpointx, relpointy, radius * radius);
        } else {
            return !areClockwise(sectstartx, sectstarty, relpointx, relpointy) &&
                    areClockwise(sectendx, sectendy, relpointx, relpointy) &&
                    isWithinRadius(relpointx, relpointy, radius * radius);
        }

    }

}
