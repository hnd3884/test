package com.sun.corba.se.impl.orbutil.fsm;

import com.sun.corba.se.spi.orbutil.fsm.Input;
import com.sun.corba.se.spi.orbutil.fsm.Guard;
import com.sun.corba.se.spi.orbutil.fsm.State;
import com.sun.corba.se.spi.orbutil.fsm.Action;
import java.util.StringTokenizer;

public class NameBase
{
    private String name;
    private String toStringName;
    
    private String getClassName() {
        final StringTokenizer stringTokenizer = new StringTokenizer(this.getClass().getName(), ".");
        String s = stringTokenizer.nextToken();
        while (stringTokenizer.hasMoreTokens()) {
            s = stringTokenizer.nextToken();
        }
        return s;
    }
    
    private String getPreferredClassName() {
        if (this instanceof Action) {
            return "Action";
        }
        if (this instanceof State) {
            return "State";
        }
        if (this instanceof Guard) {
            return "Guard";
        }
        if (this instanceof Input) {
            return "Input";
        }
        return this.getClassName();
    }
    
    public NameBase(final String name) {
        this.name = name;
        this.toStringName = this.getPreferredClassName() + "[" + name + "]";
    }
    
    public String getName() {
        return this.name;
    }
    
    @Override
    public String toString() {
        return this.toStringName;
    }
}
