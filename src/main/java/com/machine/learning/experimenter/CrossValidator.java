package com.machine.learning.experimenter;

import com.github.rschmitt.dynamicobject.DynamicObject;
import com.machine.learning.classifier.Classifier;
import com.machine.learning.model.DataModel;
import com.machine.learning.model.Result;

public class CrossValidator {
    public CrossValidator(Classifier classifier, DataModel dataModel) {

    }

    public Result evaluate() {
        return DynamicObject.newInstance(Result.class);
    }
}
