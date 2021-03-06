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
public class ContourSlice {

    private String uidCT;
    private double zValue;
    double[][] points;

    double[] rawPoints;

    public ContourSlice(String uidCT) {
        this.uidCT = uidCT;
    }

    public String getUidCT() {
        return uidCT;
    }

    public void setUidCT(String uidCT) {
        this.uidCT = uidCT;
    }

    public double[] getRawPoints() {
        return rawPoints;
    }

    public void setRawPoints(double[] rawPoints) {
        this.rawPoints = rawPoints;
    }

    /**
     *
     * @return An Array of Contouring points - [][0] is x-Coor, [][1] is y-Coor,
     * [][2] is z-Coor
     */
    public double[][] getPoints() {
        return points;
    }

    public void setPoints(double[][] points) {
        this.points = points;
    }

    public double getzValue() {
        return zValue;
    }

    public void setzValue(double zValue) {
        this.zValue = zValue;
    }

}
