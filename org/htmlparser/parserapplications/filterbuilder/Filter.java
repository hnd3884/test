package org.htmlparser.parserapplications.filterbuilder;

import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.EtchedBorder;
import java.awt.Container;
import java.util.Vector;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.io.ByteArrayOutputStream;
import javax.swing.ImageIcon;
import javax.swing.Icon;
import org.htmlparser.Parser;
import java.awt.Insets;
import java.awt.Dimension;
import java.awt.Component;
import java.awt.Color;
import javax.swing.JLabel;
import java.awt.LayoutManager;
import org.htmlparser.parserapplications.filterbuilder.layouts.VerticalLayoutManager;
import java.util.Hashtable;
import org.htmlparser.NodeFilter;
import javax.swing.JComponent;

public abstract class Filter extends JComponent implements NodeFilter
{
    protected static Hashtable mWrappers;
    
    public static Filter instantiate(final String class_name) {
        Filter ret = null;
        try {
            final Class cls = Class.forName(class_name);
            ret = cls.newInstance();
            Filter.mWrappers.put(ret.getNodeFilter().getClass().getName(), class_name);
        }
        catch (final ClassNotFoundException cnfe) {
            System.out.println("can't find class " + class_name);
        }
        catch (final InstantiationException ie) {
            System.out.println("can't instantiate class " + class_name);
        }
        catch (final IllegalAccessException ie2) {
            System.out.println("class " + class_name + " has no public constructor");
        }
        catch (final ClassCastException cce) {
            System.out.println("class " + class_name + " is not a Filter");
        }
        return ret;
    }
    
    public Filter() {
        this.setToolTipText(this.getDescription());
        this.setLayout(new VerticalLayoutManager());
        this.setSelected(false);
        final JLabel label = new JLabel(this.getDescription(), this.getIcon(), 2);
        label.setBackground(Color.green);
        label.setAlignmentX(0.0f);
        label.setHorizontalAlignment(2);
        this.add(label);
        final Dimension dimension = label.getMaximumSize();
        final Insets insets = this.getInsets();
        dimension.setSize(dimension.width + insets.left + insets.right, dimension.height + insets.top + insets.bottom);
        this.setSize(dimension);
    }
    
    public abstract String getDescription();
    
    public abstract NodeFilter getNodeFilter();
    
    public abstract void setNodeFilter(final NodeFilter p0, final Parser p1);
    
    public abstract NodeFilter[] getSubNodeFilters();
    
    public abstract void setSubNodeFilters(final NodeFilter[] p0);
    
    public abstract String toJavaCode(final StringBuffer p0, final int[] p1);
    
    public Icon getIcon() {
        ImageIcon ret = null;
        try {
            ret = new ImageIcon(this.getClass().getResource(this.getIconSpec()));
        }
        catch (final NullPointerException npe) {
            System.err.println("can't find icon " + this.getIconSpec());
        }
        return ret;
    }
    
    public abstract String getIconSpec();
    
    public String toString() {
        return this.getDescription() + " [" + this.getClass().getName() + "]";
    }
    
    public static byte[] pickle(final Object object) throws IOException {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(object);
        oos.close();
        final byte[] ret = bos.toByteArray();
        return ret;
    }
    
    public static Object unpickle(final byte[] data) throws IOException, ClassNotFoundException {
        final ByteArrayInputStream bis = new ByteArrayInputStream(data);
        final ObjectInputStream ois = new ObjectInputStream(bis);
        final Object ret = ois.readObject();
        ois.close();
        return ret;
    }
    
    public static String serialize(final byte[] data) {
        final StringBuffer ret = new StringBuffer(data.length * 2);
        for (int i = 0; i < data.length; ++i) {
            final String string = Integer.toString(0xFF & data[i], 16);
            if (string.length() < 2) {
                ret.append("0");
            }
            ret.append(string);
        }
        return ret.toString();
    }
    
    public static byte[] deserialize(final String string) {
        final byte[] ret = new byte[string.length() / 2];
        for (int i = 0; i < string.length(); i += 2) {
            ret[i / 2] = (byte)Integer.parseInt(string.substring(i, i + 2), 16);
        }
        return ret;
    }
    
    public static String deconstitute(final Filter[] filters) throws IOException {
        final StringBuffer ret = new StringBuffer(1024);
        for (int i = 0; i < filters.length; ++i) {
            ret.append("[");
            ret.append(serialize(pickle(filters[i].getNodeFilter())));
            ret.append("]");
        }
        return ret.toString();
    }
    
    public static Filter[] reconstitute(String string, final Parser context) {
        final Vector vector = new Vector();
        try {
            while (string.startsWith("[")) {
                final int index = string.indexOf(93);
                if (-1 == index) {
                    break;
                }
                final String code = string.substring(1, index);
                string = string.substring(index + 1);
                final Object object = unpickle(deserialize(code));
                if (!(object instanceof NodeFilter)) {
                    break;
                }
                final Filter filter = wrap((NodeFilter)object, context);
                if (null == filter) {
                    continue;
                }
                vector.addElement(filter);
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        final Filter[] ret = new Filter[vector.size()];
        vector.copyInto(ret);
        return ret;
    }
    
    protected static SubFilterList getEnclosed(final Component component) {
        if (component instanceof Container) {
            final Component[] list = ((Container)component).getComponents();
            for (int i = 0; i < list.length; ++i) {
                if (list[i] instanceof SubFilterList) {
                    return (SubFilterList)list[i];
                }
            }
        }
        return null;
    }
    
    public static Filter wrap(final NodeFilter filter, final Parser context) {
        Filter ret = null;
        String class_name = filter.getClass().getName();
        class_name = Filter.mWrappers.get(class_name);
        if (null != class_name) {
            try {
                ret = instantiate(class_name);
                ret.setNodeFilter(filter, context);
                final NodeFilter[] filters = ret.getSubNodeFilters();
                if (0 != filters.length) {
                    final SubFilterList list = getEnclosed(ret);
                    if (null == list) {
                        throw new IllegalStateException("filter can't have subnodes without a SubFilterList on the wrapper");
                    }
                    ret.setSubNodeFilters(new NodeFilter[0]);
                    for (int i = 0; i < filters.length; ++i) {
                        list.addFilter(wrap(filters[i], context));
                    }
                }
            }
            catch (final Exception e) {
                e.printStackTrace();
            }
        }
        else {
            System.out.println(class_name + " is not registered for wrapping.");
        }
        return ret;
    }
    
    public void setSelected(final boolean selected) {
        if (selected) {
            this.setBorder(new CompoundBorder(new EtchedBorder(), new CompoundBorder(new LineBorder(Color.blue, 2), new EmptyBorder(1, 1, 1, 1))));
        }
        else {
            this.setBorder(new CompoundBorder(new EtchedBorder(), new EmptyBorder(3, 3, 3, 3)));
        }
    }
    
    public void setExpanded(final boolean expanded) {
        final Component[] components = this.getComponents();
        for (int i = 0; i < components.length; ++i) {
            if (!(components[i] instanceof JLabel)) {
                components[i].setVisible(expanded);
            }
        }
    }
    
    public static void spaces(final StringBuffer out, final int count) {
        for (int i = 0; i < count; ++i) {
            out.append(' ');
        }
    }
    
    public static void newline(final StringBuffer out) {
        out.append('\n');
    }
    
    static {
        Filter.mWrappers = new Hashtable();
    }
}
