package javax.imageio;

import javax.imageio.metadata.IIOMetadata;

public interface ImageTranscoder
{
    IIOMetadata convertStreamMetadata(final IIOMetadata p0, final ImageWriteParam p1);
    
    IIOMetadata convertImageMetadata(final IIOMetadata p0, final ImageTypeSpecifier p1, final ImageWriteParam p2);
}
