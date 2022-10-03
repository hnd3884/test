package com.zoho.security.api.wrapper;

public class FileAccessDeniedException extends RuntimeException
{
    private static final long serialVersionUID = -4380433645752711817L;
    
    public FileAccessDeniedException(final String message) {
        super("Invalid Access To File in given Directory:" + message);
    }
}
