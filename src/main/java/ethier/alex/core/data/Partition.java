/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ethier.alex.core.data;

import java.util.Collection;

/**

 @author alex
 */

// Each partition should follow a simple rule:
// All filters are PART matches to the combination.
// Since verifying integrity is an expensive step, do not run during production.

//TODO: Make partitions immutable?
public class Partition {
    private BitList combination;
    private Collection<BitList> filters; // The only structural assymetry between filters and combinations is that filters cannot be UNSET.
    
    public Partition(BitList myCombination, Collection<BitList> myFilters) {
        combination = myCombination;
        filters = myFilters;
    }
    
    public boolean verifyIntegrity() {
        for(BitList filter : filters) {
            if(combination.getMatch(filter) != Matches.PARTLY) {
                return false;
            }
        }
        return true;
    }
    
    public BitList getCombination() {
        return combination;
    }
    
    public Collection<BitList> getFilters() {
        return filters;
    }
}
