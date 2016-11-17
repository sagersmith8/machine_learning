package com.machine.learning.model;

import com.github.rschmitt.dynamicobject.DynamicObject;
import com.github.rschmitt.dynamicobject.Key;

/**
 * Class that holds results for kfold cross validation
 */
public interface Result extends DynamicObject<Result> {
    @Key(":result/results")
    /**
     * Setter for result
     */
    Result withResults(String result);

    @Key(":result/results")
    /**
     * Getter for result
     */
    String getResults();

    /**
     * Converts the results to latex
     *
     * @return returns the results formatted as a latex table
     */
    default String toLatex() {
        return getResults();
    }
}
