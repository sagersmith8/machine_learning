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

    /**
     * Data structure for storing a decision tree over discrete attributes
     */
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

    /**
     * Trains a decision tree model based on the given training data, seperated
     * into training and validation data.
     *
     * @param dataPoints the points to train on
     */
    @Override
    public void train(List<DataPoint> dataPoints) {
	// Seperate data into training and validation
	Collections.shuffle(dataPoints);
	subtrees = new HashSet<>();
	trainingData = dataPoints.subList(0, (int)(0.6*dataPoints.size()));
	validationData = dataPoints.subList((int)(0.6*dataPoints.size()), dataPoints.size());

	// Construct decision tree
	dt = constructDT(trainingData);

	findSubtrees(dt);

	// Prune decision tree
	pruneTree();
    }
    /**
     * Finds the subtrees of a given subtree
     *
     * @param subtree the subtree to find subtrees on
     */
    private void findSubtrees(DecisionTree subtree) {
	if (subtree.clazz == null) {
	    subtrees.add(subtree);

	    findSubtrees(subtree.pos);
	    findSubtrees(subtree.neg);
	}
    }

    /**
     * Prunes the given tree by pruning nodes until it can no longer reduce error.
     */
    private void pruneTree() {
	DecisionTree bestSubtree = null;

	do {
	    int bestError = validationError(dt);
	    bestSubtree = null;
	    
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
		bestSubtree.pos = bestSubtree.neg = null;
		removeSubtrees(bestSubtree);
	    }
	} while(bestSubtree != null);
    }
    /**
     * Recursively remove subtrees from a tree
     *
     * @param subtree the subtree to be deleted
     */
    private void removeSubtrees(DecisionTree subtree) {
	subtrees.remove(subtree);

	if(subtree.pos != null) {
	    removeSubtrees(subtree.pos);
	    removeSubtrees(subtree.neg);	    
	}
    }

    /**
     * Calculates the error of the given decision tree on the validation set.
     *
     * @returns the number of errors from running the given decision tree on the validation data
     */
    private int validationError(DecisionTree dt) {
	int errors = 0;
	for (DataPoint point : validationData) {
	    if (!classify(point.getData().get(), dt).equals(point.getClassLabel().get())) {
		errors++;
	    }
	}
	return errors;
    }

    /**
     * Classify the given point based on the stored decision tree.
     *
     * @param dataPoint the point to classify
     * @return the class label for the given point
     */
    @Override
    public String classify(List dataPoint) {
	return classify(dataPoint, dt);
    }

    /**
     * Classify the given point based on a given decision tree.
     *
     * @param dataPoint the point to classify
     * @param curDT the decision tree to classify with
     * @return the class label for the given point
     */
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

    /**
     * Constructs the decision tree for a set of data
     *
     * @param remainingData the data to construct a decision tree from
     * @return a decision tree that best memorizes the given data
     */
    public DecisionTree constructDT(List<DataPoint> remainingData) {
	if (remainingData == null || remainingData.size() == 0) {
	    return null;
	}
	Set<String> classes = new HashSet<>();
	List<Set<String>> usedAttrValues = new ArrayList<>();

	for (DataPoint dataPoint : remainingData) {
	    classes.add(dataPoint.getClassLabel().get());
	}

	if (classes.size() == 1) {
	    String singleClass = classes.iterator().next();
	    return new DecisionTree(singleClass);
	}

	for (int i = 0; i < remainingData.get(0).getData().get().size(); i++) {
	    usedAttrValues.add(new HashSet<String>());
	}

	for (DataPoint dataPoint : remainingData) {
	    List<String> datum = dataPoint.getData().get();
	    for (int i = 0; i < datum.size(); i++) { 
		usedAttrValues.get(i).add(datum.get(i));
	    }
	}

	int attrIndex = 0;
	String attributeValue = "";
	double minEntropy = Double.MAX_VALUE;
	List<DataPoint> posData = new ArrayList<>();
	List<DataPoint> negData = new ArrayList<>();
	double curEntropy = calculateEntropy(remainingData);
	for (int i = 0; i < usedAttrValues.size(); i++) {
	    for (String attrValue : usedAttrValues.get(i)) {
		posData.clear();
		negData.clear();

		for (DataPoint dataPoint : remainingData) {
		    if (dataPoint.getData().get().get(i).equals(attrValue)) {
			posData.add(dataPoint);
		    } else {
			negData.add(dataPoint);
		    }
		}

		double entropy = (calculateEntropy(posData)*posData.size()/remainingData.size() +
				  calculateEntropy(negData)*negData.size()/remainingData.size());
		if(entropy < minEntropy){
		    minEntropy = entropy;
		    attrIndex = i;
		    attributeValue = attrValue;
		}		
	    }
	}

	if (aboutEqual(curEntropy, minEntropy) || minEntropy > curEntropy) {
	    return new DecisionTree(mostCommonClass(remainingData));
	}

	posData.clear();
	negData.clear();

	for (DataPoint dataPoint : remainingData) {
	    if (dataPoint.getData().get().get(attrIndex).equals(attributeValue)) {
		posData.add(dataPoint);
	    } else {
		negData.add(dataPoint);
	    }
	}

	DecisionTree cur = new DecisionTree(attrIndex, attributeValue);
	cur.maxClass = mostCommonClass(remainingData);	
	cur.pos = constructDT(posData);
	cur.neg = constructDT(negData);

	return cur;
    }

    private static final double EPSILON = 0.000001;

    /**
     * Determines if 2 double are within epsilon of each other to combat rounding error
     *
     * @param a first value to compare equality
     * @param b second value to compare equality
     * @return whether or not the values are withing epsilon
     */
    private boolean aboutEqual(double a, double b) {
	return Math.abs(a - b) < EPSILON;
    }

    public String mostCommonClass(List<DataPoint> remainingData) {
	double maxProp = 0;
	List<String> commonClasses = new ArrayList<>();

	for (Map.Entry<String, Double> prop : classProportions(remainingData).entrySet()) {
	    if (prop.getValue() > maxProp) {
		maxProp = prop.getValue();

		commonClasses.clear();
		commonClasses.add(prop.getKey());
	    } else if (aboutEqual(prop.getValue(), maxProp)) {
		commonClasses.add(prop.getKey());
	    }
	}

	return commonClasses.get((int)(commonClasses.size()*Math.random()));
    }

    /**
     * Determines the proportions of each class present in a set of data
     * 
     * @param remainingData the dataset to count over
     * @return a map from a class to that class' proportion in the given data set
     */
    public Map<String, Double> classProportions(List<DataPoint> remainingData) {
	Map<String, Double> proportions = new HashMap<>();

	for (DataPoint dataPoint : remainingData) {
	    String classLabel = dataPoint.getClassLabel().get();
	    if (!proportions.containsKey(classLabel)) {
		proportions.put(classLabel, 1.0/remainingData.size());
	    } else {
		proportions.put(classLabel, proportions.get(classLabel)+1.0/remainingData.size());
	    }
	}

	return proportions;
    }

    /**
     * Calculates the entropy for the given set of data points.
     *
     * @param remainingData the data to calculate entropy on
     * @return the entropy for the set of data
     */
    public double calculateEntropy(List<DataPoint> remainingData) {
	double sum = 0.0;
	for (double proportion : classProportions(remainingData).values()) {
	    sum -= proportion * Math.log(proportion);
	}

	return sum;
    }

    /**
     * Creates a string representation of a decision tree.
     *
     * @param dt the decision tree to stringify
     * @return string representation of the given tree
     */
    public String printTree(DecisionTree dt) {
	return printTree(dt, 0);
    }

    /**
     * Creates a string representation of a decision tree.
     *
     * @param dt the decision tree to stringify
     * @param indent the current indentation level to print with
     * @return string representation of the given tree
     */
    public String printTree(DecisionTree dt, int indent) {
	String retS = "";
	for (int i = 0; i < indent; i++) {
	    retS += "\t";
	}
	if (dt.clazz == null) {
	    retS += "a["+dt.attributeIndex+"]="+dt.attributeValue+":\n";
	    if (dt.pos != null) {
		retS += printTree(dt.pos, indent + 1);
	    }
	    for (int i = 0; i < indent; i++) {
		retS += "\t";
	    }
	    retS += "else:\n";
	    if (dt.neg != null) {
		retS += printTree(dt.neg, indent + 1);
	    }
	} else {
	    retS += "Class: " + dt.clazz + "\n";
	}
	return retS;
    }
}
