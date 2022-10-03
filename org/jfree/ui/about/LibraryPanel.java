package org.jfree.ui.about;

import java.util.ArrayList;
import org.jfree.base.Library;
import java.awt.Component;
import javax.swing.JScrollPane;
import javax.swing.table.TableModel;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import java.util.List;
import javax.swing.JTable;
import javax.swing.JPanel;

public class LibraryPanel extends JPanel
{
    private JTable table;
    private LibraryTableModel model;
    
    public LibraryPanel(final List libraries) {
        this.setLayout(new BorderLayout());
        this.model = new LibraryTableModel(libraries);
        this.table = new JTable(this.model);
        this.add(new JScrollPane(this.table));
    }
    
    public LibraryPanel(final ProjectInfo projectInfo) {
        this(getLibraries(projectInfo));
    }
    
    private static void collectLibraries(final ProjectInfo info, final List list) {
        Library[] libs = info.getLibraries();
        for (int i = 0; i < libs.length; ++i) {
            final Library lib = libs[i];
            if (!list.contains(lib)) {
                list.add(lib);
                if (lib instanceof ProjectInfo) {
                    collectLibraries((ProjectInfo)lib, list);
                }
            }
        }
        libs = info.getOptionalLibraries();
        for (int j = 0; j < libs.length; ++j) {
            final Library lib2 = libs[j];
            if (!list.contains(lib2)) {
                list.add(lib2);
                if (lib2 instanceof ProjectInfo) {
                    collectLibraries((ProjectInfo)lib2, list);
                }
            }
        }
    }
    
    private static List getLibraries(final ProjectInfo info) {
        if (info == null) {
            return new ArrayList();
        }
        final ArrayList libs = new ArrayList();
        collectLibraries(info, libs);
        return libs;
    }
    
    public LibraryTableModel getModel() {
        return this.model;
    }
    
    protected JTable getTable() {
        return this.table;
    }
}
