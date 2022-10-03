package java.lang;

class CharacterDataUndefined extends CharacterData
{
    static final CharacterData instance;
    
    @Override
    int getProperties(final int n) {
        return 0;
    }
    
    @Override
    int getType(final int n) {
        return 0;
    }
    
    @Override
    boolean isJavaIdentifierStart(final int n) {
        return false;
    }
    
    @Override
    boolean isJavaIdentifierPart(final int n) {
        return false;
    }
    
    @Override
    boolean isUnicodeIdentifierStart(final int n) {
        return false;
    }
    
    @Override
    boolean isUnicodeIdentifierPart(final int n) {
        return false;
    }
    
    @Override
    boolean isIdentifierIgnorable(final int n) {
        return false;
    }
    
    @Override
    int toLowerCase(final int n) {
        return n;
    }
    
    @Override
    int toUpperCase(final int n) {
        return n;
    }
    
    @Override
    int toTitleCase(final int n) {
        return n;
    }
    
    @Override
    int digit(final int n, final int n2) {
        return -1;
    }
    
    @Override
    int getNumericValue(final int n) {
        return -1;
    }
    
    @Override
    boolean isWhitespace(final int n) {
        return false;
    }
    
    @Override
    byte getDirectionality(final int n) {
        return -1;
    }
    
    @Override
    boolean isMirrored(final int n) {
        return false;
    }
    
    private CharacterDataUndefined() {
    }
    
    static {
        instance = new CharacterDataUndefined();
    }
}
