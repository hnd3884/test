package org.jfree.ui.action;

import org.jfree.util.StringUtils;
import java.io.File;
import javax.swing.filechooser.FileFilter;
import org.jfree.ui.ExtensionFileFilter;
import java.awt.Component;
import javax.swing.JFileChooser;

public abstract class AbstractFileSelectionAction extends AbstractActionDowngrade
{
    private JFileChooser fileChooser;
    private Component parent;
    
    public AbstractFileSelectionAction(final Component parent) {
        this.parent = parent;
    }
    
    protected JFileChooser createFileChooser() {
        final JFileChooser fc = new JFileChooser();
        fc.addChoosableFileFilter(new ExtensionFileFilter(this.getFileDescription(), this.getFileExtension()));
        fc.setMultiSelectionEnabled(false);
        fc.setCurrentDirectory(this.getCurrentDirectory());
        return fc;
    }
    
    protected File getCurrentDirectory() {
        return new File(".");
    }
    
    protected abstract String getFileDescription();
    
    protected abstract String getFileExtension();
    
    protected File performSelectFile(final File selectedFile, final int dialogType, final boolean appendExtension) {
        if (this.fileChooser == null) {
            this.fileChooser = this.createFileChooser();
        }
        this.fileChooser.setSelectedFile(selectedFile);
        this.fileChooser.setDialogType(dialogType);
        final int option = this.fileChooser.showDialog(this.parent, null);
        if (option == 0) {
            final File selFile = this.fileChooser.getSelectedFile();
            String selFileName = selFile.getAbsolutePath();
            if (!StringUtils.endsWithIgnoreCase(selFileName, this.getFileExtension())) {
                selFileName = String.valueOf(selFileName) + this.getFileExtension();
            }
            return new File(selFileName);
        }
        return null;
    }
}
