package com.sun.security.jgss;

import javax.security.auth.Subject;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSName;
import jdk.Exported;

@Exported
public class GSSUtil
{
    public static Subject createSubject(final GSSName gssName, final GSSCredential gssCredential) {
        return sun.security.jgss.GSSUtil.getSubject(gssName, gssCredential);
    }
}
