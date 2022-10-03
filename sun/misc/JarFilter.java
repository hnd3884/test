package sun.misc;

import java.io.File;
import java.io.FilenameFilter;

public class JarFilter implements FilenameFilter
{
    @Override
    public boolean accept(final File file, final String s) {
        final String lowerCase = s.toLowerCase();
        return lowerCase.endsWith(".jar") || lowerCase.endsWith(".zip");
    }
}
