/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ethier.alex.core.processor;

import ethier.alex.core.data.Bit;
import ethier.alex.core.data.BitList;
import ethier.alex.core.data.Partition;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**

 @author alex
 */
public class SimpleProcessor {
    
    private Collection<Partition> incompletePartitions;
    private Collection<Partition> completePartitions;
    
    public SimpleProcessor(Collection<Partition> myPartitions) {
        incompletePartitions = myPartitions;
        completePartitions = new ArrayList<Partition>();
        
    }
    
    public void runAll() {
        while(incompletePartitions.size() > 0) {
            this.runSet();
        }
    }
    
    public void runSet() {
        Collection<Partition> newPartitionSet = new ArrayList<Partition>();
        
        Iterator<Partition> it = incompletePartitions.iterator();
        while(it.hasNext()) {
            Partition partition = it.next();
            if(partition.getCombination().hasSplit()) {
                Collection<Partition> splitPartitions = this.splitPartition(partition);
                newPartitionSet.add(partition);
            } else {
                completePartitions.add(partition);
            }
            
            it.remove();
        }
        
        incompletePartitions = newPartitionSet;
    }
    
    public Collection<Partition> splitPartition(Partition partition) {
        Collection<Partition> newPartitions = new ArrayList<Partition>();
        
        BitList combination = partition.getCombination();
        
        BitList[] bitLists = combination.split();

        BitList zeroList = bitLists[0];
        BitList oneList = bitLists[1];

        Collection<BitList> filters = partition.getFilters();
        int splitIndex = combination.getSplitIndex();

        Collection<BitList> zeroFilters = new ArrayList<BitList>();
        Collection<BitList> oneFilters = new ArrayList<BitList>();

        for(BitList filter : filters) {
            Bit filterBit = filter.getBitArray()[splitIndex];
            if(filterBit == Bit.BOTH) {
                zeroFilters.add(filter);
                oneFilters.add(filter);
            } else if(filterBit == Bit.ZERO) {
                zeroFilters.add(filter);
            } else if(filterBit == Bit.ONE) {
                oneFilters.add(filter);
            }
        }

        // In the special case that all filters contain a '*' then we don't need to return two splits.
        if(zeroFilters.size() == oneFilters.size()) {
            zeroList.getBitArray()[splitIndex] = Bit.BOTH;
            Partition bothPartiton = new Partition(zeroList, zeroFilters);
            newPartitions.add(bothPartiton);

        } else {
            Partition zeroPartition = new Partition(zeroList, zeroFilters);
            Partition onePartition = new Partition(oneList, oneFilters);
            newPartitions.add(zeroPartition);
            newPartitions.add(onePartition);
        }

        return newPartitions;
    }
    
}
