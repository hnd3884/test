package com.sun.mail.imap.protocol;

import java.util.Iterator;
import com.sun.mail.iap.Argument;
import java.util.Collections;
import java.util.HashMap;
import com.sun.mail.iap.ProtocolException;
import com.sun.mail.iap.Response;
import java.util.Map;

public class ID
{
    private Map<String, String> serverParams;
    
    public ID(final Response r) throws ProtocolException {
        this.serverParams = null;
        r.skipSpaces();
        final int c = r.peekByte();
        if (c == 78 || c == 110) {
            return;
        }
        if (c != 40) {
            throw new ProtocolException("Missing '(' at start of ID");
        }
        this.serverParams = new HashMap<String, String>();
        final String[] v = r.readStringList();
        if (v != null) {
            for (int i = 0; i < v.length; i += 2) {
                final String name = v[i];
                if (name == null) {
                    throw new ProtocolException("ID field name null");
                }
                if (i + 1 >= v.length) {
                    throw new ProtocolException("ID field without value: " + name);
                }
                final String value = v[i + 1];
                this.serverParams.put(name, value);
            }
        }
        this.serverParams = Collections.unmodifiableMap((Map<? extends String, ? extends String>)this.serverParams);
    }
    
    Map<String, String> getServerParams() {
        return this.serverParams;
    }
    
    static Argument getArgumentList(final Map<String, String> clientParams) {
        final Argument arg = new Argument();
        if (clientParams == null) {
            arg.writeAtom("NIL");
            return arg;
        }
        final Argument list = new Argument();
        for (final Map.Entry<String, String> e : clientParams.entrySet()) {
            list.writeNString(e.getKey());
            list.writeNString(e.getValue());
        }
        arg.writeArgument(list);
        return arg;
    }
}
