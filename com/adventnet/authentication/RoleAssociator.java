package com.adventnet.authentication;

import java.util.List;
import javax.servlet.http.HttpServletRequest;

public interface RoleAssociator
{
    List getDynamicRoles(final HttpServletRequest p0);
}
