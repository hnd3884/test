package org.htmlparser.parserapplications.filterbuilder;

import org.htmlparser.NodeFilter;
import java.awt.Insets;
import java.awt.Dimension;
import javax.swing.Box;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.Color;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.LayoutManager;
import org.htmlparser.parserapplications.filterbuilder.layouts.VerticalLayoutManager;
import java.awt.Component;
import javax.swing.JPanel;

public class SubFilterList extends JPanel
{
    protected int mExtra;
    protected Component mSpacer;
    protected Filter mHome;
    protected String mTitle;
    protected int mMax;
    
    public SubFilterList(final Filter home, final String title, final int max) {
        this.mExtra = 25;
        this.mHome = home;
        this.mTitle = title;
        this.mMax = max;
        this.setLayout(new VerticalLayoutManager());
        this.addSpacer();
        this.setSelected(false);
    }
    
    public void setSelected(final boolean selected) {
        if (selected) {
            this.setBorder(new CompoundBorder(new TitledBorder(null, this.mTitle, 1, 2), new CompoundBorder(new LineBorder(Color.green, 2), new EmptyBorder(1, 1, 1, 1))));
        }
        else {
            this.setBorder(new CompoundBorder(new TitledBorder(null, this.mTitle, 1, 2), new EmptyBorder(3, 3, 3, 3)));
        }
    }
    
    protected void addSpacer() {
        final Dimension dimension = this.mHome.getSize();
        final Insets insets = this.mHome.getInsets();
        dimension.setSize(dimension.width - insets.left - insets.right, this.mExtra);
        this.add(this.mSpacer = Box.createRigidArea(dimension));
    }
    
    protected void removeSpacer() {
        this.remove(this.mSpacer);
        this.mSpacer = null;
    }
    
    public Component[] getDropTargets() {
        return new Component[] { this };
    }
    
    public void addFilter(final Filter filter) {
        int count = this.getComponentCount();
        if (null != this.mSpacer) {
            --count;
        }
        this.addFilter(filter, count);
    }
    
    public void addFilter(final Filter filter, final int index) {
        this.add(filter, index);
        final NodeFilter[] before = this.mHome.getSubNodeFilters();
        final NodeFilter[] after = new NodeFilter[before.length + 1];
        int offset = 0;
        for (int i = 0; i < after.length; ++i) {
            final int n;
            after[n] = (((n = i) == index) ? filter : before[offset++]);
        }
        this.mHome.setSubNodeFilters(after);
        if (null != this.mSpacer && 0 != this.mMax && after.length >= this.mMax) {
            this.removeSpacer();
        }
    }
    
    public void removeFilter(final Filter filter) {
        final Filter[] filters = this.getFilters();
        int index = -1;
        for (int i = 0; -1 == index && i < filters.length; ++i) {
            if (filter == filters[i]) {
                index = i;
            }
        }
        if (-1 != index) {
            this.removeFilter(index);
        }
    }
    
    public void removeFilter(final int index) {
        this.remove(index);
        final NodeFilter[] before = this.mHome.getSubNodeFilters();
        if (0 != before.length) {
            final NodeFilter[] after = new NodeFilter[before.length - 1];
            int offset = 0;
            for (int i = 0; i < before.length; ++i) {
                if (i != index) {
                    after[offset++] = before[i];
                }
            }
            this.mHome.setSubNodeFilters(after);
            if (null == this.mSpacer && 0 != this.mMax && after.length < this.mMax) {
                this.addSpacer();
            }
        }
    }
    
    public Filter[] getFilters() {
        final NodeFilter[] list = this.mHome.getSubNodeFilters();
        final Filter[] ret = new Filter[list.length];
        System.arraycopy(list, 0, ret, 0, list.length);
        return ret;
    }
    
    public boolean canAccept() {
        boolean ret;
        if (0 == this.mMax) {
            ret = true;
        }
        else {
            int count = this.getComponentCount();
            if (null != this.mSpacer) {
                --count;
            }
            ret = (count < this.mMax);
        }
        return ret;
    }
    
    public String toString(final int indent, final int level) {
        final StringBuffer ret = new StringBuffer();
        final Filter[] filters = this.getFilters();
        for (int i = 0; i < filters.length; ++i) {
            ret.append(filters[i].toString());
            if (i + 1 != filters.length) {
                ret.append("\n");
            }
        }
        return ret.toString();
    }
}
