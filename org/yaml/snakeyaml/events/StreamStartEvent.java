package org.yaml.snakeyaml.events;

import org.yaml.snakeyaml.error.Mark;

public final class StreamStartEvent extends Event
{
    public StreamStartEvent(final Mark startMark, final Mark endMark) {
        super(startMark, endMark);
    }
    
    @Override
    public ID getEventId() {
        return ID.StreamStart;
    }
}
