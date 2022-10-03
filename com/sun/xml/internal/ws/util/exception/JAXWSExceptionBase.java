package com.sun.xml.internal.ws.util.exception;

import com.sun.istack.internal.localization.Localizer;
import java.util.List;
import com.sun.istack.internal.localization.LocalizableMessageFactory;
import java.util.ArrayList;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.ObjectOutputStream;
import com.sun.istack.internal.localization.NullLocalizable;
import com.sun.istack.internal.localization.LocalizableMessage;
import com.sun.istack.internal.localization.Localizable;
import javax.xml.ws.WebServiceException;

public abstract class JAXWSExceptionBase extends WebServiceException implements Localizable
{
    private static final long serialVersionUID = 1L;
    private transient Localizable msg;
    
    @Deprecated
    protected JAXWSExceptionBase(final String key, final Object... args) {
        super(findNestedException(args));
        this.msg = new LocalizableMessage(this.getDefaultResourceBundleName(), key, args);
    }
    
    protected JAXWSExceptionBase(final String message) {
        this(new NullLocalizable(message));
    }
    
    protected JAXWSExceptionBase(final Throwable throwable) {
        this(new NullLocalizable(throwable.toString()), throwable);
    }
    
    protected JAXWSExceptionBase(final Localizable msg) {
        this.msg = msg;
    }
    
    protected JAXWSExceptionBase(final Localizable msg, final Throwable cause) {
        super(cause);
        this.msg = msg;
    }
    
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(this.msg.getResourceBundleName());
        out.writeObject(this.msg.getKey());
        final Object[] args = this.msg.getArguments();
        if (args == null) {
            out.writeInt(-1);
            return;
        }
        out.writeInt(args.length);
        for (int i = 0; i < args.length; ++i) {
            if (args[i] == null || args[i] instanceof Serializable) {
                out.writeObject(args[i]);
            }
            else {
                out.writeObject(args[i].toString());
            }
        }
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        final String resourceBundleName = (String)in.readObject();
        final String key = (String)in.readObject();
        final int len = in.readInt();
        if (len < -1) {
            throw new NegativeArraySizeException();
        }
        Object[] args;
        if (len == -1) {
            args = null;
        }
        else if (len < 255) {
            args = new Object[len];
            for (int i = 0; i < args.length; ++i) {
                args[i] = in.readObject();
            }
        }
        else {
            final List<Object> argList = new ArrayList<Object>(Math.min(len, 1024));
            for (int j = 0; j < len; ++j) {
                argList.add(in.readObject());
            }
            args = argList.toArray(new Object[argList.size()]);
        }
        this.msg = new LocalizableMessageFactory(resourceBundleName).getMessage(key, args);
    }
    
    private static Throwable findNestedException(final Object[] args) {
        if (args == null) {
            return null;
        }
        for (final Object o : args) {
            if (o instanceof Throwable) {
                return (Throwable)o;
            }
        }
        return null;
    }
    
    @Override
    public String getMessage() {
        final Localizer localizer = new Localizer();
        return localizer.localize(this);
    }
    
    protected abstract String getDefaultResourceBundleName();
    
    @Override
    public final String getKey() {
        return this.msg.getKey();
    }
    
    @Override
    public final Object[] getArguments() {
        return this.msg.getArguments();
    }
    
    @Override
    public final String getResourceBundleName() {
        return this.msg.getResourceBundleName();
    }
}
