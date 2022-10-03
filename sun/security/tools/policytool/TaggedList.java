package sun.security.tools.policytool;

import java.util.LinkedList;
import javax.swing.ListModel;
import javax.swing.DefaultListModel;
import java.util.List;
import javax.swing.JList;

class TaggedList extends JList
{
    private static final long serialVersionUID = -5676238110427785853L;
    private List<Object> data;
    
    public TaggedList(final int visibleRowCount, final boolean b) {
        super(new DefaultListModel());
        this.data = new LinkedList<Object>();
        this.setVisibleRowCount(visibleRowCount);
        this.setSelectionMode(b ? 2 : 0);
    }
    
    public Object getObject(final int n) {
        return this.data.get(n);
    }
    
    public void addTaggedItem(final String s, final Object o) {
        ((DefaultListModel)this.getModel()).addElement(s);
        this.data.add(o);
    }
    
    public void replaceTaggedItem(final String s, final Object o, final int n) {
        ((DefaultListModel)this.getModel()).set(n, s);
        this.data.set(n, o);
    }
    
    public void removeTaggedItem(final int n) {
        ((DefaultListModel)this.getModel()).remove(n);
        this.data.remove(n);
    }
}
