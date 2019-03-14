/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alphabeta.DICOM;

import alphabeta.structure.enums.SummationType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author shaesler
 */
public class DICOMDose {

    private List<DoseMatrix> doseCube = new ArrayList<>();

    private double[] doseData;

    private double resolutionX;

    private double resolutionY;

    private double resolutionZ;

    private String uid;

    private double doseGridScaling;

    private double[] gridFrameOffsetVector;

    private double[] imagePositionPatient;

    private double[] imageOrientationPatient;

    private SummationType summationType;

    private int row;

    private int column;

    public List<DoseMatrix> getDoseCube() {
        return doseCube;
    }

    public void setDoseCube(List<DoseMatrix> doseCube) {
        this.doseCube = doseCube;
    }

    public double getResolutionX() {
        return resolutionX;
    }

    public void setResolutionX(double resolutionX) {
        this.resolutionX = resolutionX;
    }

    public double getResolutionY() {
        return resolutionY;
    }

    public void setResolutionY(double resolutionY) {
        this.resolutionY = resolutionY;
    }

    public double getResolutionZ() {
        return resolutionZ;
    }

    public void setResolutionZ(double resolutionZ) {
        this.resolutionZ = resolutionZ;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public double getDoseGridScaling() {
        return doseGridScaling;
    }

    public void setDoseGridScaling(double doseGridScaling) {
        this.doseGridScaling = doseGridScaling;
    }

    public double[] getGridFrameOffsetVector() {
        return gridFrameOffsetVector;
    }

    public void setGridFrameOffsetVector(double[] gridFrameOffsetVector) {
        this.gridFrameOffsetVector = gridFrameOffsetVector;
    }

    public double[] getImagePositionPatient() {
        return imagePositionPatient;
    }

    public void setImagePositionPatient(double[] imagePositionPatient) {
        this.imagePositionPatient = imagePositionPatient;
    }

    public SummationType getSummationType() {
        return summationType;
    }

    public void setSummationType(SummationType summationType) {
        this.summationType = summationType;
    }

    public double[] getDoseData() {
        return doseData;
    }

    public void setDoseData(byte[] doseData) {
        this.doseData = new double[doseData.length];
        for (int i = 0; i < doseData.length; i++) {
            int val = Integer.parseUnsignedInt(Integer.toBinaryString(doseData[i] & 0xFF), 2);
            this.doseData[i] = val * this.doseGridScaling;
            if(val!=0){
                System.out.println(this.doseData[i]);
            }
        }
        generateDoseCube();
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public double[] getImageOrientationPatient() {
        return imageOrientationPatient;
    }

    public void setImageOrientationPatient(double[] imageOrientationPatient) {
        this.imageOrientationPatient = imageOrientationPatient;
    }

    public void generateDoseCube() {
        int i = 0;
        while (this.doseData.length > i) {
            DoseMatrix doseMatrix = new DoseMatrix(column, row);
            for (int y = 0; y < row; y++) {
                for (int x = 0; x < column; x++) {
                    doseMatrix.setMatrixPixel(y, x, this.doseData[i]);
                    i++;
                }
            }
            doseCube.add(doseMatrix);
        }

    }

    public DoseMatrix getDosePlaneBySlice(double slicePosition) {
        if (this.gridFrameOffsetVector.length > 0) {
            double imagePatientPositionPatient = this.imagePositionPatient[2];
            double[] dosePlanesZ = new double[this.gridFrameOffsetVector.length];
            for (int i = 0; i < dosePlanesZ.length; i++) {
                dosePlanesZ[i] = this.gridFrameOffsetVector[i] + imagePatientPositionPatient;
            }
            if (Arrays.stream(dosePlanesZ).min().getAsDouble() <= slicePosition && slicePosition <= Arrays.stream(dosePlanesZ).max().getAsDouble()) {
                double[] absoluteDistance = new double[dosePlanesZ.length];
                for (int i = 0; i < absoluteDistance.length; i++) {
                    absoluteDistance[i] = Math.abs(dosePlanesZ[i] - slicePosition);
                }

                // Check to see if the requested plane exists in the array (or is close enough) 
                int doseSlicePosition = -1;
                double minDistance = Arrays.stream(absoluteDistance).min().getAsDouble();
                if (minDistance < 0.5) {
                    doseSlicePosition = firstIndexOf(absoluteDistance, minDistance, 0.001);
                }
                return doseCube.get(doseSlicePosition);
            }
        }
        return null;
    }

    public double getDoseMax() {
        double[] temp = doseData.clone();
        Arrays.sort(temp);
        return temp[temp.length - 1];
    }

    private static int firstIndexOf(double[] array, double valueToFind, double tolerance) {
        for (int i = 0; i < array.length; i++) {
            if (Math.abs(array[i] - valueToFind) < tolerance) {
                return i;
            }
        }
        return -1;
    }

}
