package org.glassfish.jersey.server.internal.scanning;

import java.io.FileNotFoundException;
import java.util.NoSuchElementException;
import java.util.Stack;
import java.io.IOException;
import org.glassfish.jersey.server.ResourceFinder;
import java.io.InputStream;
import java.io.FileInputStream;
import org.glassfish.jersey.internal.util.Tokenizer;
import java.io.File;
import org.glassfish.jersey.server.internal.AbstractResourceFinderAdapter;

public final class FilesScanner extends AbstractResourceFinderAdapter
{
    private final File[] files;
    private final boolean recursive;
    private CompositeResourceFinder compositeResourceFinder;
    
    public FilesScanner(final String[] fileNames, final boolean recursive) {
        this.recursive = recursive;
        this.files = new File[Tokenizer.tokenize(fileNames, " ,;\n").length];
        for (int i = 0; i < this.files.length; ++i) {
            this.files[i] = new File(fileNames[i]);
        }
        this.init();
    }
    
    private void processFile(final File f) {
        Label_0062: {
            if (!f.getName().endsWith(".jar")) {
                if (!f.getName().endsWith(".zip")) {
                    break Label_0062;
                }
            }
            try {
                this.compositeResourceFinder.push(new JarFileScanner(new FileInputStream(f), "", true));
                return;
            }
            catch (final IOException e) {
                throw new ResourceFinderException(e);
            }
        }
        this.compositeResourceFinder.push(new AbstractResourceFinderAdapter() {
            Stack<File> files = new Stack<File>() {
                {
                    if (f.isDirectory()) {
                        final File[] subDirFiles = f.listFiles();
                        if (subDirFiles != null) {
                            for (final File file : subDirFiles) {
                                this.push(file);
                            }
                        }
                    }
                    else {
                        this.push(f);
                    }
                }
            };
            private File current;
            private File next;
            
            @Override
            public boolean hasNext() {
                while (this.next == null && !this.files.empty()) {
                    this.next = this.files.pop();
                    if (this.next.isDirectory()) {
                        if (FilesScanner.this.recursive) {
                            FilesScanner.this.processFile(this.next);
                        }
                        this.next = null;
                    }
                    else {
                        if (!this.next.getName().endsWith(".jar") && !this.next.getName().endsWith(".zip")) {
                            continue;
                        }
                        FilesScanner.this.processFile(this.next);
                        this.next = null;
                    }
                }
                return this.next != null;
            }
            
            @Override
            public String next() {
                if (this.next != null || this.hasNext()) {
                    this.current = this.next;
                    this.next = null;
                    return this.current.getName();
                }
                throw new NoSuchElementException();
            }
            
            @Override
            public InputStream open() {
                try {
                    return new FileInputStream(this.current);
                }
                catch (final FileNotFoundException e) {
                    throw new ResourceFinderException(e);
                }
            }
            
            @Override
            public void reset() {
                throw new UnsupportedOperationException();
            }
        });
    }
    
    @Override
    public boolean hasNext() {
        return this.compositeResourceFinder.hasNext();
    }
    
    @Override
    public String next() {
        return this.compositeResourceFinder.next();
    }
    
    @Override
    public InputStream open() {
        return this.compositeResourceFinder.open();
    }
    
    @Override
    public void close() {
        this.compositeResourceFinder.close();
    }
    
    @Override
    public void reset() {
        this.close();
        this.init();
    }
    
    private void init() {
        this.compositeResourceFinder = new CompositeResourceFinder();
        for (final File file : this.files) {
            this.processFile(file);
        }
    }
}
