package com.sun.java.accessibility.util;

import java.util.EventListener;
import jdk.Exported;

@Exported
public class AccessibilityListenerList
{
    private static final Object[] NULL_ARRAY;
    protected transient Object[] listenerList;
    
    public AccessibilityListenerList() {
        this.listenerList = AccessibilityListenerList.NULL_ARRAY;
    }
    
    public Object[] getListenerList() {
        return this.listenerList;
    }
    
    public int getListenerCount() {
        return this.listenerList.length / 2;
    }
    
    public int getListenerCount(final Class clazz) {
        int n = 0;
        final Object[] listenerList = this.listenerList;
        for (int i = 0; i < listenerList.length; i += 2) {
            if (clazz == listenerList[i]) {
                ++n;
            }
        }
        return n;
    }
    
    public synchronized void add(final Class clazz, final EventListener eventListener) {
        if (!clazz.isInstance(eventListener)) {
            throw new IllegalArgumentException("Listener " + eventListener + " is not of type " + clazz);
        }
        if (eventListener == null) {
            throw new IllegalArgumentException("Listener " + eventListener + " is null");
        }
        if (this.listenerList == AccessibilityListenerList.NULL_ARRAY) {
            this.listenerList = new Object[] { clazz, eventListener };
        }
        else {
            final int length = this.listenerList.length;
            final Object[] listenerList = new Object[length + 2];
            System.arraycopy(this.listenerList, 0, listenerList, 0, length);
            listenerList[length] = clazz;
            listenerList[length + 1] = eventListener;
            this.listenerList = listenerList;
        }
    }
    
    public synchronized void remove(final Class clazz, final EventListener eventListener) {
        if (!clazz.isInstance(eventListener)) {
            throw new IllegalArgumentException("Listener " + eventListener + " is not of type " + clazz);
        }
        if (eventListener == null) {
            throw new IllegalArgumentException("Listener " + eventListener + " is null");
        }
        int n = -1;
        for (int i = this.listenerList.length - 2; i >= 0; i -= 2) {
            if (this.listenerList[i] == clazz && this.listenerList[i + 1] == eventListener) {
                n = i;
                break;
            }
        }
        if (n != -1) {
            final Object[] array = new Object[this.listenerList.length - 2];
            System.arraycopy(this.listenerList, 0, array, 0, n);
            if (n < array.length) {
                System.arraycopy(this.listenerList, n + 2, array, n, array.length - n);
            }
            this.listenerList = ((array.length == 0) ? AccessibilityListenerList.NULL_ARRAY : array);
        }
    }
    
    @Override
    public String toString() {
        final Object[] listenerList = this.listenerList;
        String s = "EventListenerList: " + listenerList.length / 2 + " listeners: ";
        for (int i = 0; i <= listenerList.length - 2; i += 2) {
            s = s + " type " + ((Class)listenerList[i]).getName() + " listener " + listenerList[i + 1];
        }
        return s;
    }
    
    static {
        NULL_ARRAY = new Object[0];
    }
}
