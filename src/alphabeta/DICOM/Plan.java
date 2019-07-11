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
public class Plan {
    
    private final String planUID;
    
    private String doseUID;
    
    private String structureSetUID;
    
    private String ctUID;
    
    public Plan(String planUID) {
        this.planUID = planUID;
    }

    public String getDoseUID() {
        return doseUID;
    }

    public void setDoseUID(String doseUID) {
        this.doseUID = doseUID;
    }

    public String getStructureSetUID() {
        return structureSetUID;
    }

    public void setStructureSetUID(String structureSetUID) {
        this.structureSetUID = structureSetUID;
    }

    public String getCtUID() {
        return ctUID;
    }

    public void setCtUID(String ctUID) {
        this.ctUID = ctUID;
    }

    public String getPlanUID() {
        return planUID;
    }
    
    
}
