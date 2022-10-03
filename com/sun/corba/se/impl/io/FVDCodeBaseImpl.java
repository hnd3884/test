package com.sun.corba.se.impl.io;

import java.util.Stack;
import com.sun.org.omg.CORBA.ValueDefPackage.FullValueDescription;
import org.omg.CORBA.CompletionStatus;
import javax.rmi.CORBA.Util;
import com.sun.org.omg.CORBA.Repository;
import javax.rmi.CORBA.ValueHandler;
import com.sun.corba.se.impl.logging.OMGSystemException;
import org.omg.CORBA.ORB;
import java.util.Hashtable;
import com.sun.org.omg.SendingContext._CodeBaseImplBase;

public class FVDCodeBaseImpl extends _CodeBaseImplBase
{
    private static Hashtable fvds;
    private transient ORB orb;
    private transient OMGSystemException wrapper;
    private transient ValueHandlerImpl vhandler;
    
    public FVDCodeBaseImpl() {
        this.orb = null;
        this.wrapper = OMGSystemException.get("rpc.encoding");
        this.vhandler = null;
    }
    
    void setValueHandler(final ValueHandler valueHandler) {
        this.vhandler = (ValueHandlerImpl)valueHandler;
    }
    
    @Override
    public Repository get_ir() {
        return null;
    }
    
    @Override
    public String implementation(final String s) {
        try {
            if (this.vhandler == null) {
                this.vhandler = ValueHandlerImpl.getInstance(false);
            }
            final String codebase = Util.getCodebase(this.vhandler.getClassFromType(s));
            if (codebase == null) {
                return "";
            }
            return codebase;
        }
        catch (final ClassNotFoundException ex) {
            throw this.wrapper.missingLocalValueImpl(CompletionStatus.COMPLETED_MAYBE, ex);
        }
    }
    
    @Override
    public String[] implementations(final String[] array) {
        final String[] array2 = new String[array.length];
        for (int i = 0; i < array.length; ++i) {
            array2[i] = this.implementation(array[i]);
        }
        return array2;
    }
    
    @Override
    public FullValueDescription meta(final String s) {
        try {
            FullValueDescription fullValueDescription = FVDCodeBaseImpl.fvds.get(s);
            if (fullValueDescription == null) {
                if (this.vhandler == null) {
                    this.vhandler = ValueHandlerImpl.getInstance(false);
                }
                try {
                    fullValueDescription = ValueUtility.translate(this._orb(), ObjectStreamClass.lookup(this.vhandler.getAnyClassFromType(s)), this.vhandler);
                }
                catch (final Throwable t) {
                    if (this.orb == null) {
                        this.orb = ORB.init();
                    }
                    fullValueDescription = ValueUtility.translate(this.orb, ObjectStreamClass.lookup(this.vhandler.getAnyClassFromType(s)), this.vhandler);
                }
                if (fullValueDescription == null) {
                    throw this.wrapper.missingLocalValueImpl(CompletionStatus.COMPLETED_MAYBE);
                }
                FVDCodeBaseImpl.fvds.put(s, fullValueDescription);
            }
            return fullValueDescription;
        }
        catch (final Throwable t2) {
            throw this.wrapper.incompatibleValueImpl(CompletionStatus.COMPLETED_MAYBE, t2);
        }
    }
    
    @Override
    public FullValueDescription[] metas(final String[] array) {
        final FullValueDescription[] array2 = new FullValueDescription[array.length];
        for (int i = 0; i < array.length; ++i) {
            array2[i] = this.meta(array[i]);
        }
        return array2;
    }
    
    @Override
    public String[] bases(final String s) {
        try {
            if (this.vhandler == null) {
                this.vhandler = ValueHandlerImpl.getInstance(false);
            }
            final Stack stack = new Stack();
            for (Class<?> clazz = ObjectStreamClass.lookup(this.vhandler.getClassFromType(s)).forClass().getSuperclass(); !clazz.equals(Object.class); clazz = clazz.getSuperclass()) {
                stack.push(this.vhandler.createForAnyType(clazz));
            }
            final String[] array = new String[stack.size()];
            for (int i = array.length - 1; i >= 0; ++i) {
                array[i] = stack.pop();
            }
            return array;
        }
        catch (final Throwable t) {
            throw this.wrapper.missingLocalValueImpl(CompletionStatus.COMPLETED_MAYBE, t);
        }
    }
    
    static {
        FVDCodeBaseImpl.fvds = new Hashtable();
    }
}
