package com.adventnet.tools.update.viewer;

import com.adventnet.tools.update.util.Utils;
import java.io.File;
import javax.swing.filechooser.FileFilter;

public class DSFilter extends FileFilter
{
    String type;
    
    public DSFilter(final String type) {
        this.type = null;
        this.type = type;
    }
    
    @Override
    public boolean accept(final File f) {
        if (f.isDirectory()) {
            return true;
        }
        final String extension = Utils.getExtension(f);
        if (extension != null && this.type.equals("init")) {
            if (extension.equals("zip")) {
                return true;
            }
        }
        else {
            if (extension == null || !this.type.equals("upgrade")) {
                return false;
            }
            if (extension.equals("ppm")) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public String getDescription() {
        if (this.type.equals("init")) {
            return "*.zip";
        }
        if (this.type.equals("upgrade")) {
            return "*.ppm";
        }
        return "*.*";
    }
}
