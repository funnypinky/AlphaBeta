/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package alphabeta.structure;

import alphabeta.data.DatabaseAccess;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Steffen Häsler
 */
public class OAR extends Gewebe {

    private float NTCP = -1;
    private Synonym synonmye[];
    private float EUD = -1;
    private float n;
    private float m;
    private float t;

    /**
     *
     */
    public OAR() {
    }

    /**
     *
     * @return
     */
    public float berechneNTCP() {
        if (EUD > 0) {
            t = (EUD - d50) / (d50 * m);
            if (t < 0) {
                NTCP = (float) (.5 - .5 * Math.sqrt(1 - Math.exp(-0.6225 * (t * t))));
            } else {
                NTCP = (float) (.5 + .5 * Math.sqrt(1 - Math.exp(-0.6225 * (t * t))));
            }
            return NTCP;
        } else {
            return -1;
        }
    }

    /**
     *
     * @return
     */
    public float berechnenEUD() {
        DatabaseAccess db = new DatabaseAccess("Daten");
        ResultSet rs;
        float temp;
        float summe = 0;
        EUD = 0;
        try {
            db.connect();
            for (int i = 0; i < synonmye.length; i++) {
                if (synonmye[i].isSynonym(name)) {
                    rs = db.executeQuery("SELECT * FROM ntcp WHERE organ LIKE '" + synonmye[i].getOrgan() + "'");
                    if (rs.next()) {
                        n = rs.getFloat("n");
                        m = rs.getFloat("m");
                        d50 = rs.getFloat("TD50");
                        for (int j = 0; j < daten.size(); j++) {
                            temp = daten.get(j).getRelVolumen() * (float) Math.pow(daten.get(j).getDosis(), (1 / n));
                            daten.get(j).setOrganWert(temp);
                            summe += temp;
                        }
                        i = synonmye.length;
                    }

                } else {
                    summe = 0;
                }
            }
            db.shutdown();
        } catch (SQLException ex) {
            Logger.getLogger(OAR.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (summe != 0) {
            EUD = (float) Math.pow(summe, n);
        } else {
            EUD = -1;
        }
        berechneNTCP();
        return EUD;
    }

    /**
     *
     * @return
     */
    public float getEUD() {
        return EUD;
    }

    /**
     *
     * @return
     */
    public float getNTCP() {
        return NTCP;
    }
}
