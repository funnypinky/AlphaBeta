/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package alphabeta.thread;

import alphabeta.structure.OAR;
import alphabeta.structure.PTV;
import alphabeta.structure.Plan;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Steffen Häsler
 */
public class DatenImport implements Runnable{

    private String fileName;
    private String pathName;
    private File datei;
    private Plan plan = new Plan();

    /**
     *
     * @param datei
     */
    public DatenImport(File datei) {
        this.datei = datei;
        setFileName();
        setPathName();
    }

    /**
     *
     */
    public DatenImport() {
    }

    /**
     *
     * @param datei
     */
    public void setDatei(File datei) {
        this.datei = datei;
        setFileName();
        setPathName();
    }

    /**
     *
     * @return
     */
    public File getDatei() {
        return datei;
    }

    private void setFileName() {
        this.fileName = datei.getName();
    }

    private void setPathName() {
        this.pathName = datei.getAbsolutePath().substring(0, datei.getAbsolutePath().length() - this.fileName.length());
    }

    /**
     *
     * @return
     */
    public String getFileName() {
        return fileName;
    }

    /**
     *
     * @return
     */
    public String getPathName() {
        return pathName;
    }

    /**
     *
     */
    public void einlesenDaten() {
        BufferedReader reader;
        String zeile;
        String temp[];
        try {
            reader = new BufferedReader(new FileReader(datei));
            zeile = reader.readLine();
            while (zeile != null) {
                if (zeile.startsWith("Patientenname")) {
                    temp = zeile.split(":");
                    temp = temp[1].split(" ");
                    plan.setPatientName(temp[1] + " " + temp[2]);
                }
                if (zeile.startsWith("Patienten-ID")) {
                    temp = zeile.split(":");
                    plan.setPatientID(temp[1].trim());
                }
                if (zeile.startsWith("Plan")) {
                    if (plan.getName().isEmpty()) {
                        temp = zeile.split(":");
                        plan.setName(temp[1].trim());
                    }
                }
                if (zeile.startsWith("Gesamtdosis [Gy]")) {
                    temp = zeile.split(":");
                    plan.setDose(Float.parseFloat(temp[1].trim()));
                }
                if (zeile.startsWith("Struktur")) {
                    einlesenStruktur(reader, zeile);
                }
                zeile = reader.readLine();
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DatenImport.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
        }
        //datenAusgeben();
    }

    private void datenAusgeben() {
        double summeVol = 0;
        double summeRel = 0;
        System.out.println(plan.getName());
        System.out.println(plan.getPatientName());
        System.out.println(plan.getPatientID());
        System.out.println(plan.getDose());
        for (int i = 0; i < plan.getPTV().size(); i++) {
            summeVol = 0;
            summeRel = 0;
            System.out.println("");
            System.out.println("Organ: " + plan.getPTV().get(i).getName());
            System.out.println("Volumen: " + plan.getPTV().get(i).getVolumen());
            System.out.println("max. Dosis: " + plan.getPTV().get(i).getdMax());
            System.out.println("min. Dosis: " + plan.getPTV().get(i).getdMin());
            System.out.println("mittel. Dosis: " + plan.getPTV().get(i).getdMean());
            System.out.println("STD Dosis: " + plan.getPTV().get(i).getStdDosis());
            System.out.println("");
            System.out.println("");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(DatenImport.class.getName()).log(Level.SEVERE, null, ex);
            }
            for (int j = 0; j < plan.getPTV().get(i).getDaten().length; j++) {
                System.out.print(plan.getPTV().get(i).getDaten()[j][0] + "   ");
                System.out.print(plan.getPTV().get(i).getDaten()[j][1] + "   ");
                summeVol = summeVol + plan.getPTV().get(i).getDaten()[j][1];
                System.out.println(plan.getPTV().get(i).getDaten()[j][2]);
                summeRel = summeRel + plan.getPTV().get(i).getDaten()[j][2];
            }
            System.out.println();
            System.out.print(summeVol + "  ");
            System.out.println(summeRel);
            System.out.println();
            for (i = 0; i < plan.getOAR().size(); i++) {
                summeVol = 0;
                summeRel = 0;
                System.out.println("");
                System.out.println("Organ: " + plan.getOAR().get(i).getName());
                System.out.println("Volumen: " + plan.getOAR().get(i).getVolumen());
                System.out.println("max. Dosis: " + plan.getOAR().get(i).getdMax());
                System.out.println("min. Dosis: " + plan.getOAR().get(i).getdMin());
                System.out.println("mittel. Dosis: " + plan.getOAR().get(i).getdMean());
                System.out.println("STD Dosis: " + plan.getOAR().get(i).getStdDosis());
                System.out.println("");
                System.out.println("");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(DatenImport.class.getName()).log(Level.SEVERE, null, ex);
                }
                for (int j = 0; j < plan.getOAR().get(i).getDaten().length; j++) {
                    System.out.print(plan.getOAR().get(i).getDaten()[j][0] + "   ");
                    System.out.print(plan.getOAR().get(i).getDaten()[j][1] + "   ");
                    summeVol = summeVol + plan.getOAR().get(i).getDaten()[j][1];
                    System.out.println(plan.getOAR().get(i).getDaten()[j][2]);
                    summeRel = summeRel + plan.getOAR().get(i).getDaten()[j][2];
                }
                System.out.println();
                System.out.print(summeVol + "  ");
                System.out.println(summeRel);
                System.out.println();
            }
        }
    }

    private void einlesenStruktur(BufferedReader reader, String zeile) {
        PTV tempPTV = new PTV();
        OAR tempOAR = new OAR();
        String[] temp;
        String tempDose;
        String tempRelDose;
        String tempDvdD;
        String tempStr;
        Boolean isOAR = false;
        while (zeile != null) {
            try {
                if (zeile.startsWith("Struktur")) {
                    temp = zeile.split(":");
                    if (!temp[1].trim().contains("PTV")) {
                        tempOAR = new OAR();
                        isOAR = true;
                        tempOAR.setName(temp[1].trim());
                    } else {
                        tempPTV = new PTV();
                        isOAR = false;
                        tempPTV.setName(temp[1].trim());
                    }
                }
                if ((zeile.length() > "Volume".length()) && (zeile.substring(0, 0 + "Volume".length()).equalsIgnoreCase("Volume"))) {
                    temp = zeile.split(":");
                    if (isOAR) {
                        tempOAR.setVolumen(Float.parseFloat(temp[1].trim()));
                    } else {
                        tempPTV.setVolumen(Float.parseFloat(temp[1].trim()));
                    }

                }
                if (zeile.startsWith("Min Dosis")) {
                    temp = zeile.split(":");
                    if (isOAR) {
                        tempOAR.setdMin(Float.parseFloat(temp[1].trim()));
                    } else {
                        tempPTV.setdMin(Float.parseFloat(temp[1].trim()));
                    }
                }
                if (zeile.startsWith("Max Dosis")) {
                    temp = zeile.split(":");
                    if (isOAR) {
                        tempOAR.setdMax(Float.parseFloat(temp[1].trim()));
                    } else {
                        tempPTV.setdMax(Float.parseFloat(temp[1].trim()));
                    }
                }
                if (zeile.startsWith("Mittel Dosis")) {
                    temp = zeile.split(":");
                    if (isOAR) {
                        tempOAR.setdMean(Float.parseFloat(temp[1].trim()));
                    } else {
                        tempPTV.setdMean(Float.parseFloat(temp[1].trim()));
                    }
                }
                if (zeile.startsWith("STD")) {
                    temp = zeile.split(":");
                    if (isOAR) {
                        tempOAR.setStdDosis(Float.parseFloat(temp[1].trim()));
                    } else {
                        tempPTV.setStdDosis(Float.parseFloat(temp[1].trim()));
                    }
                }
                if (zeile.trim().contains("Dosis [Gy]  Relative Dosis [%]")) {
                    zeile = reader.readLine();
                    do {
                        zeile = zeile.trim();
                        tempStr = zeile;
                        temp = zeile.split(" ");
                        tempDose = temp[0].trim();
                        tempStr = tempStr.substring(0 + tempDose.length()).trim();
                        temp = tempStr.split(" ");
                        tempRelDose = temp[0].trim();
                        tempStr = tempStr.substring(0 + tempRelDose.length()).trim();
                        temp = tempStr.split(" ");
                        tempDvdD = temp[0].trim();
                        if (isOAR) {
                            tempOAR.setDaten(Float.parseFloat(tempDose), Float.parseFloat(tempDvdD));
                        } else {
                            tempPTV.setDaten(Float.parseFloat(tempDose), Float.parseFloat(tempDvdD));
                        }
                        zeile = reader.readLine();
                    } while ((zeile != null) && (!zeile.equalsIgnoreCase("")));
                    if (isOAR) {
                        tempOAR.korrektur();
                        tempOAR.berechnenEUD();
                        plan.setOAR(tempOAR);
                    } else {
                        tempPTV.korrektur();
                        tempPTV.berechnenTCP();
                        plan.setPTV(tempPTV);
                    }
                }
                zeile = reader.readLine();
            } catch (IOException ex) {
                Logger.getLogger(DatenImport.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     *
     * @return
     */
    public Plan getPlan() {
        return plan;
    }

    @Override
    public void run() {
        einlesenDaten();
    }
    
}
