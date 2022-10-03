package com.sun.activation.viewers;

import java.io.ByteArrayOutputStream;
import java.awt.MediaTracker;
import java.io.InputStream;
import java.io.IOException;
import java.awt.Dimension;
import java.awt.Component;
import javax.activation.DataHandler;
import java.awt.Image;
import javax.activation.CommandObject;
import java.awt.Panel;

public class ImageViewer extends Panel implements CommandObject
{
    private ImageViewerCanvas canvas;
    private Image image;
    private DataHandler _dh;
    private boolean DEBUG;
    
    public ImageViewer() {
        this.canvas = null;
        this.image = null;
        this._dh = null;
        this.DEBUG = false;
        this.add(this.canvas = new ImageViewerCanvas());
    }
    
    public void addNotify() {
        super.addNotify();
        this.invalidate();
        this.validate();
        this.doLayout();
    }
    
    public Dimension getPreferredSize() {
        return this.canvas.getPreferredSize();
    }
    
    public void setCommandContext(final String s, final DataHandler dh) throws IOException {
        this._dh = dh;
        this.setInputStream(this._dh.getInputStream());
    }
    
    private void setInputStream(final InputStream inputStream) throws IOException {
        final MediaTracker mediaTracker = new MediaTracker(this);
        final byte[] array = new byte[1024];
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int read;
        while ((read = inputStream.read(array)) > 0) {
            byteArrayOutputStream.write(array, 0, read);
        }
        inputStream.close();
        mediaTracker.addImage(this.image = this.getToolkit().createImage(byteArrayOutputStream.toByteArray()), 0);
        try {
            mediaTracker.waitForID(0);
            mediaTracker.waitForAll();
            if (mediaTracker.statusID(0, true) != 8) {
                System.out.println("Error occured in image loading = " + mediaTracker.getErrorsID(0));
            }
        }
        catch (final InterruptedException ex) {
            throw new IOException("Error reading image data");
        }
        this.canvas.setImage(this.image);
        if (this.DEBUG) {
            System.out.println("calling invalidate");
        }
    }
}
