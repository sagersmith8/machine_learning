import com.google.common.primitives.Ints;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataModel {
    private List<List<Integer>> data;

    /**
     * Creates a DataModel object from the data at the given path
     *
     *  @param filePath path to file
     */
    public DataModel(String filePath) {
        try {
            List<String> dataString = Files.readAllLines(Paths.get(filePath));
            data = preprocessData(dataString);
        } catch (java.io.IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Returns the data as a list of strings after running pre-processing on it
     *
     * @return data after pre-processing has been done
     */
    public List<List<Integer>> getData() {
        return data;
    }

    /**
     * Runs pre-processing steps on data (Discretization and Missing value checking)
     *
     * @param data to preprocess
     *
     */
    private List<List<Integer>> preprocessData(List<String> data) {
        List<List<Integer>> dataModel = new ArrayList<List<Integer>>(data.size());

        for (String dataRow : data) {
            //Fill in missing values
            dataRow = generateMissing(dataRow);
            //Discretize
            dataRow = discretize(dataRow);
            //Change from String to List<Integer>
            List<Integer> dataModelRow = Ints.asList(parseString(dataRow));
            dataModel.add(dataModelRow);
        }

        return dataModel;
    }

    /**
     * Converts a string to an int []
     * @param toParse String to change to int []
     * @return int[] created from String
     */
    private int[] parseString(String toParse) {
        return Arrays.stream(toParse.split(" ")).mapToInt(Integer::parseInt).toArray();
    }

    /**
     * Discretizes the data in the given row
     *
     * @param dataRow to discretize
     */
    private String discretize(String dataRow) {
        return dataRow;
    }

    /**
     * Generates missing values
     *
     * @param dataRow to generate missing data for
     * @return the data with missing values generated
     */
    private String generateMissing(String dataRow) {
        return dataRow.replaceAll("\\?", String.valueOf((int)(Math.random()* Integer.MAX_VALUE)));
    }
}
