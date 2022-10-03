package org.apache.tika.pipes.fetcher;

import java.util.Objects;
import java.io.Serializable;

public class FetchKey implements Serializable
{
    private static final long serialVersionUID = -3861669115439125268L;
    private String fetcherName;
    private String fetchKey;
    
    public FetchKey() {
    }
    
    public FetchKey(final String fetcherName, final String fetchKey) {
        this.fetcherName = fetcherName;
        this.fetchKey = fetchKey;
    }
    
    public String getFetcherName() {
        return this.fetcherName;
    }
    
    public String getFetchKey() {
        return this.fetchKey;
    }
    
    @Override
    public String toString() {
        return "FetcherKeyPair{fetcherName='" + this.fetcherName + '\'' + ", fetchKey='" + this.fetchKey + '\'' + '}';
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final FetchKey fetchKey = (FetchKey)o;
        return Objects.equals(this.fetcherName, fetchKey.fetcherName) && Objects.equals(this.fetchKey, fetchKey.fetchKey);
    }
    
    @Override
    public int hashCode() {
        int result = (this.fetcherName != null) ? this.fetcherName.hashCode() : 0;
        result = 31 * result + ((this.fetchKey != null) ? this.fetchKey.hashCode() : 0);
        return result;
    }
}
