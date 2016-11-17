package com.machine.learning.classifier;

import java.util.List;

public interface Classifier {
    /**
     * Trains the classifier on the given data
     *
     * @param dataPoints data to train with
     * @param classLabel type of the class for the data
     */
    void train(List<List<Integer>> dataPoints, String classLabel);

    /**
     * Classifies an unknown datapoint using its training
     *
     * @param dataPoint data to classify
     * @return  class label
     */
    String classify(List dataPoint);
}
