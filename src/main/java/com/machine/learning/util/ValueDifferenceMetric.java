package com.machine.learning.util;

import com.machine.learning.model.DataPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class ValueDifferenceMetric {
    List<Map<String, AtomicInteger>> attributeCount;
    List<Map<String, Map<String, AtomicInteger>>> attributeClassCount;
    List<Map<String, Double>> attributeDistance;
    
    Set<String> classLabels;
    
    private int numAttributes;
    
    public ValueDifferenceMetric(List<DataPoint> dataPoints) {
	if (dataPoints.size() > 0) {
	    numAttributes = dataPoints.get(0).getData().get().size();
	}
	makeAttributeLists();
	
	for (DataPoint dataPoint : dataPoints) {
	    String classLabel = dataPoint.getClassLabel().get();
	    List attributes = dataPoint.getData().get();

	    for (int attrNum = 0; attrNum < attributes.size(); attrNum++) {
		String attributeValue = (String)attributes.get(attrNum);

		ensureAttributeClass(attrNum, attributeValue, classLabel);
		attributeCount.get(attrNum).get(attributeValue).incrementAndGet();
		attributeClassCount.get(attrNum).get(attributeValue).get(classLabel).incrementAndGet();
	    }

	    classLabels.add(classLabel);
	}
    }

    private void makeAttributeLists() {
	attributeCount = new ArrayList<>();
	attributeClassCount = new ArrayList<>();
	attributeDistance = new ArrayList<>();

	for (int i = 0; i < numAttributes; i++) {
	    attributeCount.add(new HashMap<String, AtomicInteger>());
	    attributeClassCount.add(new HashMap<String, Map<String, AtomicInteger>>());
	    attributeDistance.add(new HashMap<String, Double>());
	}

	classLabels = new HashSet<>();
    }

    private void ensureAttributeClass(int attributeNum, String attributeValue, String classLabel) {
	if(!attributeCount.get(attributeNum).containsKey(attributeValue)) {
	    attributeCount.get(attributeNum).put(attributeValue, new AtomicInteger());
	    attributeClassCount.get(attributeNum).put(attributeValue, new HashMap<String, AtomicInteger>());
	}

	if(!attributeClassCount.get(attributeNum).get(attributeValue).containsKey(classLabel)) {
	    attributeClassCount.get(attributeNum).get(attributeValue).put(classLabel, new AtomicInteger());
	}
    }
   
    public double calculateDistance(List<String> pointA, List<String> pointB) {
	double sum = 0.0;
	for (int i = 0; i < pointA.size(); i++) {
	    sum += Math.pow(attributeDifference(i, pointA.get(i), pointB.get(i)), 2);
	}
	return Math.pow(sum, 1 / 2.0);
    }

    private static final double Q = 1;
    public double attributeDifference(int attributeNum, String valueA, String valueB) {
	if(valueA.compareTo(valueB) > 0) {
	    String temp = valueA;
	    valueA = valueB;
	    valueB = temp;
	}
	String pairName = valueA + "," + valueB;
	
	if (!attributeDistance.get(attributeNum).containsKey(pairName)) {
	    Map<String, AtomicInteger> classOccurancesA = attributeClassCount.get(attributeNum).get(valueA);
	    Map<String, AtomicInteger> classOccurancesB = attributeClassCount.get(attributeNum).get(valueB);
	    int valueOccurancesA = attributeCount.get(attributeNum).get(valueA).intValue();
	    int valueOccurancesB = attributeCount.get(attributeNum).get(valueB).intValue();

	    double sum = 0.0;
	    for (String classLabel : classLabels) {
		double diff = (classOccurancesA.get(classLabel).doubleValue() / valueOccurancesA -
			       classOccurancesB.get(classLabel).doubleValue() / valueOccurancesB);
		sum += Math.pow(Math.abs(diff), Q);
	    }
	    double res =  Math.pow(sum, 1 / Q);

	    attributeDistance.get(attributeNum).put(pairName, res);
	}
	
	return attributeDistance.get(attributeNum).get(pairName);	    
    }
}
