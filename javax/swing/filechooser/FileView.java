package javax.swing.filechooser;

import javax.swing.Icon;
import java.io.File;

public abstract class FileView
{
    public String getName(final File file) {
        return null;
    }
    
    public String getDescription(final File file) {
        return null;
    }
    
    public String getTypeDescription(final File file) {
        return null;
    }
    
    public Icon getIcon(final File file) {
        return null;
    }
    
    public Boolean isTraversable(final File file) {
        return null;
    }
}
