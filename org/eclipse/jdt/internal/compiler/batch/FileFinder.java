package org.eclipse.jdt.internal.compiler.batch;

import java.util.ArrayList;
import java.io.File;

public class FileFinder
{
    public static String[] find(final File f, final String pattern) {
        final ArrayList files = new ArrayList();
        find0(f, pattern, files);
        final String[] result = new String[files.size()];
        files.toArray(result);
        return result;
    }
    
    private static void find0(final File f, final String pattern, final ArrayList collector) {
        if (f.isDirectory()) {
            final String[] files = f.list();
            if (files == null) {
                return;
            }
            for (int i = 0, max = files.length; i < max; ++i) {
                final File current = new File(f, files[i]);
                if (current.isDirectory()) {
                    find0(current, pattern, collector);
                }
                else if (current.getName().toUpperCase().endsWith(pattern)) {
                    collector.add(current.getAbsolutePath());
                }
            }
        }
    }
}
