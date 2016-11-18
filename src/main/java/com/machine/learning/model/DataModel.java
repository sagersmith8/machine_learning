package com.machine.learning.model;

import com.github.rschmitt.dynamicobject.DynamicObject;
import com.github.rschmitt.dynamicobject.Key;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public interface DataModel extends DynamicObject<DataModel> {
    @Key("data")
    /**
     * Setter for data
     */
    DataModel withData(List<DataPoint> data);

    @Key("data")
    /**
     * Getter for data
     */
    Optional<List<DataPoint>> getData();

    /**
     * Creates a com.machine.learning.model.DataModel object from the data at the given path
     *
     *  @param filePath path to file
     */
    default DataModel fromFile(String filePath) {
        try (InputStream resource = Thread
                .currentThread()
                .getContextClassLoader()
                .getResourceAsStream(filePath)) {
            List<String> doc = new BufferedReader(new InputStreamReader(resource, StandardCharsets.UTF_8))
                    .lines().collect(Collectors.toList());
            List<DataPoint> processedData = preprocessData(doc);
            return DynamicObject.newInstance(DataModel.class)
                    .withData(processedData);
        } catch (java.io.IOException ex) {
            ex.printStackTrace();
        }

        return DynamicObject.newInstance(DataModel.class);
    }

    /**
     * Runs pre-processing steps on data (Discretization and Missing value checking)
     *
     * @param data to preprocess
     *
     */
     default List<DataPoint> preprocessData(List<String> data) {
        List<DataPoint> dataModel = new ArrayList<>(data.size());

        for (String dataRow : data) {
            List<String> dataModelRow = Arrays.asList(parseString(dataRow));
            dataModel.add(DynamicObject.newInstance(DataPoint.class).fromData(dataModelRow));
        }

        return dataModel;
    }

    /**
     * Converts a string to an int []
     * @param toParse String to change to int []
     * @return int[] created from String
     */
    default String[] parseString(String toParse) {
        return toParse.split(",");
    }

    /**
     * Discretizes the data in the given row
     *
     * @param dataRow to discretize
     */
    default String discretize(String dataRow) {
        return dataRow;
    }

    /**
     * Save file as EDN
     *
     * @param fileName File name to save file as
     */
    default void save(String fileName) {
        String serializedData = DynamicObject.serialize(this);
        PrintWriter pw = null;
        try {
            File file = new File("edn");
            if (!file.exists()) {
                file.mkdir();
            }
            pw = new PrintWriter(file.getAbsolutePath()+"/"+fileName, "UTF-8");
            pw.write(serializedData);
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (pw != null) {
                pw.close();
            }
        }
    }

    /**
     * Load file from EDN
     *
     * @param filepath File to load from
     * @return datamodel read from file
     */
    default DataModel loadFromEdn(String filepath) {
        try {
            String file = Files.readAllLines(Paths.get("edn/"+filepath)).get(0);
            return DynamicObject.deserialize(file, DataModel.class);
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ArrayIndexOutOfBoundsException ex) {
            System.err.println("File does not exist");
        }

        return DynamicObject.newInstance(DataModel.class);
    }
}
