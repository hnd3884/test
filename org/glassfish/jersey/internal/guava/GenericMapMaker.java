package org.glassfish.jersey.internal.guava;

@Deprecated
abstract class GenericMapMaker<K0, V0>
{
     <K extends K0, V extends V0> MapMaker.RemovalListener<K, V> getRemovalListener() {
        return (MapMaker.RemovalListener<K, V>)NullListener.INSTANCE;
    }
    
    enum NullListener implements MapMaker.RemovalListener<Object, Object>
    {
        INSTANCE;
        
        @Override
        public void onRemoval(final MapMaker.RemovalNotification<Object, Object> notification) {
        }
    }
}
