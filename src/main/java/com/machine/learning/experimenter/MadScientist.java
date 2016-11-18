package com.machine.learning.experimenter;

import com.github.rschmitt.dynamicobject.DynamicObject;
import com.machine.learning.classifier.Classifier;
import com.machine.learning.model.DataModel;
import com.machine.learning.model.Result;

import java.util.List;

public class MadScientist {
    private Result results;

    /**
     * Creates a MadScientist
     *
     * @param dataModels List of datamodels to evaluate
     * @param classifiers List of classifiers to evaluate
     */
    public MadScientist(List<DataModel> dataModels, List<Classifier> classifiers) {
        StringBuilder stringBuilder = new StringBuilder();
        for (DataModel dataModel: dataModels) {
            stringBuilder
                    .append(" & ")
                    .append(dataModel.getName().get().split("\\.")[0]);
        }

        stringBuilder.append("\\\\\n");
        for (Classifier classifier : classifiers) {
            System.out.println("Testing classifier: " + classifier);
            String [] classifierName = classifier.getClass().getName().split("\\.");
            stringBuilder.append(classifierName[classifierName.length-1]);
            for (DataModel dataModel : dataModels) {
                System.out.println("Testing data set: " + dataModel.getName().get());
                stringBuilder.append(" & ");
                Double result = Double.parseDouble(new CrossValidator(classifier, dataModel, 10).evaluate().getResults());
                stringBuilder.append(String.format("%.2f", result*100D));
                stringBuilder.append("%%");
            }
            stringBuilder.append("\\\\\n");
        }

        results = DynamicObject.newInstance(Result.class).withResults(stringBuilder.toString());
    }

    /**
     * Formats the list of results into a nice latex table
     *
     * @return String formatted table of results
     */
    public String getResults() {
        return results.getResults();
    }
}
