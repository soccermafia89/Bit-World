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
public class BitListBuilder {
    
    private static Logger logger = Logger.getLogger(BitListBuilder.class);
    
    public static BitList buildBitList(String inputStr) {
        String cleansedInput = cleanseInput(inputStr);
        char[] inputChars = cleansedInput.toCharArray();
        
        Bit[] bitList = new Bit[inputChars.length];
        
        for(int i=0;i < bitList.length;i++) {
            if(inputChars[i] == '0') {
                bitList[i] = Bit.ZERO;
            } else if(inputChars[i] == '1' ) {
                bitList[i] = Bit.ONE;
            } else if(inputChars[i] == '*') {
                bitList[i] = Bit.BOTH;
            } else if(inputChars[i] == '-') {
                bitList[i] = Bit.UNSET;
            } else {
                logger.error("Unknown filter input char received: " + inputChars[i]);
            }
        }
        
        return new BitList(bitList);
    }
        
    private static String cleanseInput(String input) {
        StringBuilder cleansedInputBuilder = new StringBuilder();
        
        for(char inputChar : input.toCharArray()) {
            if(inputChar == '0' || inputChar == '1' || inputChar == '*' || inputChar == '-') {
                cleansedInputBuilder.append(inputChar);
            }
        }
        
        return cleansedInputBuilder.toString();
    }
}
