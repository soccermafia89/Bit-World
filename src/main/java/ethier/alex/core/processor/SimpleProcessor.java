/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ethier.alex.core.processor;

import ethier.alex.core.data.Bit;
import ethier.alex.core.data.BitList;
import ethier.alex.core.data.Matches;
import ethier.alex.core.data.Partition;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.apache.log4j.Logger;

/**

 @author alex
 */
public class SimpleProcessor {
    
    private static Logger logger = Logger.getLogger(SimpleProcessor.class);
    
    private Collection<Partition> incompletePartitions;
    private Collection<BitList> finalCombinations;
    
    public SimpleProcessor(Partition myPartition) {
        incompletePartitions = new ArrayList<Partition>();
        incompletePartitions.add(myPartition);
        finalCombinations = new ArrayList<BitList>();
    }
    
    public SimpleProcessor(Collection<Partition> myPartitions) {
        incompletePartitions = myPartitions;
        finalCombinations = new ArrayList<BitList>();   
    }
    
    public Collection<BitList> getCompletedPartitions() {
        return finalCombinations;
    }
    
    public void runAll() {
        while(incompletePartitions.size() > 0) {
            this.runSet();
        }
    }
    
    public void runSet() {        
        logger.debug("Processing " + incompletePartitions.size() + " partitions");
        Collection<Partition> newPartitionSet = new ArrayList<Partition>();
                
        Iterator<Partition> it = incompletePartitions.iterator();
        while(it.hasNext()) {
            Partition partition = it.next();
            if(partition.getCombination().hasSplit()) {
                Collection<Partition> splitPartitions = this.splitPartition(partition);
                newPartitionSet.addAll(splitPartitions);
            } else {
                finalCombinations.add(partition.getCombination());
            }
            
            it.remove();
        }
        
        logger.debug(newPartitionSet.size() + " new incomplete partitions created.");
        incompletePartitions = newPartitionSet;
    }
    
    public Collection<Partition> splitPartition(Partition partition) {
        logger.debug("Splitting partition with combination: " + partition.getCombination());
        
        Collection<Partition> newPartitions = new ArrayList<Partition>();
        
        BitList combination = partition.getCombination();
        
        BitList[] bitLists = combination.getSplits();

        BitList zeroList = bitLists[0];
        BitList oneList = bitLists[1];

        Collection<BitList> filters = partition.getFilters();
        int splitIndex = combination.getSplitIndex();
        
        Collection<BitList> zeroFilters = new ArrayList<BitList>();
        Collection<BitList> oneFilters = new ArrayList<BitList>();

        boolean allBothFilters = true;
        for(BitList filter : filters) {
            Bit filterBit = filter.getBitArray()[splitIndex];
            if(filterBit == Bit.BOTH) {
                zeroFilters.add(filter);
                oneFilters.add(filter);
            } else if(filterBit == Bit.ZERO) {
                zeroFilters.add(filter);
                allBothFilters = false;
            } else if(filterBit == Bit.ONE) {
                oneFilters.add(filter);
                allBothFilters = false;
            }
        }

        // In the special case that all filters contain a '*' then we don't need to return two splits.
        if(allBothFilters) {
            zeroList.getBitArray()[splitIndex] = Bit.BOTH;
            Partition bothPartition = new Partition(zeroList, zeroFilters);
            
            if(!matchExists(bothPartition)) {
                newPartitions.add(bothPartition);
            }

        } else {
            
            Partition zeroPartition = new Partition(zeroList, zeroFilters);
            Partition onePartition = new Partition(oneList, oneFilters);
            
            if(!matchExists(zeroPartition)) {
                newPartitions.add(zeroPartition);
            }
            
            if(!matchExists(onePartition)) {
                newPartitions.add(onePartition);
            }
        }
        
        for(Partition newPartition : newPartitions) {
            if(newPartition.verifyIntegrity() == false) {
                logger.error("Integrity not maintained for partition: "  + newPartition.getCombination());
            } else {
                logger.debug("New Partition: " + newPartition.getCombination());
            }
        }

        return newPartitions;
    }
    
    private boolean matchExists(Partition partition) {
        
        BitList combination = partition.getCombination();
        
        for(BitList filter : partition.getFilters()) {
            Matches matchOutcome = combination.getMatch(filter);
            
            if(matchOutcome == Matches.ENTIRELY) {
                return true;
            }
        }
        
        return false;
    }
}
