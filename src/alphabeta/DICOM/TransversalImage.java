/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alphabeta.DICOM;

import org.dcm4che3.data.Tag;

/**
 *
 * @author shaesler
 */
public class TransversalImage extends DICOM {
    
    private double x;
    private double y;
    private double z;
           
    private final DICOM dicom;
    
    public TransversalImage(DICOM dicom) {
        this.dicom = dicom;
        String[] temp = this.dicom.getAttributes().getStrings(Tag.ImagePositionPatient);
        this.x = Double.valueOf(temp[0]);
        this.y = Double.valueOf(temp[1]);
        this.z = Double.valueOf(temp[2]);
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public DICOM getDicom() {
        return dicom;
    }
     
    public String getUID() {
        return this.dicom.getAttributes().getString(Tag.SOPInstanceUID);
    }
    
    public double getOriginX(){
        return this.dicom.getAttributes().getDoubles(Tag.ImagePositionPatient)[0];
    }
    
    public double getOriginY(){
        return this.dicom.getAttributes().getDoubles(Tag.ImagePositionPatient)[1];
    }
    
    public double getPixelSpaceX() {
        return this.dicom.getAttributes().getDoubles(Tag.PixelSpacing)[0];
    }
    
    public double getPixelSpaceY() {
        return this.dicom.getAttributes().getDoubles(Tag.PixelSpacing)[1];
    }
}
