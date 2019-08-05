/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alphabeta.DICOM;

import alphabeta.structure.enums.ApprovStatus;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author shaesler
 */
public class DICOMPlan {

    private String planName;
    private String uid;
    private String referenceUIDStructure;
    private String referenceUIDDose;
    private String referenceUID;
    private ApprovStatus approvStatus;
    private final List<Field> fields = new ArrayList<>();
    private double planDose;
    private double fraction;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getReferenceUIDStructure() {
        return referenceUIDStructure;
    }

    public void setReferenceUIDStructure(String referenceUIDStructure) {
        this.referenceUIDStructure = referenceUIDStructure;
    }

    public List<Field> getFields() {
        return fields;
    }

    public String getReferenceUIDDose() {
        return referenceUIDDose;
    }

    public void setReferenceUIDDose(String referenceUIDDose) {
        this.referenceUIDDose = referenceUIDDose;
    }

    public ApprovStatus getApprovStatus() {
        return approvStatus;
    }

    public void setApprovStatus(ApprovStatus approvStatus) {
        this.approvStatus = approvStatus;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    @Override
    public String toString() {
        return planName;
    }

    public double getPlanDose() {
        return planDose;
    }

    public void setPlanDose(double planDose) {
        this.planDose = planDose;
    }

    public double getFraction() {
        return fraction;
    }

    public void setFraction(double fraction) {
        this.fraction = fraction;
    }

    public String getReferenceUID() {
        return referenceUID;
    }

    public void setReferenceUID(String referenceUID) {
        this.referenceUID = referenceUID;
    }
    
    
}
