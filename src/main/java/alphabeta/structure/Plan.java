package alphabeta.structure;

import alphabeta.DICOM.CTImage;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Steffen Häsler
 */
public class Plan {

    private String patientName = "";
    private String patientID = "";
    private float utcp;
    private float eIndex;
    private String name = "";
    private float dose;
    private List<PTV> PTV = new ArrayList<>();
    private List<OAR> OAR = new ArrayList<>();
    private List<CTImage> ctImages=  new ArrayList<>();

    
    /**
     *
     */
    public Plan() {
    }

    /**
     *
     * @return
     */
    public float getDose() {
        return dose;
    }

    /**
     *
     * @param dose
     */
    public void setDose(float dose) {
        this.dose = dose;
    }

    /**
     *
     * @return
     */
    public String getPatientID() {
        return patientID;
    }

    /**
     *
     * @param patientID
     */
    public void setPatientID(String patientID) {
        this.patientID = patientID;
    }

    /**
     *
     * @return
     */
    public String getPatientName() {
        return patientName;
    }

    /**
     *
     * @param patientName
     */
    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    /**
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     */
    public List<PTV> getPTV() {
        return PTV;
    }

    /**
     *
     * @param PTV
     */
    public void setPTV(PTV PTV) {
        this.PTV.add(PTV);
    }

    /**
     *
     * @return
     */
    public List<OAR> getOAR() {
        return OAR;
    }

    /**
     *
     * @param OAR
     */
    public void setOAR(OAR oar) {
        this.OAR.add(oar);
    }

    /**
     *
     * @return
     */
    public float getUtcp() {
        float gi;
        double ntcpPr = 0;
        double tcpPr = 0;
        if (PTV.isEmpty() || OAR.isEmpty()) {
            return -1;
        }
        for (int i = 0; i < OAR.size(); i++) {
            if (OAR.get(i).getNTCP() != -1) {
                if (OAR.get(i).getNTCP() < .05) {
                    gi = 1;
                } else {
                    gi = 1 / OAR.get(i).getNTCP();
                }
                if (ntcpPr == 0) {
                    ntcpPr = (1 - (gi * OAR.get(i).getNTCP()));
                } else {
                    ntcpPr = ntcpPr * (1 - (gi * OAR.get(i).getNTCP()));
                }
            }
        }
        for (int i = 0; i < PTV.size(); i++) {
            if (PTV.get(i).getTCP() != -1) {
                if (tcpPr == 0) {
                    tcpPr = PTV.get(i).getTCP();
                } else {
                    tcpPr = tcpPr * PTV.get(i).getTCP();
                }
            }
        }
        utcp = (float) (Math.pow(tcpPr, 1.0/(double)PTV.size()) * ntcpPr);
        return utcp;
    }
    public float getCombSTD() {
        float STDg = 0;
        for (int n = 0; n < PTV.size(); n++) {
            if (STDg == 0) {
                STDg = PTV.get(n).getStdDosis();
            } else {
                STDg = STDg * PTV.get(n).getStdDosis();
            }
        }
        return STDg;
    }
    public float geteIndex() {
        
        eIndex = getUtcp() / getCombSTD();
        return eIndex;
    }

    public List<CTImage> getCtImages() {
        return ctImages;
    }

    public void setCtImages(List<CTImage> ctImages) {
        this.ctImages = ctImages;
    }
    
}

