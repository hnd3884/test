package org.apache.poi.util;

import java.io.IOException;
import java.io.File;

public interface TempFileCreationStrategy
{
    File createTempFile(final String p0, final String p1) throws IOException;
    
    File createTempDirectory(final String p0) throws IOException;
}
