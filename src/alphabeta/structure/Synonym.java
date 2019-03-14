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
public class Synonym {

    private final String organ;
    List<String> synonym = new ArrayList<String>();

    public void setSynonym(List<String> synonym) {
        this.synonym = synonym;
    }

    
    /**
     *
     * @param organ
     */
    public Synonym(String organ) {
        this.organ = organ;
    }

    /**
     *
     * @param synonym
     */
    public void addSynonym(String synonym) {
        if (!isSynonym(synonym)) {
            this.synonym.add(synonym);
        }
    }

    /**
     *
     * @param synonym
     * @return
     */
    public boolean isSynonym(String synonym) {
        Boolean returnVal = false;
        for (int i = 0; i < this.synonym.size(); i++) {
            if (this.synonym.get(i).equalsIgnoreCase(synonym)) {
                returnVal = true;
            }
        }
        return returnVal;
    }

    /**
     *
     * @return
     */
    public String getOrgan() {
        return organ;
    }

    /**
     *
     * @return
     */
    public List<String> getSynonym() {
        return synonym;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((organ == null) ? 0 : organ.hashCode());

        result = prime * result + ((synonym == null) ? 0 : synonym.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Synonym other = (Synonym) obj;
        if ((this.organ == null) ? (other.organ != null) : !this.organ.equals(other.organ)) {
            return false;
        }
        if (this.synonym != other.synonym && (this.synonym == null || !this.synonym.equals(other.synonym))) {
            return false;
        }
        return true;
    }
}
