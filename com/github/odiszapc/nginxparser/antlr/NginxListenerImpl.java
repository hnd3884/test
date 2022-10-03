package com.github.odiszapc.nginxparser.antlr;

import org.antlr.v4.runtime.misc.NotNull;
import com.github.odiszapc.nginxparser.NgxConfig;

public class NginxListenerImpl extends NginxBaseListener
{
    private NgxConfig result;
    
    public NgxConfig getResult() {
        return this.result;
    }
    
    @Override
    public void enterConfig(@NotNull final NginxParser.ConfigContext configContext) {
        this.result = configContext.ret;
    }
}
