package com.adventnet.authentication;

import java.util.List;
import javax.servlet.http.HttpServletRequest;

public class DefaultRoleAssociator implements RoleAssociator
{
    @Override
    public List getDynamicRoles(final HttpServletRequest req) {
        return null;
    }
}
