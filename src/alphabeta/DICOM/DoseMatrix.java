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
    private double zValue;
    
    private final double[][] matrix;
    
    public DoseMatrix(int columns, int rows, double zValue) {
        this.matrix = new double[rows][columns];
        this.columns = columns;
        this.rows = rows;
        this.zValue = zValue;
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

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public double getzValue() {
        return zValue;
    }

    public void setzValue(double zValue) {
        this.zValue = zValue;
    }
    
    
}
