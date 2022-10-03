package org.apache.xmlbeans.impl.util;

import java.io.Reader;
import java.io.FileReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.CharsetEncoder;
import java.io.OutputStreamWriter;
import java.nio.charset.CodingErrorAction;
import java.io.FileWriter;
import java.util.Collection;
import java.io.Writer;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.ArrayList;
import java.nio.charset.Charset;
import java.util.Set;
import java.util.List;
import org.apache.xmlbeans.impl.repackage.Repackager;
import java.io.File;
import org.apache.xmlbeans.Filer;

public class FilerImpl implements Filer
{
    private File classdir;
    private File srcdir;
    private Repackager repackager;
    private boolean verbose;
    private List sourceFiles;
    private boolean incrSrcGen;
    private Set seenTypes;
    private static final Charset CHARSET;
    
    public FilerImpl(final File classdir, final File srcdir, final Repackager repackager, final boolean verbose, final boolean incrSrcGen) {
        this.classdir = classdir;
        this.srcdir = srcdir;
        this.repackager = repackager;
        this.verbose = verbose;
        this.sourceFiles = ((this.sourceFiles != null) ? this.sourceFiles : new ArrayList());
        this.incrSrcGen = incrSrcGen;
        if (this.incrSrcGen) {
            this.seenTypes = new HashSet();
        }
    }
    
    @Override
    public OutputStream createBinaryFile(final String typename) throws IOException {
        if (this.verbose) {
            System.err.println("created binary: " + typename);
        }
        final File source = new File(this.classdir, typename);
        source.getParentFile().mkdirs();
        return new FileOutputStream(source);
    }
    
    @Override
    public Writer createSourceFile(String typename) throws IOException {
        if (this.incrSrcGen) {
            this.seenTypes.add(typename);
        }
        if (typename.indexOf(36) > 0) {
            typename = typename.substring(0, typename.lastIndexOf(46)) + "." + typename.substring(typename.indexOf(36) + 1);
        }
        final String filename = typename.replace('.', File.separatorChar) + ".java";
        final File sourcefile = new File(this.srcdir, filename);
        sourcefile.getParentFile().mkdirs();
        if (this.verbose) {
            System.err.println("created source: " + sourcefile.getAbsolutePath());
        }
        this.sourceFiles.add(sourcefile);
        if (this.incrSrcGen && sourcefile.exists()) {
            return new IncrFileWriter(sourcefile, this.repackager);
        }
        return (this.repackager == null) ? writerForFile(sourcefile) : new RepackagingWriter(sourcefile, this.repackager);
    }
    
    public List getSourceFiles() {
        return new ArrayList(this.sourceFiles);
    }
    
    public Repackager getRepackager() {
        return this.repackager;
    }
    
    private static final Writer writerForFile(final File f) throws IOException {
        if (FilerImpl.CHARSET == null) {
            return new FileWriter(f);
        }
        final FileOutputStream fileStream = new FileOutputStream(f);
        final CharsetEncoder ce = FilerImpl.CHARSET.newEncoder();
        ce.onUnmappableCharacter(CodingErrorAction.REPORT);
        return new OutputStreamWriter(fileStream, ce);
    }
    
    static {
        Charset temp = null;
        try {
            temp = Charset.forName(System.getProperty("file.encoding"));
        }
        catch (final Exception ex) {}
        CHARSET = temp;
    }
    
    static class IncrFileWriter extends StringWriter
    {
        private File _file;
        private Repackager _repackager;
        
        public IncrFileWriter(final File file, final Repackager repackager) {
            this._file = file;
            this._repackager = repackager;
        }
        
        @Override
        public void close() throws IOException {
            super.close();
            final StringBuffer sb = (this._repackager != null) ? this._repackager.repackage(this.getBuffer()) : this.getBuffer();
            final String str = sb.toString();
            final List diffs = new ArrayList();
            final StringReader sReader = new StringReader(str);
            final FileReader fReader = new FileReader(this._file);
            try {
                Diff.readersAsText(sReader, "<generated>", fReader, this._file.getName(), diffs);
            }
            finally {
                sReader.close();
                fReader.close();
            }
            if (diffs.size() > 0) {
                final Writer fw = writerForFile(this._file);
                try {
                    fw.write(str);
                }
                finally {
                    fw.close();
                }
            }
        }
    }
    
    static class RepackagingWriter extends StringWriter
    {
        private File _file;
        private Repackager _repackager;
        
        public RepackagingWriter(final File file, final Repackager repackager) {
            this._file = file;
            this._repackager = repackager;
        }
        
        @Override
        public void close() throws IOException {
            super.close();
            final Writer fw = writerForFile(this._file);
            try {
                fw.write(this._repackager.repackage(this.getBuffer()).toString());
            }
            finally {
                fw.close();
            }
        }
    }
}
