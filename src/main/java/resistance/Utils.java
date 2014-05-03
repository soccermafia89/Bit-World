package resistance;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.StringWriter;
import java.io.ByteArrayOutputStream;

public class Utils {
	public static String applyFormat(String combination, int numPlayers) {

		String filterType = "" + combination.substring(0, 1).toCharArray()[0];

		if (filterType.equals("+") || filterType.equals("-")) {
			combination = combination.substring(1);
		} else {
			filterType = "";
		}

		String players = combination.substring(0, numPlayers);
		String votes = combination.substring(numPlayers);

		char[] votesArray = votes.toCharArray();
		String formattedVotes = "";
		int count1 = 0;
		int count2 = 0;
		for (int i = 0; i < votesArray.length; i++) {
			if (count1 == 0) {
				formattedVotes += '{';
			}

			formattedVotes += votesArray[i];

			count1++;
			count2++;

			if (count1 == 2) {
				formattedVotes += '}';
				count1 = 0;
			}
			if (count2 == numPlayers * 2) {
				formattedVotes += '|';
				count2 = 0;
			}
		}

		return filterType + players + '|' + formattedVotes;
	}
	
	public static String getStackTrace(Exception e) {
		
		OutputStream out = new ByteArrayOutputStream();
		PrintStream printer = new PrintStream(out);
		
		e.printStackTrace(printer);

		return out.toString();
	}
}
