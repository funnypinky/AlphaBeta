/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alphabeta.thread;

import alphabeta.DICOM.CTImage;
import alphabeta.DICOM.ContourSlice;
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
import java.util.ArrayList;
import java.util.List;
import javafx.concurrent.Task;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Sequence;
import org.dcm4che3.data.Tag;

/**
 *
 * @author shaesler
 */
public class LoadThread extends Task<Patient> {

    List<File> files;
    Patient patient = new Patient();

    private int ctRows;
    private int ctCols;

    public LoadThread(List<File> files) {
        this.files = files;
    }

    private List<Structure> readStructureSet(DICOM rsDICOM) {
        List<Structure> structures = new ArrayList<>();
        Sequence ROISequence = rsDICOM.getAttributes().getSequence(Tag.StructureSetROISequence); //is for the name of the contour
        Sequence conturSequence = rsDICOM.getAttributes().getSequence(Tag.ROIContourSequence); //is for the points of the contour
        Sequence observerContour = rsDICOM.getAttributes().getSequence(Tag.RTROIObservationsSequence); //is for the contour type
        for (Attributes conturItem : conturSequence) {
            for (Attributes roiItem : ROISequence) {
                if (conturItem.getString(Tag.ReferencedROINumber).equals(roiItem.getString(Tag.ROINumber))) {
                    Structure tempStruct = new Structure(roiItem.getString(Tag.ROIName));
                    tempStruct.setColor(conturItem.getStrings(Tag.ROIDisplayColor));
                    Sequence contourSlices = conturItem.getSequence(Tag.ContourSequence);
                    contourSlices.stream().map((contourSlice) -> {
                        ContourSlice tempContour = new ContourSlice(contourSlice.getSequence(Tag.ContourImageSequence).get(0).getString(Tag.ReferencedSOPInstanceUID));
                        double[][] points = new double[contourSlice.getInt(Tag.NumberOfContourPoints, 100)][3];
                        double[] pointsFormDICOM = contourSlice.getDoubles(Tag.ContourData);
                        tempContour.setzValue(pointsFormDICOM[2]);
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
                        tempContour.setRawPoints(pointsFormDICOM);
                        return tempContour;
                    }).forEachOrdered((tempContour) -> {
                        tempStruct.getPoints().add(tempContour);
                    });
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

        plan.setPlanDose(rpDICOM.getAttributes().getSequence(Tag.DoseReferenceSequence).get(0).getDouble(Tag.TargetPrescriptionDose, 0.0));
        plan.setFraction(rpDICOM.getAttributes().getSequence(Tag.FractionGroupSequence).get(0).getDouble(Tag.NumberOfFractionsPlanned, 0.0));
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

    private DICOMDose readDose(DICOM rpDICOM) {
        DICOMDose dose = new DICOMDose(rpDICOM);
        dose.setScaleFactorY((double) ctCols / (double) rpDICOM.getAttributes().getInt(Tag.Rows, -1));
        dose.setScaleFactorX((double) ctRows / (double) rpDICOM.getAttributes().getInt(Tag.Columns, -1));
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
        dose.readDoseFromFile(rpDICOM.getDicomFile().getAbsolutePath());
        System.out.println("Max: " + dose.getDoseMax());
        System.out.println("Min: " + dose.getDoseMin());
        return dose;
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
                    this.ctCols = dcmTemp.getAttributes().getInt(Tag.Columns, -1);
                    this.ctRows = dcmTemp.getAttributes().getInt(Tag.Rows, -1);
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
                    patient.getDose().add(readDose(dcmTemp));
                    break;
            }
        }
        return patient;
    }

}
