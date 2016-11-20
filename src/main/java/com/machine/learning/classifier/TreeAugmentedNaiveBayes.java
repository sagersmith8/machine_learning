package com.machine.learning.classifier;

import com.machine.learning.model.DataPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class TreeAugmentedNaiveBayes implements Classifier {
    /* Constants for smoothing, assumes some number of possible missing points and a rate of
       occurance of any attribute in those missing points.
    */
    private static final double MISSING_POINT_RATE = 0.1;
    private static final double ATTRIBUTE_OCCURANCE_RATE = 0.03;

    /* Counters for classes, attributes, and pairs of attributes */
    private Map<String, AtomicInteger> classCounts;
    private Map<String, AtomicInteger> attributeCounts;
    private Map<String, AtomicInteger> attributePairCounts;

    /* The set of all attribute values */
    private List<Set<String>> attributeValues;

    /* The set of all class labels */
    private Set<String> classLabels;

    /* The calculated maximum spanning tree, indexed by attribute number */
    private List<Tree> attributeTree;
    
    private int numPoints, numAttributes;

    /**
     * Trains a TAN model with the given data.
     *
     * Works by first counting occurences of classes, attributes, and pairs of attributes.
     * Then computes a maximum spanning tree based on the mutual information between all
     * pairs of attributes.
     *
     * @param dataPoints the points used to train
     */
    public void train(List<DataPoint> dataPoints) {
	if(dataPoints.size() == 0) {
	    return;
	}

	numPoints = dataPoints.size();
	numAttributes = dataPoints.get(0).getData().get().size();

	initCounts();
	
	for (DataPoint point : dataPoints) {
	    countPoint(point.getData().get(), point.getClassLabel().get());
	}

	attributeTree = computeTree();
    }

    /**
     * Resets and initializes the data structures for counting attributes and classes.
     */
    private void initCounts() {
	classCounts = new HashMap<>();
	attributeCounts = new HashMap<>();
	attributePairCounts = new HashMap<>();

	classLabels = new HashSet<>();
	attributeValues = new ArrayList<>();
	for (int i = 0; i < numAttributes; i++) {
	    attributeValues.add(new HashSet<String>());
	}
    }

    /**
     * Counts the point's class, attributes, and pairs of attributes.
     *
     * @param dataPoint the point to count
     * @param classLabel the class label associated with the data point
     */
    private void countPoint(List dataPoint, String classLabel) {
	countClass(classLabel);

	for (int i = 0; i < numAttributes; i++) {
	    countAttribute(i, (String)dataPoint.get(i), classLabel);
	    for (int j = i + 1; j < numAttributes; j++) {
		countAttributePair(i, (String)dataPoint.get(i),
				   j, (String)dataPoint.get(j), classLabel);
	    }
	}
    }

    /* Convenience methods for getting and incrementing counts */
    private void countClass(String classLabel) {
	count(classCounts, classLabel);
	classLabels.add(classLabel);
    }

    private void countAttribute(int attributeNum, String attributeValue, String classLabel) {
	count(attributeCounts, attributeNum + "," + attributeValue + "," + classLabel);
	attributeValues.get(attributeNum).add(attributeValue);
    }

    private void countAttributePair(int attributeNumI, String attributeValueI,
				    int attributeNumJ, String attributeValueJ, String classLabel) {
	count(attributePairCounts, (attributeNumI + "," + attributeNumJ + "," +
				    attributeValueI + "," + attributeValueJ + "," + classLabel));
    }

    private int getClassCount(String classLabel) {
	return getCount(classCounts, classLabel);
    }

    private int getAttributeCount(int attributeNum, String attributeValue, String classLabel) {
	return getCount(attributeCounts, attributeNum + "," + attributeValue + "," + classLabel);
    }

    private int getAttributePairCount(int attributeNumI, String attributeValueI,
				       int attributeNumJ, String attributeValueJ, String classLabel) {
	if (attributeNumI > attributeNumJ) {
	    int temp = attributeNumI;
	    attributeNumI = attributeNumJ;
	    attributeNumJ = temp;
	}
	return getCount(attributePairCounts, (attributeNumI + "," + attributeNumJ + "," +
					      attributeValueI + "," + attributeValueJ + "," + classLabel));
    }

    /**
     * Counts a specified quantity in a specified map, creating a new counting
     * object if the quantity hasn't yet been added to the map.
     *
     * @param counts the map holding counters
     * @param key the name of the quantity to increment
     */
    private void count(Map<String, AtomicInteger> counts, String key) {
	if(!counts.containsKey(key)) {
	    counts.put(key, new AtomicInteger());
	}
	counts.get(key).incrementAndGet();
    }

    /**
     * Returns the specified quantity from a map, giving 0 if the quantity hasn't
     * been added to the map.
     *
     * @param counts the map holding counters
     * @param key the name of the quantity to retrieve
     */
    private int getCount(Map<String, AtomicInteger> counts, String key) {
	if(!counts.containsKey(key)) {
	    return 0;
	}
	return counts.get(key).intValue();
    }

    /**
     * Compute the maximum spanning tree over the complete graph connecting all of the
     * attributes in the data set, using the conditional mutual information between 
     * each pair of attributes as weights.
     *
     * Implementation is Prim's algorithm, using the fact that each node is added to
     * the reached set only once to build the tree structure with attribute 0 as the
     * root node.
     *
     * @return a maximum spanning tree over the mutual information between attributes,
     *         as a list indexed by attribute number
     */
    private List<Tree> computeTree() {
	PriorityQueue<Edge> edges = new PriorityQueue<Edge>();
	for(int i = 1; i < numAttributes; i++) {
	    edges.add(new Edge(0, i, mutualInformation(0, i)));
	}

	Set<Integer> reachedNodes = new HashSet<>();
	reachedNodes.add(0);

	List<Tree> spanningSubTrees = new ArrayList<>();
	for(int i = 0; i < numAttributes; i++) {
	    spanningSubTrees.add(new Tree(i));
	}
	
	while(reachedNodes.size() < numAttributes) {
	    Edge nextEdge = edges.poll();

	    if (reachedNodes.contains(nextEdge.j)) {
		continue;
	    }
	    reachedNodes.add(nextEdge.j);
	    spanningSubTrees.get(nextEdge.j).parent = spanningSubTrees.get(nextEdge.i);

	    for(int nextI = 0; nextI < numAttributes; nextI++) {
		if(!reachedNodes.contains(nextI)) {
		    edges.add(new Edge(nextEdge.j, nextI, mutualInformation(nextEdge.j, nextI)));
		}
	    }
	}
	return spanningSubTrees;
    }

    /**
     * Calculates the conditional mutual informatoin between two attribute variables,
     * given the class variable.
     *
     * @param attributeI the position of the first attribute
     * @param attributeJ the position of the second attribute
     * @return the conditional mutual information between the two attributes given the class
     */
    private Double mutualInformation(int attributeI, int attributeJ) {
	Set<String> attributeIValues = attributeValues.get(attributeI);
	Set<String> attributeJValues = attributeValues.get(attributeJ);

	double sum = 0.0;
	for (String classLabel : classLabels) {
	    for (String attrI : attributeIValues) {
		for (String attrJ : attributeJValues) {
		    int pairCount = getAttributePairCount(attributeI, attrI, attributeJ, attrJ, classLabel);
		    int attrICount = getAttributeCount(attributeI, attrI, classLabel);
		    int attrJCount = getAttributeCount(attributeJ, attrJ, classLabel);
		    int classCount = getClassCount(classLabel);

		    double pairProb = (double)pairCount / numPoints;
		    double pairProbClass = (double)pairCount / classCount;
		    double attrIProbClass = (double)attrICount / classCount;
		    double attrJProbClass = (double)attrJCount / classCount;

		    sum += pairProb * (Math.log(pairProbClass) - Math.log(attrIProbClass) - Math.log(attrJProbClass));
		}
	    }
	}

	return sum;
    }

    /**
     * Classify the given data point based on the tree built from training data.
     *
     * Similary to Naive Bayes, TAN picks the class that has the greatest likelihood
     * of occuring based on training data, but this model has to also calculate
     * more complicated conditional probabilities.
     *
     * @param dataPoint the point to classify
     * @return the predicted class label for the data point
     */
    public String classify(List dataPoint) {
	String bestClassLabel = null;
	double bestProb = Double.NEGATIVE_INFINITY;

	//calculate the log likelihood of each class label, and take the best class
	for (String classLabel : classLabels) {
	    final int numClassPoints = getClassCount(classLabel);
	    
	    //start with the prior probability
	    double prob = Math.log((double)numClassPoints / numPoints);

	    //factor in the conditional probabilities for each attribute
	    for (int i = 0; i < numAttributes; i++) {
		Tree attributeNode = attributeTree.get(i);
		String attributeValue = (String)dataPoint.get(i);

		if (attributeNode.parent == null) {
		    //If there is only one parent, calculate P(a_i | class)		    
		    int attrCount = getAttributeCount(i, attributeValue, classLabel);
		    double unseenPoints = numClassPoints * MISSING_POINT_RATE;
		    prob += Math.log((attrCount + unseenPoints * ATTRIBUTE_OCCURANCE_RATE) /
				      (numClassPoints + unseenPoints));
		} else {
		    //Otherwise, calculate P(a_i | a_parent, class)
		    int attrParent = attributeNode.parent.attributeNum;
		    String parentValue = (String)dataPoint.get(attrParent);

		    int parentAttrCount = getAttributeCount(attrParent, parentValue, classLabel);
		    int attrPairCount = getAttributePairCount(i, attributeValue,
							      attrParent, parentValue, classLabel);
		    double unseenPoints = numClassPoints * MISSING_POINT_RATE;
		    if (attrPairCount == 0) {
			int attrCount = getAttributeCount(i, attributeValue, classLabel);
			prob += Math.log((attrCount + unseenPoints * ATTRIBUTE_OCCURANCE_RATE) /
					 (numClassPoints + unseenPoints));
		    } else {
			prob += Math.log((attrPairCount + unseenPoints * ATTRIBUTE_OCCURANCE_RATE) /
					 (parentAttrCount + unseenPoints));
		    }
		}
	    }
	    
	    if (prob > bestProb) {
		bestProb = prob;
		bestClassLabel = classLabel;
	    }
	}
	return bestClassLabel;
    }
    
    @Override
    public String toString() {
		return "TreeAugmentedNaiveBayes";
    }
}

/**
 * Used to represents an edge between two attributes in the completely
 * connected graph of mutual information betwen attributes.
 */
class Edge implements Comparable<Edge>{
    public int i, j;
    public Double cost;
    
    public Edge(int i, int j, Double cost) {
	this.i = i;
	this.j = j;
	this.cost = cost;
    }

    public int compareTo(Edge o) {
	return o.cost.compareTo(this.cost);
    }
}

/**
 * Used to represent the tree of attributes produced by the maximum spanning
 * tree process performed in training the TAN-model.
 */
class Tree {
    public Tree parent;
    public int attributeNum;

    public Tree(int attributeNum) {
	this.attributeNum = attributeNum;
    }
}
