package com.machine.learning;

import com.machine.learning.classifier.Classifier;
import com.machine.learning.experimenter.MadScientist;
import com.machine.learning.model.DataModel;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String ... args) {
        System.out.println("Beginning testing...");
        OptionSet optionSet = getOptions(args);
        List<DataModel> dataModelList = new ArrayList<>();
        if (optionSet.valueOf("files").equals("*")) {

        } else {

        }

        List<Classifier> classifiers = new ArrayList<>();

        if (optionSet.valueOf("classifiers").equals("*")) {

        } else {

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
        parser.accepts("files").withRequiredArg().ofType(String.class).defaultsTo("*");
        parser.accepts("classifiers").withRequiredArg().ofType(String.class).defaultsTo("*");
        parser.accepts("outdir").withRequiredArg().ofType(String.class).defaultsTo("results");
        OptionSet options = parser.parse(args);
        return options;
    }
}
