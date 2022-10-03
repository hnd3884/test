package com.theorem.radius3.dmcoaclient;

import com.theorem.radius3.AttributeList;

public interface DMCOACallback
{
    DMCOAResponse dmcoaCallback(final int p0, final AttributeList p1);
}
