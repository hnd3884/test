package com.sun.jndi.cosnaming;

import java.util.Hashtable;
import java.util.Enumeration;
import javax.naming.CompoundName;
import java.util.Vector;
import javax.naming.CompositeName;
import javax.naming.InvalidNameException;
import org.omg.CosNaming.NameComponent;
import javax.naming.NamingException;
import javax.naming.Name;
import java.util.Properties;
import javax.naming.NameParser;

public final class CNNameParser implements NameParser
{
    private static final Properties mySyntax;
    private static final char kindSeparator = '.';
    private static final char compSeparator = '/';
    private static final char escapeChar = '\\';
    
    @Override
    public Name parse(final String s) throws NamingException {
        return new CNCompoundName(insStringToStringifiedComps(s).elements());
    }
    
    static NameComponent[] nameToCosName(final Name name) throws InvalidNameException {
        final int size = name.size();
        if (size == 0) {
            return new NameComponent[0];
        }
        final NameComponent[] array = new NameComponent[size];
        for (int i = 0; i < size; ++i) {
            array[i] = parseComponent(name.get(i));
        }
        return array;
    }
    
    static String cosNameToInsString(final NameComponent[] array) {
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < array.length; ++i) {
            if (i > 0) {
                sb.append('/');
            }
            sb.append(stringifyComponent(array[i]));
        }
        return sb.toString();
    }
    
    static Name cosNameToName(final NameComponent[] array) {
        final CompositeName compositeName = new CompositeName();
        for (int n = 0; array != null && n < array.length; ++n) {
            try {
                compositeName.add(stringifyComponent(array[n]));
            }
            catch (final InvalidNameException ex) {}
        }
        return compositeName;
    }
    
    private static Vector<String> insStringToStringifiedComps(final String s) throws InvalidNameException {
        final int length = s.length();
        final Vector vector = new Vector(10);
        final char[] array = new char[length];
        final char[] array2 = new char[length];
        for (int i = 0; i < length; ++i) {
            int n2;
            int n = n2 = 0;
            int n3 = 1;
            while (i < length && s.charAt(i) != '/') {
                if (s.charAt(i) == '\\') {
                    if (i + 1 >= length) {
                        throw new InvalidNameException(s + ": unescaped \\ at end of component");
                    }
                    if (!isMeta(s.charAt(i + 1))) {
                        throw new InvalidNameException(s + ": invalid character being escaped");
                    }
                    ++i;
                    if (n3 != 0) {
                        array[n2++] = s.charAt(i++);
                    }
                    else {
                        array2[n++] = s.charAt(i++);
                    }
                }
                else if (n3 != 0 && s.charAt(i) == '.') {
                    ++i;
                    n3 = 0;
                }
                else if (n3 != 0) {
                    array[n2++] = s.charAt(i++);
                }
                else {
                    array2[n++] = s.charAt(i++);
                }
            }
            vector.addElement(stringifyComponent(new NameComponent(new String(array, 0, n2), new String(array2, 0, n))));
            if (i < length) {}
        }
        return vector;
    }
    
    private static NameComponent parseComponent(final String s) throws InvalidNameException {
        final NameComponent nameComponent = new NameComponent();
        int n = -1;
        final int length = s.length();
        int n2 = 0;
        final char[] array = new char[length];
        int n3 = 0;
        for (int n4 = 0; n4 < length && n < 0; ++n4) {
            if (n3 != 0) {
                array[n2++] = s.charAt(n4);
                n3 = 0;
            }
            else if (s.charAt(n4) == '\\') {
                if (n4 + 1 >= length) {
                    throw new InvalidNameException(s + ": unescaped \\ at end of component");
                }
                if (!isMeta(s.charAt(n4 + 1))) {
                    throw new InvalidNameException(s + ": invalid character being escaped");
                }
                n3 = 1;
            }
            else if (s.charAt(n4) == '.') {
                n = n4;
            }
            else {
                array[n2++] = s.charAt(n4);
            }
        }
        nameComponent.id = new String(array, 0, n2);
        if (n < 0) {
            nameComponent.kind = "";
        }
        else {
            int n5 = 0;
            int n6 = 0;
            for (int i = n + 1; i < length; ++i) {
                if (n6 != 0) {
                    array[n5++] = s.charAt(i);
                    n6 = 0;
                }
                else if (s.charAt(i) == '\\') {
                    if (i + 1 >= length) {
                        throw new InvalidNameException(s + ": unescaped \\ at end of component");
                    }
                    if (!isMeta(s.charAt(i + 1))) {
                        throw new InvalidNameException(s + ": invalid character being escaped");
                    }
                    n6 = 1;
                }
                else {
                    array[n5++] = s.charAt(i);
                }
            }
            nameComponent.kind = new String(array, 0, n5);
        }
        return nameComponent;
    }
    
    private static String stringifyComponent(final NameComponent nameComponent) {
        final StringBuffer sb = new StringBuffer(escape(nameComponent.id));
        if (nameComponent.kind != null && !nameComponent.kind.equals("")) {
            sb.append('.' + escape(nameComponent.kind));
        }
        if (sb.length() == 0) {
            return ".";
        }
        return sb.toString();
    }
    
    private static String escape(final String s) {
        if (s.indexOf(46) < 0 && s.indexOf(47) < 0 && s.indexOf(92) < 0) {
            return s;
        }
        final int length = s.length();
        int n = 0;
        final char[] array = new char[length + length];
        for (int i = 0; i < length; ++i) {
            if (isMeta(s.charAt(i))) {
                array[n++] = '\\';
            }
            array[n++] = s.charAt(i);
        }
        return new String(array, 0, n);
    }
    
    private static boolean isMeta(final char c) {
        switch (c) {
            case '.':
            case '/':
            case '\\': {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    static {
        ((Hashtable<String, String>)(mySyntax = new Properties())).put("jndi.syntax.direction", "left_to_right");
        ((Hashtable<String, String>)CNNameParser.mySyntax).put("jndi.syntax.separator", "/");
        ((Hashtable<String, String>)CNNameParser.mySyntax).put("jndi.syntax.escape", "\\");
    }
    
    static final class CNCompoundName extends CompoundName
    {
        private static final long serialVersionUID = -6599252802678482317L;
        
        CNCompoundName(final Enumeration<String> enumeration) {
            super(enumeration, CNNameParser.mySyntax);
        }
        
        @Override
        public Object clone() {
            return new CNCompoundName(this.getAll());
        }
        
        @Override
        public Name getPrefix(final int n) {
            return new CNCompoundName(super.getPrefix(n).getAll());
        }
        
        @Override
        public Name getSuffix(final int n) {
            return new CNCompoundName(super.getSuffix(n).getAll());
        }
        
        @Override
        public String toString() {
            try {
                return CNNameParser.cosNameToInsString(CNNameParser.nameToCosName(this));
            }
            catch (final InvalidNameException ex) {
                return super.toString();
            }
        }
    }
}
