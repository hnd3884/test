package org.apache.tika.concurrent;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadPoolExecutor;

public class SimpleThreadPoolExecutor extends ThreadPoolExecutor implements ConfigurableThreadPoolExecutor
{
    public SimpleThreadPoolExecutor() {
        super(1, 2, 0L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), r -> new Thread(r, "Tika Executor Thread"));
    }
}
