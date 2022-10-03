package org.w3c.tidy.ant;

import org.apache.tools.ant.util.FileNameMapper;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.BufferedInputStream;
import org.apache.tools.ant.DirectoryScanner;
import java.util.Iterator;
import org.apache.tools.ant.util.IdentityMapper;
import org.apache.tools.ant.util.FlatFileNameMapper;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Parameter;
import org.apache.tools.ant.types.FileSet;
import java.util.ArrayList;
import java.util.Properties;
import org.w3c.tidy.Tidy;
import java.io.File;
import java.util.List;
import org.apache.tools.ant.Task;

public class JTidyTask extends Task
{
    private List filesets;
    private File destdir;
    private File destfile;
    private File srcfile;
    private boolean failonerror;
    private boolean flatten;
    private Tidy tidy;
    private Properties props;
    private File properties;
    
    public JTidyTask() {
        this.filesets = new ArrayList();
    }
    
    public void setDestdir(final File destdir) {
        this.destdir = destdir;
    }
    
    public void setDestfile(final File destfile) {
        this.destfile = destfile;
    }
    
    public void setSrcfile(final File srcfile) {
        this.srcfile = srcfile;
    }
    
    public void setFailonerror(final boolean failonerror) {
        this.failonerror = failonerror;
    }
    
    public void setFlatten(final boolean flatten) {
        this.flatten = flatten;
    }
    
    public void setProperties(final File properties) {
        this.properties = properties;
    }
    
    public void addFileset(final FileSet set) {
        this.filesets.add(set);
    }
    
    public void addConfiguredParameter(final Parameter parameter) {
        this.props.setProperty(parameter.getName(), parameter.getValue());
    }
    
    public void init() {
        super.init();
        this.tidy = new Tidy();
        this.props = new Properties();
    }
    
    protected void validateParameters() throws BuildException {
        if (this.srcfile == null && this.filesets.size() == 0) {
            throw new BuildException("Specify at least srcfile or a fileset.");
        }
        if (this.srcfile != null && this.filesets.size() > 0) {
            throw new BuildException("You can't specify both srcfile and nested filesets.");
        }
        if (this.destfile == null && this.destdir == null) {
            throw new BuildException("One of destfile or destdir must be set.");
        }
        if (this.srcfile == null && this.destfile != null) {
            throw new BuildException("You only can use destfile with srcfile.");
        }
        if (this.srcfile != null && this.srcfile.isDirectory()) {
            throw new BuildException("srcfile can't be a directory.");
        }
        if (this.properties != null && this.properties.isDirectory()) {
            throw new BuildException("Invalid properties file specified: " + this.properties.getPath());
        }
    }
    
    public void execute() throws BuildException {
        this.validateParameters();
        if (this.properties != null) {
            try {
                this.props.load(new FileInputStream(this.properties));
            }
            catch (final IOException ex) {
                throw new BuildException("Unable to load properties file " + this.properties, (Throwable)ex);
            }
        }
        this.tidy.setErrout(new PrintWriter(new ByteArrayOutputStream()));
        this.tidy.setConfigurationFromProps(this.props);
        if (this.srcfile != null) {
            this.executeSingle();
        }
        else {
            this.executeSet();
        }
    }
    
    protected void executeSingle() {
        if (!this.srcfile.exists()) {
            throw new BuildException("Could not find source file " + this.srcfile.getAbsolutePath() + ".");
        }
        if (this.destfile == null) {
            this.destfile = new File(this.destdir, this.srcfile.getName());
        }
        this.processFile(this.srcfile, this.destfile);
    }
    
    protected void executeSet() {
        Object o;
        if (this.flatten) {
            o = new FlatFileNameMapper();
        }
        else {
            o = new IdentityMapper();
        }
        ((FileNameMapper)o).setTo(this.destdir.getAbsolutePath());
        final Iterator iterator = this.filesets.iterator();
        while (iterator.hasNext()) {
            final DirectoryScanner directoryScanner = ((FileSet)iterator.next()).getDirectoryScanner(this.getProject());
            final String[] includedFiles = directoryScanner.getIncludedFiles();
            final File basedir = directoryScanner.getBasedir();
            ((FileNameMapper)o).setFrom(basedir.getAbsolutePath());
            for (int i = 0; i < includedFiles.length; ++i) {
                this.processFile(new File(basedir, includedFiles[i]), new File(this.destdir, ((FileNameMapper)o).mapFileName(includedFiles[i])[0]));
            }
        }
    }
    
    protected void processFile(final File file, final File file2) {
        this.log("Processing " + file.getAbsolutePath(), 4);
        BufferedInputStream bufferedInputStream;
        try {
            bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
        }
        catch (final IOException ex) {
            throw new BuildException("Unable to open file " + file);
        }
        BufferedOutputStream bufferedOutputStream;
        try {
            file2.getParentFile().mkdirs();
            file2.createNewFile();
            bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file2));
        }
        catch (final IOException ex2) {
            throw new BuildException("Unable to open destination file " + file2, (Throwable)ex2);
        }
        this.tidy.parse(bufferedInputStream, bufferedOutputStream);
        try {
            bufferedInputStream.close();
        }
        catch (final IOException ex3) {}
        try {
            bufferedOutputStream.flush();
            bufferedOutputStream.close();
        }
        catch (final IOException ex4) {}
        if (this.tidy.getParseErrors() > 0 && !this.tidy.getForceOutput()) {
            file2.delete();
        }
        if (this.failonerror && this.tidy.getParseErrors() > 0) {
            throw new BuildException("Tidy was unable to process file " + file + ", " + this.tidy.getParseErrors() + " returned.");
        }
    }
}
