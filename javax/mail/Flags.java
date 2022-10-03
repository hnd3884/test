package javax.mail;

import java.util.Vector;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Hashtable;
import java.io.Serializable;

public class Flags implements Cloneable, Serializable
{
    private int system_flags;
    private Hashtable<String, String> user_flags;
    private static final int ANSWERED_BIT = 1;
    private static final int DELETED_BIT = 2;
    private static final int DRAFT_BIT = 4;
    private static final int FLAGGED_BIT = 8;
    private static final int RECENT_BIT = 16;
    private static final int SEEN_BIT = 32;
    private static final int USER_BIT = Integer.MIN_VALUE;
    private static final long serialVersionUID = 6243590407214169028L;
    
    public Flags() {
        this.system_flags = 0;
        this.user_flags = null;
    }
    
    public Flags(final Flags flags) {
        this.system_flags = 0;
        this.user_flags = null;
        this.system_flags = flags.system_flags;
        if (flags.user_flags != null) {
            this.user_flags = (Hashtable)flags.user_flags.clone();
        }
    }
    
    public Flags(final Flag flag) {
        this.system_flags = 0;
        this.user_flags = null;
        this.system_flags |= flag.bit;
    }
    
    public Flags(final String flag) {
        this.system_flags = 0;
        this.user_flags = null;
        (this.user_flags = new Hashtable<String, String>(1)).put(flag.toLowerCase(Locale.ENGLISH), flag);
    }
    
    public void add(final Flag flag) {
        this.system_flags |= flag.bit;
    }
    
    public void add(final String flag) {
        if (this.user_flags == null) {
            this.user_flags = new Hashtable<String, String>(1);
        }
        this.user_flags.put(flag.toLowerCase(Locale.ENGLISH), flag);
    }
    
    public void add(final Flags f) {
        this.system_flags |= f.system_flags;
        if (f.user_flags != null) {
            if (this.user_flags == null) {
                this.user_flags = new Hashtable<String, String>(1);
            }
            final Enumeration<String> e = f.user_flags.keys();
            while (e.hasMoreElements()) {
                final String s = e.nextElement();
                this.user_flags.put(s, f.user_flags.get(s));
            }
        }
    }
    
    public void remove(final Flag flag) {
        this.system_flags &= ~flag.bit;
    }
    
    public void remove(final String flag) {
        if (this.user_flags != null) {
            this.user_flags.remove(flag.toLowerCase(Locale.ENGLISH));
        }
    }
    
    public void remove(final Flags f) {
        this.system_flags &= ~f.system_flags;
        if (f.user_flags != null) {
            if (this.user_flags == null) {
                return;
            }
            final Enumeration<String> e = f.user_flags.keys();
            while (e.hasMoreElements()) {
                this.user_flags.remove(e.nextElement());
            }
        }
    }
    
    public boolean retainAll(final Flags f) {
        boolean changed = false;
        final int sf = this.system_flags & f.system_flags;
        if (this.system_flags != sf) {
            this.system_flags = sf;
            changed = true;
        }
        if (this.user_flags != null && (f.system_flags & Integer.MIN_VALUE) == 0x0) {
            if (f.user_flags != null) {
                final Enumeration<String> e = this.user_flags.keys();
                while (e.hasMoreElements()) {
                    final String key = e.nextElement();
                    if (!f.user_flags.containsKey(key)) {
                        this.user_flags.remove(key);
                        changed = true;
                    }
                }
            }
            else {
                changed = (this.user_flags.size() > 0);
                this.user_flags = null;
            }
        }
        return changed;
    }
    
    public boolean contains(final Flag flag) {
        return (this.system_flags & flag.bit) != 0x0;
    }
    
    public boolean contains(final String flag) {
        return this.user_flags != null && this.user_flags.containsKey(flag.toLowerCase(Locale.ENGLISH));
    }
    
    public boolean contains(final Flags f) {
        if ((f.system_flags & this.system_flags) != f.system_flags) {
            return false;
        }
        if (f.user_flags != null) {
            if (this.user_flags == null) {
                return false;
            }
            final Enumeration<String> e = f.user_flags.keys();
            while (e.hasMoreElements()) {
                if (!this.user_flags.containsKey(e.nextElement())) {
                    return false;
                }
            }
        }
        return true;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof Flags)) {
            return false;
        }
        final Flags f = (Flags)obj;
        if (f.system_flags != this.system_flags) {
            return false;
        }
        final int size = (this.user_flags == null) ? 0 : this.user_flags.size();
        final int fsize = (f.user_flags == null) ? 0 : f.user_flags.size();
        return (size == 0 && fsize == 0) || (f.user_flags != null && this.user_flags != null && fsize == size && this.user_flags.keySet().equals(f.user_flags.keySet()));
    }
    
    @Override
    public int hashCode() {
        int hash = this.system_flags;
        if (this.user_flags != null) {
            final Enumeration<String> e = this.user_flags.keys();
            while (e.hasMoreElements()) {
                hash += e.nextElement().hashCode();
            }
        }
        return hash;
    }
    
    public Flag[] getSystemFlags() {
        final Vector<Flag> v = new Vector<Flag>();
        if ((this.system_flags & 0x1) != 0x0) {
            v.addElement(Flag.ANSWERED);
        }
        if ((this.system_flags & 0x2) != 0x0) {
            v.addElement(Flag.DELETED);
        }
        if ((this.system_flags & 0x4) != 0x0) {
            v.addElement(Flag.DRAFT);
        }
        if ((this.system_flags & 0x8) != 0x0) {
            v.addElement(Flag.FLAGGED);
        }
        if ((this.system_flags & 0x10) != 0x0) {
            v.addElement(Flag.RECENT);
        }
        if ((this.system_flags & 0x20) != 0x0) {
            v.addElement(Flag.SEEN);
        }
        if ((this.system_flags & Integer.MIN_VALUE) != 0x0) {
            v.addElement(Flag.USER);
        }
        final Flag[] f = new Flag[v.size()];
        v.copyInto(f);
        return f;
    }
    
    public String[] getUserFlags() {
        final Vector<String> v = new Vector<String>();
        if (this.user_flags != null) {
            final Enumeration<String> e = this.user_flags.elements();
            while (e.hasMoreElements()) {
                v.addElement(e.nextElement());
            }
        }
        final String[] f = new String[v.size()];
        v.copyInto(f);
        return f;
    }
    
    public void clearSystemFlags() {
        this.system_flags = 0;
    }
    
    public void clearUserFlags() {
        this.user_flags = null;
    }
    
    public Object clone() {
        Flags f = null;
        try {
            f = (Flags)super.clone();
        }
        catch (final CloneNotSupportedException ex) {}
        if (this.user_flags != null) {
            f.user_flags = (Hashtable)this.user_flags.clone();
        }
        return f;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        if ((this.system_flags & 0x1) != 0x0) {
            sb.append("\\Answered ");
        }
        if ((this.system_flags & 0x2) != 0x0) {
            sb.append("\\Deleted ");
        }
        if ((this.system_flags & 0x4) != 0x0) {
            sb.append("\\Draft ");
        }
        if ((this.system_flags & 0x8) != 0x0) {
            sb.append("\\Flagged ");
        }
        if ((this.system_flags & 0x10) != 0x0) {
            sb.append("\\Recent ");
        }
        if ((this.system_flags & 0x20) != 0x0) {
            sb.append("\\Seen ");
        }
        if ((this.system_flags & Integer.MIN_VALUE) != 0x0) {
            sb.append("\\* ");
        }
        boolean first = true;
        if (this.user_flags != null) {
            final Enumeration<String> e = this.user_flags.elements();
            while (e.hasMoreElements()) {
                if (first) {
                    first = false;
                }
                else {
                    sb.append(' ');
                }
                sb.append(e.nextElement());
            }
        }
        if (first && sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }
    
    public static final class Flag
    {
        public static final Flag ANSWERED;
        public static final Flag DELETED;
        public static final Flag DRAFT;
        public static final Flag FLAGGED;
        public static final Flag RECENT;
        public static final Flag SEEN;
        public static final Flag USER;
        private int bit;
        
        private Flag(final int bit) {
            this.bit = bit;
        }
        
        static {
            ANSWERED = new Flag(1);
            DELETED = new Flag(2);
            DRAFT = new Flag(4);
            FLAGGED = new Flag(8);
            RECENT = new Flag(16);
            SEEN = new Flag(32);
            USER = new Flag(Integer.MIN_VALUE);
        }
    }
}
