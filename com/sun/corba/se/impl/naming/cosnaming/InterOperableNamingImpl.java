package com.sun.corba.se.impl.naming.cosnaming;

import java.io.StringWriter;
import org.omg.CosNaming.NamingContextExtPackage.InvalidAddress;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NameComponent;

public class InterOperableNamingImpl
{
    public String convertToString(final NameComponent[] array) {
        String s = this.convertNameComponentToString(array[0]);
        for (int i = 1; i < array.length; ++i) {
            if (this.convertNameComponentToString(array[i]) != null) {
                s = s + "/" + this.convertNameComponentToString(array[i]);
            }
        }
        return s;
    }
    
    private String convertNameComponentToString(final NameComponent nameComponent) {
        if ((nameComponent.id == null || nameComponent.id.length() == 0) && (nameComponent.kind == null || nameComponent.kind.length() == 0)) {
            return ".";
        }
        if (nameComponent.id == null || nameComponent.id.length() == 0) {
            return "." + this.addEscape(nameComponent.kind);
        }
        if (nameComponent.kind == null || nameComponent.kind.length() == 0) {
            return this.addEscape(nameComponent.id);
        }
        return this.addEscape(nameComponent.id) + "." + this.addEscape(nameComponent.kind);
    }
    
    private String addEscape(final String s) {
        if (s != null && (s.indexOf(46) != -1 || s.indexOf(47) != -1)) {
            final StringBuffer sb = new StringBuffer();
            for (int i = 0; i < s.length(); ++i) {
                final char char1 = s.charAt(i);
                if (char1 != '.' && char1 != '/') {
                    sb.append(char1);
                }
                else {
                    sb.append('\\');
                    sb.append(char1);
                }
            }
            return new String(sb);
        }
        return s;
    }
    
    public NameComponent[] convertToNameComponent(final String s) throws InvalidName {
        final String[] breakStringToNameComponents = this.breakStringToNameComponents(s);
        if (breakStringToNameComponents == null || breakStringToNameComponents.length == 0) {
            return null;
        }
        final NameComponent[] array = new NameComponent[breakStringToNameComponents.length];
        for (int i = 0; i < breakStringToNameComponents.length; ++i) {
            array[i] = this.createNameComponentFromString(breakStringToNameComponents[i]);
        }
        return array;
    }
    
    private String[] breakStringToNameComponents(final String s) {
        final int[] array = new int[100];
        int n = 0;
        int i = 0;
        while (i <= s.length()) {
            array[n] = s.indexOf(47, i);
            if (array[n] == -1) {
                i = s.length() + 1;
            }
            else if (array[n] > 0 && s.charAt(array[n] - 1) == '\\') {
                i = array[n] + 1;
                array[n] = -1;
            }
            else {
                i = array[n] + 1;
                ++n;
            }
        }
        if (n == 0) {
            return new String[] { s };
        }
        if (n != 0) {
            ++n;
        }
        return this.StringComponentsFromIndices(array, n, s);
    }
    
    private String[] StringComponentsFromIndices(final int[] array, final int n, final String s) {
        final String[] array2 = new String[n];
        int n2 = 0;
        int n3 = array[0];
        for (int i = 0; i < n; ++i) {
            array2[i] = s.substring(n2, n3);
            if (array[i] < s.length() - 1 && array[i] != -1) {
                n2 = array[i] + 1;
            }
            else {
                n2 = 0;
                i = n;
            }
            if (i + 1 < array.length && array[i + 1] < s.length() - 1 && array[i + 1] != -1) {
                n3 = array[i + 1];
            }
            else {
                i = n;
            }
            if (n2 != 0 && i == n) {
                array2[n - 1] = s.substring(n2);
            }
        }
        return array2;
    }
    
    private NameComponent createNameComponentFromString(final String s) throws InvalidName {
        String s2 = null;
        String s3 = null;
        if (s == null || s.length() == 0 || s.endsWith(".")) {
            throw new InvalidName();
        }
        int n = s.indexOf(46, 0);
        if (n == -1) {
            s2 = s;
        }
        else if (n == 0) {
            if (s.length() != 1) {
                s3 = s.substring(1);
            }
        }
        else if (s.charAt(n - 1) != '\\') {
            s2 = s.substring(0, n);
            s3 = s.substring(n + 1);
        }
        else {
            int n2 = 0;
            while (n < s.length() && n2 != 1) {
                n = s.indexOf(46, n + 1);
                if (n > 0) {
                    if (s.charAt(n - 1) == '\\') {
                        continue;
                    }
                    n2 = 1;
                }
                else {
                    n = s.length();
                }
            }
            if (n2 == 1) {
                s2 = s.substring(0, n);
                s3 = s.substring(n + 1);
            }
            else {
                s2 = s;
            }
        }
        String cleanEscapeCharacter = this.cleanEscapeCharacter(s2);
        String cleanEscapeCharacter2 = this.cleanEscapeCharacter(s3);
        if (cleanEscapeCharacter == null) {
            cleanEscapeCharacter = "";
        }
        if (cleanEscapeCharacter2 == null) {
            cleanEscapeCharacter2 = "";
        }
        return new NameComponent(cleanEscapeCharacter, cleanEscapeCharacter2);
    }
    
    private String cleanEscapeCharacter(final String s) {
        if (s == null || s.length() == 0) {
            return s;
        }
        if (s.indexOf(92) == 0) {
            return s;
        }
        final StringBuffer sb = new StringBuffer(s);
        final StringBuffer sb2 = new StringBuffer();
        for (int i = 0; i < s.length(); ++i) {
            final char char1 = sb.charAt(i);
            if (char1 != '\\') {
                sb2.append(char1);
            }
            else if (i + 1 < s.length() && Character.isLetterOrDigit(sb.charAt(i + 1))) {
                sb2.append(char1);
            }
        }
        return new String(sb2);
    }
    
    public String createURLBasedAddress(final String s, final String s2) throws InvalidAddress {
        if (s == null || s.length() == 0) {
            throw new InvalidAddress();
        }
        return "corbaname:" + s + "#" + this.encode(s2);
    }
    
    private String encode(final String s) {
        final StringWriter stringWriter = new StringWriter();
        for (int i = 0; i < s.length(); ++i) {
            final char char1 = s.charAt(i);
            if (Character.isLetterOrDigit(char1)) {
                stringWriter.write(char1);
            }
            else if (char1 == ';' || char1 == '/' || char1 == '?' || char1 == ':' || char1 == '@' || char1 == '&' || char1 == '=' || char1 == '+' || char1 == '$' || char1 == ';' || char1 == '-' || char1 == '_' || char1 == '.' || char1 == '!' || char1 == '~' || char1 == '*' || char1 == ' ' || char1 == '(' || char1 == ')') {
                stringWriter.write(char1);
            }
            else {
                stringWriter.write(37);
                stringWriter.write(Integer.toHexString(char1));
            }
        }
        return stringWriter.toString();
    }
}
