package javax.swing;

import java.awt.Component;

public interface ListCellRenderer<E>
{
    Component getListCellRendererComponent(final JList<? extends E> p0, final E p1, final int p2, final boolean p3, final boolean p4);
}
