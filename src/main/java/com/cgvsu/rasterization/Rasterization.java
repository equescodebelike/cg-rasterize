package com.cgvsu.rasterization;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;


public class Rasterization {

    /**
     * Метод отрисовки круга по алгоритму Брезенхэма (без заполнения)
     */

    public static void drawCircle(
            final GraphicsContext graphicsContext,
            int center_x, int center_y, // центр окружности (х,у)
            int radius, // радиус окружности
            final Color color) { // цвет контура

        final PixelWriter pixelWriter = graphicsContext.getPixelWriter();

        int x = 0;
        int y = radius;
        int delta = calculateStartDelta(radius);

        while (y >= x) { // прошли 45 градусов
            pixelWriter.setColor(center_x + x, center_y + y, color); // делим круг на октанты (45 градусов)
            pixelWriter.setColor(center_x + x, center_y - y, color);
            pixelWriter.setColor(center_x - x, center_y + y, color);
            pixelWriter.setColor(center_x - x, center_y - y, color);

            pixelWriter.setColor(center_x + y, center_y + x, color);
            pixelWriter.setColor(center_x - y, center_y + x, color);
            pixelWriter.setColor(center_x + y, center_y - x, color);
            pixelWriter.setColor(center_x - y, center_y - x, color);

            if (delta < 0) {
                delta = calculateDeltaForHorizontalPixel(delta, x); // выбираем пиксель (x+1,y)
            } else {
                delta = calculateDeltaForDiagonalPixel(delta, x, y); // выбираем пиксель (x+1,y-1)
                y--;
            }
            x++;
        }

    }

    /**
     * Метод отрисовки и интерполяции сектора окружности
     */

    public static void drawSector(
            final GraphicsContext graphicsContext,
            int center_x, int center_y, // центр окружности (х,у)
            int sectstartx, int sectstarty, // точка начала сектора, от центра окружности проводится вектор к ней
            int sectendx, int sectendy, // точка конца сектора, аналогично начальной точке
            int radius, // радиус окружности
            Color c0, Color c1) { // заданные цвета для интерполяции

        final PixelWriter pixelWriter = graphicsContext.getPixelWriter();

        for (int x = -radius; x <= radius; x++) { // алгоритм заполнения круга, выбираем только те точки, которые находятся в секторе
            int height = (int) Math.sqrt(radius * radius - x * x); // без проверки радиуса, наиболее эффективный метод

            for (int y = -height; y <= height; y++)
                if (isInsideSector(x + center_x, y + center_y, center_x, center_y, sectstartx, sectstarty, sectendx, sectendy, radius)) {
                    //pixelWriter.setColor(x + centx, y + centy, c0);
                    int circle_x = findCX(radius, center_x, center_y, x, y);
                    int circle_y = findCY(radius, center_x, center_y, x, y);
                    Color color = calcColor(c0, c1, center_x, center_y, circle_x, circle_y, x + center_x, y + center_y);
                    pixelWriter.setColor(x + center_x, center_y + y, color);
                    System.out.print(x + " ");
                    System.out.println(y);
                }
        }
    }

    public static void drawSector2(
            final GraphicsContext graphicsContext,
            int center_x, int center_y, // центр окружности (х,у)
            int sectstartx, int sectstarty, // точка начала сектора, от центра окружности проводится вектор к ней
            int sectendx, int sectendy, // точка конца сектора, аналогично начальной точке
            int radius, // радиус окружности
            Color c0) {
        int x = radius;
        int y = 0;
        int xChange = 1 - (radius << 1);
        int yChange = 0;
        int radiusError = 0;
        final PixelWriter pixelWriter = graphicsContext.getPixelWriter();

        while (x >= y) {
            for (int i = center_x - x; i <= center_x + x; i++) {
                if (isInsideSector(i, center_y + y, center_x, center_y, sectstartx, sectstarty, sectendx, sectendy, radius)) {
                    pixelWriter.setColor(i, center_y + y, c0);
                }
                if (isInsideSector(i, center_y - y, center_x, center_y, sectstartx, sectstarty, sectendx, sectendy, radius)) {
                    pixelWriter.setColor(i, center_y - y, c0);
                }
            }
            for (int i = center_x - y; i <= center_x + y; i++) {
                if (isInsideSector(i, center_y + x, center_x, center_y, sectstartx, sectstarty, sectendx, sectendy, radius)) {
                    pixelWriter.setColor(i, center_y + x, c0);
                }
                if (isInsideSector(i, center_y - x, center_x, center_y, sectstartx, sectstarty, sectendx, sectendy, radius)) {
                    pixelWriter.setColor(i, center_y - x, c0);
                }
            }
            y++;
            radiusError += yChange;
            yChange += 2;
            if (((radiusError << 1) + xChange) > 0) {
                x--;
                radiusError += xChange;
                xChange += 2;
            }
        }
    }


    private static Color calcColor(Color c0, Color c1, int x0, int y0, int x1, int y1, int x, int y) { // метод для вычисления цвета в конкретной точке
        // (x0,y0) - центр окружности, (x1,y1) - точка, лежащая на окружности, (x,y) - произвольная точка
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

    private static int findCX(int radius, int x0, int y0, int xa, int ya) { // ближайшая точка на окружности координата x
        // (x0,y0) - центр окружности, (xa,ya) - произвольная точка
        return (int) (x0 + ((radius * (xa - x0)) / (Math.sqrt((int) (Math.pow(xa - x0, 2) + (int) Math.pow(ya - y0, 2))))));
    }

    private static int findCY(int radius, int x0, int y0, int xa, int ya) { // ближайшая точка на окружности координата y
        // (x0,y0) - центр окружности, (xa,ya) - произвольная точка
        return (int) (y0 + ((radius * (ya - y0)) / (Math.sqrt((int) (Math.pow(xa - x0, 2) + (int) Math.pow(ya - y0, 2))))));
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

    /* private static boolean areLeft(int v1x, int v1y, int v2x, int v2y) { // вектор лежит против часовой стрелки
        return -v1x * v2y + v1y * v2x > 0; // ==0 будет контур границ сектора
        // return v1y * v2x - v1x * v2y > 0;
    }
    */


    private static boolean isWithinRadius(int x, int y, int radius) {
        return x * x + y * y <= radius * radius; // проверка нахождения точки вне предела радиуса круга
    }

    private static boolean isInsideSector( // проверка нахождения точки внутри сектора
                                           int x, int y, // координаты точки для проверки
                                           int center_x, int center_y, // центр окружности (х,у)
                                           int sectstartx, int sectstarty, // точка начала сектора, от центра окружности проводится вектор к ней
                                           int sectendx, int sectendy, // точка конца сектора, аналогично начальной точке
                                           int radius) { // радиус окружности

        int relpointx = x - center_x;
        int relpointy = y - center_y;

        // return (!areLeft(sectstartx,sectstarty,relpointx,relpointy) && areLeft(sectendx,sectendy,relpointx,relpointy));
        return (sectstartx * relpointy - relpointx * sectstarty > 0 && relpointx * sectendy - sectendx * relpointy > 0);
        //return ((x - center_x) * (sectstarty - center_y) - (y - center_y) * (sectstartx - center_x)) > 0 && ((x - center_x) * (sectendy - center_y) - (y - center_y) * (sectendx - center_x)) > 0;
        // return ((sectstartx - center_x) * (x - center_x) + (sectstarty - center_y) * (y - center_y)) > 0 && ((sectendx - center_x) * (x - center_x) + (sectendy - center_y) * (y - center_y)) > 0;
       /* if (areLeft(sectstartx, sectstarty, sectendx, sectendy)) {
            return !(!areLeft(sectstartx, sectstarty, relpointx, relpointy) &&
                    areLeft(sectendx, sectendy, relpointx, relpointy)) &&
                    isWithinRadius(relpointx, relpointy, radius * radius);
        } else {
            return !areLeft(sectstartx, sectstarty, relpointx, relpointy) &&
                    areLeft(sectendx, sectendy, relpointx, relpointy) &&
                    isWithinRadius(relpointx, relpointy, radius * radius);
        } */

    }

}
