package com.machine.learning.classifier;

import com.machine.learning.model.DataPoint;

import java.util.List;

public class ClassifierDefault implements Classifier {
    public void train(List<DataPoint> dataPoints) {
    }

    public String classify(List datapoint) {
        return "";
    }

    @Override
    public String toString() {
        return "Default Classifier";
    }
}
