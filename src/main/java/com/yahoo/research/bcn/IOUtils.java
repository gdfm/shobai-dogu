package com.yahoo.research.bcn;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class IOUtils {

    /**
     * Create a BufferedReader from either a resource name or a file name. This method will first try to get an a resource stream (usually from a file in a
     * jar), fall back to a resource on the classpath, and finally fall back to a file by absolute path.
     * 
     * @param source
     *            the name of the source.
     * @return a BufferedReader for the given source.
     * @throws FileNotFoundException
     */
    public static BufferedReader getBufferedReader(String source) throws FileNotFoundException {
        InputStream is = StatsUtils.class.getResourceAsStream(source);
        if (is != null) {
            return new BufferedReader(new InputStreamReader(is));
        } else {
            File file;
            URL url = StatsUtils.class.getResource(source);
            if (url != null) {
                file = new File(url.getPath());
            } else {
                file = new File(source);
            }
            return new BufferedReader(new FileReader(file));
        }
    }

    /**
     * Read stopwords in memory from a file, one per line. Stopwords are converted to lowercase when read.
     * 
     * @param reader
     *            the input reader.
     * @return a set of stopwords.
     * @throws IOException
     */
    public static Set<String> readStopwords(BufferedReader reader) throws IOException {
        HashSet<String> words = new HashSet<String>();
        String nextLine;
        while (((nextLine = reader.readLine()) != null)) {
            words.add(nextLine.trim().toLowerCase());
        }
        return words;
    }

    /**
     * Computes inverse document frequencies as a map of Strings to Doubles from a tab separated file of term document frequencies.
     * 
     * @param reader
     *            the input reader.
     * @param N
     *            the number of documents in the collection.
     * @return the inverse document frequencies.
     * @throws IOException
     */
    public static Map<String, Double> readIdfs(BufferedReader reader, int N) throws IOException {
        HashMap<String, Double> idfs = new HashMap<String, Double>();
        String nextLine;
        while (((nextLine = reader.readLine()) != null)) {
            String[] parts = nextLine.split("\\t+");
            checkArgument(parts.length >= 2);
            Double df = Double.parseDouble(parts[1]);
            idfs.put(parts[0], Math.log(1.0 + N / df)); // idf
        }
        return idfs;
    }

    /**
     * Computes the number of files in the input Reader.
     * 
     * @param in
     *            the reader.
     * @return the number of lines.
     * @throws IOException
     */
    public static int getNumberOfLines(Reader in) throws IOException {
        LineNumberReader lnr = new LineNumberReader(in);
        lnr.skip(Long.MAX_VALUE);
        in.close();
        return lnr.getLineNumber();
    }
}