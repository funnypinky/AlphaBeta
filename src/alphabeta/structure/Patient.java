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

    private String patientName;

    private final HashMap<String, DICOMDose> dose = new HashMap<>();
    
    private final HashMap<String, DICOMPlan> plan = new HashMap<>();

    private final HashMap<String, TransversalImage> topo = new HashMap<>();

    private final HashMap<String, CTImageStack> ctImage = new HashMap<>();

    private final HashMap<String, StructureSet> structureSet = new HashMap<>();
    
    private final List<Plan> plans = new ArrayList<>();

    public String getPatientName() {
        return this.patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
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
    
}
