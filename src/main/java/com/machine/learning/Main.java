package com.machine.learning;

import com.github.rschmitt.dynamicobject.DynamicObject;
import com.machine.learning.classifier.Classifier;
import com.machine.learning.classifier.ClassifierDefault;
import com.machine.learning.classifier.KNearestNeighbors;
import com.machine.learning.classifier.NaiveBayes;
import com.machine.learning.classifier.TreeAugmentedNaiveBayes;
import com.machine.learning.classifier.ID3;
import com.machine.learning.experimenter.MadScientist;
import com.machine.learning.model.DataModel;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String ... args) {
        System.out.println("Beginning testing...");
        OptionSet optionSet = getOptions(args);
        List<DataModel> dataModelList = new ArrayList<>();
        if ((optionSet.valueOf("files")).equals("")) {
            dataModelList = Arrays.asList(
                    DynamicObject.newInstance(DataModel.class).fromFile("breast-cancer-wisconsin.data.txt"),
                    DynamicObject.newInstance(DataModel.class).fromFile("glass.data.txt"),
                    DynamicObject.newInstance(DataModel.class).fromFile("house-votes-84.data.txt"),
                    DynamicObject.newInstance(DataModel.class).fromFile("iris.data.txt"),
                    DynamicObject.newInstance(DataModel.class).fromFile("soybean-small.data.txt")
            );
        } else {
            List<String> files = Arrays.asList(optionSet.valueOf("files").toString().split(",", 0));
            for (String fileName: files) {
                dataModelList.add(DynamicObject.newInstance(DataModel.class).fromFile(fileName+".data.txt"));
            }
        }

        List<Classifier> classifiers = new ArrayList<>();
        Map<String, Classifier> classifierRegistry = new HashMap<>();
        classifierRegistry.put("default", new ClassifierDefault());
        for (int i = 1; i <= 15; i+=2) { 
            classifierRegistry.put("kNN"+i, new KNearestNeighbors(i));
        }
        classifierRegistry.put("naive-bayes", new NaiveBayes());
        classifierRegistry.put("tree-naive-bayes", new TreeAugmentedNaiveBayes());
        classifierRegistry.put("id3", new ID3());

        if ((optionSet.valueOf("classifiers")).equals("")) {
            classifiers.addAll(classifierRegistry.values());
        } else {
            List<String> classifierList = Arrays.asList(optionSet.valueOf("classifiers").toString().split(",", 0));
            for (String classifierId: classifierList) {
                Classifier classifier = classifierRegistry.get(classifierId);
                if (classifier != null) {
                    classifiers.add(classifier);
                } else {
                    System.err.println("Classifier "+ classifierId + "isn't registered.");
                }
            }
        }

        MadScientist madScientist = new MadScientist(dataModelList, classifiers);
        PrintWriter pw = null;
        try {
            Calendar calendar = Calendar.getInstance();
            java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(calendar.getTime().getTime());

            File file = new File(optionSet.valueOf("outdir").toString());
            if (!file.exists()) {
                file.mkdir();
            }
            String outfile = optionSet.valueOf("outdir").toString()+File.separator+currentTimestamp;
            pw = new PrintWriter(outfile);
            pw.write(madScientist.getResults());
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } finally {
            if (pw != null) {
                pw.close();
            }
        }

        System.out.println("All tests finished");
    }

    private static OptionSet getOptions(String... args) {
        OptionParser parser = new OptionParser();
        parser.accepts("files").withRequiredArg().ofType(String.class).defaultsTo("");
        parser.accepts("classifiers").withRequiredArg().ofType(String.class).defaultsTo("");
        parser.accepts("outdir").withRequiredArg().ofType(String.class).defaultsTo("results");
        OptionSet options = parser.parse(args);
        return options;
    }
}
