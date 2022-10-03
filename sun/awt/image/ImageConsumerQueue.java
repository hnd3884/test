package sun.awt.image;

import java.awt.image.ImageConsumer;

class ImageConsumerQueue
{
    ImageConsumerQueue next;
    ImageConsumer consumer;
    boolean interested;
    Object securityContext;
    boolean secure;
    
    static ImageConsumerQueue removeConsumer(ImageConsumerQueue next, final ImageConsumer imageConsumer, final boolean interested) {
        ImageConsumerQueue imageConsumerQueue = null;
        for (ImageConsumerQueue next2 = next; next2 != null; next2 = next2.next) {
            if (next2.consumer == imageConsumer) {
                if (imageConsumerQueue == null) {
                    next = next2.next;
                }
                else {
                    imageConsumerQueue.next = next2.next;
                }
                next2.interested = interested;
                break;
            }
            imageConsumerQueue = next2;
        }
        return next;
    }
    
    static boolean isConsumer(final ImageConsumerQueue imageConsumerQueue, final ImageConsumer imageConsumer) {
        for (ImageConsumerQueue next = imageConsumerQueue; next != null; next = next.next) {
            if (next.consumer == imageConsumer) {
                return true;
            }
        }
        return false;
    }
    
    ImageConsumerQueue(final InputStreamImageSource inputStreamImageSource, final ImageConsumer consumer) {
        this.consumer = consumer;
        this.interested = true;
        if (consumer instanceof ImageRepresentation) {
            if (((ImageRepresentation)consumer).image.source != inputStreamImageSource) {
                throw new SecurityException("ImageRep added to wrong image source");
            }
            this.secure = true;
        }
        else {
            final SecurityManager securityManager = System.getSecurityManager();
            if (securityManager != null) {
                this.securityContext = securityManager.getSecurityContext();
            }
            else {
                this.securityContext = null;
            }
        }
    }
    
    @Override
    public String toString() {
        return "[" + this.consumer + ", " + (this.interested ? "" : "not ") + "interested" + ((this.securityContext != null) ? (", " + this.securityContext) : "") + "]";
    }
}
