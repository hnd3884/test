package org.jfree.ui.about;

import java.util.ResourceBundle;
import java.util.List;
import javax.swing.table.AbstractTableModel;

public class ContributorsTableModel extends AbstractTableModel
{
    private List contributors;
    private String nameColumnLabel;
    private String contactColumnLabel;
    
    public ContributorsTableModel(final List contributors) {
        this.contributors = contributors;
        final String baseName = "org.jfree.ui.about.resources.AboutResources";
        final ResourceBundle resources = ResourceBundle.getBundle("org.jfree.ui.about.resources.AboutResources");
        this.nameColumnLabel = resources.getString("contributors-table.column.name");
        this.contactColumnLabel = resources.getString("contributors-table.column.contact");
    }
    
    public int getColumnCount() {
        return 2;
    }
    
    public String getColumnName(final int column) {
        String result = null;
        switch (column) {
            case 0: {
                result = this.nameColumnLabel;
                break;
            }
            case 1: {
                result = this.contactColumnLabel;
                break;
            }
        }
        return result;
    }
    
    public int getRowCount() {
        return this.contributors.size();
    }
    
    public Object getValueAt(final int row, final int column) {
        Object result = null;
        final Contributor contributor = this.contributors.get(row);
        if (column == 0) {
            result = contributor.getName();
        }
        else if (column == 1) {
            result = contributor.getEmail();
        }
        return result;
    }
}
