package sun.awt.shell;

import java.util.Comparator;
import javax.swing.SortOrder;

public class ShellFolderColumnInfo
{
    private String title;
    private Integer width;
    private boolean visible;
    private Integer alignment;
    private SortOrder sortOrder;
    private Comparator comparator;
    private boolean compareByColumn;
    
    public ShellFolderColumnInfo(final String title, final Integer width, final Integer alignment, final boolean visible, final SortOrder sortOrder, final Comparator comparator, final boolean compareByColumn) {
        this.title = title;
        this.width = width;
        this.alignment = alignment;
        this.visible = visible;
        this.sortOrder = sortOrder;
        this.comparator = comparator;
        this.compareByColumn = compareByColumn;
    }
    
    public ShellFolderColumnInfo(final String s, final Integer n, final Integer n2, final boolean b, final SortOrder sortOrder, final Comparator comparator) {
        this(s, n, n2, b, sortOrder, comparator, false);
    }
    
    public ShellFolderColumnInfo(final String s, final int n, final int n2, final boolean b) {
        this(s, n, n2, b, null, null);
    }
    
    public String getTitle() {
        return this.title;
    }
    
    public void setTitle(final String title) {
        this.title = title;
    }
    
    public Integer getWidth() {
        return this.width;
    }
    
    public void setWidth(final Integer width) {
        this.width = width;
    }
    
    public Integer getAlignment() {
        return this.alignment;
    }
    
    public void setAlignment(final Integer alignment) {
        this.alignment = alignment;
    }
    
    public boolean isVisible() {
        return this.visible;
    }
    
    public void setVisible(final boolean visible) {
        this.visible = visible;
    }
    
    public SortOrder getSortOrder() {
        return this.sortOrder;
    }
    
    public void setSortOrder(final SortOrder sortOrder) {
        this.sortOrder = sortOrder;
    }
    
    public Comparator getComparator() {
        return this.comparator;
    }
    
    public void setComparator(final Comparator comparator) {
        this.comparator = comparator;
    }
    
    public boolean isCompareByColumn() {
        return this.compareByColumn;
    }
    
    public void setCompareByColumn(final boolean compareByColumn) {
        this.compareByColumn = compareByColumn;
    }
}
