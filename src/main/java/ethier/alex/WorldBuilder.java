package ethier.alex;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class WorldBuilder {
	int worldLength;
	int combLimit;
	int currentCombs;
	Set<String> combinations;

	public WorldBuilder(int myWorldLength, FilterBuilder filterBuilder, int myCombLimit) throws Exception {
		worldLength = myWorldLength;
		combLimit = myCombLimit;
		currentCombs = 0;

		String world = this.generateWorld(filterBuilder.getPosFilters());
		combinations = this.refineWorld(world, filterBuilder.getNegFilters());
	}

	//	public WorldBuilder(int myWorldLength, List<String> posFilters, List<String> negFilters, int myCombLimit) throws InvalidFiltersException {
	//		worldLength = myWorldLength;
	//		combLimit = myCombLimit;
	//		currentCombs = 0;
	//
	//		String world = this.generateWorld(posFilters);
	//		combinations = this.refineWorld(world, negFilters);
	//	}

	public Set<String> getCombinations() {
		return combinations;
	}

	public Set<String> refineWorld(String world, List<String> negFilters) throws Exception {
		try {
			return this.buildCombTree(world, negFilters, 0);
		} catch (CombinationOverflowException e) {
			System.out.println("Combination Overflow Limit Reached: " + currentCombs);
		}
		return null;
	}

	public Set<String> buildCombTree(String combTree, List<String> negFilters, int root) throws Exception {
		if (currentCombs > combLimit) {
			throw new CombinationOverflowException();
		}
		
		System.out.println("");
		System.out.println("[" + combTree + "] : " + root);

		Set<String> returnCombs = new HashSet<String>();

		//This is somewhat redundant, but first check all the filters to see if we can immediately throw away some filters
		//Or immediately throw away the combination
		char[] combChars = combTree.toCharArray();
		Iterator<String> it = negFilters.iterator();
		while (it.hasNext()) {
			//for(String negFilter : negFilters) {
			String negFilter = it.next();
			char[] filterChars = negFilter.toCharArray();

			String reducedFilter = "";
			String reducedComb = "";
			for (int i = 0; i < filterChars.length; i++) {
				char filterChar = filterChars[i];
				if (filterChar != '*') {
					reducedFilter += filterChar;
					reducedComb += combChars[i];
				}
			}

			if (reducedComb.equals(reducedFilter)) {
				System.out.println("Match Prefound, Removing Combination!");
				return returnCombs;//A filter matches the comb and we can return.
			} else if (reducedComb.contains("-")) {
				continue;//We don't know if there is a match yet and must continue.
			} else {
				it.remove();//The filter will never match the comb and should be removed.
			}
		}

		//Next just check each filter against the single root to determine the proper branching pattern

		//Sort all filters into zero or one filters, filters are either '1', '0', or '*'
		//Note that '*' filters get added to both lists.
		//Depending on the combTree, each list may be discarded or kept.
		List<String> zeroNegFilters = new ArrayList<String>();
		List<String> oneNegFilters = new ArrayList<String>();
		Set<Character> setFilterTypes = new HashSet<Character>();//Keep track of setChecks (in case all are '*')

		for (String negFilter : negFilters) {
			char setFilter = negFilter.substring(root, root + 1).toCharArray()[0];
			if (setFilter == '*' || setFilter == '0') {
				zeroNegFilters.add(negFilter);
				setFilterTypes.add(setFilter);
			}
			if (setFilter == '*' || setFilter == '1') {
				oneNegFilters.add(negFilter);
				setFilterTypes.add(setFilter);
			}
		}
		//Done sorting the negFilters into our appropriate lists.

		//setCheck is either '-', '1', '0'
		//If it is a '1', discard all '0' filters and continue.
		//If it is a '0' discard all '1' filters and continue.
		//If it is a '-' then traverse both paths of the tree discarding the appropriate filter list.
		List<String> nextNegFilters = new ArrayList<String>();
		char setCheck = combTree.substring(root, root + 1).toCharArray()[0];
		if (setCheck == '1') {
			nextNegFilters.addAll(oneNegFilters);
		} else if (setCheck == '0') {
			nextNegFilters.addAll(zeroNegFilters);
		} else if (setCheck == '-') {
			nextNegFilters.addAll(oneNegFilters);
			nextNegFilters.addAll(zeroNegFilters);
		}

//		System.out.println("CombTree: [" + combTree + "] SetCheck: '" + setCheck + "' Root: " + root);

		//Before calling the next level of recursion, if there are no more filters, return the combination.

		if (nextNegFilters.size() == 0) {
			//When no filters are left, return the passed combination (replacing all '-' with '*')
			System.out.println("No filters left! Returning Comb!");
			combTree = combTree.replace('-', '*');
			returnCombs.add(combTree);
			return returnCombs;
		} else {
			if (root == worldLength - 1) {
				//TODO: Technically this is obsolete checking due to the previous PRE FOUND check system.
				if (setCheck == '-') {
					if (oneNegFilters.size() > 0 && zeroNegFilters.size() > 0) {
						//If setCheck is '-' and both filter lists are full remove the combination.
						return returnCombs;
					} else if (zeroNegFilters.size() == 0) {
						//If zero filters are empty, add combTree with the 0
						String oneCombTree = combTree.substring(0, root) + '0';
						returnCombs.add(oneCombTree);
						return returnCombs;
					} else if (oneNegFilters.size() == 0) {
						//If one filters are empty, add combTree with the 1
						String oneCombTree = combTree.substring(0, root) + '1';
						returnCombs.add(oneCombTree);
						return returnCombs;
					}
				} else {
					//If the combination is at the last root  and set check is not '-' discard it if there are remaining filters.
					System.out.println("Root ended with filters, removing!");
					return returnCombs;
				}
			} else {
				if (setCheck == '1' || setCheck == '0') {
					//If there are already posFilters in place, then continue without altering the combTree
					//Only pass the necessary filters.
					System.out.println("Pos Filters in place, moving to next root!");
					return this.buildCombTree(combTree, nextNegFilters, root + 1);
				} else if (setCheck == '-') {
					if (setFilterTypes.size() == 1 && setFilterTypes.contains('*')) {
						//In the event that all filter types are '*', then only traverse one tree with '*' in the combTree
						System.out.println("Only '*' Filters Left!");

						String starCombTree = combTree.substring(0, root) + '*' + combTree.substring(root + 1);
						return this.buildCombTree(starCombTree, nextNegFilters, root + 1);
					} else {
						//Otherwise traverse both branches of the combTree, passing the requisite filters.
						System.out.println("Branching Tree!");

						String zeroCombTree = combTree.substring(0, root) + '0' + combTree.substring(root + 1);
						Set<String> zeroCombs = this.buildCombTree(zeroCombTree, zeroNegFilters, root + 1);

						String oneCombTree = combTree.substring(0, root) + '1' + combTree.substring(root + 1);
						Set<String> oneCombs = this.buildCombTree(oneCombTree, oneNegFilters, root + 1);

						returnCombs.addAll(zeroCombs);
						returnCombs.addAll(oneCombs);
						return returnCombs;
					}
				}
			}
		}

		throw new Exception("Invalid State Reached!");
	}

	//	public Set<String> buildCombTree(String combTree, List<String> negFilters, int root) throws CombinationOverflowException {
	//		if (currentCombs > combLimit) {
	//			throw new CombinationOverflowException();
	//		}
	//
	//		List<String> zeroNegFilters = new ArrayList<String>();//Create a copy (TODO MAKE SURE IT IS A DUPLICATE COPY)
	//		List<String> oneNegFilters = new ArrayList<String>();//Create a copy (TODO MAKE SURE IT IS A DUPLICATE COPY)
	//
	//		//		System.out.println("Processing Comb: " + combTree);
	//		System.out.println("");
	//		System.out.println("Processing Comb: " + combTree + " root: " + root + " setCheck: " + combTree.substring(root, root + 1));
	//		System.out.println("Filters:");
	//		for (String filter : negFilters) {
	//			System.out.println(filter);
	//		}
	//
	//		char setCheck = combTree.substring(root, root + 1).toCharArray()[0];
	//		Set<Character> setFilterTypes = new HashSet<Character>();//Keep track of setChecks (in case all are '*')
	//
	//		boolean onePasses = false;
	//		boolean zeroPasses = false;
	//
	//		//First set the passes based on the positive filters
	//		if (setCheck == '-') {
	//			onePasses = true;
	//			zeroPasses = true;
	//		} else if (setCheck == '0') {
	//			zeroPasses = true;
	//			onePasses = false;
	//		} else if (setCheck == '1') {
	//			zeroPasses = false;
	//			onePasses = true;
	//		}
	//
	//		if (setCheck == '-') {
	//			System.out.println("PROCCESSING NEG FILTERS ON '-'");
	//		}
	//
	//		//Now apply negative filters to see if it still passes.
	//		if (zeroPasses) {//Assume setCheck == '0'
	//			for (String negFilter : negFilters) {
	//				if (setCheck == '-') {
	//					System.out.println("PROCCESSING NEG FILTER: " + negFilter);
	//				}
	//
	//				char setFilter = negFilter.substring(root, root + 1).toCharArray()[0];
	//				if (setFilter != '1') {//If it is a possible match, keep the filter
	//					System.out.println("ADDING ZERO FILTER: " + negFilter);
	//					zeroNegFilters.add(negFilter);
	//					setFilterTypes.add(setFilter);
	//				}
	//			}
	//		}
	//
	//		if (onePasses) {//Assume setCheck == '1'
	//			for (String negFilter : negFilters) {
	//				if (setCheck == '-') {
	//					System.out.println("PROCCESSING NEG FILTER: " + negFilter);
	//				}
	//
	//				char setFilter = negFilter.substring(root, root + 1).toCharArray()[0];
	//				System.out.println("SET FILTER: " + setFilter);
	//				if (setFilter != '0') {//If it is a possible match, keep the filter
	//					System.out.println("ADDING ONE FILTER: " + negFilter);
	//					oneNegFilters.add(negFilter);
	//					setFilterTypes.add(setFilter);
	//				}
	//			}
	//		}
	//
	//		//To check
	//		//If combination survives (is an exact match)
	//		//If it does survive, how well does it survive?
	//		Set<String> returnSet = new HashSet<String>();
	//
	//		if (zeroNegFilters.size() == 0 && oneNegFilters.size() == 0) {
	//			System.out.println("ALL FILTERS EMPTY COMB PASSES!");
	//			//The combination survives completely!
	//			currentCombs++;
	//			returnSet.add(combTree.replace('-', '*'));//Return the full combTree replacing '-' with '*'
	//			return returnSet;
	//		} else if (root == worldLength) {
	//			System.out.println("ROOT LIMIT REACHED!");
	//			//An exact match exists!
	//			return returnSet;//Return nothing
	//		} else if (zeroNegFilters.size() > 0 && oneNegFilters.size() > 0) {
	//			//Both one and zero filter lists may match
	//
	//			if (setFilterTypes.size() == 1) {//The only possibility is that all filters are a '*'
	//				System.out.println("ONLY '*' FILTERS MATCHED!");
	//				//Replace combTree with '*' and move on
	//				combTree = combTree.substring(0, root) + '*' + combTree.substring(root + 1);
	//				List<String> allFilters = zeroNegFilters;
	//				allFilters.addAll(oneNegFilters);
	//				return this.buildCombTree(combTree, allFilters, root + 1);
	//			} else {
	//				System.out.println("SPLITTING COMB TREE!");
	//				//Traverse both possibilities of combTree ('0' and '1')
	//				String zeroCombTree = combTree.substring(0, root) + '0' + combTree.substring(root + 1);
	//				Set<String> zeroCombs = this.buildCombTree(zeroCombTree, zeroNegFilters, root + 1);
	//
	//				String oneCombTree = combTree.substring(0, root) + '1' + combTree.substring(root + 1);
	//				Set<String> oneCombs = this.buildCombTree(oneCombTree, oneNegFilters, root + 1);
	//
	//				zeroCombs.addAll(oneCombs);
	//				return zeroCombs;
	//			}
	//
	//		} else if (oneNegFilters.size() == 0) {
	//			//Only zero filters are left!
	//			System.out.println("ONLY ZERO FILTERS LEFT!");
	//			returnSet.add(combTree.substring(0, root) + '1' + combTree.substring(root + 1));
	//			
	//			String zeroCombTree = combTree.substring(0, root) + '0' + combTree.substring(root + 1);
	//			returnSet.addAll(this.buildCombTree(zeroCombTree, zeroNegFilters, root + 1));
	//			
	//			return returnSet;
	//		} else if (zeroNegFilters.size() == 0) {
	//			//Only one filters are left!
	//			System.out.println("ONLY ONE FILTERS LEFT!");
	//			returnSet.add(combTree.substring(0, root) + '0' + combTree.substring(root + 1));
	//			
	//			String oneCombTree = combTree.substring(0, root) + '1' + combTree.substring(root + 1);
	//			returnSet.addAll(this.buildCombTree(oneCombTree, oneNegFilters, root + 1));
	//			
	//			return returnSet;
	//		}
	//
	//		System.out.println("Invalid State Reached!");
	//		return null;
	//	}

	//	public boolean passesNegFilters(String combTree, String negFilter) {
	//		//		System.out.println("");
	//		//		System.out.println("Processing Neg Filter: " + filter + " on " + comb);
	//
	//		//Determine all locations of a '*' and remove that index from both strings	
	//		int index = -1;
	//		while ((index = negFilter.indexOf("*")) > -1) {
	//			combTree = combTree.substring(0, index) + combTree.substring(index + 1);
	//			negFilter = negFilter.substring(0, index) + negFilter.substring(index + 1);
	//		}
	//
	//		for (int i = 0; i < negFilter.length(); i++) {
	//			//			System.out.println("f: " + filter.substring(i, i + 1));
	//
	//			String f = negFilter.substring(i, i + 1);
	//
	//			if (f.equals("*")) {
	//				continue;//It matches everything
	//			} else {
	//				//				System.out.println("c: " + comb.substring(i, i + 1));
	//
	//				String c = combTree.substring(i, i + 1);
	//				if (!f.equals(c)) {//If it doesn't match return true
	//					return true;
	//				}
	//			}
	//		}
	//
	//		//If it matches
	//		return false;
	//	}

	public String generateWorld(List<String> posFilters) throws InvalidFiltersException {
		String world = "";

		for (int i = 0; i < worldLength; i++) {
			boolean hasOne = false;
			boolean hasZero = false;

			for (String posFilter : posFilters) {
				char f = posFilter.substring(i, i + 1).toCharArray()[0];
				if (f == '1') {
					hasOne = true;
				} else if (f == '0') {
					hasZero = true;
				}
			}

			if (hasOne && hasZero) {
				throw new InvalidFiltersException("Conflicting Pos Filters Passed.");
			} else if (hasOne) {
				world += '1';
			} else if (hasZero) {
				world += '0';
			} else {
				world += '-';
			}
		}

		return world;
	}
}
