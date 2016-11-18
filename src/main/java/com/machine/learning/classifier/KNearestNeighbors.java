package com.machine.learning.classifier;

import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;
import com.machine.learning.model.DataPoint;

public class KNearestNeighbors implements Classifier {

    int k;
    List<DataWithDistance> data = new ArrayList();
    List<String> classLabel;
    Set<String> possibleClasses;
    Map<String, AtomicInteger> voteResults;
    
    class DataWithDistance implements Comparable {
        List data;
	double distance;
	String clazz;

	public DataWithDistance(DataPoint data){
	    this.data = data.getData().get();
	    this.clazz = data.getClassLabel().get();
	}

	public int compareTo(Object other) {
	    DataWithDistance otherD = (DataWithDistance) other;
	    if (distance > otherD.distance) {
		return 1;
	    } else if (distance < otherD.distance) {
		return -1;
	    } else {
		return 0;
	    }
	}
    }
    
    public KNearestNeighbors(int k) {
	this.k = (int)Math.max(k,1);
    }

    public double calculateDistance(List d1, List d2){
	return 0.0;
    }
    
    @Override
    public void train(List<DataPoint> dataPoints) {
	for(DataPoint dataPoint : dataPoints){
	    data.add(new DataWithDistance(dataPoint));
	}
    }

    @Override
    public String classify(List dataPoint) {
	for (int i = 0; i < data.size(); i++) {
	    data.get(i).distance = calculateDistance(data.get(i).data, dataPoint);
	}
	return vote(k);
    }

    public String vote(int k) {
	Collections.sort(data);
	voteResults = new HashMap<>();
	for (int i = 0; i < k; i++) {
	    String classLabel = data.get(i).clazz;
	    if (!voteResults.containsKey(classLabel)) {
		voteResults.put(classLabel, new AtomicInteger());
	    }
	    voteResults.get(classLabel).incrementAndGet();
	}
	int max = 0;
	ArrayList<String> results = new ArrayList<String>();
	for(String classLabel : voteResults.keySet()){
	    int cur = voteResults.get(classLabel).get();
	    if (cur > max) {
		max = cur;
		results.clear();
		results.add(classLabel);
	    } else if (cur == max) {
		results.add(classLabel);
	    }
	}
	Collections.shuffle(results);
	return results.get(0);
    }
}
