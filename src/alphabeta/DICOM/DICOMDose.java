/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alphabeta.DICOM;

import RawDCMLibary.DICOM.DICOMFile;
import alphabeta.structure.IsodoseLevel;
import alphabeta.structure.enums.SummationType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author shaesler
 *
 */
public class DICOMDose extends DICOM {

    private DICOM parent;

    private List<DoseMatrix> doseCube = new ArrayList<>();

    private List<IsodoseLevel> isodose = new ArrayList<>();

    private double[] doseData;

    private double resolutionX;

    private double resolutionY;

    private double resolutionZ;

    private String uid;

    private double doseGridScaling;

    private double[] gridFrameOffsetVector;

    private double[] imagePositionPatient;

    private double[] imageOrientationPatient;

    private double scaleFactorX = 1.0;

    private double scaleFactorY = 1.0;

    private double ctimageRows = 0;

    private double ctimageCols = 0;

    private SummationType summationType;

    private int row;

    private int column;

    public DICOMDose(DICOM parent) {
        this.parent = parent;
    }

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

    public void setDoseData(double[] doseData) {
        this.doseData = doseData;
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

    public void readDoseFromFile(String filePath) {
        try {
            DICOMFile rf = new DICOMFile(filePath);
            rf.readHeader();
            int[] data = rf.getPixelData();
            this.doseData = new double[data.length];
            for (int i = 0; i < data.length; i++) {
                this.doseData[i] = data[i]*this.doseGridScaling;
            }
            this.generateDoseCube();
        } catch (IOException ex) {
            Logger.getLogger(DICOMDose.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void generateDoseCube() {
        int i = 0;
        int j = 0;
        double[] sliceVector = this.gridFrameOffsetVector;
        while (this.doseData.length > i) {
            DoseMatrix doseMatrix = new DoseMatrix(column, row, sliceVector[j] + this.imagePositionPatient[2]);
            for (int y = 0; y < row; y++) {
                for (int x = 0; x < column; x++) {
                    doseMatrix.setMatrixPixel(y, x, this.doseData[i]);
                    i++;
                }
            }
            j++;
            doseMatrix.setInterpolateMatrix(interpolate2D(doseMatrix.getMatrix(), this.scaleFactorX, this.scaleFactorY));
            doseCube.add(doseMatrix);
        }

    }

    public double[][] interpolate2D(double[][] self, double scaleX, double scaleY) {
        int width = self[0].length;
        int height = self.length;
        int newWidth = (int) (self[0].length * scaleX);
        int newHeight = (int) (self.length * scaleY);
        double[][] newImage = new double[newHeight][newWidth];
        for (int x = 0; x < newWidth; ++x) {
            for (int y = 0; y < newHeight; ++y) {
                float gx = ((float) x) / newWidth * (width - 1);
                float gy = ((float) y) / newHeight * (height - 1);
                int gxi = (int) gx;
                int gyi = (int) gy;
                double c00 = self[gyi][gxi];
                double c10 = self[gyi][gxi + 1];
                double c01 = self[gyi + 1][gxi];
                double c11 = self[gyi + 1][gxi + 1];
                newImage[y][x] = blerp(c00, c10, c01, c11, gx - gxi, gy - gyi);
            }
        }
        return newImage;
    }

    private static double lerp(double s, double e, double t) {
        return s + (e - s) * t;
    }

    private static double blerp(double c00, double c10, double c01, double c11, double tx, double ty) {
        return lerp(lerp(c00, c10, tx), lerp(c01, c11, tx), ty);
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

    public double getDoseMin() {
        double[] temp = doseData.clone();
        Arrays.sort(temp);
        return temp[0];
    }

    private static int firstIndexOf(double[] array, double valueToFind, double tolerance) {
        for (int i = 0; i < array.length; i++) {
            if (Math.abs(array[i] - valueToFind) < tolerance) {
                return i;
            }
        }
        return -1;
    }

    public double getScaleFactorX() {
        return scaleFactorX;
    }

    public void setScaleFactorX(double scaleFactorX) {
        this.scaleFactorX = scaleFactorX;
    }

    public double getScaleFactorY() {
        return scaleFactorY;
    }

    public void setScaleFactorY(double scaleFactorY) {
        this.scaleFactorY = scaleFactorY;
    }

    public List<IsodoseLevel> getIsodose() {
        return isodose;
    }

    public double getCtimageRows() {
        return ctimageRows;
    }

    public void setCtimageRows(double ctimageRows) {
        this.ctimageRows = ctimageRows;
    }

    public double getCtimageCols() {
        return ctimageCols;
    }

    public void setCtimageCols(double ctimageCols) {
        this.ctimageCols = ctimageCols;
    }

}
