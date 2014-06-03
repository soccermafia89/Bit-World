/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ethier.alex.core.addon;

import ethier.alex.core.data.BitList;
import ethier.alex.core.data.Partition;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.commons.lang3.StringUtils;

/**

 @author alex
 */
public class PartitionBuilder {

    private Partition partition;
    private Collection<BitList> filters;
    private BitList combination;

    public static PartitionBuilder newInstance() {
        return new PartitionBuilder();
    }

    public PartitionBuilder() {
        filters = new ArrayList();
    }

    public PartitionBuilder setWorldSize(int worldSize) {
        if (partition != null) {
            throw new RuntimeException("Partition already created.");
        }

        String combinationStr = StringUtils.leftPad("", worldSize, '-');
        combination = BitListBuilder.buildBitList(combinationStr);

        return this;
    }

    public PartitionBuilder addFilter(BitList newFilter) {
        if (partition != null) {
            throw new RuntimeException("Partition already created.");
        }

        filters.add(newFilter);

        return this;
    }

    public PartitionBuilder addFilters(Collection<BitList> filters) {
        if (partition != null) {
            throw new RuntimeException("Partition already created.");
        }

        filters.addAll(filters);

        return this;
    }

    public Partition getPartition() {

        if (partition != null) {
            return partition;
        } else {
            if (combination == null) {
                throw new RuntimeException("PartitionBuilder does not have valid bit list set.");
            } else {
                partition = new Partition(combination, filters);
                return partition;
            }
        }
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        if (combination != null) {
            stringBuilder.append("Combination: " + combination.toString() + "\n");
        } else {
            stringBuilder.append("Combination: null\n");
        }

        for (BitList filter : filters) {
            stringBuilder.append(filter.toString() + "\n");
        }

        return stringBuilder.toString();
    }
}
