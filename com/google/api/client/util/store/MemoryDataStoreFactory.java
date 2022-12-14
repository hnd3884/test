package com.google.api.client.util.store;

import java.io.IOException;
import java.io.Serializable;

public class MemoryDataStoreFactory extends AbstractDataStoreFactory
{
    @Override
    protected <V extends Serializable> DataStore<V> createDataStore(final String id) throws IOException {
        return new MemoryDataStore<V>(this, id);
    }
    
    public static MemoryDataStoreFactory getDefaultInstance() {
        return InstanceHolder.INSTANCE;
    }
    
    static class InstanceHolder
    {
        static final MemoryDataStoreFactory INSTANCE;
        
        static {
            INSTANCE = new MemoryDataStoreFactory();
        }
    }
    
    static class MemoryDataStore<V extends Serializable> extends AbstractMemoryDataStore<V>
    {
        MemoryDataStore(final MemoryDataStoreFactory dataStore, final String id) {
            super(dataStore, id);
        }
        
        @Override
        public MemoryDataStoreFactory getDataStoreFactory() {
            return (MemoryDataStoreFactory)super.getDataStoreFactory();
        }
    }
}
