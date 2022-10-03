package com.sun.beans.decoder;

final class VoidElementHandler extends ObjectElementHandler
{
    @Override
    protected boolean isArgument() {
        return false;
    }
}
