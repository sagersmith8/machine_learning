package com.machine.learning.experimenter;

import com.machine.learning.classifier.Classifier;
import com.machine.learning.model.DataModel;
import com.machine.learning.model.Result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MadScientist {
    private Map<Class, List<Result>> results = new HashMap<>();

    /**
     * Creates a MadScientist
     *
     * @param dataModels List of datamodels to evaluate
     * @param classifiers List of classifiers to evaluate
     */
    public MadScientist(List<DataModel> dataModels, List<Classifier> classifiers) {
        for (Classifier classifier : classifiers) {
            for (DataModel dataModel : dataModels) {
                results.putIfAbsent(classifier.getClass(), new ArrayList<>());
                Result result = new CrossValidator(classifier, dataModel).evaluate();
                List<Result> resList = results.get(classifier.getClass());
                resList.add(result);
                results.put(classifier.getClass(), resList);
            }
        }
    }

    /**
     * Formats the list of results into a nice latex table
     *
     * @return String formatted table of results
     */
    public String getResults() {
        StringBuilder sb = new StringBuilder("table begin");
        for (Map.Entry<Class, List<Result>> entry: results.entrySet()) {
            sb.append(entry.getKey());
            for (Result result: entry.getValue()) {
                sb.append(result.toLatex());
            }
        }

        return sb.toString();
    }
}
