package java.awt.image;

public interface ImageProducer
{
    void addConsumer(final ImageConsumer p0);
    
    boolean isConsumer(final ImageConsumer p0);
    
    void removeConsumer(final ImageConsumer p0);
    
    void startProduction(final ImageConsumer p0);
    
    void requestTopDownLeftRightResend(final ImageConsumer p0);
}
