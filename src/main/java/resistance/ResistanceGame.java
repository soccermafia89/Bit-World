package resistance;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import ethier.alex.FilterBuilder;
import ethier.alex.InvalidFiltersException;
import ethier.alex.WorldBuilder;

public class ResistanceGame {

	int worldLength;
	int numPlayers;
	int numSpies;
	int numResistance;
	int worldLimit = 10000;

	FilterBuilder filterBuilder;

	public ResistanceGame(int myPlayers, int mySpies) {
		worldLength = myPlayers + 2 * myPlayers * 4;

		numPlayers = myPlayers;
		numSpies = mySpies;
		numResistance = numPlayers - numSpies;

		filterBuilder = ResistanceRules.generateFilters(numPlayers, numSpies);
	}

	public void playRound(String missionMembers, int numFails, int missionNumber) {
		System.out.println("Mission Members: " + missionMembers);
		
		//Mission numbers start counting at one, we use the round number internally which will then start at 0.
		int roundNumber = missionNumber - 1;

		//1 = Went on round.
		//0 = Did not go on round.

		//Add three sets of filters:
		//Set 1: Filters for players who did not go on a mission.
		//Set 2: Filters for players who did go on a mission.
		//Set 3: Filters for invalid voting outcomes (based on actual number of fails).

		//Set 1: Add filters for players who did not vote.
		//First create filter template.

		int count = numPlayers + 2 * numPlayers * roundNumber;
		String template = "+";
		for (int i = 0; i < count; i++) {
			template += "*";
		}

		//Now add positive filters indicating players who did not vote.
		int playerIndex = -1;
		int indexOffset = 0;
		String missionPlayers = missionMembers;
		while ((playerIndex = missionPlayers.indexOf('0')) > -1) {
			
			missionPlayers = StringUtils.substring(missionPlayers, playerIndex + 1);
			indexOffset += playerIndex + 1;
			//System.out.println("Mission Players: " + missionPlayers);
			
			System.out.println("Player: " + (indexOffset - 1) + " did not go on round " + roundNumber);
			String filter = template;

			int voteOffset = 2 * (indexOffset - 1);
			for (int i = 0; i < voteOffset; i++) {
				filter += "*";
			}

			filter += "10";
			System.out.println("Adding Filter: " + Utils.applyFormat(filter, numPlayers));

			this.addFilter(filter);
		}

		//Set 2: Now add filters for players that voted (remove combinations where they didn't vote).
		template = "-" + template.substring(1);

		playerIndex = -1;
		indexOffset = 0;
		missionPlayers = missionMembers;
		while ((playerIndex = missionPlayers.indexOf('1')) > -1) {
			
			missionPlayers = StringUtils.substring(missionPlayers, playerIndex + 1);
			indexOffset += playerIndex + 1;
			
			System.out.println("Player: " + (indexOffset - 1) + " went on round " + roundNumber);
			String filter1 = template;
			String filter2 = template;

			int voteOffset = 2 * (indexOffset - 1);
			for (int i = 0; i < voteOffset; i++) {
				filter1 += "*";
				filter2 += "*";
			}

			filter1 += "10";
			filter2 += "01";
			System.out.println("Adding Filter: " + Utils.applyFormat(filter1, numPlayers));
			System.out.println("Adding Filter: " + Utils.applyFormat(filter2, numPlayers));
			
			this.addFilter(filter1);
			this.addFilter(filter2);
		}
		
		//Set 3: Now add filters for the number of fails that showed up.
		int numMembers = StringUtils.countMatches(missionMembers, "1");
		
		//System.out.println("Num Fails: " + numFails + " Num Passes: " + numPasses);
		//1 = fail
		//0 = pass
		
		FilterBuilder rule2FilterBuilder = new FilterBuilder(numMembers);
		String breakStr = "";
		for (int i = 0; i < numMembers; i++) {
			breakStr += '1';
		}

		String voteComb = "";
		count = 0;
		while (true) {
			if (voteComb.equals(breakStr)) {
				break;
			}

			voteComb = "" + Integer.toBinaryString(count);
			voteComb = StringUtils.leftPad(voteComb, numMembers, '0');
			
//			System.out.println("Possible Vote Comb: " + voteComb);
			
			int fails = StringUtils.countMatches(voteComb, "1");
			//int passes = StringUtils.countMatches(voteComb, "0");
			
			if (fails == numFails) {
//				System.out.println("Adding Vote Comb Filter: " + voteComb);
				rule2FilterBuilder.addFlexFilter("-" + voteComb);
			}

			count++;
		}
		
		WorldBuilder worldBuilder = null;
		try {
			worldBuilder = new WorldBuilder(numMembers, rule2FilterBuilder, (int) Math.pow(numMembers, 2));
		} catch (Exception e) {
			System.out.println("Failed making resistance rules!");
			System.out.println(Utils.getStackTrace(e));
		}

		Set<String> combinations = worldBuilder.getCombinations();
		for (String combination : combinations) {
//			System.out.println("Invalid Comb Found: " + combination);
			
			String partFilter = "";
			//Now build out the actual filter
			for(int i=0; i< missionMembers.length();i++) {
				String member = missionMembers.substring(i, i+1);
				if(member.equals("1")) {
					String combPart = combination.substring(0, 1);
					combination = combination.substring(1);
					
					if(combPart.equals("1")) {
						partFilter += "11";
					} else if(combPart.equals("0")) {
						partFilter += "00";
					}
				} else if(member.equals("0")) {
					partFilter += "**";
				}
			}
			
			String fullFilter = template + partFilter;
//			System.out.println("Part Filter after Mask: " + partFilter);
			System.out.println("Adding Vote Filter: " + Utils.applyFormat(fullFilter, numPlayers));
			this.addFilter(fullFilter);
		}
	}

	public Set<String> determineCombinations(int rounds) throws Exception {

		int worldLength = numPlayers + 2 * numPlayers * rounds;

		filterBuilder.setWorldLength(worldLength);

		//		System.out.println("CHECK1");
		//		System.out.println(filterBuilder.toString());
		//		System.out.println("FILTERS:");
		//		System.out.println(filterBuilder.toString());

		//WorldBuilder worldBuilder = new WorldBuilder(worldLength, filterBuilder, worldLimit);
		WorldBuilder worldBuilder = new WorldBuilder(worldLength, filterBuilder, worldLimit);
		Set<String> combinations = worldBuilder.getCombinations();

		return combinations;
	}

	public void addFilter(String filter) {
		filterBuilder.addFlexFilter(filter);
	}
}
