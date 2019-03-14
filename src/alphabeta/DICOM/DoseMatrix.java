/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alphabeta.DICOM;

/**
 *
 * @author shaesler
 */
public class DoseMatrix {
    private int rows;
    private int columns;
    
    private final double[][] matrix;
    
    public DoseMatrix(int columns, int rows) {
        this.matrix = new double[rows][columns];
    }
    
    public void setMatrixPixel(int row, int column, double value){
        this.matrix[row][column] = value;
    }
    
    public double getMatrixPixel(int row, int column){
        return this.matrix[row][column];
    }
    
    public double[][] getMatrix() {
        return this.matrix;
    }
}
