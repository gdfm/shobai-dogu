package com.yahoo.research.bcn;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

/**
 * A line by line iterator.
 * 
 */
public class LineIterable implements Iterable<String>, Closeable {
    private final BufferedReader reader;

    public LineIterable(String fileName) throws IOException {
        reader = new BufferedReader(new FileReader(fileName));
    }

    public LineIterable(File file) throws IOException {
        reader = new BufferedReader(new FileReader(file));
    }

    public LineIterable(BufferedReader bufferedReader) throws IOException {
        reader = bufferedReader;
    }

    @Override
    public Iterator<String> iterator() {
        return new Iterator<String>() {
            private String line = null;

            public boolean hasNext() {
                try {
                    line = reader.readLine();
                    if (line != null) {
                        return true;
                    } else {
                        reader.close();
                        return false;
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            public String next() {
                return line;
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }

    @Override
    protected void finalize() throws Throwable {
        reader.close();
    }
}
