package it.unifi.simonesantarsiero.wcgraphs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static it.unifi.simonesantarsiero.wcgraphs.commons.Utils.VALUE_TIME;

public class AlgorithmResults {
	private List<Map<String, Object>> list;
	private final String algorithmName;

	public AlgorithmResults(String algorithmName) {
		this.algorithmName = algorithmName;
		list = new ArrayList<>();
	}

	public String getAlgorithmName() {
		return algorithmName;
	}

	public void add(Map<String, Object> result) {
		list.add(result);
	}

	public int size() {
		return list.size();
	}

	public Map<String, Object> get(int i) {
		return list.get(i);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (Map<String, Object> stringObjectMap : list) {
			builder.append(stringObjectMap)
					.append("\n");
		}
		return builder.toString();
	}

	public double getMean() {
		double sum = 0.0;
		for (Map<String, Object> stringObjectMap : list) {
			double time = (double) stringObjectMap.get(VALUE_TIME);
			sum += time;
		}
		return roundAvoid(sum / list.size(), 4);
	}

	// truncate the value to the 'places' decimal
	private double roundAvoid(double value, int places) {
		double scale = Math.pow(10, places);
		return Math.round(value * scale) / scale;
	}
}
