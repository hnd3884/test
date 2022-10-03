package org.apache.xmlbeans.impl.tool;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import org.apache.xmlbeans.SystemProperties;
import java.io.IOException;
import org.apache.xmlbeans.Filer;
import org.apache.xmlbeans.impl.util.FilerImpl;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.impl.repackage.Repackager;
import java.io.File;
import org.apache.xmlbeans.SchemaTypeSystem;
import java.util.Set;

public class SchemaCodeGenerator
{
    private static Set deleteFileQueue;
    private static int triesRemaining;
    
    @Deprecated
    public static void saveTypeSystem(final SchemaTypeSystem system, final File classesDir, final File sourceFile, final Repackager repackager, final XmlOptions options) throws IOException {
        final Filer filer = new FilerImpl(classesDir, null, repackager, false, false);
        system.save(filer);
    }
    
    static void deleteObsoleteFiles(final File rootDir, final File srcDir, final Set seenFiles) {
        if (!rootDir.isDirectory() || !srcDir.isDirectory()) {
            throw new IllegalArgumentException();
        }
        final String absolutePath = srcDir.getAbsolutePath();
        if (absolutePath.length() <= 5) {
            return;
        }
        if (absolutePath.startsWith("/home/") && (absolutePath.indexOf("/", 6) >= absolutePath.length() - 1 || absolutePath.indexOf("/", 6) < 0)) {
            return;
        }
        final File[] files = srcDir.listFiles();
        for (int i = 0; i < files.length; ++i) {
            if (files[i].isDirectory()) {
                deleteObsoleteFiles(rootDir, files[i], seenFiles);
            }
            else if (!seenFiles.contains(files[i])) {
                deleteXmlBeansFile(files[i]);
                deleteDirRecursively(rootDir, files[i].getParentFile());
            }
        }
    }
    
    private static void deleteXmlBeansFile(final File file) {
        if (file.getName().endsWith(".java")) {
            file.delete();
        }
    }
    
    private static void deleteDirRecursively(final File root, File dir) {
        for (String[] list = dir.list(); list != null && list.length == 0 && !dir.equals(root); dir = dir.getParentFile(), list = dir.list()) {
            dir.delete();
        }
    }
    
    protected static File createTempDir() throws IOException {
        try {
            final File tmpDirFile = new File(SystemProperties.getProperty("java.io.tmpdir"));
            tmpDirFile.mkdirs();
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        final File tmpFile = File.createTempFile("xbean", null);
        String path = tmpFile.getAbsolutePath();
        if (!path.endsWith(".tmp")) {
            throw new IOException("Error: createTempFile did not create a file ending with .tmp");
        }
        path = path.substring(0, path.length() - 4);
        File tmpSrcDir = null;
        int count = 0;
        while (count < 100) {
            final String name = path + ".d" + ((count == 0) ? "" : Integer.toString(count++));
            tmpSrcDir = new File(name);
            if (!tmpSrcDir.exists()) {
                final boolean created = tmpSrcDir.mkdirs();
                assert created : "Could not create " + tmpSrcDir.getAbsolutePath();
                break;
            }
            else {
                ++count;
            }
        }
        tmpFile.deleteOnExit();
        return tmpSrcDir;
    }
    
    protected static void tryHardToDelete(final File dir) {
        tryToDelete(dir);
        if (dir.exists()) {
            tryToDeleteLater(dir);
        }
    }
    
    private static void tryToDelete(final File dir) {
        if (dir.exists()) {
            if (dir.isDirectory()) {
                final String[] list = dir.list();
                if (list != null) {
                    for (int i = 0; i < list.length; ++i) {
                        tryToDelete(new File(dir, list[i]));
                    }
                }
            }
            if (!dir.delete()) {
                return;
            }
        }
    }
    
    private static boolean tryNowThatItsLater() {
        final List files;
        synchronized (SchemaCodeGenerator.deleteFileQueue) {
            files = new ArrayList(SchemaCodeGenerator.deleteFileQueue);
            SchemaCodeGenerator.deleteFileQueue.clear();
        }
        final List retry = new ArrayList();
        for (final File file : files) {
            tryToDelete(file);
            if (file.exists()) {
                retry.add(file);
            }
        }
        synchronized (SchemaCodeGenerator.deleteFileQueue) {
            if (SchemaCodeGenerator.triesRemaining > 0) {
                --SchemaCodeGenerator.triesRemaining;
            }
            if (SchemaCodeGenerator.triesRemaining <= 0 || retry.size() == 0) {
                SchemaCodeGenerator.triesRemaining = 0;
            }
            else {
                SchemaCodeGenerator.deleteFileQueue.addAll(retry);
            }
            return SchemaCodeGenerator.triesRemaining <= 0;
        }
    }
    
    private static void giveUp() {
        synchronized (SchemaCodeGenerator.deleteFileQueue) {
            SchemaCodeGenerator.deleteFileQueue.clear();
            SchemaCodeGenerator.triesRemaining = 0;
        }
    }
    
    private static void tryToDeleteLater(final File dir) {
        synchronized (SchemaCodeGenerator.deleteFileQueue) {
            SchemaCodeGenerator.deleteFileQueue.add(dir);
            if (SchemaCodeGenerator.triesRemaining == 0) {
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            while (!tryNowThatItsLater()) {
                                Thread.sleep(3000L);
                            }
                        }
                        catch (final InterruptedException e) {
                            giveUp();
                        }
                    }
                };
            }
            if (SchemaCodeGenerator.triesRemaining < 10) {
                SchemaCodeGenerator.triesRemaining = 10;
            }
        }
    }
    
    static {
        SchemaCodeGenerator.deleteFileQueue = new HashSet();
        SchemaCodeGenerator.triesRemaining = 0;
    }
}
