package com.machine.learning.util;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.List;
import java.util.Map;

public class ValueDifferenceMetric {
    List<Map<String, AtomicInteger>> attributeCount;
    List<Map<String, Map<String, AtomicInteger>>> attributeClassCount;
    List<Map<String, Map<String, Double>>> attributeDistance;

    private int numAttributes;
    
    public ValueDifferenceMetric(List<DataPoint> dataPoints) {
	if (dataPoints.size() > 0) {
	    numAttributes = dataPoints.get(0).getAttributes().size();
	}
	makeAttributeLists();
	
	for (DataPoint dataPoint : dataPoints) {
	    String classLabel = dataPoint.getClassLabel();
	    List attributes = dataPoint.getAttributes();

	    for (int attrNum = 0; attrNum < attributes.size(); attrNum++) {
		String attributeValue = (String)attributes.get(attrNum);

		ensureAttributeClass(attrNum, attributeValue, classLabel);
		attributeCount.get(attrNum).get(attributeValue).incrementAndGet();
	    }
	}
    }

    private void makeAttributeLists() {
	attributeCount = new ArrayList<>();
	attributeClassCount = new ArrayList<>();
	attributeDistance = new ArrayList<>();

	for (int i = 0; i < numAttributes; i++) {
	    attributeCount.add(new Map<String, AtomicInteger>());
	    attributeClassCount.add(new Map<String, Map<String, AtomicInteger>>());
	    attributeDistance.add(new Map<String, Map<String, Double>>());
	}
    }

    private void ensureAttributeClass(int attributeNum, String attributeValue, String classLabel) {
	if(!attributeCount.get(attributeNum).containsKey(attributeValue)) {
	    attributeCount.get(attributeNum).put(attributeValue, new AtomicInteger());
	    attributeClassCount.get(attributeNum).put(attributeValue, new Map<String, AtomicInteger>());
	}

	if(!attributeClassCount.get(attributeNum).get(attributeValue).containsKey(classLabel)) {
	    attributeClassCount.get(attributeNum).get(attributeValue).put(classLabel, new AtomicInteger());
	}
    }

    public double calculateDistance(List pointA, List pointB) {
	if (numAttributes == 0)
	    return 0.0;
	
    }
}
