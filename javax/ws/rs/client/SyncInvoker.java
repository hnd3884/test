package javax.ws.rs.client;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

public interface SyncInvoker
{
    Response get();
    
     <T> T get(final Class<T> p0);
    
     <T> T get(final GenericType<T> p0);
    
    Response put(final Entity<?> p0);
    
     <T> T put(final Entity<?> p0, final Class<T> p1);
    
     <T> T put(final Entity<?> p0, final GenericType<T> p1);
    
    Response post(final Entity<?> p0);
    
     <T> T post(final Entity<?> p0, final Class<T> p1);
    
     <T> T post(final Entity<?> p0, final GenericType<T> p1);
    
    Response delete();
    
     <T> T delete(final Class<T> p0);
    
     <T> T delete(final GenericType<T> p0);
    
    Response head();
    
    Response options();
    
     <T> T options(final Class<T> p0);
    
     <T> T options(final GenericType<T> p0);
    
    Response trace();
    
     <T> T trace(final Class<T> p0);
    
     <T> T trace(final GenericType<T> p0);
    
    Response method(final String p0);
    
     <T> T method(final String p0, final Class<T> p1);
    
     <T> T method(final String p0, final GenericType<T> p1);
    
    Response method(final String p0, final Entity<?> p1);
    
     <T> T method(final String p0, final Entity<?> p1, final Class<T> p2);
    
     <T> T method(final String p0, final Entity<?> p1, final GenericType<T> p2);
}
