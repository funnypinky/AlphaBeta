/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alphabeta.DICOM;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author shaesler
 */
public class CTImageStack {
    private final String seriesUID;
    private final String studyUID;
    
    private String referenceFrame;
    
    private final List<TransversalImage> images = new ArrayList<>();

    public CTImageStack(String seriesUID, String studyUID) {
        this.seriesUID = seriesUID;
        this.studyUID = studyUID;
    }

    public String getSeriesUID() {
        return seriesUID;
    }

    public String getStudyUID() {
        return studyUID;
    }

    public List<TransversalImage> getImages() {
        return images;
    }

    public String getReferenceFrame() {
        return referenceFrame;
    }

    public void setReferenceFrame(String referenceFrame) {
        this.referenceFrame = referenceFrame;
    }
    
    
}
