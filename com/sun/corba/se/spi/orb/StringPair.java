package com.sun.corba.se.spi.orb;

public class StringPair
{
    private String first;
    private String second;
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StringPair)) {
            return false;
        }
        final StringPair stringPair = (StringPair)o;
        return this.first.equals(stringPair.first) && this.second.equals(stringPair.second);
    }
    
    @Override
    public int hashCode() {
        return this.first.hashCode() ^ this.second.hashCode();
    }
    
    public StringPair(final String first, final String second) {
        this.first = first;
        this.second = second;
    }
    
    public String getFirst() {
        return this.first;
    }
    
    public String getSecond() {
        return this.second;
    }
}
