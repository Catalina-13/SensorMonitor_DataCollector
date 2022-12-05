package com.example.sensormonitor_datacollector;

import android.os.Build;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.BufferedReader;
import java.io.DataInput;
import java.io.IOException;
import java.lang.Math;

import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class FeatureVector {

    ArrayList featureVector = new ArrayList();

    public ArrayList transformCSVIn2DimTab(String fileName1,String fileName2,String fileName3) {

        ArrayList<ArrayList<Float>> generalTab = new ArrayList<>();
        Path pathToFile = null;
        ArrayList<String> files = new ArrayList<String>();
        files.add(fileName1);
        files.add(fileName2);
        files.add(fileName3);

        for (int i = 0; i < 3; i++) {

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                pathToFile = Paths.get(files.get(i));
            }

            // create an instance of BufferedReader
            // using try with resource, Java 7 feature to close resources
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                try (BufferedReader br = Files.newBufferedReader(pathToFile,
                        StandardCharsets.US_ASCII)) {

                    // read the first line from the text file
                    String line = br.readLine();
                    int numLine = 1;

                    // loop until all lines are read
                    while (line != null) {

                        String[] stringAttributes = line.split(",");
                        ArrayList<Float> attributes = new ArrayList<>();
                        for (int k = 0; k < stringAttributes.length; k++) {
                            attributes.add(Float.parseFloat(stringAttributes[k]));
                        }

                        for (int j = 0; j < 3; i++) {
                            generalTab.get(numLine).add(attributes.get(j+1));
                        }

                        // read next line before looping
                        // if end of file reached, line would be null
                        numLine++;
                        line = br.readLine();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return generalTab;

    }

    double s1 = 0;
    double s2 = 0;
    double entropy = 0.0;

    public void mean (ArrayList<Double> timestamps){
        for (int n = 0; n < timestamps.size(); n++) {
            s1 += timestamps.get(n);
        }
        featureVector.add(s1 / timestamps.size());
    }

    public void informationEntropy (ArrayList<Integer> timestamps, int m) {
        // compute frequencies
        // freq[n] = # times integer n appears
        int[] freq = new int[m+1];
        for (int j = 0; j < timestamps.size(); j++) {
            freq[timestamps.get(j)]++;
        }

        // compute Shannon entropy
        for (int n = 1; n <= timestamps.size(); n++) {
            double p = 1.0 * freq[n] / timestamps.size();
            if (freq[n] > 0)
                entropy -= p * Math.log(p) / Math.log(2);
        }
        featureVector.add(entropy);
    }

    public void totalEnergyOfFrequencySpectrum (ArrayList<Double> timestamps, int m){
        for (int n = 0; n < m; n++) {
            s2 += Math.abs(timestamps.get(n)) * Math.abs(timestamps.get(n));
        }
        featureVector.add(s2 / timestamps.size());
    }


    public void correlation (double[] xs, double[] ys) {

        double sx = 0.0;
        double sy = 0.0;
        double sxx = 0.0;
        double syy = 0.0;
        double sxy = 0.0;

        int n = xs.length;

        for(int i = 0; i < n; ++i) {
            double x = xs[i];
            double y = ys[i];

            sx += x;
            sy += y;
            sxx += x * x;
            syy += y * y;
            sxy += x * y;
        }

        // covariation
        double cov = sxy / n - sx * sy / n / n;
        // standard error of x
        double sigmax = Math.sqrt(sxx / n -  sx * sx / n / n);
        // standard error of y
        double sigmay = Math.sqrt(syy / n -  sy * sy / n / n);

        // correlation is just a normalized covariation
        featureVector.add(cov / sigmax / sigmay);
    }

}
