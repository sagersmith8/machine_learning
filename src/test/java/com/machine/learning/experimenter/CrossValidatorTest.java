package com.machine.learning.experimenter;

import com.github.rschmitt.dynamicobject.DynamicObject;
import com.machine.learning.classifier.ClassifierDefault;
import com.machine.learning.model.DataModel;
import com.machine.learning.model.Result;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;


public class CrossValidatorTest {
    @Test
    public void crossValidatorNoOpTest () {
        CrossValidator crossValidator = new CrossValidator(
                new ClassifierDefault(), DynamicObject.newInstance(DataModel.class)
        );
        assertThat(crossValidator.evaluate(), is(equalTo(DynamicObject.newInstance(Result.class))));
    }
}
