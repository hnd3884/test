package com.unboundid.util;

import java.util.UUID;

final class UUIDValuePatternComponent extends ValuePatternComponent
{
    private static final long serialVersionUID = -5006381863724555309L;
    
    @Override
    void append(final StringBuilder buffer) {
        buffer.append(UUID.randomUUID().toString());
    }
    
    @Override
    boolean supportsBackReference() {
        return true;
    }
}
