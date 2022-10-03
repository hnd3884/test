package javax.sound.midi;

import java.util.List;

public interface MidiDevice extends AutoCloseable
{
    Info getDeviceInfo();
    
    void open() throws MidiUnavailableException;
    
    void close();
    
    boolean isOpen();
    
    long getMicrosecondPosition();
    
    int getMaxReceivers();
    
    int getMaxTransmitters();
    
    Receiver getReceiver() throws MidiUnavailableException;
    
    List<Receiver> getReceivers();
    
    Transmitter getTransmitter() throws MidiUnavailableException;
    
    List<Transmitter> getTransmitters();
    
    public static class Info
    {
        private String name;
        private String vendor;
        private String description;
        private String version;
        
        protected Info(final String name, final String vendor, final String description, final String version) {
            this.name = name;
            this.vendor = vendor;
            this.description = description;
            this.version = version;
        }
        
        @Override
        public final boolean equals(final Object o) {
            return super.equals(o);
        }
        
        @Override
        public final int hashCode() {
            return super.hashCode();
        }
        
        public final String getName() {
            return this.name;
        }
        
        public final String getVendor() {
            return this.vendor;
        }
        
        public final String getDescription() {
            return this.description;
        }
        
        public final String getVersion() {
            return this.version;
        }
        
        @Override
        public final String toString() {
            return this.name;
        }
    }
}
