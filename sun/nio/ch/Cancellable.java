package sun.nio.ch;

interface Cancellable
{
    void onCancel(final PendingFuture<?, ?> p0);
}
