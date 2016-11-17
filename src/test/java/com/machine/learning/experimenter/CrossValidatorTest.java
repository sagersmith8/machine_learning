package com.machine.learning.experimenter;

import com.github.rschmitt.dynamicobject.DynamicObject;
import com.machine.learning.classifier.ClassifierDefault;
import com.machine.learning.model.DataModel;
import com.machine.learning.model.Result;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;


public class CrossValidatorTest {
    @Test
    public void crossValidatorNoOpTest () {
        CrossValidator crossValidator = new CrossValidator(
                new ClassifierDefault(), DynamicObject.newInstance(DataModel.class), 10
        );
        assertThat(crossValidator.evaluate(), is(equalTo(DynamicObject.newInstance(Result.class))));
    }

    @Test
    public void testCreateFolds () {
        List<List> data = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            data.add(Collections.singletonList(i));
        }

        CrossValidator crossValidator = new CrossValidator(
           new ClassifierDefault(), DynamicObject.newInstance(DataModel.class).withData(data), 10
        );

        List<List<List>> folds = crossValidator.createFolds(data, 10);
        assertThat(folds.size(), is(equalTo(10)));
        boolean inOrder = true;
        for (int i = 0; i < folds.size(); i++) {
            assertThat(folds.get(i).size(), is(equalTo(1)));
            if (i >  0) {
                if (folds.get(i).get(0) != folds.get(i-1).get(0)) {
                    inOrder = false;
                }
            }
        }

        assertThat(inOrder, is(false));
    }
}
