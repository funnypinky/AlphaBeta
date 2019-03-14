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
public class PTV extends Gewebe {

    private float TCP;
    private float y;

    /**
     *
     */
    public PTV() {
    }

    /**
     *
     * @return
     */
    public float berechnenTCP() {
        DatabaseAccess db = new DatabaseAccess("Daten");
        ResultSet rs;
        float temp;
        float temp2;
        float summe = 0;
        float ln2 = (float) Math.log(2);
        float dblY;
        TCP = 0;
        try {
            db.connect();

            rs = db.executeQuery("SELECT * FROM tcp WHERE tumorentitaet LIKE 'hypoxischer Tumor'");
            if (rs.next()) {
                y = rs.getFloat("y");
                dblY = y * 2;
                d50 = rs.getFloat("TD50");
                for (int j = 0; j < daten.size(); j++) {
                    temp = (float) (daten.get(j).getRelVolumen() * Math.exp(dblY * (1 - (daten.get(j).getDosis() / d50) / ln2)));
                    daten.get(j).setOrganWert(temp);
                    summe += temp;
                }
            }
            TCP = (float) Math.pow(.5, summe);
            db.shutdown();
        } catch (SQLException ex) {
            Logger.getLogger(OAR.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            return TCP;
        }
    }

    /**
     *
     * @return
     */
    public float getTCP() {
        return TCP;
    }
    
}
