package java.text;

final class RuleBasedCollationKey extends CollationKey
{
    private String key;
    
    @Override
    public int compareTo(final CollationKey collationKey) {
        final int compareTo = this.key.compareTo(((RuleBasedCollationKey)collationKey).key);
        if (compareTo <= -1) {
            return -1;
        }
        if (compareTo >= 1) {
            return 1;
        }
        return 0;
    }
    
    @Override
    public boolean equals(final Object o) {
        return this == o || (o != null && this.getClass().equals(o.getClass()) && this.key.equals(((RuleBasedCollationKey)o).key));
    }
    
    @Override
    public int hashCode() {
        return this.key.hashCode();
    }
    
    @Override
    public byte[] toByteArray() {
        final char[] charArray = this.key.toCharArray();
        final byte[] array = new byte[2 * charArray.length];
        int n = 0;
        for (int i = 0; i < charArray.length; ++i) {
            array[n++] = (byte)(charArray[i] >>> 8);
            array[n++] = (byte)(charArray[i] & '\u00ff');
        }
        return array;
    }
    
    RuleBasedCollationKey(final String s, final String key) {
        super(s);
        this.key = null;
        this.key = key;
    }
}
