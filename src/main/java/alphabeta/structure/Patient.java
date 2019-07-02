/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alphabeta.structure;

import alphabeta.DICOM.DICOMPlan;
import alphabeta.DICOM.CTImage;
import alphabeta.DICOM.DICOMDose;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author shaesler
 */
public class Patient {

    private String patientName;

    private List<DICOMDose> dose = new ArrayList<>();
    
    private List<DICOMPlan> plan = new ArrayList<>();

    private List<CTImage> topo = new ArrayList<>();

    private List<CTImage> ctImage = new ArrayList<>();

    private List<StructureSet> structureSet = new ArrayList<>();

    public String getPatientName() {
        return this.patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public List<DICOMPlan> getPlan() {
        return plan;
    }

    public List<CTImage> getTopo() {
        return topo;
    }

    public List<CTImage> getCtImage() {
        return ctImage;
    }

    public List<StructureSet> getStructureSet() {
        return structureSet;
    }

    public List<DICOMDose> getDose() {
        return dose;
    }
    
}
