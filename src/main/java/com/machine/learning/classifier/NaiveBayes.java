package com.machine.learning.classifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NaiveBayes implements Classifier {
    //Keeps track of the occurences of each class
    private Map<String, Counter> classCount = new HashMap<>();

    //Keeps track of the occurences of an attribute value for each class
    private Map<String, List<Map<String, Counter>>> attributeCount = new HashMap<>();

    private int numPoints, numAttributes;

    /* Constants for smoothing, assumes some number of possible missing points and a rate of
       occurance of any attribute in those missing points.
    */
    private final double MISSING_POINT_RATE = 0.1; 
    private final double ATTRIBUTE_OCCURANCE_RATE = 0.05;
    
    /**
     * Gather the data to perform classification based on a naive bayes model.
     *
     * This comes down to counting all of the occurences of classes,
     * and counting all of the occurences of attribute values, based on the point's class label.
     *
     * @param dataPoints the points to use to construct the naive bayes model
     * @param classLabel the labels for the given points
     */
    public void train(List<List> dataPoints, List<String> classLabel) {
	numPoints = dataPoints.size();
	if (numPoints > 0) {
	    numAttributes = dataPoints.get(0).size();
	}
	
	for (int i = 0; i < dataPoints.size(); i++) {
	    countPoint(dataPoint.get(i), classLabel.get(i));
	}
    }

    /**
     * Count the occurences of the class label and attributes in a given point.
     *
     * @param dataPoint the point to count attributes on
     * @param classLabel the class label for the given data point
     */
    private void countPoint(List dataPoint, String classLabel) {
	//count the class for the given point
	ensureClass(classLabel);
	classCount.get(classLabel).increment();
	    
	for (int attrNum = 0; attrNum < numAttributes; attrNum++) {
	    String attributeValue = (String)dataPoint.get(attrNum);

	    //count each attribute value for the given point, grouped by the point's class label
	    ensureClassAttribute(classLabel, attrNum, attributeValue);
	    attributeCount.get(classLabel).get(attrNum).get(attributeValue).increment();
	}
    }

    /**
     * Ensure that the given class can be counted.
     * That is, ensure that the counting data structures are fully-built for the given class,
     * and that there is a counter for the class.
     *
     * @param classLabel the class to ensure
     */
    private void ensureClass(String classLabel) {
	if (classCount.containsKey(pointClass))
	    return;
	
	classCount.put(pointClass, new Counter());
	
	List<Map<String, Counter>> classAttributeCounts = new ArrayList<>();
	for (int attrNum = 0; attrNum < numAttributes; attrNum++) {
	    classAttributeCounts.add(new HashMap<String, Counter>());
	}
	
	attributeCount.put(pointClass, classAttributeCounts);
    }

    /**
     * Ensure that a given observed attribute value can be counted.
     * That is, ensure that there is a counter for the attribute value for the given attribute.
     *
     * @param classLabel the class associated with the observed attribute
     * @param attributeNum on which attribute the attribute value is observed
     * @param attributeValue the observed attribute value
     */
    private void ensureClassAttribute(String classLabel, int attributeNum, String attributeValue) {
	if (attributeCount.get(classLabel).get(attributeNum).containsKey(attributeValue))
	    return;

	attributeCount.get(classLabel).get(attributeNum).put(attributeValue, new Counter());
    }

    /**
     * Classify a data point using the information from training the naive bayes model.
     *
     * @param dataPoint data point to classify
     * @return predicted class label for the given data point
     */
    public String classify(List dataPoint) {
	double bestProb = 0;
	String bestClassLabel = null;

	//Calculate the unnormalized probability of observing each class for the given
	//point, using the conditional independence assumption of naive bayes
	for (String classLabel : classCount.keySet()) {
	    final int classCount = classCount.get(classLabel).getValue();
	    final double unseenPoints = classCount * MISSING_POINT_RATE;
	    
	    //Start with the prior probability, P(class)
	    double prob = (double)classCount / numPoints; 

	    //Factor in each conditional probability, P(attribute n = data point value | class)
	    for (int attrNum = 0; attrNum < numAttributes; attrNum++) {
		String attributeValue = dataPoint.get(attrNum);
		int attrCount = attributeCount.get(classLabel).get(attrNum).get(attributeValue).getValue();
		
		prob *= (attrCount + unseenPoints * ATTRIBUTE_OCCURANCE_RATE) / (classCount + unseenPoints);
	    }

	    //Remember only the best class and probability
	    if(prob > bestProb) {
		bestProb = prob;
		bestClassLabel = classLabel;
	    }
	}

	return bestClassLabel;
    }
}

/* A convenience class for counting points.
 * Stores only an  integer and only supports incrementing.
 */
class Counter {
    private int value = 0;

    public void increment() {
	value++;
    }

    public int getValue() {
	return value;
    }
}
