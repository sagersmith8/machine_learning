package com.machine.learning;

import com.github.rschmitt.dynamicobject.DynamicObject;
import com.machine.learning.classifier.Classifier;
import com.machine.learning.classifier.ClassifierDefault;
import com.machine.learning.experimenter.MadScientist;
import com.machine.learning.model.DataModel;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String ... args) {
        System.out.println("Beginning testing...");
        OptionSet optionSet = getOptions(args);
        List<DataModel> dataModelList = new ArrayList<>();
        if (((List) optionSet.valueOf("files")).size() == 0) {
            dataModelList = Arrays.asList(
                    DynamicObject.newInstance(DataModel.class).fromFile("breast-cancer-wisconsin.data.txt"),
                    DynamicObject.newInstance(DataModel.class).fromFile("glass.data.txt"),
                    DynamicObject.newInstance(DataModel.class).fromFile("house-votes-84.data.txt"),
                    DynamicObject.newInstance(DataModel.class).fromFile("iris.data.txt"),
                    DynamicObject.newInstance(DataModel.class).fromFile("soybean-small.data.txt")
            );
        } else {
            List<String> files = (List<String>) optionSet.valueOf("files");
            for (String fileName: files) {
                dataModelList.add(DynamicObject.newInstance(DataModel.class).fromFile(fileName+".data.txt"));
            }
        }

        List<Classifier> classifiers = new ArrayList<>();
        Map<String, Classifier> classifierRegistry = new HashMap<>();
        classifierRegistry.put("default", new ClassifierDefault());

        if (((List) optionSet.valueOf("classifiers")).size() == 0) {
            Classifier classifier = new ClassifierDefault();
            classifiers.add(classifier);
        } else {
            List<String> classifierList = (List<String>) optionSet.valueOf("classifiers");
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
            String outfile = optionSet.valueOf("outdir").toString()+ System.nanoTime();
            pw = new PrintWriter(outfile);
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
        parser.accepts("files").withRequiredArg().ofType(List.class).defaultsTo(Collections.emptyList());
        parser.accepts("classifiers").withRequiredArg().ofType(List.class).defaultsTo(Collections.emptyList());
        parser.accepts("outdir").withRequiredArg().ofType(String.class).defaultsTo("results");
        OptionSet options = parser.parse(args);
        return options;
    }
}
