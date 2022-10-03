package com.sun.mail.imap.protocol;

import java.util.List;
import java.util.ArrayList;
import com.sun.mail.iap.ProtocolException;
import com.sun.mail.iap.Response;

public class Namespaces
{
    public Namespace[] personal;
    public Namespace[] otherUsers;
    public Namespace[] shared;
    
    public Namespaces(final Response r) throws ProtocolException {
        this.personal = this.getNamespaces(r);
        this.otherUsers = this.getNamespaces(r);
        this.shared = this.getNamespaces(r);
    }
    
    private Namespace[] getNamespaces(final Response r) throws ProtocolException {
        if (r.isNextNonSpace('(')) {
            final List<Namespace> v = new ArrayList<Namespace>();
            do {
                final Namespace ns = new Namespace(r);
                v.add(ns);
            } while (!r.isNextNonSpace(')'));
            return v.toArray(new Namespace[v.size()]);
        }
        final String s = r.readAtom();
        if (s == null) {
            throw new ProtocolException("Expected NIL, got null");
        }
        if (!s.equalsIgnoreCase("NIL")) {
            throw new ProtocolException("Expected NIL, got " + s);
        }
        return null;
    }
    
    public static class Namespace
    {
        public String prefix;
        public char delimiter;
        
        public Namespace(final Response r) throws ProtocolException {
            if (!r.isNextNonSpace('(')) {
                throw new ProtocolException("Missing '(' at start of Namespace");
            }
            this.prefix = r.readString();
            if (!r.supportsUtf8()) {
                this.prefix = BASE64MailboxDecoder.decode(this.prefix);
            }
            r.skipSpaces();
            if (r.peekByte() == 34) {
                r.readByte();
                this.delimiter = (char)r.readByte();
                if (this.delimiter == '\\') {
                    this.delimiter = (char)r.readByte();
                }
                if (r.readByte() != 34) {
                    throw new ProtocolException("Missing '\"' at end of QUOTED_CHAR");
                }
            }
            else {
                final String s = r.readAtom();
                if (s == null) {
                    throw new ProtocolException("Expected NIL, got null");
                }
                if (!s.equalsIgnoreCase("NIL")) {
                    throw new ProtocolException("Expected NIL, got " + s);
                }
                this.delimiter = '\0';
            }
            if (r.isNextNonSpace(')')) {
                return;
            }
            r.readString();
            r.skipSpaces();
            r.readStringList();
            if (!r.isNextNonSpace(')')) {
                throw new ProtocolException("Missing ')' at end of Namespace");
            }
        }
    }
}
