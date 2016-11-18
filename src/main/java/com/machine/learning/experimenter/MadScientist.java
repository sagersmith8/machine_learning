package com.machine.learning.experimenter;

import com.github.rschmitt.dynamicobject.DynamicObject;
import com.machine.learning.classifier.Classifier;
import com.machine.learning.model.DataModel;
import com.machine.learning.model.Result;

import java.util.Collections;
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
        StringBuilder stringBuilder = new StringBuilder("\\begin{table}\n\\begin{tabular}{c|c|c|c|c|c|}\n");
        for (DataModel dataModel: dataModels) {
            stringBuilder
                    .append(" & ")
                    .append(dataModel.getName().get().split("\\.")[0].split("-")[0]);
        }

        stringBuilder.append("\\\\\n");
        stringBuilder.append("\\hline\n");
        Collections.sort(classifiers, (x, y) -> x.toString().compareTo(y.toString()));
        for (Classifier classifier : classifiers) {
            System.out.println("Testing classifier: " + classifier);
            stringBuilder.append(classifier);
            for (DataModel dataModel : dataModels) {
                System.out.println("Testing data set: " + dataModel.getName().get());
                stringBuilder.append(" & ");
                String result = new CrossValidator(classifier, dataModel, 10).evaluate().getResults();
                stringBuilder.append(result);
                stringBuilder.append("\\%");
            }
            stringBuilder.append("\\\\\n");
            stringBuilder.append("\\hline\n");
        }
        stringBuilder.append("\\end{tabular}\n\\end{table}");

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
