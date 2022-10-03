package io.netty.handler.codec.stomp;

import io.netty.util.AsciiString;
import io.netty.handler.codec.Headers;
import io.netty.util.internal.StringUtil;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.util.ByteProcessor;
import io.netty.handler.codec.DecoderException;
import io.netty.buffer.Unpooled;
import io.netty.buffer.ByteBufUtil;
import io.netty.handler.codec.DecoderResult;
import java.util.List;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.AppendableCharSequence;
import io.netty.util.internal.ObjectUtil;
import io.netty.handler.codec.ReplayingDecoder;

public class StompSubframeDecoder extends ReplayingDecoder<State>
{
    private static final int DEFAULT_CHUNK_SIZE = 8132;
    private static final int DEFAULT_MAX_LINE_LENGTH = 1024;
    private final Utf8LineParser commandParser;
    private final HeaderParser headerParser;
    private final int maxChunkSize;
    private int alreadyReadChunkSize;
    private LastStompContentSubframe lastContent;
    private long contentLength;
    
    public StompSubframeDecoder() {
        this(1024, 8132);
    }
    
    public StompSubframeDecoder(final boolean validateHeaders) {
        this(1024, 8132, validateHeaders);
    }
    
    public StompSubframeDecoder(final int maxLineLength, final int maxChunkSize) {
        this(maxLineLength, maxChunkSize, false);
    }
    
    public StompSubframeDecoder(final int maxLineLength, final int maxChunkSize, final boolean validateHeaders) {
        super(State.SKIP_CONTROL_CHARACTERS);
        this.contentLength = -1L;
        ObjectUtil.checkPositive(maxLineLength, "maxLineLength");
        ObjectUtil.checkPositive(maxChunkSize, "maxChunkSize");
        this.maxChunkSize = maxChunkSize;
        this.commandParser = new Utf8LineParser(new AppendableCharSequence(16), maxLineLength);
        this.headerParser = new HeaderParser(new AppendableCharSequence(128), maxLineLength, validateHeaders);
    }
    
    @Override
    protected void decode(final ChannelHandlerContext ctx, final ByteBuf in, final List<Object> out) throws Exception {
        switch (this.state()) {
            case SKIP_CONTROL_CHARACTERS: {
                skipControlCharacters(in);
                this.checkpoint(State.READ_HEADERS);
            }
            case READ_HEADERS: {
                StompCommand command = StompCommand.UNKNOWN;
                StompHeadersSubframe frame = null;
                try {
                    command = this.readCommand(in);
                    frame = new DefaultStompHeadersSubframe(command);
                    this.checkpoint(this.readHeaders(in, frame.headers()));
                    out.add(frame);
                    break;
                }
                catch (final Exception e) {
                    if (frame == null) {
                        frame = new DefaultStompHeadersSubframe(command);
                    }
                    frame.setDecoderResult(DecoderResult.failure(e));
                    out.add(frame);
                    this.checkpoint(State.BAD_FRAME);
                    return;
                }
            }
            case BAD_FRAME: {
                in.skipBytes(this.actualReadableBytes());
                return;
            }
        }
        try {
            int toRead;
            int remainingLength;
            ByteBuf chunkBuffer;
            int alreadyReadChunkSize;
            int nulIndex;
            switch (this.state()) {
                case READ_CONTENT:
                    Label_0474: {
                        toRead = in.readableBytes();
                        if (toRead == 0) {
                            return;
                        }
                        if (toRead > this.maxChunkSize) {
                            toRead = this.maxChunkSize;
                        }
                        if (this.contentLength >= 0L) {
                            remainingLength = (int)(this.contentLength - this.alreadyReadChunkSize);
                            if (toRead > remainingLength) {
                                toRead = remainingLength;
                            }
                            chunkBuffer = ByteBufUtil.readBytes(ctx.alloc(), in, toRead);
                            alreadyReadChunkSize = this.alreadyReadChunkSize + toRead;
                            this.alreadyReadChunkSize = alreadyReadChunkSize;
                            if (alreadyReadChunkSize >= this.contentLength) {
                                this.lastContent = new DefaultLastStompContentSubframe(chunkBuffer);
                                this.checkpoint(State.FINALIZE_FRAME_READ);
                                break Label_0474;
                            }
                            out.add(new DefaultStompContentSubframe(chunkBuffer));
                            return;
                        }
                        else {
                            nulIndex = ByteBufUtil.indexOf(in, in.readerIndex(), in.writerIndex(), (byte)0);
                            if (nulIndex == in.readerIndex()) {
                                this.checkpoint(State.FINALIZE_FRAME_READ);
                                break Label_0474;
                            }
                            if (nulIndex > 0) {
                                toRead = nulIndex - in.readerIndex();
                            }
                            else {
                                toRead = in.writerIndex() - in.readerIndex();
                            }
                            chunkBuffer = ByteBufUtil.readBytes(ctx.alloc(), in, toRead);
                            this.alreadyReadChunkSize += toRead;
                            if (nulIndex > 0) {
                                this.lastContent = new DefaultLastStompContentSubframe(chunkBuffer);
                                this.checkpoint(State.FINALIZE_FRAME_READ);
                                break Label_0474;
                            }
                            out.add(new DefaultStompContentSubframe(chunkBuffer));
                            return;
                        }
                        break;
                    }
                case FINALIZE_FRAME_READ: {
                    skipNullCharacter(in);
                    if (this.lastContent == null) {
                        this.lastContent = LastStompContentSubframe.EMPTY_LAST_CONTENT;
                    }
                    out.add(this.lastContent);
                    this.resetDecoder();
                    break;
                }
            }
        }
        catch (final Exception e2) {
            final StompContentSubframe errorContent = new DefaultLastStompContentSubframe(Unpooled.EMPTY_BUFFER);
            errorContent.setDecoderResult(DecoderResult.failure(e2));
            out.add(errorContent);
            this.checkpoint(State.BAD_FRAME);
        }
    }
    
    private StompCommand readCommand(final ByteBuf in) {
        final CharSequence commandSequence = this.commandParser.parse(in);
        if (commandSequence == null) {
            throw new DecoderException("Failed to read command from channel");
        }
        final String commandStr = commandSequence.toString();
        try {
            return StompCommand.valueOf(commandStr);
        }
        catch (final IllegalArgumentException iae) {
            throw new DecoderException("Cannot to parse command " + commandStr);
        }
    }
    
    private State readHeaders(final ByteBuf buffer, final StompHeaders headers) {
        boolean headerRead;
        do {
            headerRead = this.headerParser.parseHeader(headers, buffer);
        } while (headerRead);
        if (((Headers<AsciiString, V, T>)headers).contains(StompHeaders.CONTENT_LENGTH)) {
            this.contentLength = getContentLength(headers);
            if (this.contentLength == 0L) {
                return State.FINALIZE_FRAME_READ;
            }
        }
        return State.READ_CONTENT;
    }
    
    private static long getContentLength(final StompHeaders headers) {
        final long contentLength = ((Headers<AsciiString, V, T>)headers).getLong(StompHeaders.CONTENT_LENGTH, 0L);
        if (contentLength < 0L) {
            throw new DecoderException((Object)StompHeaders.CONTENT_LENGTH + " must be non-negative");
        }
        return contentLength;
    }
    
    private static void skipNullCharacter(final ByteBuf buffer) {
        final byte b = buffer.readByte();
        if (b != 0) {
            throw new IllegalStateException("unexpected byte in buffer " + b + " while expecting NULL byte");
        }
    }
    
    private static void skipControlCharacters(final ByteBuf buffer) {
        byte b;
        do {
            b = buffer.readByte();
        } while (b == 13 || b == 10);
        buffer.readerIndex(buffer.readerIndex() - 1);
    }
    
    private void resetDecoder() {
        this.checkpoint(State.SKIP_CONTROL_CHARACTERS);
        this.contentLength = -1L;
        this.alreadyReadChunkSize = 0;
        this.lastContent = null;
    }
    
    enum State
    {
        SKIP_CONTROL_CHARACTERS, 
        READ_HEADERS, 
        READ_CONTENT, 
        FINALIZE_FRAME_READ, 
        BAD_FRAME, 
        INVALID_CHUNK;
    }
    
    private static class Utf8LineParser implements ByteProcessor
    {
        private final AppendableCharSequence charSeq;
        private final int maxLineLength;
        private int lineLength;
        private char interim;
        private boolean nextRead;
        
        Utf8LineParser(final AppendableCharSequence charSeq, final int maxLineLength) {
            this.charSeq = ObjectUtil.checkNotNull(charSeq, "charSeq");
            this.maxLineLength = maxLineLength;
        }
        
        AppendableCharSequence parse(final ByteBuf byteBuf) {
            this.reset();
            final int offset = byteBuf.forEachByte(this);
            if (offset == -1) {
                return null;
            }
            byteBuf.readerIndex(offset + 1);
            return this.charSeq;
        }
        
        AppendableCharSequence charSequence() {
            return this.charSeq;
        }
        
        @Override
        public boolean process(final byte nextByte) throws Exception {
            if (nextByte == 13) {
                ++this.lineLength;
                return true;
            }
            if (nextByte == 10) {
                return false;
            }
            if (++this.lineLength > this.maxLineLength) {
                throw new TooLongFrameException("An STOMP line is larger than " + this.maxLineLength + " bytes.");
            }
            if (this.nextRead) {
                this.interim |= (char)((nextByte & 0x3F) << 6);
                this.nextRead = false;
            }
            else if (this.interim != '\0') {
                this.charSeq.append((char)(this.interim | (nextByte & 0x3F)));
                this.interim = '\0';
            }
            else if (nextByte >= 0) {
                this.charSeq.append((char)nextByte);
            }
            else if ((nextByte & 0xE0) == 0xC0) {
                this.interim = (char)((nextByte & 0x1F) << 6);
            }
            else {
                this.interim = (char)((nextByte & 0xF) << 12);
                this.nextRead = true;
            }
            return true;
        }
        
        protected void reset() {
            this.charSeq.reset();
            this.lineLength = 0;
            this.interim = '\0';
            this.nextRead = false;
        }
    }
    
    private static final class HeaderParser extends Utf8LineParser
    {
        private final boolean validateHeaders;
        private String name;
        private boolean valid;
        
        HeaderParser(final AppendableCharSequence charSeq, final int maxLineLength, final boolean validateHeaders) {
            super(charSeq, maxLineLength);
            this.validateHeaders = validateHeaders;
        }
        
        boolean parseHeader(final StompHeaders headers, final ByteBuf buf) {
            final AppendableCharSequence value = super.parse(buf);
            if (value == null || (this.name == null && value.length() == 0)) {
                return false;
            }
            if (this.valid) {
                ((Headers<String, String, Headers>)headers).add(this.name, value.toString());
            }
            else if (this.validateHeaders) {
                if (StringUtil.isNullOrEmpty(this.name)) {
                    throw new IllegalArgumentException("received an invalid header line '" + (Object)value + '\'');
                }
                final String line = this.name + ':' + (Object)value;
                throw new IllegalArgumentException("a header value or name contains a prohibited character ':', " + line);
            }
            return true;
        }
        
        @Override
        public boolean process(final byte nextByte) throws Exception {
            if (nextByte == 58) {
                if (this.name == null) {
                    final AppendableCharSequence charSeq = this.charSequence();
                    if (charSeq.length() != 0) {
                        this.name = charSeq.substring(0, charSeq.length());
                        charSeq.reset();
                        return this.valid = true;
                    }
                    this.name = "";
                }
                else {
                    this.valid = false;
                }
            }
            return super.process(nextByte);
        }
        
        @Override
        protected void reset() {
            this.name = null;
            this.valid = false;
            super.reset();
        }
    }
}
