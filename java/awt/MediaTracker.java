package java.awt;

import sun.awt.image.MultiResolutionToolkitImage;
import java.io.Serializable;

public class MediaTracker implements Serializable
{
    Component target;
    MediaEntry head;
    private static final long serialVersionUID = -483174189758638095L;
    public static final int LOADING = 1;
    public static final int ABORTED = 2;
    public static final int ERRORED = 4;
    public static final int COMPLETE = 8;
    static final int DONE = 14;
    
    public MediaTracker(final Component target) {
        this.target = target;
    }
    
    public void addImage(final Image image, final int n) {
        this.addImage(image, n, -1, -1);
    }
    
    public synchronized void addImage(final Image image, final int n, final int n2, final int n3) {
        this.addImageImpl(image, n, n2, n3);
        final Image resolutionVariant = getResolutionVariant(image);
        if (resolutionVariant != null) {
            this.addImageImpl(resolutionVariant, n, (n2 == -1) ? -1 : (2 * n2), (n3 == -1) ? -1 : (2 * n3));
        }
    }
    
    private void addImageImpl(final Image image, final int n, final int n2, final int n3) {
        this.head = MediaEntry.insert(this.head, new ImageMediaEntry(this, image, n, n2, n3));
    }
    
    public boolean checkAll() {
        return this.checkAll(false, true);
    }
    
    public boolean checkAll(final boolean b) {
        return this.checkAll(b, true);
    }
    
    private synchronized boolean checkAll(final boolean b, final boolean b2) {
        MediaEntry mediaEntry = this.head;
        boolean b3 = true;
        while (mediaEntry != null) {
            if ((mediaEntry.getStatus(b, b2) & 0xE) == 0x0) {
                b3 = false;
            }
            mediaEntry = mediaEntry.next;
        }
        return b3;
    }
    
    public synchronized boolean isErrorAny() {
        for (MediaEntry mediaEntry = this.head; mediaEntry != null; mediaEntry = mediaEntry.next) {
            if ((mediaEntry.getStatus(false, true) & 0x4) != 0x0) {
                return true;
            }
        }
        return false;
    }
    
    public synchronized Object[] getErrorsAny() {
        MediaEntry mediaEntry = this.head;
        int n = 0;
        while (mediaEntry != null) {
            if ((mediaEntry.getStatus(false, true) & 0x4) != 0x0) {
                ++n;
            }
            mediaEntry = mediaEntry.next;
        }
        if (n == 0) {
            return null;
        }
        final Object[] array = new Object[n];
        MediaEntry mediaEntry2 = this.head;
        int n2 = 0;
        while (mediaEntry2 != null) {
            if ((mediaEntry2.getStatus(false, false) & 0x4) != 0x0) {
                array[n2++] = mediaEntry2.getMedia();
            }
            mediaEntry2 = mediaEntry2.next;
        }
        return array;
    }
    
    public void waitForAll() throws InterruptedException {
        this.waitForAll(0L);
    }
    
    public synchronized boolean waitForAll(final long n) throws InterruptedException {
        final long n2 = System.currentTimeMillis() + n;
        boolean b = true;
        while (true) {
            final int statusAll = this.statusAll(b, b);
            if ((statusAll & 0x1) == 0x0) {
                return statusAll == 8;
            }
            b = false;
            long n3;
            if (n == 0L) {
                n3 = 0L;
            }
            else {
                n3 = n2 - System.currentTimeMillis();
                if (n3 <= 0L) {
                    return false;
                }
            }
            this.wait(n3);
        }
    }
    
    public int statusAll(final boolean b) {
        return this.statusAll(b, true);
    }
    
    private synchronized int statusAll(final boolean b, final boolean b2) {
        MediaEntry mediaEntry = this.head;
        int n = 0;
        while (mediaEntry != null) {
            n |= mediaEntry.getStatus(b, b2);
            mediaEntry = mediaEntry.next;
        }
        return n;
    }
    
    public boolean checkID(final int n) {
        return this.checkID(n, false, true);
    }
    
    public boolean checkID(final int n, final boolean b) {
        return this.checkID(n, b, true);
    }
    
    private synchronized boolean checkID(final int n, final boolean b, final boolean b2) {
        MediaEntry mediaEntry = this.head;
        boolean b3 = true;
        while (mediaEntry != null) {
            if (mediaEntry.getID() == n && (mediaEntry.getStatus(b, b2) & 0xE) == 0x0) {
                b3 = false;
            }
            mediaEntry = mediaEntry.next;
        }
        return b3;
    }
    
    public synchronized boolean isErrorID(final int n) {
        for (MediaEntry mediaEntry = this.head; mediaEntry != null; mediaEntry = mediaEntry.next) {
            if (mediaEntry.getID() == n && (mediaEntry.getStatus(false, true) & 0x4) != 0x0) {
                return true;
            }
        }
        return false;
    }
    
    public synchronized Object[] getErrorsID(final int n) {
        MediaEntry mediaEntry = this.head;
        int n2 = 0;
        while (mediaEntry != null) {
            if (mediaEntry.getID() == n && (mediaEntry.getStatus(false, true) & 0x4) != 0x0) {
                ++n2;
            }
            mediaEntry = mediaEntry.next;
        }
        if (n2 == 0) {
            return null;
        }
        final Object[] array = new Object[n2];
        MediaEntry mediaEntry2 = this.head;
        int n3 = 0;
        while (mediaEntry2 != null) {
            if (mediaEntry2.getID() == n && (mediaEntry2.getStatus(false, false) & 0x4) != 0x0) {
                array[n3++] = mediaEntry2.getMedia();
            }
            mediaEntry2 = mediaEntry2.next;
        }
        return array;
    }
    
    public void waitForID(final int n) throws InterruptedException {
        this.waitForID(n, 0L);
    }
    
    public synchronized boolean waitForID(final int n, final long n2) throws InterruptedException {
        final long n3 = System.currentTimeMillis() + n2;
        boolean b = true;
        while (true) {
            final int statusID = this.statusID(n, b, b);
            if ((statusID & 0x1) == 0x0) {
                return statusID == 8;
            }
            b = false;
            long n4;
            if (n2 == 0L) {
                n4 = 0L;
            }
            else {
                n4 = n3 - System.currentTimeMillis();
                if (n4 <= 0L) {
                    return false;
                }
            }
            this.wait(n4);
        }
    }
    
    public int statusID(final int n, final boolean b) {
        return this.statusID(n, b, true);
    }
    
    private synchronized int statusID(final int n, final boolean b, final boolean b2) {
        MediaEntry mediaEntry = this.head;
        int n2 = 0;
        while (mediaEntry != null) {
            if (mediaEntry.getID() == n) {
                n2 |= mediaEntry.getStatus(b, b2);
            }
            mediaEntry = mediaEntry.next;
        }
        return n2;
    }
    
    public synchronized void removeImage(final Image image) {
        this.removeImageImpl(image);
        final Image resolutionVariant = getResolutionVariant(image);
        if (resolutionVariant != null) {
            this.removeImageImpl(resolutionVariant);
        }
        this.notifyAll();
    }
    
    private void removeImageImpl(final Image image) {
        MediaEntry head = this.head;
        MediaEntry mediaEntry = null;
        while (head != null) {
            final MediaEntry next = head.next;
            if (head.getMedia() == image) {
                if (mediaEntry == null) {
                    this.head = next;
                }
                else {
                    mediaEntry.next = next;
                }
                head.cancel();
            }
            else {
                mediaEntry = head;
            }
            head = next;
        }
    }
    
    public synchronized void removeImage(final Image image, final int n) {
        this.removeImageImpl(image, n);
        final Image resolutionVariant = getResolutionVariant(image);
        if (resolutionVariant != null) {
            this.removeImageImpl(resolutionVariant, n);
        }
        this.notifyAll();
    }
    
    private void removeImageImpl(final Image image, final int n) {
        MediaEntry head = this.head;
        MediaEntry mediaEntry = null;
        while (head != null) {
            final MediaEntry next = head.next;
            if (head.getID() == n && head.getMedia() == image) {
                if (mediaEntry == null) {
                    this.head = next;
                }
                else {
                    mediaEntry.next = next;
                }
                head.cancel();
            }
            else {
                mediaEntry = head;
            }
            head = next;
        }
    }
    
    public synchronized void removeImage(final Image image, final int n, final int n2, final int n3) {
        this.removeImageImpl(image, n, n2, n3);
        final Image resolutionVariant = getResolutionVariant(image);
        if (resolutionVariant != null) {
            this.removeImageImpl(resolutionVariant, n, (n2 == -1) ? -1 : (2 * n2), (n3 == -1) ? -1 : (2 * n3));
        }
        this.notifyAll();
    }
    
    private void removeImageImpl(final Image image, final int n, final int n2, final int n3) {
        MediaEntry head = this.head;
        MediaEntry mediaEntry = null;
        while (head != null) {
            final MediaEntry next = head.next;
            if (head.getID() == n && head instanceof ImageMediaEntry && ((ImageMediaEntry)head).matches(image, n2, n3)) {
                if (mediaEntry == null) {
                    this.head = next;
                }
                else {
                    mediaEntry.next = next;
                }
                head.cancel();
            }
            else {
                mediaEntry = head;
            }
            head = next;
        }
    }
    
    synchronized void setDone() {
        this.notifyAll();
    }
    
    private static Image getResolutionVariant(final Image image) {
        if (image instanceof MultiResolutionToolkitImage) {
            return ((MultiResolutionToolkitImage)image).getResolutionVariant();
        }
        return null;
    }
}
