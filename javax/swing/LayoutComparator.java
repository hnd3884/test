package javax.swing;

import java.util.ListIterator;
import java.awt.Window;
import java.util.LinkedList;
import java.awt.ComponentOrientation;
import java.io.Serializable;
import java.awt.Component;
import java.util.Comparator;

final class LayoutComparator implements Comparator<Component>, Serializable
{
    private static final int ROW_TOLERANCE = 10;
    private boolean horizontal;
    private boolean leftToRight;
    
    LayoutComparator() {
        this.horizontal = true;
        this.leftToRight = true;
    }
    
    void setComponentOrientation(final ComponentOrientation componentOrientation) {
        this.horizontal = componentOrientation.isHorizontal();
        this.leftToRight = componentOrientation.isLeftToRight();
    }
    
    @Override
    public int compare(Component parent, Component parent2) {
        if (parent == parent2) {
            return 0;
        }
        Label_0198: {
            if (parent.getParent() != parent2.getParent()) {
                final LinkedList list = new LinkedList();
                while (parent != null) {
                    list.add(parent);
                    if (parent instanceof Window) {
                        break;
                    }
                    parent = parent.getParent();
                }
                if (parent == null) {
                    throw new ClassCastException();
                }
                final LinkedList<Component> list2 = new LinkedList<Component>();
                while (parent2 != null) {
                    list2.add(parent2);
                    if (parent2 instanceof Window) {
                        break;
                    }
                    parent2 = parent2.getParent();
                }
                if (parent2 == null) {
                    throw new ClassCastException();
                }
                final ListIterator listIterator = list.listIterator(list.size());
                final ListIterator<Component> listIterator2 = list2.listIterator(list2.size());
                while (listIterator.hasPrevious()) {
                    parent = (Component)listIterator.previous();
                    if (!listIterator2.hasPrevious()) {
                        return 1;
                    }
                    parent2 = listIterator2.previous();
                    if (parent != parent2) {
                        break Label_0198;
                    }
                }
                return -1;
            }
        }
        final int x = parent.getX();
        final int y = parent.getY();
        final int x2 = parent2.getX();
        final int y2 = parent2.getY();
        final int n = parent.getParent().getComponentZOrder(parent) - parent2.getParent().getComponentZOrder(parent2);
        if (this.horizontal) {
            if (this.leftToRight) {
                if (Math.abs(y - y2) < 10) {
                    return (x < x2) ? -1 : ((x > x2) ? 1 : n);
                }
                return (y < y2) ? -1 : 1;
            }
            else {
                if (Math.abs(y - y2) < 10) {
                    return (x > x2) ? -1 : ((x < x2) ? 1 : n);
                }
                return (y < y2) ? -1 : 1;
            }
        }
        else if (this.leftToRight) {
            if (Math.abs(x - x2) < 10) {
                return (y < y2) ? -1 : ((y > y2) ? 1 : n);
            }
            return (x < x2) ? -1 : 1;
        }
        else {
            if (Math.abs(x - x2) < 10) {
                return (y < y2) ? -1 : ((y > y2) ? 1 : n);
            }
            return (x > x2) ? -1 : 1;
        }
    }
}
