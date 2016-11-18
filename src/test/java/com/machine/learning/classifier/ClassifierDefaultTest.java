package com.machine.learning.classifier;

import org.junit.Test;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.equalTo;

public class ClassifierDefaultTest {
    @Test
    public void testClassifyNoOp() {
        ClassifierDefault classifierDefault = new ClassifierDefault();
        classifierDefault.train(Collections.emptyList());
        assertThat(classifierDefault.classify(null), is(equalTo("")));
    }
}
