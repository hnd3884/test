package eu.medsea.mimeutil.detector;

import java.util.Arrays;
import java.io.IOException;
import java.io.BufferedReader;
import java.util.regex.Pattern;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.Enumeration;
import java.io.Reader;
import java.io.InputStreamReader;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import eu.medsea.mimeutil.MimeException;
import java.io.BufferedInputStream;
import eu.medsea.mimeutil.MimeUtil;
import java.net.URL;
import java.io.File;
import java.io.InputStream;
import java.util.LinkedHashSet;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import eu.medsea.mimeutil.MimeUtil2;

public class MagicMimeMimeDetector extends MimeDetector
{
    private static final MimeUtil2.MimeLogger log;
    protected static String[] defaultLocations;
    private static List magicMimeFileLocations;
    private static ArrayList mMagicMimeEntries;
    
    public MagicMimeMimeDetector() {
        initMagicRules();
    }
    
    @Override
    public String getDescription() {
        return "Get the mime types of files or streams using the Unix file(5) magic.mime files";
    }
    
    public Collection getMimeTypesByteArray(final byte[] data) throws UnsupportedOperationException {
        final Collection mimeTypes = new LinkedHashSet();
        final int len = MagicMimeMimeDetector.mMagicMimeEntries.size();
        try {
            for (int i = 0; i < len; ++i) {
                final MagicMimeEntry me = MagicMimeMimeDetector.mMagicMimeEntries.get(i);
                final MagicMimeEntry matchingMagicMimeEntry = me.getMatch(data);
                if (matchingMagicMimeEntry != null) {
                    mimeTypes.add(matchingMagicMimeEntry.getMimeType());
                }
            }
        }
        catch (final Exception e) {
            MagicMimeMimeDetector.log.error(e.getMessage(), e);
        }
        return mimeTypes;
    }
    
    public Collection getMimeTypesInputStream(final InputStream in) throws UnsupportedOperationException {
        final Collection mimeTypes = new LinkedHashSet();
        final int len = MagicMimeMimeDetector.mMagicMimeEntries.size();
        try {
            for (int i = 0; i < len; ++i) {
                final MagicMimeEntry me = MagicMimeMimeDetector.mMagicMimeEntries.get(i);
                final MagicMimeEntry matchingMagicMimeEntry = me.getMatch(in);
                if (matchingMagicMimeEntry != null) {
                    mimeTypes.add(matchingMagicMimeEntry.getMimeType());
                }
            }
        }
        catch (final Exception e) {
            MagicMimeMimeDetector.log.error(e.getMessage(), e);
        }
        return mimeTypes;
    }
    
    public Collection getMimeTypesFileName(final String fileName) throws UnsupportedOperationException {
        return this.getMimeTypesFile(new File(fileName));
    }
    
    public Collection getMimeTypesURL(final URL url) throws UnsupportedOperationException {
        InputStream in = null;
        try {
            return this.getMimeTypesInputStream(in = new BufferedInputStream(MimeUtil.getInputStreamForURL(url)));
        }
        catch (final Exception e) {
            throw new MimeException(e);
        }
        finally {
            MimeDetector.closeStream(in);
        }
    }
    
    public Collection getMimeTypesFile(final File file) throws UnsupportedOperationException {
        InputStream in = null;
        try {
            return this.getMimeTypesInputStream(in = new BufferedInputStream(new FileInputStream(file)));
        }
        catch (final FileNotFoundException e) {
            throw new UnsupportedOperationException(e.getLocalizedMessage());
        }
        catch (final Exception e2) {
            throw new MimeException(e2);
        }
        finally {
            MimeDetector.closeStream(in);
        }
    }
    
    private static void initMagicRules() {
        InputStream in = null;
        try {
            final String fname = System.getProperty("magic-mime");
            if (fname != null && fname.length() != 0) {
                in = new FileInputStream(fname);
                if (in != null) {
                    parse("-Dmagic-mime=" + fname, new InputStreamReader(in));
                }
            }
        }
        catch (final Exception e) {
            MagicMimeMimeDetector.log.error("Failed to parse custom magic mime file defined by system property -Dmagic-mime [" + System.getProperty("magic-mime") + "]. File will be ignored.", e);
        }
        finally {
            in = MimeDetector.closeStream(in);
        }
        try {
            final Enumeration en = MimeUtil.class.getClassLoader().getResources("magic.mime");
            while (en.hasMoreElements()) {
                final URL url = en.nextElement();
                in = url.openStream();
                if (in != null) {
                    try {
                        parse("classpath:[" + url + "]", new InputStreamReader(in));
                    }
                    catch (final Exception ex) {
                        MagicMimeMimeDetector.log.error("Failed to parse magic.mime rule file [" + url + "] on the classpath. File will be ignored.", ex);
                    }
                }
            }
        }
        catch (final Exception e) {
            MagicMimeMimeDetector.log.error("Problem while processing magic.mime files from classpath. Files will be ignored.", e);
        }
        finally {
            in = MimeDetector.closeStream(in);
        }
        try {
            final File f = new File(System.getProperty("user.home") + File.separator + ".magic.mime");
            if (f.exists()) {
                in = new FileInputStream(f);
                if (in != null) {
                    try {
                        parse(f.getAbsolutePath(), new InputStreamReader(in));
                    }
                    catch (final Exception ex2) {
                        MagicMimeMimeDetector.log.error("Failed to parse .magic.mime file from the users home directory. File will be ignored.", ex2);
                    }
                }
            }
        }
        catch (final Exception e) {
            MagicMimeMimeDetector.log.error("Problem while processing .magic.mime file from the users home directory. File will be ignored.", e);
        }
        finally {
            in = MimeDetector.closeStream(in);
        }
        try {
            String name = System.getProperty("MAGIC");
            if (name != null && name.length() != 0) {
                if (name.indexOf(46) < 0) {
                    name += ".mime";
                }
                else {
                    name = name.substring(0, name.indexOf(46) - 1) + "mime";
                }
                final File f2 = new File(name);
                if (f2.exists()) {
                    in = new FileInputStream(f2);
                    if (in != null) {
                        try {
                            parse(f2.getAbsolutePath(), new InputStreamReader(in));
                        }
                        catch (final Exception ex) {
                            MagicMimeMimeDetector.log.error("Failed to parse magic.mime file from directory located by environment variable MAGIC. File will be ignored.", ex);
                        }
                    }
                }
            }
        }
        catch (final Exception e) {
            MagicMimeMimeDetector.log.error("Problem while processing magic.mime file from directory located by environment variable MAGIC. File will be ignored.", e);
        }
        finally {
            in = MimeDetector.closeStream(in);
        }
        final int mMagicMimeEntriesSizeBeforeReadingOS = MagicMimeMimeDetector.mMagicMimeEntries.size();
        final Iterator it = MagicMimeMimeDetector.magicMimeFileLocations.iterator();
        while (it.hasNext()) {
            parseMagicMimeFileLocation(it.next());
        }
        if (mMagicMimeEntriesSizeBeforeReadingOS == MagicMimeMimeDetector.mMagicMimeEntries.size()) {
            try {
                final String resource = "eu/medsea/mimeutil/magic.mime";
                in = MimeUtil.class.getClassLoader().getResourceAsStream(resource);
                if (in != null) {
                    try {
                        parse("resource:" + resource, new InputStreamReader(in));
                    }
                    catch (final Exception ex3) {
                        MagicMimeMimeDetector.log.error("Failed to parse internal magic.mime file.", ex3);
                    }
                }
            }
            catch (final Exception e2) {
                MagicMimeMimeDetector.log.error("Problem while processing internal magic.mime file.", e2);
            }
            finally {
                in = MimeDetector.closeStream(in);
            }
        }
    }
    
    private static void parseMagicMimeFileLocation(final String location) {
        InputStream is = null;
        final List magicMimeFiles = getMagicFilesFromMagicMimeFileLocation(location);
        for (final File f : magicMimeFiles) {
            try {
                if (!f.exists()) {
                    continue;
                }
                is = new FileInputStream(f);
                try {
                    parse(f.getAbsolutePath(), new InputStreamReader(is));
                }
                catch (final Exception e) {
                    MagicMimeMimeDetector.log.error("Failed to parse " + f.getName() + ". File will be ignored.");
                }
            }
            catch (final Exception e) {
                MagicMimeMimeDetector.log.error(e.getMessage(), e);
            }
            finally {
                is = MimeDetector.closeStream(is);
            }
        }
    }
    
    private static List getMagicFilesFromMagicMimeFileLocation(final String magicMimeFileLocation) {
        final List magicMimeFiles = new LinkedList();
        if (magicMimeFileLocation.indexOf(42) < 0) {
            magicMimeFiles.add(new File(magicMimeFileLocation));
        }
        else {
            final int lastSlashPos = magicMimeFileLocation.lastIndexOf(47);
            File dir;
            String fileNameSimplePattern;
            if (lastSlashPos < 0) {
                dir = new File("someProbablyNotExistingFile").getAbsoluteFile().getParentFile();
                fileNameSimplePattern = magicMimeFileLocation;
            }
            else {
                final String dirName = magicMimeFileLocation.substring(0, lastSlashPos);
                if (dirName.indexOf(42) >= 0) {
                    throw new UnsupportedOperationException("The wildcard '*' is not allowed in directory part of the location! Do you want to implement expressions like /path/**/*.mime for recursive search? Please do!");
                }
                dir = new File(dirName);
                fileNameSimplePattern = magicMimeFileLocation.substring(lastSlashPos + 1);
            }
            if (!dir.isDirectory()) {
                return Collections.EMPTY_LIST;
            }
            String s = fileNameSimplePattern.replaceAll("\\.", "\\\\.");
            s = s.replaceAll("\\*", ".*");
            final Pattern fileNamePattern = Pattern.compile(s);
            final File[] files = dir.listFiles();
            for (int i = 0; i < files.length; ++i) {
                final File file = files[i];
                if (fileNamePattern.matcher(file.getName()).matches()) {
                    magicMimeFiles.add(file);
                }
            }
        }
        return magicMimeFiles;
    }
    
    private static void parse(final String magicFile, final Reader r) throws IOException {
        final long start = System.currentTimeMillis();
        final BufferedReader br = new BufferedReader(r);
        final ArrayList sequence = new ArrayList();
        long lineNumber = 0L;
        String line = br.readLine();
        if (line != null) {
            ++lineNumber;
        }
        while (line != null) {
            line = line.trim();
            if (line.length() == 0 || line.charAt(0) == '#') {
                line = br.readLine();
                if (line == null) {
                    continue;
                }
                ++lineNumber;
            }
            else {
                sequence.add(line);
                while (true) {
                    line = br.readLine();
                    if (line != null) {
                        ++lineNumber;
                    }
                    if (line == null) {
                        addEntry(magicFile, lineNumber, sequence);
                        sequence.clear();
                        break;
                    }
                    line = line.trim();
                    if (line.length() == 0) {
                        continue;
                    }
                    if (line.charAt(0) == '#') {
                        continue;
                    }
                    if (line.charAt(0) != '>') {
                        addEntry(magicFile, lineNumber, sequence);
                        sequence.clear();
                        break;
                    }
                    sequence.add(line);
                }
            }
        }
        if (!sequence.isEmpty()) {
            addEntry(magicFile, lineNumber, sequence);
        }
        if (MagicMimeMimeDetector.log.isDebugEnabled()) {
            MagicMimeMimeDetector.log.debug("Parsing \"" + magicFile + "\" took " + (System.currentTimeMillis() - start) + " msec.");
        }
    }
    
    private static void addEntry(final String magicFile, final long lineNumber, final ArrayList aStringArray) {
        try {
            final MagicMimeEntry magicEntry = new MagicMimeEntry(aStringArray);
            MagicMimeMimeDetector.mMagicMimeEntries.add(magicEntry);
            if (magicEntry.getMimeType() != null) {
                MimeUtil.addKnownMimeType(magicEntry.getMimeType());
            }
        }
        catch (final InvalidMagicMimeEntryException e) {
            MagicMimeMimeDetector.log.warn(e.getClass().getName() + ": " + e.getMessage() + ": file \"" + magicFile + "\": before or at line " + lineNumber, e);
        }
    }
    
    static {
        log = new MimeUtil2.MimeLogger(MagicMimeMimeDetector.class.getName());
        MagicMimeMimeDetector.defaultLocations = new String[] { "/usr/share/mimelnk/magic", "/usr/share/file/magic.mime", "/etc/magic.mime" };
        MagicMimeMimeDetector.magicMimeFileLocations = Arrays.asList(MagicMimeMimeDetector.defaultLocations);
        MagicMimeMimeDetector.mMagicMimeEntries = new ArrayList();
    }
}
