package org.jfree.io;

import java.util.StringTokenizer;
import java.io.File;

public class FileUtilities
{
    private FileUtilities() {
    }
    
    public static File findFileOnClassPath(final String name) {
        final String classpath = System.getProperty("java.class.path");
        final String pathSeparator = System.getProperty("path.separator");
        final StringTokenizer tokenizer = new StringTokenizer(classpath, pathSeparator);
        while (tokenizer.hasMoreTokens()) {
            final String pathElement = tokenizer.nextToken();
            final File directoryOrJar = new File(pathElement);
            final File absoluteDirectoryOrJar = directoryOrJar.getAbsoluteFile();
            if (absoluteDirectoryOrJar.isFile()) {
                final File target = new File(absoluteDirectoryOrJar.getParent(), name);
                if (target.exists()) {
                    return target;
                }
                continue;
            }
            else {
                final File target = new File(directoryOrJar, name);
                if (target.exists()) {
                    return target;
                }
                continue;
            }
        }
        return null;
    }
}
