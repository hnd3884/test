package org.apache.lucene.search.suggest;

import java.util.Set;
import org.apache.lucene.util.BytesRef;
import java.io.Closeable;
import org.apache.lucene.util.BytesRefBuilder;
import java.io.IOException;
import org.apache.lucene.util.IOUtils;
import java.nio.charset.StandardCharsets;
import java.io.Reader;
import java.io.InputStream;
import java.io.BufferedReader;
import org.apache.lucene.search.spell.Dictionary;

public class FileDictionary implements Dictionary
{
    public static final String DEFAULT_FIELD_DELIMITER = "\t";
    private BufferedReader in;
    private String line;
    private boolean done;
    private final String fieldDelimiter;
    
    public FileDictionary(final InputStream dictFile) {
        this(dictFile, "\t");
    }
    
    public FileDictionary(final Reader reader) {
        this(reader, "\t");
    }
    
    public FileDictionary(final Reader reader, final String fieldDelimiter) {
        this.done = false;
        this.in = new BufferedReader(reader);
        this.fieldDelimiter = fieldDelimiter;
    }
    
    public FileDictionary(final InputStream dictFile, final String fieldDelimiter) {
        this.done = false;
        this.in = new BufferedReader(IOUtils.getDecodingReader(dictFile, StandardCharsets.UTF_8));
        this.fieldDelimiter = fieldDelimiter;
    }
    
    @Override
    public InputIterator getEntryIterator() {
        try {
            return new FileIterator();
        }
        catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    final class FileIterator implements InputIterator
    {
        private long curWeight;
        private final BytesRefBuilder spare;
        private BytesRefBuilder curPayload;
        private boolean isFirstLine;
        private boolean hasPayloads;
        
        private FileIterator() throws IOException {
            this.spare = new BytesRefBuilder();
            this.curPayload = new BytesRefBuilder();
            this.isFirstLine = true;
            this.hasPayloads = false;
            FileDictionary.this.line = FileDictionary.this.in.readLine();
            if (FileDictionary.this.line == null) {
                FileDictionary.this.done = true;
                IOUtils.close(new Closeable[] { FileDictionary.this.in });
            }
            else {
                final String[] fields = FileDictionary.this.line.split(FileDictionary.this.fieldDelimiter);
                if (fields.length > 3) {
                    throw new IllegalArgumentException("More than 3 fields in one line");
                }
                if (fields.length == 3) {
                    this.hasPayloads = true;
                    this.spare.copyChars((CharSequence)fields[0]);
                    this.readWeight(fields[1]);
                    this.curPayload.copyChars((CharSequence)fields[2]);
                }
                else if (fields.length == 2) {
                    this.spare.copyChars((CharSequence)fields[0]);
                    this.readWeight(fields[1]);
                }
                else {
                    this.spare.copyChars((CharSequence)fields[0]);
                    this.curWeight = 1L;
                }
            }
        }
        
        @Override
        public long weight() {
            return this.curWeight;
        }
        
        public BytesRef next() throws IOException {
            if (FileDictionary.this.done) {
                return null;
            }
            if (this.isFirstLine) {
                this.isFirstLine = false;
                return this.spare.get();
            }
            FileDictionary.this.line = FileDictionary.this.in.readLine();
            if (FileDictionary.this.line == null) {
                FileDictionary.this.done = true;
                IOUtils.close(new Closeable[] { FileDictionary.this.in });
                return null;
            }
            final String[] fields = FileDictionary.this.line.split(FileDictionary.this.fieldDelimiter);
            if (fields.length > 3) {
                throw new IllegalArgumentException("More than 3 fields in one line");
            }
            if (fields.length == 3) {
                this.spare.copyChars((CharSequence)fields[0]);
                this.readWeight(fields[1]);
                if (this.hasPayloads) {
                    this.curPayload.copyChars((CharSequence)fields[2]);
                }
            }
            else if (fields.length == 2) {
                this.spare.copyChars((CharSequence)fields[0]);
                this.readWeight(fields[1]);
                if (this.hasPayloads) {
                    this.curPayload = new BytesRefBuilder();
                }
            }
            else {
                this.spare.copyChars((CharSequence)fields[0]);
                this.curWeight = 1L;
                if (this.hasPayloads) {
                    this.curPayload = new BytesRefBuilder();
                }
            }
            return this.spare.get();
        }
        
        @Override
        public BytesRef payload() {
            return this.hasPayloads ? this.curPayload.get() : null;
        }
        
        @Override
        public boolean hasPayloads() {
            return this.hasPayloads;
        }
        
        private void readWeight(final String weight) {
            try {
                this.curWeight = Long.parseLong(weight);
            }
            catch (final NumberFormatException e) {
                this.curWeight = (long)Double.parseDouble(weight);
            }
        }
        
        @Override
        public Set<BytesRef> contexts() {
            return null;
        }
        
        @Override
        public boolean hasContexts() {
            return false;
        }
    }
}
