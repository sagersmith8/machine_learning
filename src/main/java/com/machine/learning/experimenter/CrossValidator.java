package com.machine.learning.experimenter;

import com.github.rschmitt.dynamicobject.DynamicObject;
import com.machine.learning.classifier.Classifier;
import com.machine.learning.model.DataModel;
import com.machine.learning.model.Result;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CrossValidator {
    private Classifier classifier;
    private List<String> classes;
    private List<List<List>> folds;

    /**
     * Performs class validation on all of the data
     *
     * @param classifier Algorithm to test
     * @param classes The classes to train with
     * @param dataModel Data to test on
     * @param numberOfFolds Number of folds to test with
     */
    public CrossValidator(Classifier classifier, List<String> classes, DataModel dataModel, Integer numberOfFolds) {
        this.classifier = classifier;
        this.classes = classes;
        this.folds = createFolds(dataModel.getData().orElse(Collections.emptyList()), numberOfFolds);

    }

    /**
     * Creates the folds to test with
     *
     * @param data data to test with
     * @param numberOfFolds number of folds to create
     * @return List of folds to test with
     */
    public List<List<List>> createFolds(List<List> data, Integer numberOfFolds) {
        List<List<List>> folds = new ArrayList<>();
        Collections.shuffle(new ArrayList<>(data));
        Integer segmentSize = data.size()/numberOfFolds;
        for (int i = 0; i < numberOfFolds; i++) {
            folds.add(data.subList(segmentSize*i, segmentSize*(i+1)));
        }

        return folds;
    }

    /**
     * Evaluates the data using k-fold-cross-validation
     * @return Result of k-fold-cross-validation
     */
    public Result evaluate() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < folds.size(); i++) {
            List<List> trainingData = new ArrayList<>();
            for (int j = 0; j < folds.size(); j++) {
                if (j != i) {
                    trainingData.addAll(folds.get(j));
                }
            }

            classifier.train(trainingData, classes);
            result.append(classifier.classify(folds.get(i)));
            if (i != folds.size()-1) {
                result.append(" ");
            }
        }
        return DynamicObject.newInstance(Result.class).withResults(result.toString());
    }
}
