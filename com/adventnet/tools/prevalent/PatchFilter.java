package com.adventnet.tools.prevalent;

import java.io.File;
import javax.swing.filechooser.FileFilter;

public class PatchFilter extends FileFilter
{
    private String exten;
    
    public PatchFilter(final String text) {
        this.exten = text;
    }
    
    @Override
    public boolean accept(final File f) {
        return f.isDirectory() || this.isExtensionPatch(f);
    }
    
    @Override
    public String getDescription() {
        return "License Files(*.xml)";
    }
    
    private boolean isExtensionPatch(final File file) {
        final String fileName = file.getName();
        final int index = fileName.lastIndexOf(".xml");
        final String extension = fileName.substring(index + 1, fileName.length());
        return extension.equals(this.exten);
    }
}
