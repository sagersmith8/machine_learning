package com.machine.learning.classifier;

import com.machine.learning.model.DataPoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;

public class ID3 implements Classifier {

    List<DataPoint> trainingData = new ArrayList<>();
    List<DataPoint> validationData = new ArrayList<>();
    DecisionTree dt;
    Set<DecisionTree> subtrees = new HashSet<>();
    
    public class DecisionTree {
	int attributeIndex;
	String attributeValue;
	String clazz;
	String maxClass;
	
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
	trainingData.addAll(dataPoints.subList(0, (int)(0.6*dataPoints.size())));
	validationData.addAll(dataPoints.subList((int)(0.6*dataPoints.size()), dataPoints.size()));

	// Construct decision tree
	dt = constructDT(trainingData);

	// Prune decision tree
	pruneTree();

	System.out.println("Printing pruned decision tree:");
	System.out.println(printTree(dt));
    }

    private void pruneTree() {
	while(pruneNode());
    }

    private boolean pruneNode() {
	int initialError = validationError(dt);

	if (dt.maxClass == null) {
	    //find the majority node for each subtree, if it hasn't been determined yet
	    Stack<DecisionTree> toVisit = new Stack<>();

	    while(!toVisit.empty()) {
		DecisionTree next = toVisit.pop();
		if (next.clazz != null)
		    continue;

		subtrees.add(next);
		
		Map<String, AtomicInteger> classCounts = countClasses(dt, next);

		String maxClass = null;
		int maxOccurences = 0;
		
		for (String classLabel : classCounts.keySet()) {
		    if (classCounts.get(classLabel).intValue() > maxOccurences) {
			maxOccurences = classCounts.get(classLabel).intValue();
			maxClass = classLabel;
		    }
		}

		next.maxClass = maxClass;

		toVisit.push(next.pos);
		toVisit.push(next.neg);
	    }
	}

	int bestError = initialError;
	DecisionTree bestSubtree = null;
	
	//test on each of the subtree nodes
	for (DecisionTree subtree : subtrees) {
	    subtree.clazz = subtree.maxClass; //prune the node to be its majority class
	    int newError = validationError(subtree);
	    subtree.clazz = null; //unprune the node for now

	    if (newError < bestError) {
		bestError = newError;
		bestSubtree = subtree;
	    }
	}

	if (bestSubtree != null) {
	    bestSubtree.clazz = bestSubtree.maxClass;
	    removeSubtree(bestSubtree);
	    
	    bestSubtree.pos = bestSubtree.neg = null;	    
	    return true;
	}
	return false;
    }

    private void removeSubtree(DecisionTree subtree) {
	subtrees.remove(subtree);
	
	removeSubtree(subtree.pos);
	removeSubtree(subtree.neg);
    }

    private Map<String, AtomicInteger> countClasses(DecisionTree dt, DecisionTree count) {
	Map<String, AtomicInteger> classCounts = new HashMap<>();

	for (DataPoint point : trainingData) {
	    String classLabel = point.getClassLabel().get();
	    if (!classCounts.containsKey(point.getClassLabel().get())) {
		classCounts.put(point.getClassLabel().get(), new AtomicInteger());
	    }
	    if (reaches(dt, count, point.getData().get())) {
		classCounts.get(classLabel).incrementAndGet();
	    }
	}

	return classCounts;
    }

    private boolean reaches(DecisionTree curDT, DecisionTree count, List dataPoint) {
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

    private int validationError(DecisionTree dt) {
	int errors = 0;
	for (DataPoint point : validationData) {
	    if (classify(point.getData().get(), dt).equals(point.getClassLabel().get())) {
		errors++;
	    }
	}
	return errors;
    }

    @Override
    public String classify(List dataPoint) {
	return classify(dataPoint, dt);
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
        remainingData.get(0).getClassLabel().get();
	//ArrayList<double> classProportion = new ArrayList<>();
	boolean allOneClazz = true;
	ArrayList<ArrayList<String>> usedAttrValues = new ArrayList<>();

	for(int i = 0; i < remainingData.get(0).getData().get().size(); i++) {
	    usedAttrValues.add(new ArrayList<>());
	}

	for (DataPoint dataPoint : remainingData) {
	    if (allOneClazz && !dataPoint.getClassLabel().get().equals(classes.get(0))) {
		allOneClazz = false;
	    }
	    if (!classes.contains(dataPoint.getClassLabel().get())) {
		classes.add(dataPoint.getClassLabel().get());
		//classProportion.add(1.0);
	    } else {
		int index = classes.indexOf(dataPoint.getClassLabel().get());
		//classProportion.set(index, classProportion.get(index)++);
	    }
	    /*
	    ArrayList<String> curData = dataPoint.getData().get();
	    for(int j = 0; j < curData.size(); j++){
		if (!usedAttrValues.get(j).contains(curData.get(j))) {
		    usedAttrValues.get(j).add(curData.get(j));
		}
	    }
	    */
	}

	if (allOneClazz) {
	    return new DecisionTree(classes.get(0));
	}

	// for (int i = 0;  i < classProportion.size(); i++) {
	//     classProportion.set(i, classProportion.get(i)/remainingData.size());
	// }
	
	// for (DataPoint dataPoint : remainingData) {
	//     for (String attr : dataPoint.getData().get()) {
	// 	//store somewhere//
	// 	calculateEntropy(attr, remainingData);
	//     }
	// }
	int attrIndex = 0;
	String attributeValue = "";
	double minEntropy = Double.MAX_VALUE;
	ArrayList<DataPoint> posData = new ArrayList<>();
	ArrayList<DataPoint> negData = new ArrayList<>();

	for (int i = 0; i < usedAttrValues.size(); i++) {
	    for (String attrValue : usedAttrValues.get(i)) {
		for (DataPoint dataPoint : remainingData) {
		    posData.clear();
		    negData.clear();

		    if (dataPoint.getData().get().contains(attrValue)) {
			posData.add(dataPoint);
		    } else {
			negData.add(dataPoint);
		    }
		    double entropy = Math.min(calculateEntropy(posData), calculateEntropy(negData));
		    if(entropy < minEntropy){
			minEntropy = entropy;
			attrIndex = i;
			attributeValue = attrValue;
		    }
		}
	    }
	}

	posData.clear();
	negData.clear();

	//get attribute (attrValue) and index (attrIndex) of lowest entropy value
	for (DataPoint dataPoint : remainingData) {
	    if (dataPoint.getData().get().contains(attributeValue)) {
		posData.add(dataPoint);
	    } else {
		negData.add(dataPoint);
	    }
	}
	
	DecisionTree cur = new DecisionTree(attrIndex, attributeValue);
	cur.pos = constructDT(posData);
	cur.neg = constructDT(negData);

	return cur;
	
    }

    public double calculateEntropy(List<DataPoint> remainingData) {
	ArrayList<Double> proportions = new ArrayList<>();
	ArrayList<String> classes = new ArrayList<>();

	for (DataPoint dataPoint : remainingData) {
	    if (!classes.contains(dataPoint.getClassLabel().get())) {
		classes.add(dataPoint.getClassLabel().get());
		proportions.add(1.0/remainingData.size());
	    } else {
		int index = proportions.indexOf(dataPoint.getClassLabel().get());
		proportions.set(index, proportions.get(index)+1/remainingData.size());
	    }
	}

	double sum = 0.0;
	for (double proportion : proportions) {
	    sum += proportion * Math.log(proportion);
	}

	return -sum;
    }

    public String printTree(DecisionTree dt) {
	String retS = "(";
	if (dt.pos != null || dt.neg != null) {
	    retS += "["+dt.attributeIndex+","+dt.attributeValue+"]";
	    if (dt.pos != null) {
		retS += printTree(dt.pos);
	    }
	    if (dt.neg != null) {
		retS += printTree(dt.neg);
	    }
	} else {
	    retS += "Class= " + dt.clazz;
	}
	retS += ")";
	return retS;
    }
}
