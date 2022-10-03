package javax.swing;

import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.awt.event.ActionListener;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.lang.ref.WeakReference;
import java.beans.PropertyChangeEvent;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.LinkedBlockingQueue;
import sun.awt.AppContext;
import java.util.concurrent.ExecutorService;
import java.beans.PropertyChangeListener;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutionException;
import java.util.List;
import java.util.concurrent.Callable;
import sun.swing.AccumulativeRunnable;
import java.beans.PropertyChangeSupport;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;

public abstract class SwingWorker<T, V> implements RunnableFuture<T>
{
    private static final int MAX_WORKER_THREADS = 10;
    private volatile int progress;
    private volatile StateValue state;
    private final FutureTask<T> future;
    private final PropertyChangeSupport propertyChangeSupport;
    private AccumulativeRunnable<V> doProcess;
    private AccumulativeRunnable<Integer> doNotifyProgressChange;
    private final AccumulativeRunnable<Runnable> doSubmit;
    private static final Object DO_SUBMIT_KEY;
    
    public SwingWorker() {
        this.doSubmit = getDoSubmit();
        this.future = new FutureTask<T>(new Callable<T>() {
            @Override
            public T call() throws Exception {
                SwingWorker.this.setState(StateValue.STARTED);
                return SwingWorker.this.doInBackground();
            }
        }) {
            @Override
            protected void done() {
                SwingWorker.this.doneEDT();
                SwingWorker.this.setState(StateValue.DONE);
            }
        };
        this.state = StateValue.PENDING;
        this.propertyChangeSupport = new SwingWorkerPropertyChangeSupport(this);
        this.doProcess = null;
        this.doNotifyProgressChange = null;
    }
    
    protected abstract T doInBackground() throws Exception;
    
    @Override
    public final void run() {
        this.future.run();
    }
    
    @SafeVarargs
    protected final void publish(final V... array) {
        synchronized (this) {
            if (this.doProcess == null) {
                this.doProcess = new AccumulativeRunnable<V>() {
                    public void run(final List<V> list) {
                        SwingWorker.this.process(list);
                    }
                    
                    @Override
                    protected void submit() {
                        SwingWorker.this.doSubmit.add(this);
                    }
                };
            }
        }
        this.doProcess.add(array);
    }
    
    protected void process(final List<V> list) {
    }
    
    protected void done() {
    }
    
    protected final void setProgress(final int progress) {
        if (progress < 0 || progress > 100) {
            throw new IllegalArgumentException("the value should be from 0 to 100");
        }
        if (this.progress == progress) {
            return;
        }
        final int progress2 = this.progress;
        this.progress = progress;
        if (!this.getPropertyChangeSupport().hasListeners("progress")) {
            return;
        }
        synchronized (this) {
            if (this.doNotifyProgressChange == null) {
                this.doNotifyProgressChange = new AccumulativeRunnable<Integer>() {
                    public void run(final List<Integer> list) {
                        SwingWorker.this.firePropertyChange("progress", list.get(0), list.get(list.size() - 1));
                    }
                    
                    @Override
                    protected void submit() {
                        SwingWorker.this.doSubmit.add(this);
                    }
                };
            }
        }
        this.doNotifyProgressChange.add(progress2, progress);
    }
    
    public final int getProgress() {
        return this.progress;
    }
    
    public final void execute() {
        getWorkersExecutorService().execute(this);
    }
    
    @Override
    public final boolean cancel(final boolean b) {
        return this.future.cancel(b);
    }
    
    @Override
    public final boolean isCancelled() {
        return this.future.isCancelled();
    }
    
    @Override
    public final boolean isDone() {
        return this.future.isDone();
    }
    
    @Override
    public final T get() throws InterruptedException, ExecutionException {
        return this.future.get();
    }
    
    @Override
    public final T get(final long n, final TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
        return this.future.get(n, timeUnit);
    }
    
    public final void addPropertyChangeListener(final PropertyChangeListener propertyChangeListener) {
        this.getPropertyChangeSupport().addPropertyChangeListener(propertyChangeListener);
    }
    
    public final void removePropertyChangeListener(final PropertyChangeListener propertyChangeListener) {
        this.getPropertyChangeSupport().removePropertyChangeListener(propertyChangeListener);
    }
    
    public final void firePropertyChange(final String s, final Object o, final Object o2) {
        this.getPropertyChangeSupport().firePropertyChange(s, o, o2);
    }
    
    public final PropertyChangeSupport getPropertyChangeSupport() {
        return this.propertyChangeSupport;
    }
    
    public final StateValue getState() {
        if (this.isDone()) {
            return StateValue.DONE;
        }
        return this.state;
    }
    
    private void setState(final StateValue state) {
        this.firePropertyChange("state", this.state, this.state = state);
    }
    
    private void doneEDT() {
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                SwingWorker.this.done();
            }
        };
        if (SwingUtilities.isEventDispatchThread()) {
            runnable.run();
        }
        else {
            this.doSubmit.add(runnable);
        }
    }
    
    private static synchronized ExecutorService getWorkersExecutorService() {
        final AppContext appContext = AppContext.getAppContext();
        ExecutorService executorService = (ExecutorService)appContext.get(SwingWorker.class);
        if (executorService == null) {
            executorService = new ThreadPoolExecutor(10, 10, 10L, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>(), new ThreadFactory() {
                final ThreadFactory defaultFactory = Executors.defaultThreadFactory();
                
                @Override
                public Thread newThread(final Runnable runnable) {
                    final Thread thread = this.defaultFactory.newThread(runnable);
                    thread.setName("SwingWorker-" + thread.getName());
                    thread.setDaemon(true);
                    return thread;
                }
            });
            appContext.put(SwingWorker.class, executorService);
            appContext.addPropertyChangeListener("disposed", new PropertyChangeListener() {
                @Override
                public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
                    if (propertyChangeEvent.getNewValue()) {
                        final ExecutorService executorService = new WeakReference<ExecutorService>(executorService).get();
                        if (executorService != null) {
                            AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
                                @Override
                                public Void run() {
                                    executorService.shutdown();
                                    return null;
                                }
                            });
                        }
                    }
                }
            });
        }
        return executorService;
    }
    
    private static AccumulativeRunnable<Runnable> getDoSubmit() {
        synchronized (SwingWorker.DO_SUBMIT_KEY) {
            final AppContext appContext = AppContext.getAppContext();
            Object value = appContext.get(SwingWorker.DO_SUBMIT_KEY);
            if (value == null) {
                value = new DoSubmitAccumulativeRunnable();
                appContext.put(SwingWorker.DO_SUBMIT_KEY, value);
            }
            return (AccumulativeRunnable<Runnable>)value;
        }
    }
    
    static {
        DO_SUBMIT_KEY = new StringBuilder("doSubmit");
    }
    
    public enum StateValue
    {
        PENDING, 
        STARTED, 
        DONE;
    }
    
    private static class DoSubmitAccumulativeRunnable extends AccumulativeRunnable<Runnable> implements ActionListener
    {
        private static final int DELAY = 33;
        
        @Override
        protected void run(final List<Runnable> list) {
            final Iterator<Runnable> iterator = list.iterator();
            while (iterator.hasNext()) {
                iterator.next().run();
            }
        }
        
        @Override
        protected void submit() {
            final Timer timer = new Timer(33, this);
            timer.setRepeats(false);
            timer.start();
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            this.run();
        }
    }
    
    private class SwingWorkerPropertyChangeSupport extends PropertyChangeSupport
    {
        SwingWorkerPropertyChangeSupport(final Object o) {
            super(o);
        }
        
        @Override
        public void firePropertyChange(final PropertyChangeEvent propertyChangeEvent) {
            if (SwingUtilities.isEventDispatchThread()) {
                super.firePropertyChange(propertyChangeEvent);
            }
            else {
                SwingWorker.this.doSubmit.add(new Runnable() {
                    @Override
                    public void run() {
                        SwingWorkerPropertyChangeSupport.this.firePropertyChange(propertyChangeEvent);
                    }
                });
            }
        }
    }
}
