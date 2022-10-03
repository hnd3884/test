package com.google.api.client.googleapis.mtls;

import com.google.api.client.util.Key;
import java.util.List;
import com.google.api.client.util.Beta;
import com.google.api.client.json.GenericJson;

@Beta
public class ContextAwareMetadataJson extends GenericJson
{
    @Key("cert_provider_command")
    private List<String> commands;
    
    public final List<String> getCommands() {
        return this.commands;
    }
}
