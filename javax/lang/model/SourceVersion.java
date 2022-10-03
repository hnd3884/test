package javax.lang.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public enum SourceVersion
{
    RELEASE_0, 
    RELEASE_1, 
    RELEASE_2, 
    RELEASE_3, 
    RELEASE_4, 
    RELEASE_5, 
    RELEASE_6, 
    RELEASE_7, 
    RELEASE_8;
    
    private static final SourceVersion latestSupported;
    private static final Set<String> keywords;
    
    public static SourceVersion latest() {
        return SourceVersion.RELEASE_8;
    }
    
    private static SourceVersion getLatestSupported() {
        try {
            final String property = System.getProperty("java.specification.version");
            if ("1.8".equals(property)) {
                return SourceVersion.RELEASE_8;
            }
            if ("1.7".equals(property)) {
                return SourceVersion.RELEASE_7;
            }
            if ("1.6".equals(property)) {
                return SourceVersion.RELEASE_6;
            }
        }
        catch (final SecurityException ex) {}
        return SourceVersion.RELEASE_5;
    }
    
    public static SourceVersion latestSupported() {
        return SourceVersion.latestSupported;
    }
    
    public static boolean isIdentifier(final CharSequence charSequence) {
        final String string = charSequence.toString();
        if (string.length() == 0) {
            return false;
        }
        final int codePoint = string.codePointAt(0);
        if (!Character.isJavaIdentifierStart(codePoint)) {
            return false;
        }
        int codePoint2;
        for (int i = Character.charCount(codePoint); i < string.length(); i += Character.charCount(codePoint2)) {
            codePoint2 = string.codePointAt(i);
            if (!Character.isJavaIdentifierPart(codePoint2)) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean isName(final CharSequence charSequence) {
        for (final String s : charSequence.toString().split("\\.", -1)) {
            if (!isIdentifier(s) || isKeyword(s)) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean isKeyword(final CharSequence charSequence) {
        return SourceVersion.keywords.contains(charSequence.toString());
    }
    
    static {
        latestSupported = getLatestSupported();
        final HashSet set = new HashSet();
        final String[] array = { "abstract", "continue", "for", "new", "switch", "assert", "default", "if", "package", "synchronized", "boolean", "do", "goto", "private", "this", "break", "double", "implements", "protected", "throw", "byte", "else", "import", "public", "throws", "case", "enum", "instanceof", "return", "transient", "catch", "extends", "int", "short", "try", "char", "final", "interface", "static", "void", "class", "finally", "long", "strictfp", "volatile", "const", "float", "native", "super", "while", "null", "true", "false" };
        for (int length = array.length, i = 0; i < length; ++i) {
            set.add(array[i]);
        }
        keywords = Collections.unmodifiableSet((Set<?>)set);
    }
}
