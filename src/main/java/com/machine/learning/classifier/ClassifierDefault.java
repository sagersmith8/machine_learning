package com.machine.learning.classifier;

import java.util.List;

public class ClassifierDefault implements Classifier {
    public void train(List<List<Integer>> dataPoints, ClassLabel classLabel) {
    }

    public ClassLabel classify(Integer datapoint) {
        return ClassLabel.NONE;
    }
}
