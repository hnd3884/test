package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.misc.MurmurHash;
import org.antlr.v4.runtime.Lexer;

public final class LexerChannelAction implements LexerAction
{
    private final int channel;
    
    public LexerChannelAction(final int channel) {
        this.channel = channel;
    }
    
    public int getChannel() {
        return this.channel;
    }
    
    @Override
    public LexerActionType getActionType() {
        return LexerActionType.CHANNEL;
    }
    
    @Override
    public boolean isPositionDependent() {
        return false;
    }
    
    @Override
    public void execute(final Lexer lexer) {
        lexer.setChannel(this.channel);
    }
    
    @Override
    public int hashCode() {
        int hash = MurmurHash.initialize();
        hash = MurmurHash.update(hash, this.getActionType().ordinal());
        hash = MurmurHash.update(hash, this.channel);
        return MurmurHash.finish(hash, 2);
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj == this || (obj instanceof LexerChannelAction && this.channel == ((LexerChannelAction)obj).channel);
    }
    
    @Override
    public String toString() {
        return String.format("channel(%d)", this.channel);
    }
}
