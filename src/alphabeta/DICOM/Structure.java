/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alphabeta.DICOM;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.paint.Color;

/**
 *
 * @author shaesler
 */
public class Structure {

    private String name;

    private String ROIContourSequence;

    private String typ;

    private Color color;

    List<ContourSlice> points = new ArrayList<>();
    
    private boolean visible = true;

    public Structure(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getROIContourSequence() {
        return ROIContourSequence;
    }

    public void setROIContourSequence(String ROIContourSequence) {
        this.ROIContourSequence = ROIContourSequence;
    }

    public String getTyp() {
        return typ;
    }

    public void setTyp(String typ) {
        this.typ = typ;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    /**
     *
     * @param color - Array of String 0 is red, 1 is green, 2 is blue
     */
    public void setColor(String[] color) {
        this.color = Color.rgb(Integer.valueOf(color[0]), Integer.valueOf(color[1]), Integer.valueOf(color[2]));
    }

    public List<ContourSlice> getPoints() {
        return points;
    }

    public void setPoints(List<ContourSlice> points) {
        this.points = points;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
}
