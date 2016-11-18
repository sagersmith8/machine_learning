package com.machine.learning.classifier;

import com.machine.learning.model.DataPoint;

import java.util.List;

public interface Classifier {
    /**
     * Trains the classifier on the given data
     *
     * @param dataPoints data to train with
     */
    void train(List<DataPoint> dataPoints);

    /**
     * Classifies an unknown datapoint using its training
     *
     * @param dataPoint data to classify
     * @return  class label
     */
    String classify(List dataPoint);
}
