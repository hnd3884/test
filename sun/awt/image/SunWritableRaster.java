package sun.awt.image;

import java.awt.Rectangle;
import java.awt.Point;
import java.awt.image.SampleModel;
import sun.java2d.SurfaceData;
import java.awt.Image;
import sun.java2d.StateTrackable;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferUShort;
import java.awt.image.DataBufferByte;
import sun.java2d.StateTrackableDelegate;
import java.awt.image.WritableRaster;

public class SunWritableRaster extends WritableRaster
{
    private static DataStealer stealer;
    private StateTrackableDelegate theTrackable;
    
    public static void setDataStealer(final DataStealer stealer) {
        if (SunWritableRaster.stealer != null) {
            throw new InternalError("Attempt to set DataStealer twice");
        }
        SunWritableRaster.stealer = stealer;
    }
    
    public static byte[] stealData(final DataBufferByte dataBufferByte, final int n) {
        return SunWritableRaster.stealer.getData(dataBufferByte, n);
    }
    
    public static short[] stealData(final DataBufferUShort dataBufferUShort, final int n) {
        return SunWritableRaster.stealer.getData(dataBufferUShort, n);
    }
    
    public static int[] stealData(final DataBufferInt dataBufferInt, final int n) {
        return SunWritableRaster.stealer.getData(dataBufferInt, n);
    }
    
    public static StateTrackableDelegate stealTrackable(final DataBuffer dataBuffer) {
        return SunWritableRaster.stealer.getTrackable(dataBuffer);
    }
    
    public static void setTrackable(final DataBuffer dataBuffer, final StateTrackableDelegate stateTrackableDelegate) {
        SunWritableRaster.stealer.setTrackable(dataBuffer, stateTrackableDelegate);
    }
    
    public static void makeTrackable(final DataBuffer dataBuffer) {
        SunWritableRaster.stealer.setTrackable(dataBuffer, StateTrackableDelegate.createInstance(StateTrackable.State.STABLE));
    }
    
    public static void markDirty(final DataBuffer dataBuffer) {
        SunWritableRaster.stealer.getTrackable(dataBuffer).markDirty();
    }
    
    public static void markDirty(final WritableRaster writableRaster) {
        if (writableRaster instanceof SunWritableRaster) {
            ((SunWritableRaster)writableRaster).markDirty();
        }
        else {
            markDirty(writableRaster.getDataBuffer());
        }
    }
    
    public static void markDirty(final Image image) {
        SurfaceData.getPrimarySurfaceData(image).markDirty();
    }
    
    public SunWritableRaster(final SampleModel sampleModel, final Point point) {
        super(sampleModel, point);
        this.theTrackable = stealTrackable(this.dataBuffer);
    }
    
    public SunWritableRaster(final SampleModel sampleModel, final DataBuffer dataBuffer, final Point point) {
        super(sampleModel, dataBuffer, point);
        this.theTrackable = stealTrackable(dataBuffer);
    }
    
    public SunWritableRaster(final SampleModel sampleModel, final DataBuffer dataBuffer, final Rectangle rectangle, final Point point, final WritableRaster writableRaster) {
        super(sampleModel, dataBuffer, rectangle, point, writableRaster);
        this.theTrackable = stealTrackable(dataBuffer);
    }
    
    public final void markDirty() {
        this.theTrackable.markDirty();
    }
    
    public interface DataStealer
    {
        byte[] getData(final DataBufferByte p0, final int p1);
        
        short[] getData(final DataBufferUShort p0, final int p1);
        
        int[] getData(final DataBufferInt p0, final int p1);
        
        StateTrackableDelegate getTrackable(final DataBuffer p0);
        
        void setTrackable(final DataBuffer p0, final StateTrackableDelegate p1);
    }
}
