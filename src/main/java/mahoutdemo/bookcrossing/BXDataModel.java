package mahoutdemo.bookcrossing;

import org.apache.commons.io.Charsets;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.common.iterator.FileLineIterable;

import java.io.*;
import java.util.regex.Pattern;

public class BXDataModel extends FileDataModel {

    private static Pattern NON_DIGIT_SEMICOLON_DELIMITER = Pattern.compile("[^0-9;]");

    public BXDataModel(File ratingFile, Boolean ignoreRatings) throws IOException {
        super(convertFile(ratingFile, ignoreRatings));
    }

    public static File convertFile(File orginalFile, Boolean ignoreRatings) throws IOException {
        File resultFile = new File(System.getProperty("java.io.tmpdir"), "bookcrossing.csv");
        if (resultFile.exists()) {
            resultFile.delete();
        }
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(resultFile), Charsets.UTF_8)) {

            for (String line : new FileLineIterable(orginalFile, true)) {
                if (line.endsWith("\"0\"")) {
                    continue;
                }
                String convertedLine = NON_DIGIT_SEMICOLON_DELIMITER.matcher(line).replaceAll("").replace(';', ',');
                if (convertedLine.contains(",,")) {
                    continue;
                }
                if (ignoreRatings) {
                    convertedLine = convertedLine.substring(0, convertedLine.lastIndexOf(','));
                }

                writer.write(convertedLine);
                writer.write('\n');
            }
        } catch (IOException ioe) {
            resultFile.delete();
            throw ioe;
        }
        return resultFile;
    }

}
