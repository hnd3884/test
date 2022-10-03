package javax.swing.plaf;

import javax.swing.JButton;
import java.io.File;
import javax.swing.filechooser.FileView;
import javax.swing.filechooser.FileFilter;
import javax.swing.JFileChooser;

public abstract class FileChooserUI extends ComponentUI
{
    public abstract FileFilter getAcceptAllFileFilter(final JFileChooser p0);
    
    public abstract FileView getFileView(final JFileChooser p0);
    
    public abstract String getApproveButtonText(final JFileChooser p0);
    
    public abstract String getDialogTitle(final JFileChooser p0);
    
    public abstract void rescanCurrentDirectory(final JFileChooser p0);
    
    public abstract void ensureFileIsVisible(final JFileChooser p0, final File p1);
    
    public JButton getDefaultButton(final JFileChooser fileChooser) {
        return null;
    }
}
