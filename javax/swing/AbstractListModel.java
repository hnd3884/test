package javax.swing;

import java.util.EventListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.EventListenerList;
import java.io.Serializable;

public abstract class AbstractListModel<E> implements ListModel<E>, Serializable
{
    protected EventListenerList listenerList;
    
    public AbstractListModel() {
        this.listenerList = new EventListenerList();
    }
    
    @Override
    public void addListDataListener(final ListDataListener listDataListener) {
        this.listenerList.add(ListDataListener.class, listDataListener);
    }
    
    @Override
    public void removeListDataListener(final ListDataListener listDataListener) {
        this.listenerList.remove(ListDataListener.class, listDataListener);
    }
    
    public ListDataListener[] getListDataListeners() {
        return this.listenerList.getListeners(ListDataListener.class);
    }
    
    protected void fireContentsChanged(final Object o, final int n, final int n2) {
        final Object[] listenerList = this.listenerList.getListenerList();
        ListDataEvent listDataEvent = null;
        for (int i = listenerList.length - 2; i >= 0; i -= 2) {
            if (listenerList[i] == ListDataListener.class) {
                if (listDataEvent == null) {
                    listDataEvent = new ListDataEvent(o, 0, n, n2);
                }
                ((ListDataListener)listenerList[i + 1]).contentsChanged(listDataEvent);
            }
        }
    }
    
    protected void fireIntervalAdded(final Object o, final int n, final int n2) {
        final Object[] listenerList = this.listenerList.getListenerList();
        ListDataEvent listDataEvent = null;
        for (int i = listenerList.length - 2; i >= 0; i -= 2) {
            if (listenerList[i] == ListDataListener.class) {
                if (listDataEvent == null) {
                    listDataEvent = new ListDataEvent(o, 1, n, n2);
                }
                ((ListDataListener)listenerList[i + 1]).intervalAdded(listDataEvent);
            }
        }
    }
    
    protected void fireIntervalRemoved(final Object o, final int n, final int n2) {
        final Object[] listenerList = this.listenerList.getListenerList();
        ListDataEvent listDataEvent = null;
        for (int i = listenerList.length - 2; i >= 0; i -= 2) {
            if (listenerList[i] == ListDataListener.class) {
                if (listDataEvent == null) {
                    listDataEvent = new ListDataEvent(o, 2, n, n2);
                }
                ((ListDataListener)listenerList[i + 1]).intervalRemoved(listDataEvent);
            }
        }
    }
    
    public <T extends EventListener> T[] getListeners(final Class<T> clazz) {
        return this.listenerList.getListeners(clazz);
    }
}
