package javax.swing.plaf.multi;

import javax.accessibility.Accessible;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JComponent;
import java.io.File;
import javax.swing.filechooser.FileView;
import javax.swing.filechooser.FileFilter;
import javax.swing.JFileChooser;
import javax.swing.plaf.ComponentUI;
import java.util.Vector;
import javax.swing.plaf.FileChooserUI;

public class MultiFileChooserUI extends FileChooserUI
{
    protected Vector uis;
    
    public MultiFileChooserUI() {
        this.uis = new Vector();
    }
    
    public ComponentUI[] getUIs() {
        return MultiLookAndFeel.uisToArray(this.uis);
    }
    
    @Override
    public FileFilter getAcceptAllFileFilter(final JFileChooser fileChooser) {
        final FileFilter acceptAllFileFilter = this.uis.elementAt(0).getAcceptAllFileFilter(fileChooser);
        for (int i = 1; i < this.uis.size(); ++i) {
            ((FileChooserUI)this.uis.elementAt(i)).getAcceptAllFileFilter(fileChooser);
        }
        return acceptAllFileFilter;
    }
    
    @Override
    public FileView getFileView(final JFileChooser fileChooser) {
        final FileView fileView = this.uis.elementAt(0).getFileView(fileChooser);
        for (int i = 1; i < this.uis.size(); ++i) {
            ((FileChooserUI)this.uis.elementAt(i)).getFileView(fileChooser);
        }
        return fileView;
    }
    
    @Override
    public String getApproveButtonText(final JFileChooser fileChooser) {
        final String approveButtonText = this.uis.elementAt(0).getApproveButtonText(fileChooser);
        for (int i = 1; i < this.uis.size(); ++i) {
            ((FileChooserUI)this.uis.elementAt(i)).getApproveButtonText(fileChooser);
        }
        return approveButtonText;
    }
    
    @Override
    public String getDialogTitle(final JFileChooser fileChooser) {
        final String dialogTitle = this.uis.elementAt(0).getDialogTitle(fileChooser);
        for (int i = 1; i < this.uis.size(); ++i) {
            ((FileChooserUI)this.uis.elementAt(i)).getDialogTitle(fileChooser);
        }
        return dialogTitle;
    }
    
    @Override
    public void rescanCurrentDirectory(final JFileChooser fileChooser) {
        for (int i = 0; i < this.uis.size(); ++i) {
            ((FileChooserUI)this.uis.elementAt(i)).rescanCurrentDirectory(fileChooser);
        }
    }
    
    @Override
    public void ensureFileIsVisible(final JFileChooser fileChooser, final File file) {
        for (int i = 0; i < this.uis.size(); ++i) {
            ((FileChooserUI)this.uis.elementAt(i)).ensureFileIsVisible(fileChooser, file);
        }
    }
    
    @Override
    public boolean contains(final JComponent component, final int n, final int n2) {
        final boolean contains = this.uis.elementAt(0).contains(component, n, n2);
        for (int i = 1; i < this.uis.size(); ++i) {
            ((ComponentUI)this.uis.elementAt(i)).contains(component, n, n2);
        }
        return contains;
    }
    
    @Override
    public void update(final Graphics graphics, final JComponent component) {
        for (int i = 0; i < this.uis.size(); ++i) {
            ((ComponentUI)this.uis.elementAt(i)).update(graphics, component);
        }
    }
    
    public static ComponentUI createUI(final JComponent component) {
        final MultiFileChooserUI multiFileChooserUI = new MultiFileChooserUI();
        return MultiLookAndFeel.createUIs(multiFileChooserUI, multiFileChooserUI.uis, component);
    }
    
    @Override
    public void installUI(final JComponent component) {
        for (int i = 0; i < this.uis.size(); ++i) {
            ((ComponentUI)this.uis.elementAt(i)).installUI(component);
        }
    }
    
    @Override
    public void uninstallUI(final JComponent component) {
        for (int i = 0; i < this.uis.size(); ++i) {
            ((ComponentUI)this.uis.elementAt(i)).uninstallUI(component);
        }
    }
    
    @Override
    public void paint(final Graphics graphics, final JComponent component) {
        for (int i = 0; i < this.uis.size(); ++i) {
            ((ComponentUI)this.uis.elementAt(i)).paint(graphics, component);
        }
    }
    
    @Override
    public Dimension getPreferredSize(final JComponent component) {
        final Dimension preferredSize = this.uis.elementAt(0).getPreferredSize(component);
        for (int i = 1; i < this.uis.size(); ++i) {
            ((ComponentUI)this.uis.elementAt(i)).getPreferredSize(component);
        }
        return preferredSize;
    }
    
    @Override
    public Dimension getMinimumSize(final JComponent component) {
        final Dimension minimumSize = this.uis.elementAt(0).getMinimumSize(component);
        for (int i = 1; i < this.uis.size(); ++i) {
            ((ComponentUI)this.uis.elementAt(i)).getMinimumSize(component);
        }
        return minimumSize;
    }
    
    @Override
    public Dimension getMaximumSize(final JComponent component) {
        final Dimension maximumSize = this.uis.elementAt(0).getMaximumSize(component);
        for (int i = 1; i < this.uis.size(); ++i) {
            ((ComponentUI)this.uis.elementAt(i)).getMaximumSize(component);
        }
        return maximumSize;
    }
    
    @Override
    public int getAccessibleChildrenCount(final JComponent component) {
        final int accessibleChildrenCount = this.uis.elementAt(0).getAccessibleChildrenCount(component);
        for (int i = 1; i < this.uis.size(); ++i) {
            ((ComponentUI)this.uis.elementAt(i)).getAccessibleChildrenCount(component);
        }
        return accessibleChildrenCount;
    }
    
    @Override
    public Accessible getAccessibleChild(final JComponent component, final int n) {
        final Accessible accessibleChild = this.uis.elementAt(0).getAccessibleChild(component, n);
        for (int i = 1; i < this.uis.size(); ++i) {
            ((ComponentUI)this.uis.elementAt(i)).getAccessibleChild(component, n);
        }
        return accessibleChild;
    }
}
