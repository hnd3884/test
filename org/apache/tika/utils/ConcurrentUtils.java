package org.apache.tika.utils;

import java.util.concurrent.FutureTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import org.apache.tika.parser.ParseContext;

public class ConcurrentUtils
{
    public static Future execute(final ParseContext context, final Runnable runnable) {
        Future future = null;
        final ExecutorService executorService = context.get(ExecutorService.class);
        if (executorService == null) {
            final FutureTask task = new FutureTask(runnable, null);
            final Thread thread = new Thread(task, "Tika Thread");
            thread.start();
            future = task;
        }
        else {
            future = executorService.submit(runnable);
        }
        return future;
    }
}
