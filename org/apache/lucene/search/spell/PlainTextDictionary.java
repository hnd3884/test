package org.apache.lucene.search.spell;

import java.io.Closeable;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.BytesRefBuilder;
import org.apache.lucene.util.BytesRefIterator;
import org.apache.lucene.search.suggest.InputIterator;
import java.io.Reader;
import org.apache.lucene.util.IOUtils;
import java.io.InputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.io.BufferedReader;

public class PlainTextDictionary implements Dictionary
{
    private BufferedReader in;
    
    public PlainTextDictionary(final Path path) throws IOException {
        this.in = Files.newBufferedReader(path, StandardCharsets.UTF_8);
    }
    
    public PlainTextDictionary(final InputStream dictFile) {
        this.in = new BufferedReader(IOUtils.getDecodingReader(dictFile, StandardCharsets.UTF_8));
    }
    
    public PlainTextDictionary(final Reader reader) {
        this.in = new BufferedReader(reader);
    }
    
    @Override
    public InputIterator getEntryIterator() throws IOException {
        return new InputIterator.InputIteratorWrapper((BytesRefIterator)new FileIterator());
    }
    
    final class FileIterator implements BytesRefIterator
    {
        private boolean done;
        private final BytesRefBuilder spare;
        
        FileIterator() {
            this.done = false;
            this.spare = new BytesRefBuilder();
        }
        
        public BytesRef next() throws IOException {
            if (this.done) {
                return null;
            }
            boolean success = false;
            BytesRef result;
            try {
                final String line;
                if ((line = PlainTextDictionary.this.in.readLine()) != null) {
                    this.spare.copyChars((CharSequence)line);
                    result = this.spare.get();
                }
                else {
                    this.done = true;
                    IOUtils.close(new Closeable[] { PlainTextDictionary.this.in });
                    result = null;
                }
                success = true;
            }
            finally {
                if (!success) {
                    IOUtils.closeWhileHandlingException(new Closeable[] { PlainTextDictionary.this.in });
                }
            }
            return result;
        }
    }
}
