package com.machine.learning.model;

import com.github.rschmitt.dynamicobject.DynamicObject;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class ResultTest {
    @Test
    public void testCreateResult() {
        assertThat(DynamicObject.newInstance(Result.class), is(notNullValue()));
    }

    @Test
    public void testAddAndGetResult() {
        Result result = DynamicObject.newInstance(Result.class)
                .withResults("Testing");
        assertThat(result.getResults(), is(equalTo("Testing")));
    }

    @Test
    public void testToLatex() {
        Result result = DynamicObject.newInstance(Result.class)
                .withResults("Testing");
        assertThat(result.toLatex(), is(equalTo("Testing")));
    }
}
