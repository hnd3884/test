package org.apache.xmlbeans.impl.tool;

import java.io.FileFilter;
import java.io.OutputStream;
import org.apache.xmlbeans.impl.common.IOUtil;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.io.File;

public class SchemaResourceManager extends BaseSchemaResourceManager
{
    private File _directory;
    
    public static void printUsage() {
        System.out.println("Maintains \"xsdownload.xml\", an index of locally downloaded .xsd files");
        System.out.println("usage: sdownload [-dir directory] [-refresh] [-recurse] [-sync] [url/file...]");
        System.out.println("");
        System.out.println("URLs that are specified are downloaded if they aren't already cached.");
        System.out.println("In addition:");
        System.out.println("  -dir specifies the directory for the xsdownload.xml file (default .).");
        System.out.println("  -sync synchronizes the index to any local .xsd files in the tree.");
        System.out.println("  -recurse recursively downloads imported and included .xsd files.");
        System.out.println("  -refresh redownloads all indexed .xsd files.");
        System.out.println("If no files or URLs are specified, all indexed files are relevant.");
    }
    
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            printUsage();
            System.exit(0);
            return;
        }
        final Set flags = new HashSet();
        flags.add("h");
        flags.add("help");
        flags.add("usage");
        flags.add("license");
        flags.add("version");
        flags.add("sync");
        flags.add("refresh");
        flags.add("recurse");
        final Set opts = new HashSet();
        opts.add("dir");
        final CommandLine cl = new CommandLine(args, flags, opts);
        if (cl.getOpt("h") != null || cl.getOpt("help") != null || cl.getOpt("usage") != null) {
            printUsage();
            System.exit(0);
            return;
        }
        final String[] badopts = cl.getBadOpts();
        if (badopts.length > 0) {
            for (int i = 0; i < badopts.length; ++i) {
                System.out.println("Unrecognized option: " + badopts[i]);
            }
            printUsage();
            System.exit(0);
            return;
        }
        if (cl.getOpt("license") != null) {
            CommandLine.printLicense();
            System.exit(0);
            return;
        }
        if (cl.getOpt("version") != null) {
            CommandLine.printVersion();
            System.exit(0);
            return;
        }
        args = cl.args();
        final boolean sync = cl.getOpt("sync") != null;
        final boolean refresh = cl.getOpt("refresh") != null;
        final boolean imports = cl.getOpt("recurse") != null;
        String dir = cl.getOpt("dir");
        if (dir == null) {
            dir = ".";
        }
        final File directory = new File(dir);
        SchemaResourceManager mgr;
        try {
            mgr = new SchemaResourceManager(directory);
        }
        catch (final IllegalStateException e) {
            if (e.getMessage() != null) {
                System.out.println(e.getMessage());
            }
            else {
                e.printStackTrace();
            }
            System.exit(1);
            return;
        }
        final List uriList = new ArrayList();
        List fileList = new ArrayList();
        for (int j = 0; j < args.length; ++j) {
            if (looksLikeURL(args[j])) {
                uriList.add(args[j]);
            }
            else {
                fileList.add(new File(directory, args[j]));
            }
        }
        final Iterator k = fileList.iterator();
        while (k.hasNext()) {
            final File file = k.next();
            if (!isInDirectory(file, directory)) {
                System.err.println("File not within directory: " + file);
                k.remove();
            }
        }
        fileList = collectXSDFiles(fileList.toArray(new File[0]));
        final String[] uris = uriList.toArray(new String[0]);
        final File[] files = fileList.toArray(new File[0]);
        final String[] filenames = relativeFilenames(files, directory);
        if (uris.length + filenames.length > 0) {
            mgr.process(uris, filenames, sync, refresh, imports);
        }
        else {
            mgr.processAll(sync, refresh, imports);
        }
        mgr.writeCache();
        System.exit(0);
    }
    
    private static boolean looksLikeURL(final String str) {
        return str.startsWith("http:") || str.startsWith("https:") || str.startsWith("ftp:") || str.startsWith("file:");
    }
    
    private static String relativeFilename(final File file, final File directory) {
        if (file == null || file.equals(directory)) {
            return ".";
        }
        return relativeFilename(file.getParentFile(), directory) + "/" + file.getName();
    }
    
    private static String[] relativeFilenames(final File[] files, final File directory) {
        final String[] result = new String[files.length];
        for (int i = 0; i < files.length; ++i) {
            result[i] = relativeFilename(files[i], directory);
        }
        return result;
    }
    
    private static boolean isInDirectory(final File file, final File dir) {
        return file != null && (file.equals(dir) || isInDirectory(file.getParentFile(), dir));
    }
    
    public SchemaResourceManager(final File directory) {
        this._directory = directory;
        this.init();
    }
    
    @Override
    protected void warning(final String msg) {
        System.out.println(msg);
    }
    
    @Override
    protected boolean fileExists(final String filename) {
        return new File(this._directory, filename).exists();
    }
    
    @Override
    protected InputStream inputStreamForFile(final String filename) throws IOException {
        return new FileInputStream(new File(this._directory, filename));
    }
    
    @Override
    protected void writeInputStreamToFile(final InputStream input, final String filename) throws IOException {
        final File targetFile = new File(this._directory, filename);
        final File parent = targetFile.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }
        final OutputStream output = new FileOutputStream(targetFile);
        IOUtil.copyCompletely(input, output);
    }
    
    @Override
    protected void deleteFile(final String filename) {
        new File(this._directory, filename).delete();
    }
    
    @Override
    protected String[] getAllXSDFilenames() {
        final File[] allFiles = collectXSDFiles(new File[] { this._directory }).toArray(new File[0]);
        return relativeFilenames(allFiles, this._directory);
    }
    
    private static List collectXSDFiles(final File[] dirs) {
        final List files = new ArrayList();
        for (int i = 0; i < dirs.length; ++i) {
            final File f = dirs[i];
            if (!f.isDirectory()) {
                files.add(f);
            }
            else {
                files.addAll(collectXSDFiles(f.listFiles(new FileFilter() {
                    @Override
                    public boolean accept(final File file) {
                        return file.isDirectory() || (file.isFile() && file.getName().endsWith(".xsd"));
                    }
                })));
            }
        }
        return files;
    }
}
