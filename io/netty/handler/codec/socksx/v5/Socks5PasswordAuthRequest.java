package io.netty.handler.codec.socksx.v5;

public interface Socks5PasswordAuthRequest extends Socks5Message
{
    String username();
    
    String password();
}
