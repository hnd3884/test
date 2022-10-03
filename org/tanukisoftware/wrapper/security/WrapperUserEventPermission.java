package org.tanukisoftware.wrapper.security;

import java.security.AccessControlException;
import org.tanukisoftware.wrapper.WrapperManager;
import java.util.StringTokenizer;
import java.security.Permission;
import java.util.ArrayList;
import java.security.BasicPermission;

public final class WrapperUserEventPermission extends BasicPermission
{
    private static final long serialVersionUID = 8916489326587298168L;
    private final int EVENT_MIN = 1;
    private final int EVENT_MAX = 32767;
    private ArrayList m_eventArr;
    
    public WrapperUserEventPermission(final String action) {
        super("fireUserEvent", String.valueOf(action));
        this.parseValids(action);
    }
    
    public WrapperUserEventPermission(final String name, final String action) {
        super(name, action);
        this.parseValids(action);
    }
    
    public String getActions() {
        String s = "";
        for (int i = 0; i < this.m_eventArr.size(); ++i) {
            if (i > 0) {
                s = s.concat(",");
            }
            s = s.concat(this.m_eventArr.get(i));
        }
        return s;
    }
    
    public boolean implies(final Permission p2) {
        final int check = Integer.parseInt(p2.getActions());
        for (int i = 0; i < this.m_eventArr.size(); ++i) {
            final String element = this.m_eventArr.get(i);
            final int border = element.indexOf(45);
            if (border >= 0) {
                final int min = Integer.parseInt(element.substring(0, border));
                final int max = Integer.parseInt(element.substring(border + 1));
                if (min <= check && check <= max) {
                    return true;
                }
            }
            else {
                final int current = Integer.parseInt(element);
                if (current == check) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private void parseValids(final String action) {
        int lastValue = 0;
        this.m_eventArr = new ArrayList();
        if (action.compareTo("*") == 0) {
            this.m_eventArr.add(new String("1-32767"));
            return;
        }
        final StringTokenizer strok = new StringTokenizer(action.trim(), ",");
        while (strok.hasMoreTokens()) {
            final String element = strok.nextToken();
            if (element.indexOf(42) >= 0) {
                throw new AccessControlException(WrapperManager.getRes().getString("can''t define ''*'' inside a sequence."));
            }
            final int range = element.indexOf("-");
            if (range >= 0) {
                if (range == 0) {
                    if (this.m_eventArr.size() != 0) {
                        throw new AccessControlException(WrapperManager.getRes().getString("Value {0} has to be first element in sequence.", element));
                    }
                    lastValue = Integer.parseInt(element.substring(1));
                    if (lastValue <= 1 || lastValue > 32767) {
                        throw new AccessControlException(WrapperManager.getRes().getString("Value {0} is out of bounds.", new Integer(lastValue)));
                    }
                    this.m_eventArr.add(new String("1-" + lastValue));
                }
                else if (range == element.length() - 1) {
                    final int currentValue = Integer.parseInt(element.substring(0, element.length() - 1));
                    if (currentValue <= 1 || currentValue > 32767) {
                        throw new AccessControlException(WrapperManager.getRes().getString("Value {0} is out of bounds.", new Integer(lastValue)));
                    }
                    if (currentValue < lastValue) {
                        throw new AccessControlException(WrapperManager.getRes().getString("Value {0} is not sorted.", new Integer(currentValue)));
                    }
                    lastValue = currentValue;
                    if (strok.hasMoreTokens()) {
                        throw new AccessControlException(WrapperManager.getRes().getString("Value {0} has to be last element in sequence.", element));
                    }
                    this.m_eventArr.add(currentValue + "-" + 32767);
                }
                else {
                    int currentValue = Integer.parseInt(element.substring(0, range));
                    if (currentValue <= 1 || currentValue > 32767) {
                        throw new AccessControlException(WrapperManager.getRes().getString("Value {0} is out of bounds.", new Integer(lastValue)));
                    }
                    if (currentValue < lastValue) {
                        throw new AccessControlException(WrapperManager.getRes().getString("Value {0} is not sorted.", new Integer(currentValue)));
                    }
                    lastValue = currentValue;
                    currentValue = Integer.parseInt(element.substring(range + 1));
                    if (currentValue <= 1 || currentValue > 32767) {
                        throw new AccessControlException(WrapperManager.getRes().getString("Value {0} is out of bounds.", new Integer(lastValue)));
                    }
                    if (currentValue < lastValue) {
                        throw new AccessControlException(WrapperManager.getRes().getString("Value {0} is not sorted.", new Integer(currentValue)));
                    }
                    this.m_eventArr.add(lastValue + "-" + currentValue);
                    lastValue = currentValue;
                }
            }
            else {
                final int currentValue = Integer.parseInt(element);
                if (currentValue < lastValue) {
                    throw new AccessControlException(WrapperManager.getRes().getString("Value {0} is not sorted.", new Integer(currentValue)));
                }
                lastValue = currentValue;
                this.m_eventArr.add(element);
            }
        }
    }
}
