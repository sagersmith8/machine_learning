package com.machine.learning.model;

import com.github.rschmitt.dynamicobject.DynamicObject;
import com.github.rschmitt.dynamicobject.Key;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Class for datapoint
 */
public interface DataPoint extends DynamicObject<DataPoint> {
    @Key(":datapoint/data")
    /**
     * Setter for data
     */
    DataPoint withData(List data);

    @Key(":datapoint/data")
    /**
     * Getter for data
     */
    Optional<List> getData();

    @Key(":datapoint/class")
    /**
     * Setter for class
     */
    DataPoint withClass(String clazz);

    @Key(":datapoint/class")
    /**
     * Getter for class
     */
    Optional<String> getClassLabel();

    default DataPoint fromData(List<String> data) {
        data = new ArrayList(data);
        String clazz = ""+data.remove(data.size()-1);
        for (String dataValue : data) {
            while ("?".equals(dataValue)) {
                dataValue = data.get((int)(Math.random() * data.size()));
            }
        }
        return DynamicObject.newInstance(DataPoint.class)
                .withClass(clazz)
                .withData(data);
    }
}
