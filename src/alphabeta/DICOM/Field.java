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
public class Field {

    public static final int SETUP = 1;

    public static final int STATIC = 2;

    public static final int IMRT = 3;

    public static final int VMAT = 4;

    public static final int ELECTRON = 5;

    public static final int PHOTON = 5;

    public static final int TREATMENT = 6;

    private String fieldName;

    private String unit;

    private double meterSet;

    private double fieldDose;

    private int beamTyp;

    private int energy;

    private int beamQuality;

    private double beamAngle;

    private double collimatorAngle;

    private int fieldTyp;
    
    private double PatientSupportAngle;
    
    private double[] isocenter = new double[3];

    public Field(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public double getMeterSet() {
        return meterSet;
    }

    public void setMeterSet(double meterSet) {
        this.meterSet = meterSet;
    }

    public double getFieldDose() {
        return fieldDose;
    }

    public void setFieldDose(double fieldDose) {
        this.fieldDose = fieldDose;
    }

    public int getBeamTyp() {
        return beamTyp;
    }

    public void setBeamTyp(int typ) {
        this.beamTyp = typ;
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public int getBeamQuality() {
        return beamQuality;
    }

    public void setBeamQuality(int beamQuality) {
        this.beamQuality = beamQuality;
    }

    public double getBeamAngle() {
        return beamAngle;
    }

    public void setBeamAngle(double beamAngle) {
        this.beamAngle = beamAngle;
    }

    public double getCollimatorAngle() {
        return collimatorAngle;
    }

    public void setCollimatorAngle(double collimatorAngle) {
        this.collimatorAngle = collimatorAngle;
    }

    public int getFieldTyp() {
        return fieldTyp;
    }

    public void setFieldTyp(int fieldTyp) {
        this.fieldTyp = fieldTyp;
    }

    public double getPatientSupportAngle() {
        return PatientSupportAngle;
    }

    public void setPatientSupportAngle(double PatientSupportAngle) {
        this.PatientSupportAngle = PatientSupportAngle;
    }

    public double[] getIsocenter() {
        return isocenter;
    }

    public void setIsocenter(double[] isocenter) {
        this.isocenter = isocenter;
    }

    
}
