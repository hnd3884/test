package com.adventnet.sym.server.mdm.certificates.scep;

public class PasswordResponse
{
    private final PasswordRequestStatus passwordRequestStatus;
    private final String password;
    
    public PasswordResponse(final PasswordRequestStatus passwordRequestStatus, final String password) {
        this.passwordRequestStatus = passwordRequestStatus;
        this.password = password;
    }
    
    public PasswordRequestStatus getPasswordRequestStatus() {
        return this.passwordRequestStatus;
    }
    
    public String getPassword() {
        return this.password;
    }
}
