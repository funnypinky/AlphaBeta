/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package alphabeta.structure;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Steffen Häsler
 */
public class Gewebe {
    String name;
    float d50;
    float dMax;
    float dMean;
    float dMin;
    float volumen;
    List<Werte> daten = new ArrayList<>();
    float stdDosis;

    /**
     *
     */
    public Gewebe(){

    }

    /**
     *
     * @return
     */
    public float getVolumen() {
        return volumen;
    }

    /**
     *
     * @return
     */
    public float getD50() {
        return d50;
    }

    /**
     *
     * @return
     */
    public float getdMax() {
        return dMax;
    }

    /**
     *
     * @return
     */
    public float getdMean() {
        return dMean;
    }

    /**
     *
     * @return
     */
    public float getdMin() {
        return dMin;
    }

    /**
     * Gibt die Werte des differentiellen Dosisvolumenhistograms zurück.
     * @return
     * [0]: absolute Dosis in Gray
     * [1]: absolutes Volumen in cm³
     * [2]: relatives Volumen in %
     */
    public double[][] getDaten() {
        double temp[][] = new double[this.daten.size()][3];
        for (int i = 0; i< temp.length; i++) {
            temp[i][0] = this.daten.get(i).getDosis();
            temp[i][1] = this.daten.get(i).getDVdD();
            temp[i][2] = this.daten.get(i).getRelVolumen();
        }
        return temp;
    }
    /**
     *
     */
    public void korrektur() {
        float faktor = 1;
        if (daten.size() > 2) {
            faktor = daten.get(1).getDosis()-daten.get(0).getDosis();
            for (int i =0; i< this.daten.size();i++){
              this.daten.get(i).setdVdD(this.daten.get(i).getDVdD() *  faktor);
              this.daten.get(i).setRelVolumen(daten.get(i).getDVdD()/volumen);
            }
        }
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
     * @return
     */
    public float getStdDosis() {
        return stdDosis;
    }

    /**
     *
     * @param Volumen
     */
    public void setVolumen(float Volumen) {
        this.volumen = Volumen;
    }

    /**
     *
     * @param d50
     */
    public void setD50(float d50) {
        this.d50 = d50;
    }

    /**
     *
     * @param dMax
     */
    public void setdMax(float dMax) {
        this.dMax = dMax;
    }

    /**
     *
     * @param dMean
     */
    public void setdMean(float dMean) {
        this.dMean = dMean;
    }

    /**
     *
     * @param dMin
     */
    public void setdMin(float dMin) {
        this.dMin = dMin;
    }

    /**
     *
     * @param dose
     * @param dVdD
     */
    public void setDaten(float dose, float dVdD) {
        Werte temp = new Werte(dose, dVdD);
        this.daten.add(temp);
        this.daten.get(this.daten.size()-1).setRelVolumen(daten.get(this.daten.size()-1).getDVdD()/volumen);
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
     * @param stdDosis
     */
    public void setStdDosis(float stdDosis) {
        this.stdDosis = stdDosis;
    }
}
