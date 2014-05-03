/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ethier.alex.core.addon;

//import org.apache.log4j.*;

import ethier.alex.core.data.Bit;
import ethier.alex.core.data.BitList;
import org.apache.log4j.Logger;

/**

 @author alex
 */

//Should be made into a static helper type method since it is not required to save state.
public class StringFilter {
    
    private static Logger logger = Logger.getLogger(StringFilter.class);
    private BitList combination;
    
    public StringFilter(String inputStr) {
        String cleansedInput = cleanseInput(inputStr);
        char[] inputChars = cleansedInput.toCharArray();
        
        Bit[] filterBits = new Bit[inputChars.length];
        
        for(int i=0;i < filterBits.length;i++) {
            if(inputChars[i] == '0') {
                filterBits[i] = Bit.ZERO;
            } else if(inputChars[i] == '1' ) {
                filterBits[i] = Bit.ONE;
            } else if(inputChars[i] == '*') {
                filterBits[i] = Bit.BOTH;
            } else {
                logger.error("Unknown filter input char received: " + inputChars[i]);
            }
        }
        
        combination = new BitList(filterBits);
    }
    
    public BitList getCombination() {
        return combination;
    }
        
    private String cleanseInput(String input) {
        StringBuilder cleansedInputBuilder = new StringBuilder();
        
        for(char inputChar : input.toCharArray()) {
            if(inputChar == '0' || inputChar == '1' || inputChar == '*') {
                cleansedInputBuilder.append(inputChar);
            }
        }
        
        return cleansedInputBuilder.toString();
    }
}
