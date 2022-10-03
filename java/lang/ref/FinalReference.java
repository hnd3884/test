package java.lang.ref;

class FinalReference<T> extends Reference<T>
{
    public FinalReference(final T t, final ReferenceQueue<? super T> referenceQueue) {
        super(t, referenceQueue);
    }
}
