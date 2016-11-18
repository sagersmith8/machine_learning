package com.machine.learning.experimenter;

import com.github.rschmitt.dynamicobject.DynamicObject;
import com.machine.learning.classifier.Classifier;
import com.machine.learning.model.DataModel;
import com.machine.learning.model.DataPoint;
import com.machine.learning.model.Result;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CrossValidator {
    private Classifier classifier;
    private List<List<DataPoint>> folds;

    /**
     * Performs cross validation on all of the data
     *
     * @param classifier Algorithm to test
     * @param dataModel Data to test on
     * @param numberOfFolds Number of folds to test with
     */
    public CrossValidator(Classifier classifier, DataModel dataModel, Integer numberOfFolds) {
        this.classifier = classifier;
        List<DataPoint> data = dataModel.getData().orElse(new ArrayList<>());
        this.folds = createFolds(data, numberOfFolds);
    }

    /**
     * Creates the folds to test with
     *
     * @param data data to test with
     * @param numberOfFolds number of folds to create
     * @return List of folds to test with
     */
    public List<List<DataPoint>> createFolds(List<DataPoint> data, Integer numberOfFolds) {
        List<List<DataPoint>> folds = new ArrayList<>();
        Collections.shuffle(new ArrayList<>(data));
        Integer segmentSize = data.size()/numberOfFolds;
        for (int i = 0; i < numberOfFolds; i++) {
            folds.add(new ArrayList<>(data.subList(segmentSize*i, segmentSize*(i+1))));
        }

        int c = 0;
        for (int i = segmentSize*numberOfFolds; i < data.size(); i++, c++) {
            folds.get(c).add(data.get(i));
        }

        return folds;
    }

    /**
     * Evaluates the data using k-fold-cross-validation
     * @return Result of k-fold-cross-validation
     */
    public Result evaluate() {
        System.out.println("Performing K-Fold cross validation on " +classifier);
        List<Double> results = new ArrayList<>();
        for (int i = 0; i < folds.size(); i++) {
            List<DataPoint> trainingData = new ArrayList<>();
            for (int j = 0; j < folds.size(); j++) {
                if (j != i) {
                    trainingData.addAll(folds.get(j));
                }
            }

            classifier.train(trainingData);
            int numCorrect = 0;
            for (DataPoint datapoint : folds.get(i)) {
                if (classifier.classify(datapoint.getData().get()).equals(datapoint.getClassLabel().orElse(""))) {
                    numCorrect++;
                }
            }

            results.add((double)numCorrect/(double)folds.get(i).size());
        }

        double average = 0D;
        for (Double result : results) {
            average += result;
        }
        average/=(double)results.size();

        double standardDeviation = 0D;
        for (Double result :results) {
            Double num = result - average;
            standardDeviation += num*num;
        }
        standardDeviation = Math.sqrt(standardDeviation);
        double confidenceInterval = standardDeviation/Math.sqrt(results.size());
        confidenceInterval*=1.96;
        System.out.println("Percent correct "+average);
        return DynamicObject.newInstance(Result.class).withResults(
                "$"+ String.format("%.2f", average*100D)+" \\pm "+String.format("%.2f", confidenceInterval) + "$"
        );
    }
}
