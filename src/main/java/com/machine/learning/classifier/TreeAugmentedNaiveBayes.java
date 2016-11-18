package com.machine.learning.classifier;

import com.machine.learning.model.DataPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class TreeAugmentedNaiveBayes implements Classifier {
    private Map<String, AtomicInteger> priorProbability;

    private int numPoints, numAttributes;
    
    public void train(List<DataPoint> dataPoints) {
	if(dataPoints.size() == 0) {
	    return;
	}

	numPoints = dataPoints.size();
	numAttributes = dataPoints.get(0).getData().get().size();
    }

    public String classify(List dataPoint) {
	return "";
    }
    
    @Override
    public String toString() {
	return TreeAugmentedNaiveBayes.class.getName();
    }
}
