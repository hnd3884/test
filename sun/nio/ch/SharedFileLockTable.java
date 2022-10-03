package sun.nio.ch;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.nio.channels.OverlappingFileLockException;
import java.util.ArrayList;
import java.io.IOException;
import java.io.FileDescriptor;
import java.nio.channels.Channel;
import java.nio.channels.FileLock;
import java.lang.ref.ReferenceQueue;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

class SharedFileLockTable extends FileLockTable
{
    private static ConcurrentHashMap<FileKey, List<FileLockReference>> lockMap;
    private static ReferenceQueue<FileLock> queue;
    private final Channel channel;
    private final FileKey fileKey;
    
    SharedFileLockTable(final Channel channel, final FileDescriptor fileDescriptor) throws IOException {
        this.channel = channel;
        this.fileKey = FileKey.create(fileDescriptor);
    }
    
    @Override
    public void add(final FileLock fileLock) throws OverlappingFileLockException {
        List<FileLockReference> list = SharedFileLockTable.lockMap.get(this.fileKey);
        while (true) {
            if (list == null) {
                final ArrayList<FileLockReference> list2 = new ArrayList<FileLockReference>(2);
                final List<FileLockReference> list3;
                synchronized (list2) {
                    list3 = SharedFileLockTable.lockMap.putIfAbsent(this.fileKey, list2);
                    if (list3 == null) {
                        list2.add(new FileLockReference(fileLock, SharedFileLockTable.queue, this.fileKey));
                        break;
                    }
                }
                list = list3;
            }
            synchronized (list) {
                final List list4 = SharedFileLockTable.lockMap.get(this.fileKey);
                if (list == list4) {
                    this.checkList(list, fileLock.position(), fileLock.size());
                    list.add(new FileLockReference(fileLock, SharedFileLockTable.queue, this.fileKey));
                    break;
                }
                list = list4;
            }
        }
        this.removeStaleEntries();
    }
    
    private void removeKeyIfEmpty(final FileKey fileKey, final List<FileLockReference> list) {
        assert Thread.holdsLock(list);
        assert SharedFileLockTable.lockMap.get(fileKey) == list;
        if (list.isEmpty()) {
            SharedFileLockTable.lockMap.remove(fileKey);
        }
    }
    
    @Override
    public void remove(final FileLock fileLock) {
        assert fileLock != null;
        final List list = SharedFileLockTable.lockMap.get(this.fileKey);
        if (list == null) {
            return;
        }
        synchronized (list) {
            int i = 0;
            while (i < list.size()) {
                final FileLockReference fileLockReference = (FileLockReference)list.get(i);
                final FileLock fileLock2 = fileLockReference.get();
                if (fileLock2 == fileLock) {
                    assert fileLock2 != null && fileLock2.acquiredBy() == this.channel;
                    fileLockReference.clear();
                    list.remove(i);
                    break;
                }
                else {
                    ++i;
                }
            }
        }
    }
    
    @Override
    public List<FileLock> removeAll() {
        final ArrayList list = new ArrayList();
        final List list2 = SharedFileLockTable.lockMap.get(this.fileKey);
        if (list2 != null) {
            synchronized (list2) {
                int i = 0;
                while (i < list2.size()) {
                    final FileLockReference fileLockReference = (FileLockReference)list2.get(i);
                    final FileLock fileLock = fileLockReference.get();
                    if (fileLock != null && fileLock.acquiredBy() == this.channel) {
                        fileLockReference.clear();
                        list2.remove(i);
                        list.add(fileLock);
                    }
                    else {
                        ++i;
                    }
                }
                this.removeKeyIfEmpty(this.fileKey, list2);
            }
        }
        return list;
    }
    
    @Override
    public void replace(final FileLock fileLock, final FileLock fileLock2) {
        final List list = SharedFileLockTable.lockMap.get(this.fileKey);
        assert list != null;
        synchronized (list) {
            for (int i = 0; i < list.size(); ++i) {
                final FileLockReference fileLockReference = (FileLockReference)list.get(i);
                if (fileLockReference.get() == fileLock) {
                    fileLockReference.clear();
                    list.set(i, new FileLockReference(fileLock2, SharedFileLockTable.queue, this.fileKey));
                    break;
                }
            }
        }
    }
    
    private void checkList(final List<FileLockReference> list, final long n, final long n2) throws OverlappingFileLockException {
        assert Thread.holdsLock(list);
        final Iterator<FileLockReference> iterator = list.iterator();
        while (iterator.hasNext()) {
            final FileLock fileLock = iterator.next().get();
            if (fileLock != null && fileLock.overlaps(n, n2)) {
                throw new OverlappingFileLockException();
            }
        }
    }
    
    private void removeStaleEntries() {
        FileLockReference fileLockReference;
        while ((fileLockReference = (FileLockReference)SharedFileLockTable.queue.poll()) != null) {
            final FileKey fileKey = fileLockReference.fileKey();
            final List list = SharedFileLockTable.lockMap.get(fileKey);
            if (list != null) {
                synchronized (list) {
                    list.remove(fileLockReference);
                    this.removeKeyIfEmpty(fileKey, list);
                }
            }
        }
    }
    
    static {
        SharedFileLockTable.lockMap = new ConcurrentHashMap<FileKey, List<FileLockReference>>();
        SharedFileLockTable.queue = new ReferenceQueue<FileLock>();
    }
    
    private static class FileLockReference extends WeakReference<FileLock>
    {
        private FileKey fileKey;
        
        FileLockReference(final FileLock fileLock, final ReferenceQueue<FileLock> referenceQueue, final FileKey fileKey) {
            super(fileLock, referenceQueue);
            this.fileKey = fileKey;
        }
        
        FileKey fileKey() {
            return this.fileKey;
        }
    }
}
