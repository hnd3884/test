package com.sun.corba.se.impl.protocol.giopmsgheaders;

import java.io.IOException;

public interface MessageHandler
{
    void handleInput(final Message p0) throws IOException;
    
    void handleInput(final RequestMessage_1_0 p0) throws IOException;
    
    void handleInput(final RequestMessage_1_1 p0) throws IOException;
    
    void handleInput(final RequestMessage_1_2 p0) throws IOException;
    
    void handleInput(final ReplyMessage_1_0 p0) throws IOException;
    
    void handleInput(final ReplyMessage_1_1 p0) throws IOException;
    
    void handleInput(final ReplyMessage_1_2 p0) throws IOException;
    
    void handleInput(final LocateRequestMessage_1_0 p0) throws IOException;
    
    void handleInput(final LocateRequestMessage_1_1 p0) throws IOException;
    
    void handleInput(final LocateRequestMessage_1_2 p0) throws IOException;
    
    void handleInput(final LocateReplyMessage_1_0 p0) throws IOException;
    
    void handleInput(final LocateReplyMessage_1_1 p0) throws IOException;
    
    void handleInput(final LocateReplyMessage_1_2 p0) throws IOException;
    
    void handleInput(final FragmentMessage_1_1 p0) throws IOException;
    
    void handleInput(final FragmentMessage_1_2 p0) throws IOException;
    
    void handleInput(final CancelRequestMessage p0) throws IOException;
}
