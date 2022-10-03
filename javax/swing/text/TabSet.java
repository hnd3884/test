package javax.swing.text;

import java.io.Serializable;

public class TabSet implements Serializable
{
    private TabStop[] tabs;
    private int hashCode;
    
    public TabSet(final TabStop[] array) {
        this.hashCode = Integer.MAX_VALUE;
        if (array != null) {
            final int length = array.length;
            System.arraycopy(array, 0, this.tabs = new TabStop[length], 0, length);
        }
        else {
            this.tabs = null;
        }
    }
    
    public int getTabCount() {
        return (this.tabs == null) ? 0 : this.tabs.length;
    }
    
    public TabStop getTab(final int n) {
        final int tabCount = this.getTabCount();
        if (n < 0 || n >= tabCount) {
            throw new IllegalArgumentException(n + " is outside the range of tabs");
        }
        return this.tabs[n];
    }
    
    public TabStop getTabAfter(final float n) {
        final int tabIndexAfter = this.getTabIndexAfter(n);
        return (tabIndexAfter == -1) ? null : this.tabs[tabIndexAfter];
    }
    
    public int getTabIndex(final TabStop tabStop) {
        for (int i = this.getTabCount() - 1; i >= 0; --i) {
            if (this.getTab(i) == tabStop) {
                return i;
            }
        }
        return -1;
    }
    
    public int getTabIndexAfter(final float n) {
        int i = 0;
        int tabCount = this.getTabCount();
        while (i != tabCount) {
            final int n2 = (tabCount - i) / 2 + i;
            if (n > this.tabs[n2].getPosition()) {
                if (i == n2) {
                    i = tabCount;
                }
                else {
                    i = n2;
                }
            }
            else {
                if (n2 == 0 || n > this.tabs[n2 - 1].getPosition()) {
                    return n2;
                }
                tabCount = n2;
            }
        }
        return -1;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof TabSet)) {
            return false;
        }
        final TabSet set = (TabSet)o;
        final int tabCount = this.getTabCount();
        if (set.getTabCount() != tabCount) {
            return false;
        }
        for (int i = 0; i < tabCount; ++i) {
            final TabStop tab = this.getTab(i);
            final TabStop tab2 = set.getTab(i);
            if ((tab == null && tab2 != null) || (tab != null && !this.getTab(i).equals(set.getTab(i)))) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        if (this.hashCode == Integer.MAX_VALUE) {
            this.hashCode = 0;
            for (int tabCount = this.getTabCount(), i = 0; i < tabCount; ++i) {
                this.hashCode ^= ((this.getTab(i) != null) ? this.getTab(i).hashCode() : 0);
            }
            if (this.hashCode == Integer.MAX_VALUE) {
                --this.hashCode;
            }
        }
        return this.hashCode;
    }
    
    @Override
    public String toString() {
        final int tabCount = this.getTabCount();
        final StringBuilder sb = new StringBuilder("[ ");
        for (int i = 0; i < tabCount; ++i) {
            if (i > 0) {
                sb.append(" - ");
            }
            sb.append(this.getTab(i).toString());
        }
        sb.append(" ]");
        return sb.toString();
    }
}
