package com.machine.learning.classifier;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;

public class ID3 implements Comparable {

    List<DataPoint> trainingData = new ArrayList<>();
    List<DataPoint> validationData = new ArrayList<>();
    DecisionTree dt;

    public class DecisionTree {
	int attributeIndex;
	String attributeValue;
	String clazz;

	DecisionTree pos;
	DecisionTree neg;

	public DecisionTree(int attrIndex, String attrValue) {
	    attributeIndex = attrIndex;
	    attributeValue = attrValue;
	}

	public DecisionTree(String clazz) {
	    this.clazz = clazz;
	}
	
    }

    @Override
    public void train(List<DataPoint> dataPoints) {
	// Seperate data into training and validation
	Collections.shuffle(dataPoints);
	trainingData.clear();
	validationData.clear();
	trainingData.addAll(dataPoints.sublist(0, (int)(0.6*dataPoints.size())));
	validationData.addAll(dataPoints.sublist((int)(0.6*dataPoints.size())), dataPoints.size()+1);


	// Construct decision tree
	dt = constructDT(trainingData);

	// Prune decision tree
	pruneTree();
    }

    private void pruneTree() {
	while(pruneNode());
    }

    private boolean pruneNode() {
	int initialError = validationError(dt);

	//find the majority node for each subtree
	Stack<DecisionTree> toVisit = new Stack<>();
	List<DecisionTree> subtrees;
	List<String> majorityClass;

	while(!toVisit.empty()) {
	    DecisionTree next = toVisit.pop();
	    Map<String, Integer> classCounts = countClasses(dt, next);

	    String maxClass = null;
	    int maxOccurences = 0;
	    
	    for (String classLabel : classCounts.keySet()) {
		if (classCounts.get(classLabel) > maxOccurences) {
		    maxOccurences = classCounts.get(classLabel);
		    maxClass = classLabel;
		}
	    }

	    majorityClass.append(maxClass);

	    toVisit.push(next.pos);
	    toVisit.push(next.neg);
	}

	//test on each of the subtree nodes
    }

    private Map<String, AtomicInteger> countClasses(DecisionTree dt, DecisionTree count) {
	Map<String, AtomicInteger> classCounts = new HashMap<>();

	for (DataPoint point : trainingData) {
	    String classLabel = point.getClassLabel().get();
	    if (!classCounts.containsKey(point.getClassLabel().get())) {
		classCounts.put(point.getClassLabel().get(), new AtomicInteger)
	    }
	    if (reaches(dt, count, point.getData().get())) {
		classCounts.get(classLabel).incrementAndGet();
	    }
	}

	return classCounts;
    }

    private boolean reaches(DecisionTree dt, DecisionTree count, DataPoint point) {
	while (curDT.clazz == null) {
	    if (curDT == count) {
		return true;
	    }
	    if (dataPoint.get(curDT.attributeIndex).equals(curDT.attributeValue)) {
		curDT = curDT.pos;
	    } else {
		curDT = curDT.neg;
	    }
	}
	
	    return false;
    }

    private void validationError(DecisionTree dt) {
	int errors = 0;
	for (DataPoint point : validationData) {
	    if (classify(point.getData().get(), dt)) {
		errors++;
	    }
	}
	return errors;
    }

    @Override
    public String classify(List dataPoint) {
	classify(dataPoint, dt);
    }

    private String classify(List dataPoint, DecisionTree curDT) {
	while (curDT.clazz == null) {
	    if (dataPoint.get(curDT.attributeIndex).equals(curDT.attributeValue)) {
		curDT = curDT.pos;
	    } else {
		curDT = curDT.neg;
	    }
	}
	
	return curDT.clazz;
    }

    public DecisionTree constructDT(List<DataPoint> remainingData) {
	ArrayList<String> classes = new ArrayList<>();
	classes.add(remainingData.getData(0).get());
	ArrayList<double> classProportion = new ArrayList<>();
	boolean allOneClazz = true;
	for (DataPoint dataPoint : remainingData) {
	    if (!dataPoint.clazz.equals(classes.get(0))) {
		allOneClazz = false;
	    }
	    if (!classes.contains(dataPoint.clazz)) {

	    } else {
		int index = classes.indexOf(dataPoint.clazz);
		classProportion.set(index, classProportion.get(index)++);
	    }
	}

	if (allOneClazz) {
	    return new DecisionTree(clazz);
	}

	for (double clazz : classProportion) {
	    clazz = clazz / remainingData.size();
	}
	
	for (DataPoint dataPoint : remainingData) {
	    for (String attr : dataPoint.getData().get()) {
		//store somewhere//
		calculateEntropy(attr, remainingData);
	    }
	}

	//get attribute (attrValue) and index (attrIndex) of lowest entropy value
	ArrayList<DataPoint> posData = new ArrayList<>();
	ArrayList<DataPoint> negData = new ArrayList<>();
	
	for (DataPoint dataPoint : remainingData) {
	    if (dataPoint.getData().get().equals(attrValue)) {
		posData.add(dataPoint);
	    } else {
		negData.add(dataPoint);
	    }
	}
	
	DecisionTree cur = new DecisionTree(attrIndex, attrValue);
	cur.pos = constructDT(posList);
	cur.neg = constructDT(negList);

	return cur;
	
    }

    //maybe doesn't need to be a method
    public double calculateEntropy(String attr, List<DataPoint> remainingData) {

    }
}
