package alphabeta.structure;

/**
 *
 * @author Steffen Häsler
 */
public class Werte {
    private float dosis;
    private float dVdD;
    private float relVolumen;
    private float organWert;

    /**
     * 
     * @param dosis
     * @param dVdD
     */
    public Werte(float dosis, float dVdD) {
        this.dosis = dosis;
        this.dVdD = dVdD;
    }

    /**
     *
     * @return
     */
    public float getDosis() {
        return this.dosis;
    }
    /**
     *
     * @return
     */
    public float getDVdD() {
        return this.dVdD;
    }
    /**
     *
     * @param relVolumen
     */
    public void setRelVolumen(float relVolumen) {
        this.relVolumen = relVolumen;
    }
    /**
     *
     * @return
     */
    public float getRelVolumen() {
        return relVolumen;
    }

    /**
     *
     * @param dVdD
     */
    public void setdVdD(float dVdD) {
        this.dVdD = dVdD;
    }

    /**
     *
     * @param dosis
     */
    public void setDosis(float dosis) {
        this.dosis = dosis;
    }

    /**
     *
     * @return
     */
    public float getOrganWert() {
        return organWert;
    }

    /**
     *
     * @param organWert
     */
    public void setOrganWert(float organWert) {
        this.organWert = organWert;
    }
    
}
