/*package com.cgvsu.rasterization;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

public class Circle { class realisation?

    public Circle(
            GraphicsContext graphicsContext,
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


}
*/