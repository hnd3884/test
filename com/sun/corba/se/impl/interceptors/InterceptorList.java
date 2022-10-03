package com.sun.corba.se.impl.interceptors;

import org.omg.PortableInterceptor.IORInterceptor;
import org.omg.PortableInterceptor.ServerRequestInterceptor;
import org.omg.PortableInterceptor.ClientRequestInterceptor;
import java.util.Iterator;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import java.lang.reflect.Array;
import org.omg.PortableInterceptor.ORBInitInfoPackage.DuplicateName;
import org.omg.PortableInterceptor.Interceptor;
import com.sun.corba.se.impl.logging.InterceptorsSystemException;

public class InterceptorList
{
    static final int INTERCEPTOR_TYPE_CLIENT = 0;
    static final int INTERCEPTOR_TYPE_SERVER = 1;
    static final int INTERCEPTOR_TYPE_IOR = 2;
    static final int NUM_INTERCEPTOR_TYPES = 3;
    static final Class[] classTypes;
    private boolean locked;
    private InterceptorsSystemException wrapper;
    private Interceptor[][] interceptors;
    
    InterceptorList(final InterceptorsSystemException wrapper) {
        this.locked = false;
        this.interceptors = new Interceptor[3][];
        this.wrapper = wrapper;
        this.initInterceptorArrays();
    }
    
    void register_interceptor(final Interceptor interceptor, final int n) throws DuplicateName {
        if (this.locked) {
            throw this.wrapper.interceptorListLocked();
        }
        final String name = interceptor.name();
        final boolean equals = name.equals("");
        boolean b = false;
        final Interceptor[] array = this.interceptors[n];
        if (!equals) {
            for (int length = array.length, i = 0; i < length; ++i) {
                if (array[i].name().equals(name)) {
                    b = true;
                    break;
                }
            }
        }
        if (!b) {
            this.growInterceptorArray(n);
            this.interceptors[n][this.interceptors[n].length - 1] = interceptor;
            return;
        }
        throw new DuplicateName(name);
    }
    
    void lock() {
        this.locked = true;
    }
    
    Interceptor[] getInterceptors(final int n) {
        return this.interceptors[n];
    }
    
    boolean hasInterceptorsOfType(final int n) {
        return this.interceptors[n].length > 0;
    }
    
    private void initInterceptorArrays() {
        for (int i = 0; i < 3; ++i) {
            this.interceptors[i] = (Interceptor[])Array.newInstance(InterceptorList.classTypes[i], 0);
        }
    }
    
    private void growInterceptorArray(final int n) {
        final Class clazz = InterceptorList.classTypes[n];
        final int length = this.interceptors[n].length;
        final Interceptor[] array = (Interceptor[])Array.newInstance(clazz, length + 1);
        System.arraycopy(this.interceptors[n], 0, array, 0, length);
        this.interceptors[n] = array;
    }
    
    void destroyAll() {
        for (int length = this.interceptors.length, i = 0; i < length; ++i) {
            for (int length2 = this.interceptors[i].length, j = 0; j < length2; ++j) {
                this.interceptors[i][j].destroy();
            }
        }
    }
    
    void sortInterceptors() {
        List list = null;
        List<Interceptor> list2 = null;
        for (int length = this.interceptors.length, i = 0; i < length; ++i) {
            final int length2 = this.interceptors[i].length;
            if (length2 > 0) {
                list = new ArrayList<Interceptor>();
                list2 = new ArrayList<Interceptor>();
            }
            for (int j = 0; j < length2; ++j) {
                final Interceptor interceptor = this.interceptors[i][j];
                if (interceptor instanceof Comparable) {
                    list.add(interceptor);
                }
                else {
                    list2.add(interceptor);
                }
            }
            if (length2 > 0 && list.size() > 0) {
                Collections.sort((List<Comparable>)list);
                final Iterator iterator = list.iterator();
                final Iterator<Interceptor> iterator2 = list2.iterator();
                for (int k = 0; k < length2; ++k) {
                    if (iterator.hasNext()) {
                        this.interceptors[i][k] = (Interceptor)iterator.next();
                    }
                    else {
                        if (!iterator2.hasNext()) {
                            throw this.wrapper.sortSizeMismatch();
                        }
                        this.interceptors[i][k] = iterator2.next();
                    }
                }
            }
        }
    }
    
    static {
        classTypes = new Class[] { ClientRequestInterceptor.class, ServerRequestInterceptor.class, IORInterceptor.class };
    }
}
