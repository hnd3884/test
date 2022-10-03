package com.sun.media.sound;

import java.util.List;
import javax.sound.midi.ControllerEventListener;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineEvent;
import java.util.ArrayList;

final class EventDispatcher implements Runnable
{
    private static final int AUTO_CLOSE_TIME = 5000;
    private final ArrayList eventQueue;
    private Thread thread;
    private final ArrayList<ClipInfo> autoClosingClips;
    private final ArrayList<LineMonitor> lineMonitors;
    static final int LINE_MONITOR_TIME = 400;
    
    EventDispatcher() {
        this.eventQueue = new ArrayList();
        this.thread = null;
        this.autoClosingClips = new ArrayList<ClipInfo>();
        this.lineMonitors = new ArrayList<LineMonitor>();
    }
    
    synchronized void start() {
        if (this.thread == null) {
            this.thread = JSSecurityManager.createThread(this, "Java Sound Event Dispatcher", true, -1, true);
        }
    }
    
    void processEvent(final EventInfo eventInfo) {
        final int listenerCount = eventInfo.getListenerCount();
        if (eventInfo.getEvent() instanceof LineEvent) {
            final LineEvent lineEvent = (LineEvent)eventInfo.getEvent();
            for (int i = 0; i < listenerCount; ++i) {
                try {
                    ((LineListener)eventInfo.getListener(i)).update(lineEvent);
                }
                catch (final Throwable t) {}
            }
            return;
        }
        if (eventInfo.getEvent() instanceof MetaMessage) {
            final MetaMessage metaMessage = (MetaMessage)eventInfo.getEvent();
            for (int j = 0; j < listenerCount; ++j) {
                try {
                    ((MetaEventListener)eventInfo.getListener(j)).meta(metaMessage);
                }
                catch (final Throwable t2) {}
            }
            return;
        }
        if (eventInfo.getEvent() instanceof ShortMessage) {
            final ShortMessage shortMessage = (ShortMessage)eventInfo.getEvent();
            if ((shortMessage.getStatus() & 0xF0) == 0xB0) {
                for (int k = 0; k < listenerCount; ++k) {
                    try {
                        ((ControllerEventListener)eventInfo.getListener(k)).controlChange(shortMessage);
                    }
                    catch (final Throwable t3) {}
                }
            }
            return;
        }
        Printer.err("Unknown event type: " + eventInfo.getEvent());
    }
    
    void dispatchEvents() {
        EventInfo eventInfo = null;
        synchronized (this) {
            try {
                if (this.eventQueue.size() == 0) {
                    if (this.autoClosingClips.size() > 0 || this.lineMonitors.size() > 0) {
                        int n = 5000;
                        if (this.lineMonitors.size() > 0) {
                            n = 400;
                        }
                        this.wait(n);
                    }
                    else {
                        this.wait();
                    }
                }
            }
            catch (final InterruptedException ex) {}
            if (this.eventQueue.size() > 0) {
                eventInfo = this.eventQueue.remove(0);
            }
        }
        if (eventInfo != null) {
            this.processEvent(eventInfo);
        }
        else {
            if (this.autoClosingClips.size() > 0) {
                this.closeAutoClosingClips();
            }
            if (this.lineMonitors.size() > 0) {
                this.monitorLines();
            }
        }
    }
    
    private synchronized void postEvent(final EventInfo eventInfo) {
        this.eventQueue.add(eventInfo);
        this.notifyAll();
    }
    
    @Override
    public void run() {
        while (true) {
            try {
                while (true) {
                    this.dispatchEvents();
                }
            }
            catch (final Throwable t) {
                continue;
            }
            break;
        }
    }
    
    void sendAudioEvents(final Object o, final List list) {
        if (list == null || list.size() == 0) {
            return;
        }
        this.start();
        this.postEvent(new EventInfo(o, list));
    }
    
    private void closeAutoClosingClips() {
        synchronized (this.autoClosingClips) {
            final long currentTimeMillis = System.currentTimeMillis();
            for (int i = this.autoClosingClips.size() - 1; i >= 0; --i) {
                final ClipInfo clipInfo = this.autoClosingClips.get(i);
                if (clipInfo.isExpired(currentTimeMillis)) {
                    final AutoClosingClip clip = clipInfo.getClip();
                    if (!clip.isOpen() || !clip.isAutoClosing()) {
                        this.autoClosingClips.remove(i);
                    }
                    else if (!clip.isRunning() && !clip.isActive() && clip.isAutoClosing()) {
                        clip.close();
                    }
                }
            }
        }
    }
    
    private int getAutoClosingClipIndex(final AutoClosingClip autoClosingClip) {
        synchronized (this.autoClosingClips) {
            for (int i = this.autoClosingClips.size() - 1; i >= 0; --i) {
                if (autoClosingClip.equals(this.autoClosingClips.get(i).getClip())) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    void autoClosingClipOpened(final AutoClosingClip autoClosingClip) {
        int autoClosingClipIndex = 0;
        synchronized (this.autoClosingClips) {
            autoClosingClipIndex = this.getAutoClosingClipIndex(autoClosingClip);
            if (autoClosingClipIndex == -1) {
                this.autoClosingClips.add(new ClipInfo(autoClosingClip));
            }
        }
        if (autoClosingClipIndex == -1) {
            synchronized (this) {
                this.notifyAll();
            }
        }
    }
    
    void autoClosingClipClosed(final AutoClosingClip autoClosingClip) {
        synchronized (this.autoClosingClips) {
            final int autoClosingClipIndex = this.getAutoClosingClipIndex(autoClosingClip);
            if (autoClosingClipIndex != -1) {
                this.autoClosingClips.remove(autoClosingClipIndex);
            }
        }
    }
    
    private void monitorLines() {
        synchronized (this.lineMonitors) {
            for (int i = 0; i < this.lineMonitors.size(); ++i) {
                this.lineMonitors.get(i).checkLine();
            }
        }
    }
    
    void addLineMonitor(final LineMonitor lineMonitor) {
        synchronized (this.lineMonitors) {
            if (this.lineMonitors.indexOf(lineMonitor) >= 0) {
                return;
            }
            this.lineMonitors.add(lineMonitor);
        }
        synchronized (this) {
            this.notifyAll();
        }
    }
    
    void removeLineMonitor(final LineMonitor lineMonitor) {
        synchronized (this.lineMonitors) {
            if (this.lineMonitors.indexOf(lineMonitor) < 0) {
                return;
            }
            this.lineMonitors.remove(lineMonitor);
        }
    }
    
    private class EventInfo
    {
        private final Object event;
        private final Object[] listeners;
        
        EventInfo(final Object event, final List list) {
            this.event = event;
            this.listeners = list.toArray();
        }
        
        Object getEvent() {
            return this.event;
        }
        
        int getListenerCount() {
            return this.listeners.length;
        }
        
        Object getListener(final int n) {
            return this.listeners[n];
        }
    }
    
    private class ClipInfo
    {
        private final AutoClosingClip clip;
        private final long expiration;
        
        ClipInfo(final AutoClosingClip clip) {
            this.clip = clip;
            this.expiration = System.currentTimeMillis() + 5000L;
        }
        
        AutoClosingClip getClip() {
            return this.clip;
        }
        
        boolean isExpired(final long n) {
            return n > this.expiration;
        }
    }
    
    interface LineMonitor
    {
        void checkLine();
    }
}
