package org.glassfish.jersey.message.internal;

import java.util.ArrayList;
import java.util.List;

final class TracingInfo
{
    private final List<Message> messageList;
    
    TracingInfo() {
        this.messageList = new ArrayList<Message>();
    }
    
    public static String formatDuration(final long duration) {
        if (duration == 0L) {
            return " ----";
        }
        return String.format("%5.2f", duration / 1000000.0);
    }
    
    public static String formatDuration(final long fromTimestamp, final long toTimestamp) {
        return formatDuration(toTimestamp - fromTimestamp);
    }
    
    public static String formatPercent(final long value, final long top) {
        if (value == 0L) {
            return "  ----";
        }
        return String.format("%6.2f", 100.0 * value / top);
    }
    
    public String[] getMessages() {
        final long fromTimestamp = this.messageList.get(0).getTimestamp() - this.messageList.get(0).getDuration();
        final long toTimestamp = this.messageList.get(this.messageList.size() - 1).getTimestamp();
        final String[] messages = new String[this.messageList.size()];
        for (int i = 0; i < messages.length; ++i) {
            final Message message = this.messageList.get(i);
            final StringBuilder textSB = new StringBuilder();
            textSB.append(String.format("%-11s ", message.getEvent().category()));
            textSB.append('[').append(formatDuration(message.getDuration())).append(" / ").append(formatDuration(fromTimestamp, message.getTimestamp())).append(" ms |").append(formatPercent(message.getDuration(), toTimestamp - fromTimestamp)).append(" %] ");
            textSB.append(message.toString());
            messages[i] = textSB.toString();
        }
        return messages;
    }
    
    public void addMessage(final Message message) {
        this.messageList.add(message);
    }
    
    public static class Message
    {
        private final TracingLogger.Event event;
        private final long duration;
        private final long timestamp;
        private final String text;
        
        public Message(final TracingLogger.Event event, final long duration, final String[] args) {
            this.event = event;
            this.duration = duration;
            this.timestamp = System.nanoTime();
            if (event.messageFormat() != null) {
                this.text = String.format(event.messageFormat(), (Object[])args);
            }
            else {
                final StringBuilder textSB = new StringBuilder();
                for (final String arg : args) {
                    textSB.append(arg).append(' ');
                }
                this.text = textSB.toString();
            }
        }
        
        private TracingLogger.Event getEvent() {
            return this.event;
        }
        
        private long getDuration() {
            return this.duration;
        }
        
        private long getTimestamp() {
            return this.timestamp;
        }
        
        @Override
        public String toString() {
            return this.text;
        }
    }
}
