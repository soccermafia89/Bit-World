package ethier.alex;

import java.util.HashSet;
import java.util.Set;

public class Analyzer {

	Set<String> combinations;

	int numPlayers;
	int numSpies;
	int numResistance;

	public Analyzer(Set<String> myCombinations, int myPlayers, int mySpies) {
		combinations = myCombinations;
		numPlayers = myPlayers;
		numSpies = mySpies;
		numResistance = myPlayers - mySpies;
	}

	public void outputPlayerTypes() {
		//Assume all resistance
		Set<Integer> spies = new HashSet<Integer>();//Contains indicies of possible spies
		Set<Integer> resistance = new HashSet<Integer>();//Contains indicies of possible resistance

		for (String combination : combinations) {
			for (int i = 0; i < numPlayers; i++) {
				String player = combination.substring(0, i + 1);
				if (player.equals('*')) {
					spies.add(i);
					resistance.add(i);
				} else if (player.equals('1')) {
					spies.add(i);
				} else if (player.equals('0')) {
					resistance.add(i);
				}
			}
		}
		
		for(int i=0;i < numPlayers;i++) {
			if(spies.contains(i) && resistance.contains(i)) {
				System.out.println("Player " + i + " Unknown");
			} else if(spies.contains(i)) {
				System.out.println("Player " + i + " Spy");
			} else if(resistance.contains(i)) {
				System.out.println("Player " + i + " Resistance");
			}
		}
	}
}
