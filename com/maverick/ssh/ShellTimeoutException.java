package com.maverick.ssh;

public class ShellTimeoutException extends Exception
{
    ShellTimeoutException() {
        super("The shell operation timed out");
    }
    
    ShellTimeoutException(final String s) {
        super(s);
    }
}
