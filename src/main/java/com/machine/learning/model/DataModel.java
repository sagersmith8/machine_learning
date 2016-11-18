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
    @Key("name")
    DataModel withName(String name);

    @Key("name")
    Optional<String> getName();

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
	    System.out.println("Preprocessing " + filePath);
            List<DataPoint> processedData = preprocessData(doc);
            return DynamicObject.newInstance(DataModel.class)
		.withName(filePath)
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
        List<DataPoint> dataModel = new ArrayList<DataPoint>(data.size());

        for (String dataRow : data) {
            List<String> dataModelRow = Arrays.asList(parseString(dataRow));
            if(dataModelRow.size() > 0) {
                dataModel.add(DynamicObject.newInstance(DataPoint.class).fromData(dataModelRow));
            }
        }

	List<String> discretizationParams = dataModel.remove(0).getData().get();

	System.out.println("Generating missing values...");
	dataModel = generateMissing(dataModel);

	System.out.println("Discretizing data...");
	dataModel = discretize(dataModel, discretizationParams);

        return dataModel;
    }

    /**
     * Converts a string to an int []
     * @param toParse String to change to int []
     * @return int[] created from String
     */
    default String[] parseString(String toParse) {
        return toParse.split(",", 0);
    }

    /**
     * Discretizes the data in the given row
     *
     * @param dataRow to discretize
     */
    default List<DataPoint> discretize(List<DataPoint> dataRow, List<String> discretizationParams) {
	if (dataRow.size() == 0) {
	    return dataRow;
	}
	int numAttributes = dataRow.get(0).getData().get().size();

	final int NUM_BUCKETS = 10;

	for (int i = 0; i < numAttributes; i++) {
	    if (discretizationParams.get(i).equals("=")) {
		continue;
	    }

	    Double min = Double.MAX_VALUE;
	    Double max = Double.MIN_VALUE;

	    for (int j = 0; j < dataRow.size(); j++) {
		Double num = asNumber((String)dataRow.get(j).getData().get().get(i));
		min = Math.min(min, num);
		max = Math.max(max, num);
	    }

	    max += 0.001;

	    for (int j = 0; j < dataRow.size(); j++) {
		Double num = asNumber((String)dataRow.get(j).getData().get().get(i));
		List<String> modifiedData = new ArrayList(dataRow.get(j).getData().get());
		modifiedData.set(i, Integer.toString((int)((num - min) * NUM_BUCKETS / (max - min))));
		dataRow.set(j, dataRow.get(j).withData(modifiedData));
	    }	    
	}

        return dataRow;
    }

    default Double asNumber(String str) {
	try {
	    return new Double(str);
	} catch (NumberFormatException nfe) { }
	return null;
    }

    /**
     * Generates missing values
     *
     * @param dataPoints to generate missing data for
     * @return the data with missing values generated
     */
    default List<DataPoint> generateMissing(List<DataPoint> dataPoints) {
        for (int i = 0; i < dataPoints.size(); i++) {
            DataPoint dataPoint = dataPoints.get(i);
            for (int j = 0; j < dataPoint.getData().get().size(); j++) {
                String value = (String) dataPoint.getData().get().get(j);
                while ("?".equals(value)) {
                    List<String> fixedDataPoint = new ArrayList<>(dataPoint.getData().get());
                    List<String> randomDataPoint = dataPoints.get((int)(Math.random() * dataPoints.size())).getData().get();
                    value = randomDataPoint.get(j);
                    fixedDataPoint.set(j, value);
                    dataPoint = dataPoint.withData(fixedDataPoint);
                    dataPoints.set(i, dataPoint);
                }
            }
        }
        return dataPoints;
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
