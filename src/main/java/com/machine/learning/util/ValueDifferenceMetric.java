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
    private static final AtomicInteger ZERO = new AtomicInteger(0);

    /* Counts all of the occurences of attributes and attribute-classes */
    List<Map<String, AtomicInteger>> attributeCount;
    List<Map<String, Map<String, AtomicInteger>>> attributeClassCount;

    /* Caches the calculated distance between two attributes */
    List<Map<String, Double>> attributeDistance;

    /* Set of observed classes in data*/
    Set<String> classLabels;
    
    private int numAttributes;
    
    /**
     * Creates a value difference metric that can compute the difference between two
     * points based on the occurance rates of classes on each attribute value.
     */
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

    /**
     * Initializes the data structures for caculating VDM
     */
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

    /**
     * Ensures that the given attribute value can be counted, and the class label can
     * be counted for the given class label.
     *
     * @param attributeNum the position of the provided attribute value
     * @param attributeValue the attribute value to be ensured is countable
     * @param classLabel the class label associated with the data point containing the attribute value
     */
    private void ensureAttributeClass(int attributeNum, String attributeValue, String classLabel) {
        if (!attributeCount.get(attributeNum).containsKey(attributeValue)) {
            attributeCount.get(attributeNum).put(attributeValue, new AtomicInteger());
            attributeClassCount.get(attributeNum).put(attributeValue, new HashMap<String, AtomicInteger>());
        }

        if (!attributeClassCount.get(attributeNum).get(attributeValue).containsKey(classLabel)) {
            attributeClassCount.get(attributeNum).get(attributeValue).put(classLabel, new AtomicInteger());
        }
    }

    /**
     * Calculate the distance between two data points based on the VDM between each of the attributes.
     * Uses euclidean distance (p=2) for each attribute distance.
     * 
     * @param pointA the first point
     * @param pointB the second point
     * @return the distance between the two points, using the VDM to compare attributes
     */
    public double calculateDistance(List<String> pointA, List<String> pointB) {
        double sum = 0.0;
        for (int i = 0; i < pointA.size(); i++) {
            sum += Math.pow(attributeDifference(i, pointA.get(i), pointB.get(i)), 2);
        }
        return Math.pow(sum, 1 / 2.0);
    }

    private static final double Q = 1;
    /**
     * Calculates the value difference metric between two attribute values.
     * Essentially calculates the manhattan distance between the class occurance vector
     * for both of the given attribute values.
     *
     * @param attributeNum the position of the attribute value in the data point
     * @param valueA the first attribute value
     * @param valueB the second attribute value
     * @param the value difference metric distance between the two attribute values
     */
    public double attributeDifference(int attributeNum, String valueA, String valueB) {
        if (valueA.compareTo(valueB) > 0) {
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
                double diff = (classOccurancesA.getOrDefault(classLabel, ZERO).doubleValue() / valueOccurancesA -
                               classOccurancesB.getOrDefault(classLabel, ZERO).doubleValue() / valueOccurancesB);
                sum += Math.pow(Math.abs(diff), Q);
            }
            double res =  Math.pow(sum, 1 / Q);

            attributeDistance.get(attributeNum).put(pairName, res);
        }
        
        return attributeDistance.get(attributeNum).get(pairName);           
    }
}
