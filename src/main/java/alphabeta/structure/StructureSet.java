/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alphabeta.structure;

import alphabeta.DICOM.Structure;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author shaesler
 */
public class StructureSet {

    private String name;

    private String uid;

    private String referenceCtUID;

    private final List<Structure> structure = new ArrayList<>();

    public StructureSet(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<Structure> getStructure() {
        return structure;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getReferenceCtUID() {
        return referenceCtUID;
    }

    public void setReferenceCtUID(String referenceCtUID) {
        this.referenceCtUID = referenceCtUID;
    }

    @Override
    public String toString() {
        return name;
    }

}
