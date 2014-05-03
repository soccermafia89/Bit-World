/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ethier.alex.core.data;

/**

 @author alex
 */

//Wrap Bit[] in a java type.
public class BitList {
    
    private Bit[] combination;
    private int splitIndex = -1; //-1 is synonymous with N/A.
    
    public BitList(Bit[] myCombination) {
        combination = myCombination;
        
        for(int i=0; i < combination.length;i++) {
                
            Bit bit = combination[i];
            if(bit == Bit.UNSET) {
                splitIndex = i;
            }
        }
    }
    
    public Bit[] getBitArray() {
        return combination;
    }
    
    public boolean hasSplit() {
        if(splitIndex > 0) {
            return true;
        } else {
            return false;
        }
    }
    
    public int getSplitIndex() {
        return splitIndex;
    }
    
    public BitList[] split() {
        Bit[] splitOne = new Bit[combination.length];
        Bit[] splitZero = new Bit[combination.length];

        splitOne[splitIndex] = Bit.ONE;
        splitZero[splitIndex] = Bit.ZERO;

        BitList bitListOne = new BitList(splitOne);
        BitList bitListZero = new BitList(splitZero);

        BitList[] bitLists = new BitList[2];
        bitLists[0] = bitListZero;
        bitLists[1] = bitListOne;

        return bitLists;     
    }
    
    
    
    public Matches getMatch(BitList otherList) {
        
        boolean possibleMatch = false;
        
        Bit[] otherCombination = otherList.getBitArray();
        
        for(int i=0; i < combination.length;i++) {
            Bit bit = combination[i];
            Bit otherBit = otherCombination[i];
            
            //Order of if statements matters!
            //1) If either has a Both then they match
            //2) If 1 is false, and either has an UNSET it is now a part match.
            //3) If the bits match, then they still match.
            //4) Otherwise they completely don't match.
            if(bit == Bit.BOTH || otherBit == Bit.BOTH) {
                continue;
            } else if(bit == Bit.UNSET || otherBit == Bit.UNSET) {
                possibleMatch = true; // No longer a match, only a possible match.
            } else if(bit == otherBit) {
                continue;
            } else {
                return Matches.NO;
            }
        }
        
        if(possibleMatch) {
            return Matches.PARTLY;
        } else {
            return Matches.ENTIRELY;
        }        
    }
}
