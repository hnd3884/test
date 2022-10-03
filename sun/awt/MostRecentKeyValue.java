package sun.awt;

final class MostRecentKeyValue
{
    Object key;
    Object value;
    
    MostRecentKeyValue(final Object key, final Object value) {
        this.key = key;
        this.value = value;
    }
    
    void setPair(final Object key, final Object value) {
        this.key = key;
        this.value = value;
    }
}
