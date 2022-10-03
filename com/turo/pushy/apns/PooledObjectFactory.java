package com.turo.pushy.apns;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;

interface PooledObjectFactory<T>
{
    Future<T> create(final Promise<T> p0);
    
    Future<Void> destroy(final T p0, final Promise<Void> p1);
}
