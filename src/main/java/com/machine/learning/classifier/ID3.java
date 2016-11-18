package com.machine.learning.classifier;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

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
	dt = pruneDT();
    }

    @Override
    public String classify(List dataPoint) {
	DecisionTree curDT = dt;

	while (curDT.pos != null && curDT.neg != null) {
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
        remainingData.get(0).getClassLabel().get()
	ArrayList<double> classProportion = new ArrayList<>();
	boolean allOneClazz = true;
	ArrayList<ArrayList<String>> usedAttrValues = new ArrayList<>();

	for(int i = 0; i < remainingData.get(0).getData().get().size(); i++) {
	    usedAttrValues.set(i, new ArrayList<>());
	}

	for (DataPoint dataPoint : remainingData) {
	    if (allOneClass && !dataPoint.clazz.equals(classes.get(0))) {
		allOneClazz = false;
	    }
	    if (!classes.contains(dataPoint.clazz)) {
		classes.add(dataPoint.clazz);
		classProportion.add(1.0);
	    } else {
		int index = classes.indexOf(dataPoint.clazz);
		classProportion.set(index, classProportion.get(index)++);
	    }
	    ArrayList<String> curData = dataPoint.getData().get();
	    for(int j = 0; j < curData.size(); j++){
		if (!usedAttrValues.get(j).contains(curData.get(j))) {
		    usedAttrValues.get(j).add(curData.get(j));
		}
	    }
	}

	if (allOneClazz) {
	    return new DecisionTree(classes.get(0));
	}

	for (int i = 0;  i < classProportion.size(); i++) {
	    classProportion.set(i, classProportion.get(i)/remainingData.size());
	}
	
	// for (DataPoint dataPoint : remainingData) {
	//     for (String attr : dataPoint.getData().get()) {
	// 	//store somewhere//
	// 	calculateEntropy(attr, remainingData);
	//     }
	// }
	int attrIndex = 0;
	String attributeValue;
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
		}
	    }
	}

	//get attribute (attrValue) and index (attrIndex) of lowest entropy value
	for (DataPoint dataPoint : remainingData) {
	    if (dataPoint.getData().get().contains(attrValue)) {
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
    public double calculateEntropy(List<DataPoint> remainingData) {

    }
}
