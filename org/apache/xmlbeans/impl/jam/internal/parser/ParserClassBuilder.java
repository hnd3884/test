package org.apache.xmlbeans.impl.jam.internal.parser;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.xmlbeans.impl.jam.JamClassLoader;
import java.io.Reader;
import java.io.InputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.File;
import org.apache.xmlbeans.impl.jam.mutable.MClass;
import org.apache.xmlbeans.impl.jam.provider.JamServiceContext;
import java.io.OutputStream;
import java.io.PrintWriter;
import org.apache.xmlbeans.impl.jam.provider.ResourcePath;
import org.apache.xmlbeans.impl.jam.provider.JamClassPopulator;
import org.apache.xmlbeans.impl.jam.provider.JamClassBuilder;

public class ParserClassBuilder extends JamClassBuilder implements JamClassPopulator
{
    private static final boolean VERBOSE = false;
    private ResourcePath mSourcePath;
    private boolean mVerbose;
    private PrintWriter mOut;
    
    private ParserClassBuilder() {
        this.mVerbose = false;
        this.mOut = new PrintWriter(System.out);
    }
    
    public ParserClassBuilder(final JamServiceContext jsp) {
        this.mVerbose = false;
        this.mOut = new PrintWriter(System.out);
        this.mSourcePath = jsp.getInputSourcepath();
    }
    
    @Override
    public MClass build(final String pkg, final String name) {
        if (pkg == null) {
            throw new IllegalArgumentException("null pkg");
        }
        if (name == null) {
            throw new IllegalArgumentException("null name");
        }
        final String filespec = pkg.replace('.', File.separatorChar) + File.separatorChar + name + ".java";
        if (name.indexOf(".") != -1) {
            throw new IllegalArgumentException("inner classes are NYI at the moment");
        }
        final InputStream in = this.mSourcePath.findInPath(filespec);
        if (in == null) {
            if (this.mVerbose) {
                this.mOut.println("[ParserClassBuilder] could not find " + filespec);
            }
            return null;
        }
        if (this.mVerbose) {
            this.mOut.println("[ParserClassBuilder] loading class " + pkg + "  " + name);
            this.mOut.println("[ParserClassBuilder] from file " + filespec);
        }
        final Reader rin = new InputStreamReader(in);
        try {
            rin.close();
        }
        catch (final IOException ohwell) {
            ohwell.printStackTrace();
        }
        return null;
    }
    
    @Override
    public void populate(final MClass m) {
        throw new IllegalStateException("NYI");
    }
    
    private static MClass[] parse(final Reader in, final JamClassLoader loader) throws Exception {
        if (in == null) {
            throw new IllegalArgumentException("null in");
        }
        if (loader == null) {
            throw new IllegalArgumentException("null loader");
        }
        throw new IllegalStateException("temporarily NI");
    }
    
    public static void main(final String[] files) {
        new MainTool().process(files);
    }
    
    static class MainTool
    {
        private List mFailures;
        private int mCount;
        private PrintWriter mOut;
        private long mStartTime;
        
        MainTool() {
            this.mFailures = new ArrayList();
            this.mCount = 0;
            this.mOut = new PrintWriter(System.out);
            this.mStartTime = System.currentTimeMillis();
        }
        
        public void process(final String[] files) {
            try {
                for (int i = 0; i < files.length; ++i) {
                    final File input = new File(files[i]);
                    this.parse(new ParserClassBuilder((ParserClassBuilder$1)null), input);
                }
            }
            catch (final Exception e) {
                e.printStackTrace();
            }
            this.mOut.println("\n\n\n");
            final int fails = this.mFailures.size();
            if (fails != 0) {
                this.mOut.println("The following files failed to parse:");
                for (int j = 0; j < fails; ++j) {
                    this.mOut.println(this.mFailures.get(j).getAbsolutePath());
                }
            }
            this.mOut.println((this.mCount - fails) * 100 / this.mCount + "% (" + (this.mCount - fails) + "/" + this.mCount + ") " + "of input java files successfully parsed.");
            this.mOut.println("Total time: " + (System.currentTimeMillis() - this.mStartTime) / 1000L + " seconds.");
            this.mOut.flush();
            System.out.flush();
            System.err.flush();
        }
        
        private void parse(final ParserClassBuilder parser, final File input) throws Exception {
            System.gc();
            if (input.isDirectory()) {
                final File[] files = input.listFiles();
                for (int i = 0; i < files.length; ++i) {
                    this.parse(parser, files[i]);
                }
            }
            else {
                if (!input.getName().endsWith(".java")) {
                    return;
                }
                ++this.mCount;
                MClass[] results = null;
                try {
                    results = parse(new FileReader(input), null);
                    if (results == null) {
                        this.mOut.println("[error, parser result is null]");
                        this.addFailure(input);
                    }
                }
                catch (final Throwable e) {
                    e.printStackTrace(this.mOut);
                    this.addFailure(input);
                }
            }
        }
        
        private void addFailure(final File file) {
            this.mFailures.add(file);
        }
    }
}
