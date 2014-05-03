package resistance;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import ethier.alex.FilterBuilder;
import ethier.alex.InvalidFiltersException;
import ethier.alex.WorldBuilder;

public class ResistanceRules {
	public static FilterBuilder generateFilters(int numPlayers, int numSpies) {
		try {
			int worldLength = numPlayers + 2 * numPlayers * 4;
			FilterBuilder filterBuilder = new FilterBuilder(worldLength);

			//Rule 1: Resistance members cannot cast fail votes

			//One filter for each round for each player (remove combination where player is resistance and casts a fail vote.)
			String filterTemplate = "";
			for (int i = 0; i < numPlayers + 2 * numPlayers * 4; i++) {
				filterTemplate += '*';
			}

			int playerOffset = numPlayers;
			for (int playerIndex = 0; playerIndex < numPlayers; playerIndex++) {

				int voteOffset = 2 * playerIndex;
				for (int roundIndex = 0; roundIndex < 4; roundIndex++) {
					int roundOffset = 2 * numPlayers * roundIndex;

					int offset = playerOffset + voteOffset + roundOffset;

					//Set player as resistance.
					String newFilter = filterTemplate.substring(0, playerIndex)
							+ "0" + filterTemplate.substring(playerIndex + 1);

					//Set vote as fail vote.
					newFilter = newFilter.substring(0, offset) +
							"11" + newFilter.substring(offset + 2);

					//Set string to a neg filter.
					newFilter = "-" + newFilter;
					System.out.println("Adding Resistance Filter: " + newFilter);
					filterBuilder.addFilter(newFilter);
				}
			}

			//Rule 2: Only a set number of spies and resistance players.
			//Remove all combinations of invalid spy and resistance numbers;
			//Remove all combinations where there are one too many spies
			//Remove all combinations where there are one too many resistance

			//Generate all possible combinations of players
			FilterBuilder rule2FilterBuilder = new FilterBuilder(worldLength);
			int count = 0;

			String breakStr = "";
			for (int i = 0; i < numPlayers; i++) {
				breakStr += '1';
			}

			String playerComb = "";
			while (true) {
				if (playerComb.equals(breakStr)) {
					break;
				}

				playerComb = "" + Integer.toBinaryString(count);
				playerComb = StringUtils.leftPad(playerComb, numPlayers, '0');
				int spies = StringUtils.countMatches(playerComb, "1");
				if (spies == numSpies) {
					rule2FilterBuilder.addFlexFilter("-" + playerComb);
				}

				count++;
			}

			WorldBuilder worldBuilder = null;
			try {
				worldBuilder = new WorldBuilder(numPlayers, rule2FilterBuilder, (int) Math.pow(numPlayers, 2));
			} catch (Exception e) {
				System.out.println("Failed making resistance rules!");
				e.printStackTrace();
			}

			Set<String> combinations = worldBuilder.getCombinations();
			for (String combination : combinations) {
				System.out.println("Adding Resistance Filter: -" + combination);
				filterBuilder.addFlexFilter("-" + combination);
			}
			
			System.out.println(filterBuilder.toString());

			return filterBuilder;
		} catch (InvalidFiltersException e) {
			e.printStackTrace();
			return null;
		}
	}
}