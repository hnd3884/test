package javax.swing.filechooser;

import java.io.File;

public abstract class FileFilter
{
    public abstract boolean accept(final File p0);
    
    public abstract String getDescription();
}
