package com.gihub.gdfm.shobaidogu;

/*
 * #%L
 * shobai-dogu
 * %%
 * Copyright (C) 2012 - 2013 gdfm
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
