package org.jfree.ui;

import java.io.File;
import javax.swing.filechooser.FileFilter;

public class ExtensionFileFilter extends FileFilter
{
    private String description;
    private String extension;
    
    public ExtensionFileFilter(final String description, final String extension) {
        this.description = description;
        this.extension = extension;
    }
    
    public boolean accept(final File file) {
        if (file.isDirectory()) {
            return true;
        }
        final String name = file.getName().toLowerCase();
        return name.endsWith(this.extension);
    }
    
    public String getDescription() {
        return this.description;
    }
}
