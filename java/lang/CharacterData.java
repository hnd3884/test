package java.lang;

abstract class CharacterData
{
    abstract int getProperties(final int p0);
    
    abstract int getType(final int p0);
    
    abstract boolean isWhitespace(final int p0);
    
    abstract boolean isMirrored(final int p0);
    
    abstract boolean isJavaIdentifierStart(final int p0);
    
    abstract boolean isJavaIdentifierPart(final int p0);
    
    abstract boolean isUnicodeIdentifierStart(final int p0);
    
    abstract boolean isUnicodeIdentifierPart(final int p0);
    
    abstract boolean isIdentifierIgnorable(final int p0);
    
    abstract int toLowerCase(final int p0);
    
    abstract int toUpperCase(final int p0);
    
    abstract int toTitleCase(final int p0);
    
    abstract int digit(final int p0, final int p1);
    
    abstract int getNumericValue(final int p0);
    
    abstract byte getDirectionality(final int p0);
    
    int toUpperCaseEx(final int n) {
        return this.toUpperCase(n);
    }
    
    char[] toUpperCaseCharArray(final int n) {
        return null;
    }
    
    boolean isOtherLowercase(final int n) {
        return false;
    }
    
    boolean isOtherUppercase(final int n) {
        return false;
    }
    
    boolean isOtherAlphabetic(final int n) {
        return false;
    }
    
    boolean isIdeographic(final int n) {
        return false;
    }
    
    static final CharacterData of(final int n) {
        if (n >>> 8 == 0) {
            return CharacterDataLatin1.instance;
        }
        switch (n >>> 16) {
            case 0: {
                return CharacterData00.instance;
            }
            case 1: {
                return CharacterData01.instance;
            }
            case 2: {
                return CharacterData02.instance;
            }
            case 14: {
                return CharacterData0E.instance;
            }
            case 15:
            case 16: {
                return CharacterDataPrivateUse.instance;
            }
            default: {
                return CharacterDataUndefined.instance;
            }
        }
    }
}
