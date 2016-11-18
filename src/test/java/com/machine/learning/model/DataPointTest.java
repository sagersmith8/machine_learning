package com.machine.learning.model;

import com.github.rschmitt.dynamicobject.DynamicObject;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class DataPointTest {
    @Test
    public void testDataPoint() {
        DataPoint dataPoint = DynamicObject.newInstance(DataPoint.class).fromData(new ArrayList<>(Arrays.asList("0", "0")));
        assertThat(dataPoint.getData().get(), is(equalTo(Arrays.asList("0"))));
        assertThat(dataPoint.getClassLabel().get(), is(equalTo("0")));
    }
}
