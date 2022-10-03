package com.me.devicemanagement.framework.server.dms;

import java.util.Collections;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Future;
import java.util.Queue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.logging.Logger;

public class DMSScheduleWorker
{
    private static final Logger LOGGER;
    private static DMSScheduleWorker worker;
    private final ScheduledThreadPoolExecutor service;
    private final Queue<Future<String>> runningTasks;
    private final ConcurrentMap<String, Long> runningTaskFeatureToCount;
    
    private DMSScheduleWorker() {
        int threadPoolSize;
        try {
            final String threadPoolString = DMSDownloadUtil.getInstance().getProductSpecificProps("dms.threadpool.size");
            threadPoolSize = (threadPoolString.isEmpty() ? 5 : Integer.parseInt(threadPoolString));
        }
        catch (final Exception ex) {
            DMSScheduleWorker.LOGGER.log(Level.WARNING, "Exception while initializing scheduler worker pool", ex);
            threadPoolSize = 5;
        }
        (this.service = (ScheduledThreadPoolExecutor)Executors.newScheduledThreadPool(threadPoolSize)).setMaximumPoolSize(threadPoolSize * 4);
        this.service.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardOldestPolicy());
        this.runningTasks = new LinkedBlockingDeque<Future<String>>(this.service.getMaximumPoolSize());
        this.runningTaskFeatureToCount = new ConcurrentHashMap<String, Long>();
    }
    
    public static DMSScheduleWorker getInstance() {
        if (DMSScheduleWorker.worker == null) {
            DMSScheduleWorker.worker = new DMSScheduleWorker();
        }
        return DMSScheduleWorker.worker;
    }
    
    public void runImmediately(final DMSTaskRunner task) {
        DMSScheduleWorker.LOGGER.log(Level.INFO, "Task has been triggered immediately for the following feature : " + task.getFeatureName());
        final Future<String> futureTask = this.service.submit((Callable<String>)task);
        this.runningTasks.add(futureTask);
        this.incrementCount(task.getFeatureName());
    }
    
    public void scheduleTask(final DMSTaskRunner task, final long delay, final TimeUnit unit) {
        DMSScheduleWorker.LOGGER.log(Level.INFO, "Task has been scheduled at " + new Date(unit.toMillis(delay)).toString() + " for the following feature : " + task.getFeatureName());
        final Future<String> futureTask = (Future<String>)this.service.schedule((Callable<Object>)task, delay, unit);
        this.runningTasks.add(futureTask);
        this.incrementCount(task.getFeatureName());
    }
    
    private void incrementCount(final String featureName) {
        this.runningTaskFeatureToCount.merge(featureName, 1L, Long::sum);
    }
    
    private void decrementCount(final String featureName, final Long count) {
        this.runningTaskFeatureToCount.compute(featureName, (key, value) -> (value == null || n > value) ? null : Long.valueOf(value - n));
    }
    
    private void doQueueCleanup() {
        final Map<String, Long> completedCount = this.runningTasks.stream().filter(Future::isDone).map(stringFuture -> {
            try {
                return (String)stringFuture.get();
            }
            catch (final Exception ex) {
                return "";
            }
        }).filter(string -> !string.isEmpty()).collect(Collectors.groupingBy(featureName -> featureName, Collectors.counting()));
        completedCount.forEach(this::decrementCount);
        this.runningTasks.removeIf(Future::isDone);
    }
    
    public Map<String, Long> getCurrentlyAliveTasks() {
        this.doQueueCleanup();
        return Collections.unmodifiableMap((Map<? extends String, ? extends Long>)this.runningTaskFeatureToCount);
    }
    
    private Future<String> removeAndReturnLongestRunningTask() {
        return this.runningTasks.poll();
    }
    
    public Future<String> returnLongestRunningTask() {
        return this.runningTasks.peek();
    }
    
    public boolean cancelLongestRunningTask() {
        boolean isCancelled = true;
        final Future<String> longestRunningTask = DMSScheduleWorker.worker.removeAndReturnLongestRunningTask();
        if (longestRunningTask != null) {
            isCancelled = longestRunningTask.cancel(Boolean.TRUE);
            DMSScheduleWorker.LOGGER.log(Level.SEVERE, "Tried to cancel longest running task : ");
        }
        return isCancelled;
    }
    
    static {
        LOGGER = Logger.getLogger("dms");
    }
}
