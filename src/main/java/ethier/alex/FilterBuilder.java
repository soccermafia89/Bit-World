package ethier.alex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class FilterBuilder {
	private int worldLength;
	private StringBuilder stringBuilderPos;
	private StringBuilder stringBuilderNeg;

	public FilterBuilder(int myWorldLength) {
		worldLength = myWorldLength;
		stringBuilderPos = new StringBuilder();
		stringBuilderNeg = new StringBuilder();
	}

	public void addFilter(String filter) throws InvalidFiltersException {

		filter = this.format(filter);

		char filterType = filter.substring(0, 1).toCharArray()[0];
		filter = filter.substring(1);

		if (filter.length() != worldLength) {
			throw new InvalidFiltersException("Invalid Filter Length: " + filter.length());
		}

		if (filterType == '+') {
			stringBuilderPos.append(filter + ",");
			return;
		} else if (filterType == '-') {
			stringBuilderNeg.append(filter + ",");
			return;
		}
		
		throw new InvalidFiltersException("Invalid Filter Type Passed: " + filter + " Type: " + filterType);
	}

	public void addFlexFilter(String filter) {

		filter = this.format(filter);
		char filterType = filter.substring(0, 1).toCharArray()[0];
		filter = filter.substring(1);

		if (filter.length() > worldLength) {
			filter = filter.substring(0, worldLength);
		} else if (filter.length() < worldLength) {
			filter = StringUtils.rightPad(filter, worldLength, '*');
		}

		filter = filterType + filter;

		try {
			this.addFilter(filter);
		} catch (InvalidFiltersException e) {
			e.printStackTrace();
		}
	}

	public List<String> getPosFilters() {
		String posFilters = stringBuilderPos.toString();
		if (posFilters.contains(",")) {
			String[] posFiltersArray = posFilters.split(",");
			return new ArrayList<String>(Arrays.asList(posFiltersArray));
		} else {
			return new ArrayList<String>();
		}
	}

	public List<String> getNegFilters() {
		String negFilters = stringBuilderNeg.toString();
		if (negFilters.contains(",")) {
			String[] negFiltersArray = negFilters.split(",");
			return new ArrayList<String>(Arrays.asList(negFiltersArray));
		} else {
			return new ArrayList<String>();
		}
	}

	private String format(String comb) {
		return comb.replace('{', 'r').replace('}', 'r').replace('|', 'r').replaceAll("r", "");
	}

	public String toString() {

		String toReturn = "";
		for (String filter : this.getPosFilters()) {
			toReturn += "+" + filter + "\n";
		}
		for (String filter : this.getNegFilters()) {
			toReturn += "-" + filter + "\n";
		}

		return toReturn;
	}

	public List<String> getFilters() {
		List<String> filters = new ArrayList<String>();
		for(String posFilter : this.getPosFilters()) {
			filters.add("+" + posFilter);
		}
		for(String negFilter : this.getNegFilters()) {
			filters.add("-" + negFilter);
		}

		return filters;
	}

	public void setWorldLength(int myWorldLength) {
		worldLength = myWorldLength;
		List<String> filters = this.getFilters();

		stringBuilderPos = new StringBuilder();
		stringBuilderNeg = new StringBuilder();

		for (String filter : filters) {
			//If a filter contains non '*' passed the required length, remove it.
			if (filter.length() > myWorldLength) {
				String checkString = filter.substring(worldLength + 1); 
				
				if (StringUtils.countMatches(checkString, "*") == checkString.length()) {
					String tmpFilter = filter.substring(0, worldLength + 1);
					this.addFlexFilter(tmpFilter);
				}
			} else {
				this.addFlexFilter(filter);
			}
		}
	}
}
