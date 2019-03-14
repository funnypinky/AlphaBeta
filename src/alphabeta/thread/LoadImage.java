/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alphabeta.thread;

import alphabeta.DICOM.CTImage;
import alphabeta.DICOM.Contour;
import alphabeta.DICOM.DICOM;
import alphabeta.DICOM.DICOMDose;
import alphabeta.DICOM.DICOMPlan;
import alphabeta.DICOM.Field;
import alphabeta.DICOM.Structure;
import alphabeta.structure.Patient;
import alphabeta.structure.StructureSet;
import alphabeta.structure.enums.ApprovStatus;
import alphabeta.structure.enums.SummationType;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Task;
import javax.imageio.ImageIO;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Sequence;
import org.dcm4che3.data.Tag;

/**
 *
 * @author shaesler
 */
public class LoadImage extends Task<Patient> {

    List<File> files;
    Patient patient = new Patient();

    public LoadImage(List<File> files) {
        this.files = files;
    }

    private List<Structure> readStructureSet(DICOM rsDICOM) {
        List<Structure> structures = new ArrayList<>();
        Sequence ROISequence = rsDICOM.getAttributes().getSequence(Tag.StructureSetROISequence);
        Sequence conturSequence = rsDICOM.getAttributes().getSequence(Tag.ROIContourSequence);
        Sequence observerContour = rsDICOM.getAttributes().getSequence(Tag.RTROIObservationsSequence);
        for (Attributes conturItem : conturSequence) {
            for (Attributes roiItem : ROISequence) {
                if (conturItem.getString(Tag.ReferencedROINumber).equals(roiItem.getString(Tag.ROINumber))) {
                    Structure tempStruct = new Structure(roiItem.getString(Tag.ROIName));
                    tempStruct.setColor(conturItem.getStrings(Tag.ROIDisplayColor));
                    Sequence contourSlices = conturItem.getSequence(Tag.ContourSequence);
                    for (Attributes contourSlice : contourSlices) {
                        Contour tempContour = new Contour(contourSlice.getSequence(Tag.ContourImageSequence).get(0).getString(Tag.ReferencedSOPInstanceUID));
                        double[][] points = new double[contourSlice.getInt(Tag.NumberOfContourPoints, 100)][3];
                        double[] pointsFormDICOM = contourSlice.getDoubles(Tag.ContourData);
                        int j = 0;
                        for (int i = 0; i < pointsFormDICOM.length; i += 3) {
                            if (i < points.length * 3) {
                                points[j][0] = pointsFormDICOM[i];
                                points[j][1] = pointsFormDICOM[i + 1];
                                points[j][2] = pointsFormDICOM[i + 2];
                            }
                            j += 1;
                        }
                        tempContour.setPoints(points);
                        tempStruct.getPoints().add(tempContour);
                    }
                    for (Attributes observerItem : observerContour) {
                        if (observerItem.getInt(Tag.ReferencedROINumber, -1) == conturItem.getInt(Tag.ReferencedROINumber, -2)) {
                            tempStruct.setTyp(observerItem.getString(Tag.RTROIInterpretedType));
                        }

                    }
                    structures.add(tempStruct);
                }
            }
        }
        return structures;
    }

    private DICOMPlan readPlan(DICOM rpDICOM) {
        int beamParam = 0;
        int controlParam;
        DICOMPlan plan = new DICOMPlan();
        plan.setPlanName(rpDICOM.getAttributes().getString(Tag.RTPlanLabel));
        plan.setUid(rpDICOM.getAttributes().getString(Tag.SOPInstanceUID));
        plan.setReferenceUIDStructure(rpDICOM.getAttributes().getSequence(Tag.ReferencedStructureSetSequence).get(0).getString(Tag.ReferencedSOPInstanceUID));
        switch (rpDICOM.getAttributes().getString(Tag.ApprovalStatus)) {
            case ("REJECTED"):
                plan.setApprovStatus(ApprovStatus.REJECTED);
                break;
            case ("APPROVED"):
                plan.setApprovStatus(ApprovStatus.APPROVED);
                break;
        }
        if (rpDICOM.getAttributes().getSequence(Tag.BeamSequence) != null) {
            beamParam = Tag.BeamSequence;
            controlParam = Tag.ControlPointSequence;
        } else if (rpDICOM.getAttributes().getSequence(Tag.IonBeamSequence) != null) {
            beamParam = Tag.IonBeamSequence;
            controlParam = Tag.IonControlPointSequence;
        } else {
            Exception exception = new Exception("Not supported kind of DICOM RT plan file.");
        }
        Sequence beamSequence = rpDICOM.getAttributes().getSequence(beamParam);
        for (Attributes beam : beamSequence) {
            Field field = new Field(beam.getString(Tag.BeamName));
            field.setUnit(beam.getString(Tag.PrimaryDosimeterUnit));
            switch (beam.getString(Tag.TreatmentDeliveryType)) {
                case "TREATMENT":
                    field.setFieldTyp(Field.TREATMENT);
                    break;
            }
            Attributes fieldInfo = beam.getSequence(Tag.ControlPointSequence).get(0);
            field.setBeamAngle(fieldInfo.getDouble(Tag.BeamAngle, 0));
            field.setPatientSupportAngle(fieldInfo.getDouble(Tag.PatientSupportAngle, 0));
            field.setIsocenter(fieldInfo.getDoubles(Tag.IsocenterPosition));
            plan.getFields().add(field);
        }
        return plan;
    }

    private void readDose(DICOM rpDICOM) {
        DICOMDose dose = new DICOMDose();
        dose.setUid(rpDICOM.getAttributes().getString(Tag.SOPInstanceUID));
        dose.setDoseGridScaling(rpDICOM.getAttributes().getDouble(Tag.DoseGridScaling, 1.0));
        dose.setResolutionX(rpDICOM.getAttributes().getDoubles(Tag.PixelSpacing)[0]);
        dose.setResolutionY(rpDICOM.getAttributes().getDoubles(Tag.PixelSpacing)[1]);
        dose.setResolutionZ(rpDICOM.getAttributes().getDouble(Tag.SliceThickness, 0));
        dose.setGridFrameOffsetVector(rpDICOM.getAttributes().getDoubles(Tag.GridFrameOffsetVector));
        dose.setImagePositionPatient(rpDICOM.getAttributes().getDoubles(Tag.ImagePositionPatient));
        dose.setImageOrientationPatient(rpDICOM.getAttributes().getDoubles(Tag.ImageOrientationPatient));
        dose.setRow(rpDICOM.getAttributes().getInt(Tag.Rows, 0));
        dose.setColumn(rpDICOM.getAttributes().getInt(Tag.Columns, 0));
        switch (rpDICOM.getAttributes().getString(Tag.DoseSummationType)) {
            case ("PLAN"):
                dose.setSummationType(SummationType.PLAN);
                break;
            case ("BEAM"):
                dose.setSummationType(SummationType.BEAM);
                break;
        }
        try {
            dose.setDoseData(rpDICOM.getAttributes().getBytes(Tag.PixelData));
        } catch (IOException ex) {
            Logger.getLogger(LoadImage.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(dose.getDoseMax());
    }

    @Override
    public Patient call() throws Exception {
        final int size = files.size();
        for (int i = 0; i < files.size(); i++) {
            File selectedFile = files.get(i);
            updateProgress(i, size);
            String s = String.format("Lade Bild %d von %d", i, size);
            updateMessage(s);
            DICOM dcmTemp = new DICOM(selectedFile);
            patient.setPatientName(dcmTemp.getFullPatientName());
            switch (dcmTemp.getModalitiy()) {
                case CT:
                    if (dcmTemp.getCSImageType()[2].equalsIgnoreCase("axial")) {
                        patient.getCtImage().add(new CTImage(dcmTemp));
                    } else if (dcmTemp.getCSImageType()[2].equalsIgnoreCase("LOCALIZER")) {
                        patient.getTopo().add(new CTImage(dcmTemp));
                    }
                    break;
                case RTSTRUCT:
                    StructureSet rtSS = new StructureSet(dcmTemp.getAttributes().getString(Tag.StructureSetLabel));
                    rtSS.setUid(dcmTemp.getAttributes().getString(Tag.SOPInstanceUID));
                    Sequence tempRef = dcmTemp.getAttributes().getSequence(Tag.ReferencedFrameOfReferenceSequence);
                    rtSS.setReferenceCtUID(tempRef.get(0).getString(Tag.ReferencedFrameOfReferenceUID));
                    rtSS.getStructure().addAll(readStructureSet(dcmTemp));
                    patient.getStructureSet().add(rtSS);
                    break;
                case RTPLAN:
                    patient.getPlan().add(readPlan(dcmTemp));
                    break;
                case RTDOSE:
                    ImageIO.write(dcmTemp.getBufferedImage(), "png", new File("C://temp//image.png"));
                    readDose(dcmTemp);
                    break;
            }
        }
        return patient;
    }

}
