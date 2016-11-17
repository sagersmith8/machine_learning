package com.machine.learning.model;

import com.github.rschmitt.dynamicobject.DynamicObject;
import com.github.rschmitt.dynamicobject.Key;

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
    Optional<String> getClazz();

    default DataPoint fromData(List<String> data) {
        String clazz = data.remove(data.size()-1);
        return DynamicObject.newInstance(DataPoint.class)
                .withClass(clazz)
                .withData(data);
    }
}
