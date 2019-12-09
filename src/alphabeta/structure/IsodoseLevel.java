/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alphabeta.structure;

import javafx.scene.paint.Color;

/**
 *
 * @author shaesler
 */
public class IsodoseLevel {
     private int level; 
     private final double absoluteDose; 
     private double thickness; 
     private boolean visible = true;
     private Color color; 
     
 
 
     public IsodoseLevel(int level, Color color, double planDose) { 
         this.level = level; 
         this.color = color; 
         this.absoluteDose = ((this.level) * planDose) / 100.0; 
     } 
 
 
     public int getLevel() { 
         return this.level; 
     } 
 
 
     public void setLevel(int level) { 
         this.level = level; 
     } 
 
 
     public Color getColor() { 
         return this.color; 
     } 
 
 
     public void setColor(Color color) { 
         this.color = color; 
     } 
 
 
     public double getAbsoluteDose() { 
         return this.absoluteDose; 
     } 
 
 
     public double getThickness() { 
         return this.thickness; 
     } 
 
 
     public void setThickness(double value) { 
         this.thickness = value; 
     } 
 
 
     public String getLabel() { 
         String result = this.level + " % / " + String.format("%.3g Gy", this.absoluteDose); 
         return result; 
     } 
 
 
     @Override 
     public String toString() { 
         return getLabel(); 
     } 

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
      
     
 } 


