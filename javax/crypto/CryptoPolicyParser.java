package javax.crypto;

import java.security.GeneralSecurityException;
import java.util.Enumeration;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Locale;
import java.io.IOException;
import java.util.Hashtable;
import java.io.BufferedReader;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.util.Vector;

final class CryptoPolicyParser
{
    private Vector<GrantEntry> grantEntries;
    private StreamTokenizer st;
    private int lookahead;
    
    CryptoPolicyParser() {
        this.grantEntries = new Vector<GrantEntry>();
    }
    
    void read(Reader reader) throws ParsingException, IOException {
        if (!(reader instanceof BufferedReader)) {
            reader = new BufferedReader(reader);
        }
        (this.st = new StreamTokenizer(reader)).resetSyntax();
        this.st.wordChars(97, 122);
        this.st.wordChars(65, 90);
        this.st.wordChars(46, 46);
        this.st.wordChars(48, 57);
        this.st.wordChars(95, 95);
        this.st.wordChars(36, 36);
        this.st.wordChars(160, 255);
        this.st.whitespaceChars(0, 32);
        this.st.commentChar(47);
        this.st.quoteChar(39);
        this.st.quoteChar(34);
        this.st.lowerCaseMode(false);
        this.st.ordinaryChar(47);
        this.st.slashSlashComments(true);
        this.st.slashStarComments(true);
        this.st.parseNumbers();
        final Hashtable<String, Vector<String>> hashtable = null;
        this.lookahead = this.st.nextToken();
        while (this.lookahead != -1) {
            if (!this.peek("grant")) {
                throw new ParsingException(this.st.lineno(), "expected grant statement");
            }
            final GrantEntry grantEntry = this.parseGrantEntry(hashtable);
            if (grantEntry != null) {
                this.grantEntries.addElement(grantEntry);
            }
            this.match(";");
        }
    }
    
    private GrantEntry parseGrantEntry(final Hashtable<String, Vector<String>> hashtable) throws ParsingException, IOException {
        final GrantEntry grantEntry = new GrantEntry();
        this.match("grant");
        this.match("{");
        while (!this.peek("}")) {
            if (!this.peek("Permission")) {
                throw new ParsingException(this.st.lineno(), "expected permission entry");
            }
            grantEntry.add(this.parsePermissionEntry(hashtable));
            this.match(";");
        }
        this.match("}");
        return grantEntry;
    }
    
    private CryptoPermissionEntry parsePermissionEntry(final Hashtable<String, Vector<String>> hashtable) throws ParsingException, IOException {
        final CryptoPermissionEntry cryptoPermissionEntry = new CryptoPermissionEntry();
        this.match("Permission");
        cryptoPermissionEntry.cryptoPermission = this.match("permission type");
        if (cryptoPermissionEntry.cryptoPermission.equals("javax.crypto.CryptoAllPermission")) {
            cryptoPermissionEntry.alg = "CryptoAllPermission";
            cryptoPermissionEntry.maxKeySize = Integer.MAX_VALUE;
            return cryptoPermissionEntry;
        }
        if (this.peek("\"")) {
            cryptoPermissionEntry.alg = this.match("quoted string").toUpperCase(Locale.ENGLISH);
        }
        else {
            if (!this.peek("*")) {
                throw new ParsingException(this.st.lineno(), "Missing the algorithm name");
            }
            this.match("*");
            cryptoPermissionEntry.alg = "*";
        }
        this.peekAndMatch(",");
        if (this.peek("\"")) {
            cryptoPermissionEntry.exemptionMechanism = this.match("quoted string").toUpperCase(Locale.ENGLISH);
        }
        this.peekAndMatch(",");
        if (!this.isConsistent(cryptoPermissionEntry.alg, cryptoPermissionEntry.exemptionMechanism, hashtable)) {
            throw new ParsingException(this.st.lineno(), "Inconsistent policy");
        }
        if (this.peek("number")) {
            cryptoPermissionEntry.maxKeySize = this.match();
        }
        else if (this.peek("*")) {
            this.match("*");
            cryptoPermissionEntry.maxKeySize = Integer.MAX_VALUE;
        }
        else {
            if (!this.peek(";")) {
                throw new ParsingException(this.st.lineno(), "Missing the maximum allowable key size");
            }
            cryptoPermissionEntry.maxKeySize = Integer.MAX_VALUE;
        }
        this.peekAndMatch(",");
        if (this.peek("\"")) {
            final String match = this.match("quoted string");
            final Vector vector = new Vector(1);
            while (this.peek(",")) {
                this.match(",");
                if (this.peek("number")) {
                    vector.addElement(new Integer(this.match()));
                }
                else {
                    if (!this.peek("*")) {
                        throw new ParsingException(this.st.lineno(), "Expecting an integer");
                    }
                    this.match("*");
                    vector.addElement(new Integer(Integer.MAX_VALUE));
                }
            }
            final Integer[] array = new Integer[vector.size()];
            vector.copyInto(array);
            cryptoPermissionEntry.checkParam = true;
            cryptoPermissionEntry.algParamSpec = getInstance(match, array);
        }
        return cryptoPermissionEntry;
    }
    
    private static final AlgorithmParameterSpec getInstance(final String s, final Integer[] array) throws ParsingException {
        AlgorithmParameterSpec algorithmParameterSpec;
        try {
            final Class<?> forName = Class.forName(s);
            final Class[] array2 = new Class[array.length];
            for (int i = 0; i < array.length; ++i) {
                array2[i] = Integer.TYPE;
            }
            algorithmParameterSpec = (AlgorithmParameterSpec)forName.getConstructor((Class<?>[])array2).newInstance((Object[])array);
        }
        catch (final Exception ex) {
            throw new ParsingException("Cannot call the constructor of " + s + ex);
        }
        return algorithmParameterSpec;
    }
    
    private boolean peekAndMatch(final String s) throws ParsingException, IOException {
        if (this.peek(s)) {
            this.match(s);
            return true;
        }
        return false;
    }
    
    private boolean peek(final String s) {
        boolean b = false;
        switch (this.lookahead) {
            case -3: {
                if (s.equalsIgnoreCase(this.st.sval)) {
                    b = true;
                    break;
                }
                break;
            }
            case -2: {
                if (s.equalsIgnoreCase("number")) {
                    b = true;
                    break;
                }
                break;
            }
            case 44: {
                if (s.equals(",")) {
                    b = true;
                    break;
                }
                break;
            }
            case 123: {
                if (s.equals("{")) {
                    b = true;
                    break;
                }
                break;
            }
            case 125: {
                if (s.equals("}")) {
                    b = true;
                    break;
                }
                break;
            }
            case 34: {
                if (s.equals("\"")) {
                    b = true;
                    break;
                }
                break;
            }
            case 42: {
                if (s.equals("*")) {
                    b = true;
                    break;
                }
                break;
            }
            case 59: {
                if (s.equals(";")) {
                    b = true;
                    break;
                }
                break;
            }
        }
        return b;
    }
    
    private int match() throws ParsingException, IOException {
        int n = -1;
        final int lineno = this.st.lineno();
        String s = null;
        switch (this.lookahead) {
            case -2: {
                n = (int)this.st.nval;
                if (n < 0) {
                    s = String.valueOf(this.st.nval);
                }
                this.lookahead = this.st.nextToken();
                break;
            }
            default: {
                s = this.st.sval;
                break;
            }
        }
        if (n <= 0) {
            throw new ParsingException(lineno, "a non-negative number", s);
        }
        return n;
    }
    
    private String match(final String s) throws ParsingException, IOException {
        String s2 = null;
        switch (this.lookahead) {
            case -2: {
                throw new ParsingException(this.st.lineno(), s, "number " + String.valueOf(this.st.nval));
            }
            case -1: {
                throw new ParsingException("expected " + s + ", read end of file");
            }
            case -3: {
                if (s.equalsIgnoreCase(this.st.sval)) {
                    this.lookahead = this.st.nextToken();
                    break;
                }
                if (s.equalsIgnoreCase("permission type")) {
                    s2 = this.st.sval;
                    this.lookahead = this.st.nextToken();
                    break;
                }
                throw new ParsingException(this.st.lineno(), s, this.st.sval);
            }
            case 34: {
                if (s.equalsIgnoreCase("quoted string")) {
                    s2 = this.st.sval;
                    this.lookahead = this.st.nextToken();
                    break;
                }
                if (s.equalsIgnoreCase("permission type")) {
                    s2 = this.st.sval;
                    this.lookahead = this.st.nextToken();
                    break;
                }
                throw new ParsingException(this.st.lineno(), s, this.st.sval);
            }
            case 44: {
                if (s.equals(",")) {
                    this.lookahead = this.st.nextToken();
                    break;
                }
                throw new ParsingException(this.st.lineno(), s, ",");
            }
            case 123: {
                if (s.equals("{")) {
                    this.lookahead = this.st.nextToken();
                    break;
                }
                throw new ParsingException(this.st.lineno(), s, "{");
            }
            case 125: {
                if (s.equals("}")) {
                    this.lookahead = this.st.nextToken();
                    break;
                }
                throw new ParsingException(this.st.lineno(), s, "}");
            }
            case 59: {
                if (s.equals(";")) {
                    this.lookahead = this.st.nextToken();
                    break;
                }
                throw new ParsingException(this.st.lineno(), s, ";");
            }
            case 42: {
                if (s.equals("*")) {
                    this.lookahead = this.st.nextToken();
                    break;
                }
                throw new ParsingException(this.st.lineno(), s, "*");
            }
            default: {
                throw new ParsingException(this.st.lineno(), s, new String(new char[] { (char)this.lookahead }));
            }
        }
        return s2;
    }
    
    CryptoPermission[] getPermissions() {
        final Vector vector = new Vector();
        final Enumeration<GrantEntry> elements = this.grantEntries.elements();
        while (elements.hasMoreElements()) {
            final Enumeration<CryptoPermissionEntry> permissionElements = elements.nextElement().permissionElements();
            while (permissionElements.hasMoreElements()) {
                final CryptoPermissionEntry cryptoPermissionEntry = permissionElements.nextElement();
                if (cryptoPermissionEntry.cryptoPermission.equals("javax.crypto.CryptoAllPermission")) {
                    vector.addElement(CryptoAllPermission.INSTANCE);
                }
                else if (cryptoPermissionEntry.checkParam) {
                    vector.addElement(new CryptoPermission(cryptoPermissionEntry.alg, cryptoPermissionEntry.maxKeySize, cryptoPermissionEntry.algParamSpec, cryptoPermissionEntry.exemptionMechanism));
                }
                else {
                    vector.addElement(new CryptoPermission(cryptoPermissionEntry.alg, cryptoPermissionEntry.maxKeySize, cryptoPermissionEntry.exemptionMechanism));
                }
            }
        }
        final CryptoPermission[] array = new CryptoPermission[vector.size()];
        vector.copyInto(array);
        return array;
    }
    
    private boolean isConsistent(final String s, final String s2, final Hashtable<String, Vector<String>> hashtable) {
        final String s3 = (s2 == null) ? "none" : s2;
        if (hashtable == null) {
            final Hashtable hashtable2 = new Hashtable();
            final Vector vector = new Vector(1);
            vector.addElement(s3);
            hashtable2.put(s, vector);
            return true;
        }
        if (hashtable.containsKey("CryptoAllPermission")) {
            return false;
        }
        Vector vector2;
        if (hashtable.containsKey(s)) {
            vector2 = hashtable.get(s);
            if (vector2.contains(s3)) {
                return false;
            }
        }
        else {
            vector2 = new Vector(1);
        }
        vector2.addElement(s3);
        hashtable.put(s, vector2);
        return true;
    }
    
    private static class GrantEntry
    {
        private Vector<CryptoPermissionEntry> permissionEntries;
        
        GrantEntry() {
            this.permissionEntries = new Vector<CryptoPermissionEntry>();
        }
        
        void add(final CryptoPermissionEntry cryptoPermissionEntry) {
            this.permissionEntries.addElement(cryptoPermissionEntry);
        }
        
        boolean remove(final CryptoPermissionEntry cryptoPermissionEntry) {
            return this.permissionEntries.removeElement(cryptoPermissionEntry);
        }
        
        boolean contains(final CryptoPermissionEntry cryptoPermissionEntry) {
            return this.permissionEntries.contains(cryptoPermissionEntry);
        }
        
        Enumeration<CryptoPermissionEntry> permissionElements() {
            return this.permissionEntries.elements();
        }
    }
    
    private static class CryptoPermissionEntry
    {
        String cryptoPermission;
        String alg;
        String exemptionMechanism;
        int maxKeySize;
        boolean checkParam;
        AlgorithmParameterSpec algParamSpec;
        
        CryptoPermissionEntry() {
            this.maxKeySize = 0;
            this.alg = null;
            this.exemptionMechanism = null;
            this.checkParam = false;
            this.algParamSpec = null;
        }
        
        @Override
        public int hashCode() {
            int hashCode = this.cryptoPermission.hashCode();
            if (this.alg != null) {
                hashCode ^= this.alg.hashCode();
            }
            if (this.exemptionMechanism != null) {
                hashCode ^= this.exemptionMechanism.hashCode();
            }
            int n = hashCode ^ this.maxKeySize;
            if (this.checkParam) {
                n ^= 0x64;
            }
            if (this.algParamSpec != null) {
                n ^= this.algParamSpec.hashCode();
            }
            return n;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof CryptoPermissionEntry)) {
                return false;
            }
            final CryptoPermissionEntry cryptoPermissionEntry = (CryptoPermissionEntry)o;
            if (this.cryptoPermission == null) {
                if (cryptoPermissionEntry.cryptoPermission != null) {
                    return false;
                }
            }
            else if (!this.cryptoPermission.equals(cryptoPermissionEntry.cryptoPermission)) {
                return false;
            }
            if (this.alg == null) {
                if (cryptoPermissionEntry.alg != null) {
                    return false;
                }
            }
            else if (!this.alg.equalsIgnoreCase(cryptoPermissionEntry.alg)) {
                return false;
            }
            if (this.maxKeySize != cryptoPermissionEntry.maxKeySize) {
                return false;
            }
            if (this.checkParam != cryptoPermissionEntry.checkParam) {
                return false;
            }
            if (this.algParamSpec == null) {
                if (cryptoPermissionEntry.algParamSpec != null) {
                    return false;
                }
            }
            else if (!this.algParamSpec.equals(cryptoPermissionEntry.algParamSpec)) {
                return false;
            }
            return true;
        }
    }
    
    static final class ParsingException extends GeneralSecurityException
    {
        private static final long serialVersionUID = 7147241245566588374L;
        
        ParsingException(final String s) {
            super(s);
        }
        
        ParsingException(final int n, final String s) {
            super("line " + n + ": " + s);
        }
        
        ParsingException(final int n, final String s, final String s2) {
            super("line " + n + ": expected '" + s + "', found '" + s2 + "'");
        }
    }
}
