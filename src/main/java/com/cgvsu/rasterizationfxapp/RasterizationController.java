package com.cgvsu.rasterizationfxapp;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.AnchorPane;

import com.cgvsu.rasterization.*;
import javafx.scene.paint.Color;

public class RasterizationController {

    @FXML
    AnchorPane anchorPane;
    @FXML
    private Canvas canvas;

    @FXML
    private void initialize() {
        anchorPane.prefWidthProperty().addListener((ov, oldValue, newValue) -> canvas.setWidth(newValue.doubleValue()));
        anchorPane.prefHeightProperty().addListener((ov, oldValue, newValue) -> canvas.setHeight(newValue.doubleValue()));

        Rasterization.drawCircle(canvas.getGraphicsContext2D(), 200, 200, 150, Color.BLACK);
        //Rasterization.drawSector(canvas.getGraphicsContext2D(), 200, 200, 1, 4, -1, 4, 150, Color.BLUE, Color.MAGENTA);
        //Rasterization.drawSector(canvas.getGraphicsContext2D(), 200, 200, -1, 2, -10, 10, 150, Color.BLUE, Color.MAGENTA);
        //Rasterization.drawSector(canvas.getGraphicsContext2D(), 200, 200, 4, 1, 1, 4, 150, Color.BLUE, Color.MAGENTA);
        //Rasterization.drawSector(canvas.getGraphicsContext2D(), 200, 200, -4, -1, 1, -4, 150, Color.BLUE, Color.MAGENTA);
        Rasterization.drawSector(canvas.getGraphicsContext2D(), 200, 200, -4, 0, 1, 3, 150, Color.BLUE, Color.MAGENTA, true);
        //Rasterization.drawSector2(canvas.getGraphicsContext2D(), 200, 200, 0, -4, -1, -4, 150, Color.BLUE);
        // если больше 180 градусов startx++, sectendy--
        //Rasterization.drawSector(canvas.getGraphicsContext2D(), 200, 200, -1, 4, 4, -11, 150, Color.BLUE, Color.MAGENTA);
        //Rasterization.drawSector2(canvas.getGraphicsContext2D(),200,200,1,4,4,1,150,Color.MAGENTA);
    }

}