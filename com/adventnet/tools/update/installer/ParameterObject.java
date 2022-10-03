package com.adventnet.tools.update.installer;

import java.applet.Applet;
import java.util.Enumeration;
import java.util.Properties;
import javax.swing.event.EventListenerList;
import java.util.Hashtable;

public class ParameterObject
{
    private Hashtable ht;
    private EventListenerList listenerList;
    private static String usage;
    
    public ParameterObject() {
        this.ht = new Hashtable();
        this.listenerList = new EventListenerList();
    }
    
    public ParameterObject(final String[] params, final String[] args) {
        this.ht = new Hashtable();
        this.listenerList = new EventListenerList();
        createUsage(params);
        for (int i = 0; i < args.length; i += 2) {
            final int index = args[i].indexOf("-");
            if (index == -1) {
                usage_error();
            }
            if (args[i].length() == 1) {
                usage_error();
            }
            final String key = args[i].substring(index + 1);
            final String value = args[i + 1];
            if (value == null) {
                usage_error();
            }
            this.ht.put(key, value);
        }
        for (int i = 0; i < params.length; ++i) {
            if (!this.ht.containsKey(params[i])) {
                usage_error();
            }
        }
    }
    
    public ParameterObject(final Properties props) {
        this.ht = new Hashtable();
        this.listenerList = new EventListenerList();
        this.ht = props;
    }
    
    public void setParameters(final Properties props) {
        if (props == null) {
            return;
        }
        boolean changed = false;
        changed = this.checkIfDifferent(props);
        if (changed) {
            final Enumeration enum1 = props.keys();
            while (enum1.hasMoreElements()) {
                final Object key = enum1.nextElement();
                final Object value = ((Hashtable<K, Object>)props).get(key);
                this.ht.put(key, value);
            }
            this.fireParameterChanged();
        }
    }
    
    public boolean checkIfDifferent(final Properties props) {
        boolean modified = false;
        final Enumeration enum1 = props.keys();
        while (enum1.hasMoreElements()) {
            final Object key = enum1.nextElement();
            final Object value = ((Hashtable<K, Object>)props).get(key);
            if (!((String)value).equals(this.ht.get(key))) {
                modified = true;
                break;
            }
        }
        return modified;
    }
    
    public Object getParameter(final Object input) {
        return this.ht.get(input);
    }
    
    public void initializeParameters(final Applet app, final String[] params) {
        createUsage(params);
        if (app == null) {
            return;
        }
        for (int i = 0; i < params.length; ++i) {
            this.ht.put(params[i], app.getParameter(params[i]));
        }
        this.fireParameterChanged();
    }
    
    private static void createUsage(final String[] param) {
        for (int i = 0; i < param.length; ++i) {
            ParameterObject.usage = ParameterObject.usage + "-" + param[i] + " " + param[i].toLowerCase() + " ";
        }
    }
    
    static void usage_error() {
        ConsoleOut.println("Warning Message :Application needs certain parameters. ");
        ConsoleOut.println("Usage: " + ParameterObject.usage);
    }
    
    public ParameterChangeListener[] getListenerList() {
        final Object[] lList = this.listenerList.getListenerList();
        final int n = this.listenerList.getListenerCount(ParameterChangeListener.class);
        final ParameterChangeListener[] returnArray = new ParameterChangeListener[n];
        int temp = 0;
        for (int i = lList.length - 2; i >= 0; i -= 2) {
            if (lList[i] == ParameterChangeListener.class) {
                returnArray[temp++] = (ParameterChangeListener)lList[i + 1];
            }
        }
        return returnArray;
    }
    
    public void addParameterChangeListener(final ParameterChangeListener pcl) {
        this.listenerList.add(ParameterChangeListener.class, pcl);
    }
    
    public void removeParameterChangeListener(final ParameterChangeListener pcl) {
        this.listenerList.remove(ParameterChangeListener.class, pcl);
    }
    
    public void setParameter(final Object key, final Object value) {
        this.ht.put(key, value);
    }
    
    public void fireParameterChanged() {
        final int size = this.listenerList.getListenerCount() * 2;
        final Object[] obj = this.listenerList.getListenerList();
        for (int i = 1; i < size; i += 2) {
            ((ParameterChangeListener)obj[i]).parameterChanged(this);
        }
    }
    
    static {
        ParameterObject.usage = "";
    }
}
