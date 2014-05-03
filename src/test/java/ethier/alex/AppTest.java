package ethier.alex;

import java.util.Collection;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import resistance.ResistanceGame;
import resistance.Utils;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest
		extends TestCase
{
	/**
	 * Create the test case
	 * 
	 * @param testName
	 *            name of the test case
	 */
	public AppTest(String testName)
	{
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite()
	{
		return new TestSuite(AppTest.class);
	}

	public void testWorldBuilderSimple() throws Exception
	{
		System.out.println("");
		System.out.println("");
		System.out.println("********************************************");
		System.out.println("*****    World Builder Simple Test    ******");
		System.out.println("********************************************");
		System.out.println("");
		System.out.println("");

		try {
			//Test basic setup
			int worldLength = 5;
			int combLimit = 10000;

			FilterBuilder filterBuilder = new FilterBuilder(worldLength);
			filterBuilder.addFilter("+*****");
			filterBuilder.addFilter("+*1***");
			filterBuilder.addFilter("+0****");

			System.out.println("Testing Filters:");
			System.out.println(filterBuilder.toString());

			WorldBuilder worldBuilder = new WorldBuilder(worldLength, filterBuilder, combLimit);
			Set<String> combinations = worldBuilder.getCombinations();

			System.out.println("Combinations Found: " + combinations.size());
			assertTrue(combinations.size() == 1);
			for (String combination : combinations) {
				System.out.println(combination);
				assertTrue(combination.equals("01***"));
			}

			//Test conflicting rules
			try {
				filterBuilder.addFilter("+1****");
				filterBuilder.addFilter("+0****");
				worldBuilder = new WorldBuilder(worldLength, filterBuilder, combLimit);

				assertTrue(false);//Code should not reach here.
			} catch (InvalidFiltersException e) {
				assertTrue(true);//Code should reach here.
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}

		assertTrue(true);
	}

	public void testWorldBuilderFull() throws Exception {
		System.out.println("");
		System.out.println("");
		System.out.println("********************************************");
		System.out.println("*****     World Builder Full Test     ******");
		System.out.println("********************************************");
		System.out.println("");
		System.out.println("");

		try {
			//Test basic setup
			int worldLength = 5;
			int combLimit = 10000;

			FilterBuilder filterBuilder = new FilterBuilder(worldLength);
			filterBuilder.addFilter("+*****");
			filterBuilder.addFilter("+*1***");
			filterBuilder.addFilter("+0****");

			filterBuilder.addFilter("-**1**");

			System.out.println("Testing Filters:");
			System.out.println(filterBuilder.toString());

			WorldBuilder worldBuilder = new WorldBuilder(worldLength, filterBuilder, combLimit);
			Set<String> combinations = worldBuilder.getCombinations();

			System.out.println("Combinations Found: " + combinations.size());
			assertTrue(combinations.size() == 1);
			for (String combination : combinations) {
				System.out.println(combination);
				assertTrue(combination.equals("010**"));
			}

			//Test another basic setup
			worldLength = 3;
			combLimit = 10000;
			filterBuilder = new FilterBuilder(worldLength);
			filterBuilder.addFilter("+*1*");

			filterBuilder.addFilter("-0*0");

			System.out.println("");
			System.out.println("");
			System.out.println("Testing Filters:");
			System.out.println(filterBuilder.toString());

			worldBuilder = new WorldBuilder(worldLength, filterBuilder, combLimit);
			combinations = worldBuilder.getCombinations();
			System.out.println("TODO: HAVE COMBINATION EXPANDER!");
			System.out.println("THEN ADD THIS TEST CASE!");
			System.out.println("Combinations Found: " + combinations.size());
			//assertTrue(combinations.size() == 2);
			for (String combination : combinations) {
				System.out.println(combination);
				//assertTrue(combinations.equals("11*") || combinations.equals("*11"));
			}

			//Test a more moderately intricate pattern
			worldLength = 7;
			combLimit = 10000;
			filterBuilder = new FilterBuilder(worldLength);
			filterBuilder.addFilter("+*1*|****");
			filterBuilder.addFilter("+**0|****");

			filterBuilder.addFilter("-***|1**1");
			filterBuilder.addFilter("-***|*000");
			filterBuilder.addFilter("-***|1011");

			System.out.println("");
			System.out.println("");
			System.out.println("Testing Filters:");
			System.out.println(filterBuilder.toString());

			worldBuilder = new WorldBuilder(worldLength, filterBuilder, combLimit);
			combinations = worldBuilder.getCombinations();
			System.out.println("TODO: HAVE COMBINATION EXPANDER!");
			System.out.println("THEN ADD THIS TEST CASE!");
			System.out.println("Combinations Found: " + combinations.size());
			//assertTrue(combinations.size() == 2);
			for (String combination : combinations) {
				System.out.println(combination);
				//assertTrue(combinations.equals("11*") || combinations.equals("*11"));
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

	public void testComplementTest() throws Exception {
		System.out.println("");
		System.out.println("");
		System.out.println("********************************************");
		System.out.println("********      Complement Test      *********");
		System.out.println("********************************************");
		System.out.println("");
		System.out.println("");
		
		//This is testing the complement done by creating an arbitrary set of allowed combinations
		//Then creating filters out of them, applying and getting the new set of combinations (which is the complement)
		//Do the process again to get the complement of the complement and we should get back to what we started.
		
		//Note the complement is a great way to determine the efficiency of the algorithm.
		
		
		try {
//			int ones = 9;
//			int worldLength = 18;
			int ones = 2;
			int worldLength = 4;
			
			FilterBuilder filterBuilder = new FilterBuilder(worldLength);

			String breakStr = "";
			for (int i = 0; i < worldLength; i++) {
				breakStr += '1';
			}

			String comb = "";
			int count = 0;
			while (true) {
				if (comb.equals(breakStr)) {
					break;
				}

				comb = "" + Integer.toBinaryString(count);
				comb = StringUtils.leftPad(comb, worldLength, '0');
				int combOnes = StringUtils.countMatches(comb, "1");
				if (combOnes == ones) {
					filterBuilder.addFilter("-" + comb);
				}

				count++;
			}

			WorldBuilder worldBuilder = null;
			worldBuilder = new WorldBuilder(worldLength, filterBuilder, (int) Math.pow(2, worldLength));

			Set<String> combinations = worldBuilder.getCombinations();
			
			filterBuilder = new FilterBuilder(worldLength);
			for (String combination : combinations) {
				System.out.println(combination);
				filterBuilder.addFilter("-" + combination);
			}
			
			try {
				worldBuilder = new WorldBuilder(worldLength, filterBuilder, 10000);
				combinations = worldBuilder.getCombinations();

				for (String combination : combinations) {
					System.out.println(combination);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			System.out.println("Combinations found: " + combinations.size());

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		
		assertTrue ( true );
	}
	
	public void testResistance3Player() throws Exception {
		System.out.println("");
		System.out.println("");
		System.out.println("********************************************");
		System.out.println("********      Complement Test      *********");
		System.out.println("********************************************");
		System.out.println("");
		System.out.println("");
		
		int numPlayers = 3;
		int numSpies = 1;
		
		try {
			ResistanceGame game = new ResistanceGame(numPlayers, numSpies);
			
			//Round one players 1 and 2 vote and there is one fail.
			game.addFilter("+***|{**}{**}{10}");//Player 3 did not vote.
			
			game.addFilter("-***|{00}{00}{**}");//There is at least one fail
			game.addFilter("-***|{10}{**}{**}");//Player one voted.
			game.addFilter("-***|{01}{**}{**}");//Player one voted.
			game.addFilter("-***|{**}{10}{**}");//Player two voted.
			game.addFilter("-***|{**}{01}{**}");//Player two voted.
			
			//Round two players 2 and 3 vote and there is one fail.
			game.addFilter("+***|{**}{**}{**}|{10}{**}{**}");//Player 1 did not vote.
			
			game.addFilter("-***|{**}{**}{**}|{**}{00}{00}");//There is at least one fail
			game.addFilter("-***|{**}{**}{**}|{**}{**}{10}");//Player three voted.
			game.addFilter("-***|{**}{**}{**}|{**}{**}{01}");//Player three voted.
			game.addFilter("-***|{**}{**}{**}|{**}{10}{**}");//Player two voted.
			game.addFilter("-***|{**}{**}{**}|{**}{01}{**}");//Player two voted.
			
			Set<String> combinations = game.determineCombinations(2);
			
			System.out.println("Combinations Found!");
			for(String combination : combinations) {
				System.out.println(Utils.applyFormat(combination, numPlayers));
			}
			
			assertTrue ( combinations.size() == 1 );
			assertTrue ( combinations.contains("010001110101100"));
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}
	
	public void testResistanceMockGame1() throws Exception {
		System.out.println("");
		System.out.println("");
		System.out.println("********************************************");
		System.out.println("*******    Resistance Mock Game 1   ********");
		System.out.println("********************************************");
		System.out.println("");
		System.out.println("");
		
		int numPlayers = 3;
		int numSpies = 1;
		
		try {
			ResistanceGame game = new ResistanceGame(numPlayers, numSpies);
			
			game.playRound("110", 1, 1);
			game.playRound("011", 1, 2);
			
			
			Set<String> combinations = game.determineCombinations(2);
			
			System.out.println("Combinations Found!");
			for(String combination : combinations) {
				System.out.println(Utils.applyFormat(combination, numPlayers));
			}
			
			assertTrue ( combinations.size() == 1 );
			assertTrue ( combinations.contains("010001110101100"));
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}
	
	public void testResistanceMockGame2() throws Exception {
		System.out.println("");
		System.out.println("");
		System.out.println("********************************************");
		System.out.println("*******    Resistance Mock Game 2   ********");
		System.out.println("********************************************");
		System.out.println("");
		System.out.println("");
		
		int numPlayers = 5;
		int numSpies = 2;
		
		try {
			ResistanceGame game = new ResistanceGame(numPlayers, numSpies);
			
			game.playRound("11000", 1, 1);
			game.playRound("00111", 1, 2);
			game.playRound("10100", 1, 3);
			game.playRound("01110", 1, 4);
			
			game.addFilter("-**0**|{11}{**}{**}{**}{**}|{**}{**}{**}{**}{**}|{**}{**}{**}{**}{**}|{**}{**}{**}{**}{**}|");
			
			Set<String> combinations = game.determineCombinations(4);
			
			System.out.println("Combinations Found!");
			for(String combination : combinations) {
				System.out.println(Utils.applyFormat(combination, numPlayers));
			}
			
			assertTrue ( true );
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}
}
