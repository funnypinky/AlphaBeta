/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alphabeta.structure;

import alphabeta.DICOM.CTImageStack;
import alphabeta.DICOM.DICOMPlan;
import alphabeta.DICOM.TransversalImage;
import alphabeta.DICOM.DICOMDose;
import alphabeta.DICOM.Plan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author shaesler
 */
public class Patient {

    private String patientLastName;
    private String patientMiddleName = null;

    private String patientFirstName;

    private final HashMap<String, DICOMDose> dose = new HashMap<>();

    private final HashMap<String, DICOMPlan> plan = new HashMap<>();

    private final HashMap<String, TransversalImage> topo = new HashMap<>();

    private final HashMap<String, CTImageStack> ctImage = new HashMap<>();

    private final HashMap<String, StructureSet> structureSet = new HashMap<>();

    private final List<Plan> plans = new ArrayList<>();

    public String getPatientName() {
        String temp = this.patientLastName;
        if (this.patientMiddleName != null) {
            temp += ", " + this.patientMiddleName;
        }
        temp += ", " + this.patientFirstName;
        return temp;
    }

    public void setPatientLastName(String patientName) {
        this.patientLastName = patientName;
    }

    public HashMap<String, DICOMPlan> getDICOMPlan() {
        return plan;
    }

    public HashMap<String, TransversalImage> getTopo() {
        return topo;
    }

    public HashMap<String, CTImageStack> getCtImage() {
        return ctImage;
    }

    public HashMap<String, StructureSet> getStructureSet() {
        return structureSet;
    }

    public HashMap<String, DICOMDose> getDose() {
        return dose;
    }

    public List<Plan> getPlan() {
        return plans;
    }

    public String getPatientMiddleName() {
        return patientMiddleName;
    }

    public void setPatientMiddleName(String patientMiddleName) {
        this.patientMiddleName = patientMiddleName;
    }

    public String getPatientFirstName() {
        return patientFirstName;
    }

    public void setPatientFirstName(String patientFirstName) {
        this.patientFirstName = patientFirstName;
    }

    public String getPatientLastName() {
        return patientLastName;
    }

}
