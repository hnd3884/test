package org.glassfish.jersey.message.internal;

import javax.ws.rs.WebApplicationException;
import javax.imageio.ImageWriter;
import java.io.OutputStream;
import javax.imageio.stream.ImageInputStream;
import java.util.Iterator;
import javax.imageio.ImageReader;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.io.InputStream;
import javax.ws.rs.core.MultivaluedMap;
import java.awt.image.BufferedImage;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.core.MediaType;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import java.awt.image.RenderedImage;

@Produces({ "image/*" })
@Consumes({ "image/*", "application/octet-stream" })
@Singleton
public final class RenderedImageProvider extends AbstractMessageReaderWriterProvider<RenderedImage>
{
    private static final MediaType IMAGE_MEDIA_TYPE;
    
    public boolean isReadable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return RenderedImage.class == type || BufferedImage.class == type;
    }
    
    public RenderedImage readFrom(final Class<RenderedImage> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, String> httpHeaders, final InputStream entityStream) throws IOException {
        if (!RenderedImageProvider.IMAGE_MEDIA_TYPE.isCompatible(mediaType)) {
            return ImageIO.read(entityStream);
        }
        final Iterator<ImageReader> readers = ImageIO.getImageReadersByMIMEType(mediaType.toString());
        if (!readers.hasNext()) {
            throw new IOException("The image-based media type " + mediaType + "is not supported for reading");
        }
        final ImageReader reader = readers.next();
        final ImageInputStream in = ImageIO.createImageInputStream(entityStream);
        reader.setInput(in, true, true);
        final BufferedImage bi = reader.read(0, reader.getDefaultReadParam());
        in.close();
        reader.dispose();
        return bi;
    }
    
    public boolean isWriteable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return RenderedImage.class.isAssignableFrom(type);
    }
    
    public void writeTo(final RenderedImage t, final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, Object> httpHeaders, final OutputStream entityStream) throws IOException {
        final String formatName = this.getWriterFormatName(mediaType);
        if (formatName == null) {
            throw new IOException("The image-based media type " + mediaType + " is not supported for writing");
        }
        ImageIO.write(t, formatName, entityStream);
    }
    
    private String getWriterFormatName(final MediaType t) {
        return this.getWriterFormatName(t.toString());
    }
    
    private String getWriterFormatName(final String t) {
        final Iterator<ImageWriter> i = ImageIO.getImageWritersByMIMEType(t);
        if (!i.hasNext()) {
            return null;
        }
        return i.next().getOriginatingProvider().getFormatNames()[0];
    }
    
    static {
        IMAGE_MEDIA_TYPE = new MediaType("image", "*");
    }
}
