package javax.ws.rs.client;

import javax.ws.rs.core.GenericType;

public interface RxInvoker<T>
{
    T get();
    
     <R> T get(final Class<R> p0);
    
     <R> T get(final GenericType<R> p0);
    
    T put(final Entity<?> p0);
    
     <R> T put(final Entity<?> p0, final Class<R> p1);
    
     <R> T put(final Entity<?> p0, final GenericType<R> p1);
    
    T post(final Entity<?> p0);
    
     <R> T post(final Entity<?> p0, final Class<R> p1);
    
     <R> T post(final Entity<?> p0, final GenericType<R> p1);
    
    T delete();
    
     <R> T delete(final Class<R> p0);
    
     <R> T delete(final GenericType<R> p0);
    
    T head();
    
    T options();
    
     <R> T options(final Class<R> p0);
    
     <R> T options(final GenericType<R> p0);
    
    T trace();
    
     <R> T trace(final Class<R> p0);
    
     <R> T trace(final GenericType<R> p0);
    
    T method(final String p0);
    
     <R> T method(final String p0, final Class<R> p1);
    
     <R> T method(final String p0, final GenericType<R> p1);
    
    T method(final String p0, final Entity<?> p1);
    
     <R> T method(final String p0, final Entity<?> p1, final Class<R> p2);
    
     <R> T method(final String p0, final Entity<?> p1, final GenericType<R> p2);
}
