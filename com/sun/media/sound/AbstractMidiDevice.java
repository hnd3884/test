package com.sun.media.sound;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDeviceTransmitter;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiDeviceReceiver;
import javax.sound.midi.Transmitter;
import java.util.Collections;
import javax.sound.midi.MidiUnavailableException;
import java.util.List;
import javax.sound.midi.Receiver;
import java.util.ArrayList;
import javax.sound.midi.MidiDevice;

abstract class AbstractMidiDevice implements MidiDevice, ReferenceCountingDevice
{
    private static final boolean TRACE_TRANSMITTER = false;
    private ArrayList<Receiver> receiverList;
    private TransmitterList transmitterList;
    private final Object traRecLock;
    private final Info info;
    private volatile boolean open;
    private int openRefCount;
    private List openKeepingObjects;
    protected volatile long id;
    
    protected AbstractMidiDevice(final Info info) {
        this.traRecLock = new Object();
        this.info = info;
        this.openRefCount = 0;
    }
    
    @Override
    public final Info getDeviceInfo() {
        return this.info;
    }
    
    @Override
    public final void open() throws MidiUnavailableException {
        synchronized (this) {
            this.openRefCount = -1;
            this.doOpen();
        }
    }
    
    private void openInternal(final Object o) throws MidiUnavailableException {
        synchronized (this) {
            if (this.openRefCount != -1) {
                ++this.openRefCount;
                this.getOpenKeepingObjects().add(o);
            }
            this.doOpen();
        }
    }
    
    private void doOpen() throws MidiUnavailableException {
        synchronized (this) {
            if (!this.isOpen()) {
                this.implOpen();
                this.open = true;
            }
        }
    }
    
    @Override
    public final void close() {
        synchronized (this) {
            this.doClose();
            this.openRefCount = 0;
        }
    }
    
    public final void closeInternal(final Object o) {
        synchronized (this) {
            if (this.getOpenKeepingObjects().remove(o) && this.openRefCount > 0) {
                --this.openRefCount;
                if (this.openRefCount == 0) {
                    this.doClose();
                }
            }
        }
    }
    
    public final void doClose() {
        synchronized (this) {
            if (this.isOpen()) {
                this.implClose();
                this.open = false;
            }
        }
    }
    
    @Override
    public final boolean isOpen() {
        return this.open;
    }
    
    protected void implClose() {
        synchronized (this.traRecLock) {
            if (this.receiverList != null) {
                for (int i = 0; i < this.receiverList.size(); ++i) {
                    this.receiverList.get(i).close();
                }
                this.receiverList.clear();
            }
            if (this.transmitterList != null) {
                this.transmitterList.close();
            }
        }
    }
    
    @Override
    public long getMicrosecondPosition() {
        return -1L;
    }
    
    @Override
    public final int getMaxReceivers() {
        if (this.hasReceivers()) {
            return -1;
        }
        return 0;
    }
    
    @Override
    public final int getMaxTransmitters() {
        if (this.hasTransmitters()) {
            return -1;
        }
        return 0;
    }
    
    @Override
    public final Receiver getReceiver() throws MidiUnavailableException {
        final Receiver receiver;
        synchronized (this.traRecLock) {
            receiver = this.createReceiver();
            this.getReceiverList().add(receiver);
        }
        return receiver;
    }
    
    @Override
    public final List<Receiver> getReceivers() {
        List<Object> list;
        synchronized (this.traRecLock) {
            if (this.receiverList == null) {
                list = Collections.unmodifiableList((List<?>)new ArrayList<Object>(0));
            }
            else {
                list = Collections.unmodifiableList((List<?>)this.receiverList.clone());
            }
        }
        return (List<Receiver>)list;
    }
    
    @Override
    public final Transmitter getTransmitter() throws MidiUnavailableException {
        final Transmitter transmitter;
        synchronized (this.traRecLock) {
            transmitter = this.createTransmitter();
            this.getTransmitterList().add(transmitter);
        }
        return transmitter;
    }
    
    @Override
    public final List<Transmitter> getTransmitters() {
        List<Object> list;
        synchronized (this.traRecLock) {
            if (this.transmitterList == null || this.transmitterList.transmitters.size() == 0) {
                list = Collections.unmodifiableList((List<?>)new ArrayList<Object>(0));
            }
            else {
                list = Collections.unmodifiableList((List<?>)this.transmitterList.transmitters.clone());
            }
        }
        return (List<Transmitter>)list;
    }
    
    final long getId() {
        return this.id;
    }
    
    @Override
    public final Receiver getReceiverReferenceCounting() throws MidiUnavailableException {
        final Receiver receiver;
        synchronized (this.traRecLock) {
            receiver = this.getReceiver();
            this.openInternal(receiver);
        }
        return receiver;
    }
    
    @Override
    public final Transmitter getTransmitterReferenceCounting() throws MidiUnavailableException {
        final Transmitter transmitter;
        synchronized (this.traRecLock) {
            transmitter = this.getTransmitter();
            this.openInternal(transmitter);
        }
        return transmitter;
    }
    
    private synchronized List getOpenKeepingObjects() {
        if (this.openKeepingObjects == null) {
            this.openKeepingObjects = new ArrayList();
        }
        return this.openKeepingObjects;
    }
    
    private List<Receiver> getReceiverList() {
        synchronized (this.traRecLock) {
            if (this.receiverList == null) {
                this.receiverList = new ArrayList<Receiver>();
            }
        }
        return this.receiverList;
    }
    
    protected boolean hasReceivers() {
        return false;
    }
    
    protected Receiver createReceiver() throws MidiUnavailableException {
        throw new MidiUnavailableException("MIDI IN receiver not available");
    }
    
    final TransmitterList getTransmitterList() {
        synchronized (this.traRecLock) {
            if (this.transmitterList == null) {
                this.transmitterList = new TransmitterList();
            }
        }
        return this.transmitterList;
    }
    
    protected boolean hasTransmitters() {
        return false;
    }
    
    protected Transmitter createTransmitter() throws MidiUnavailableException {
        throw new MidiUnavailableException("MIDI OUT transmitter not available");
    }
    
    protected abstract void implOpen() throws MidiUnavailableException;
    
    @Override
    protected final void finalize() {
        this.close();
    }
    
    abstract class AbstractReceiver implements MidiDeviceReceiver
    {
        private volatile boolean open;
        
        AbstractReceiver() {
            this.open = true;
        }
        
        @Override
        public final synchronized void send(final MidiMessage midiMessage, final long n) {
            if (!this.open) {
                throw new IllegalStateException("Receiver is not open");
            }
            this.implSend(midiMessage, n);
        }
        
        abstract void implSend(final MidiMessage p0, final long p1);
        
        @Override
        public final void close() {
            this.open = false;
            synchronized (AbstractMidiDevice.this.traRecLock) {
                AbstractMidiDevice.this.getReceiverList().remove(this);
            }
            AbstractMidiDevice.this.closeInternal(this);
        }
        
        @Override
        public final MidiDevice getMidiDevice() {
            return AbstractMidiDevice.this;
        }
        
        final boolean isOpen() {
            return this.open;
        }
    }
    
    class BasicTransmitter implements MidiDeviceTransmitter
    {
        private Receiver receiver;
        TransmitterList tlist;
        
        protected BasicTransmitter() {
            this.receiver = null;
            this.tlist = null;
        }
        
        private void setTransmitterList(final TransmitterList tlist) {
            this.tlist = tlist;
        }
        
        @Override
        public final void setReceiver(final Receiver receiver) {
            if (this.tlist != null && this.receiver != receiver) {
                this.tlist.receiverChanged(this, this.receiver, receiver);
                this.receiver = receiver;
            }
        }
        
        @Override
        public final Receiver getReceiver() {
            return this.receiver;
        }
        
        @Override
        public final void close() {
            AbstractMidiDevice.this.closeInternal(this);
            if (this.tlist != null) {
                this.tlist.receiverChanged(this, this.receiver, null);
                this.tlist.remove(this);
                this.tlist = null;
            }
        }
        
        @Override
        public final MidiDevice getMidiDevice() {
            return AbstractMidiDevice.this;
        }
    }
    
    final class TransmitterList
    {
        private final ArrayList<Transmitter> transmitters;
        private MidiOutDevice.MidiOutReceiver midiOutReceiver;
        private int optimizedReceiverCount;
        
        TransmitterList() {
            this.transmitters = new ArrayList<Transmitter>();
            this.optimizedReceiverCount = 0;
        }
        
        private void add(final Transmitter transmitter) {
            synchronized (this.transmitters) {
                this.transmitters.add(transmitter);
            }
            if (transmitter instanceof BasicTransmitter) {
                ((BasicTransmitter)transmitter).setTransmitterList(this);
            }
        }
        
        private void remove(final Transmitter transmitter) {
            synchronized (this.transmitters) {
                final int index = this.transmitters.indexOf(transmitter);
                if (index >= 0) {
                    this.transmitters.remove(index);
                }
            }
        }
        
        private void receiverChanged(final BasicTransmitter basicTransmitter, final Receiver receiver, final Receiver receiver2) {
            synchronized (this.transmitters) {
                if (this.midiOutReceiver == receiver) {
                    this.midiOutReceiver = null;
                }
                if (receiver2 != null && receiver2 instanceof MidiOutDevice.MidiOutReceiver && this.midiOutReceiver == null) {
                    this.midiOutReceiver = (MidiOutDevice.MidiOutReceiver)receiver2;
                }
                this.optimizedReceiverCount = ((this.midiOutReceiver != null) ? 1 : 0);
            }
        }
        
        void close() {
            synchronized (this.transmitters) {
                for (int i = 0; i < this.transmitters.size(); ++i) {
                    this.transmitters.get(i).close();
                }
                this.transmitters.clear();
            }
        }
        
        void sendMessage(final int n, final long n2) {
            try {
                synchronized (this.transmitters) {
                    final int size = this.transmitters.size();
                    if (this.optimizedReceiverCount == size) {
                        if (this.midiOutReceiver != null) {
                            this.midiOutReceiver.sendPackedMidiMessage(n, n2);
                        }
                    }
                    else {
                        for (int i = 0; i < size; ++i) {
                            final Receiver receiver = this.transmitters.get(i).getReceiver();
                            if (receiver != null) {
                                if (this.optimizedReceiverCount > 0) {
                                    if (receiver instanceof MidiOutDevice.MidiOutReceiver) {
                                        ((MidiOutDevice.MidiOutReceiver)receiver).sendPackedMidiMessage(n, n2);
                                    }
                                    else {
                                        receiver.send(new FastShortMessage(n), n2);
                                    }
                                }
                                else {
                                    receiver.send(new FastShortMessage(n), n2);
                                }
                            }
                        }
                    }
                }
            }
            catch (final InvalidMidiDataException ex) {}
        }
        
        void sendMessage(final byte[] array, final long n) {
            try {
                synchronized (this.transmitters) {
                    for (int size = this.transmitters.size(), i = 0; i < size; ++i) {
                        final Receiver receiver = this.transmitters.get(i).getReceiver();
                        if (receiver != null) {
                            receiver.send(new FastSysexMessage(array), n);
                        }
                    }
                }
            }
            catch (final InvalidMidiDataException ex) {}
        }
        
        void sendMessage(final MidiMessage midiMessage, final long n) {
            if (midiMessage instanceof FastShortMessage) {
                this.sendMessage(((FastShortMessage)midiMessage).getPackedMsg(), n);
                return;
            }
            synchronized (this.transmitters) {
                final int size = this.transmitters.size();
                if (this.optimizedReceiverCount == size) {
                    if (this.midiOutReceiver != null) {
                        this.midiOutReceiver.send(midiMessage, n);
                    }
                }
                else {
                    for (int i = 0; i < size; ++i) {
                        final Receiver receiver = this.transmitters.get(i).getReceiver();
                        if (receiver != null) {
                            receiver.send(midiMessage, n);
                        }
                    }
                }
            }
        }
    }
}
