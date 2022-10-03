package org.apache.commons.text.lookup;

import javax.script.ScriptEngine;
import java.util.Objects;
import javax.script.ScriptEngineManager;

final class ScriptStringLookup extends AbstractStringLookup
{
    static final ScriptStringLookup INSTANCE;
    
    private ScriptStringLookup() {
    }
    
    @Override
    public String lookup(final String key) {
        if (key == null) {
            return null;
        }
        final String[] keys = key.split(ScriptStringLookup.SPLIT_STR);
        final int keyLen = keys.length;
        if (keyLen != 2) {
            throw IllegalArgumentExceptions.format("Bad script key format [%s]; expected format is DocumentPath:Key.", key);
        }
        final String engineName = keys[0];
        final String script = keys[1];
        try {
            final ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName(engineName);
            if (scriptEngine == null) {
                throw new IllegalArgumentException("No script engine named " + engineName);
            }
            return Objects.toString(scriptEngine.eval(script), null);
        }
        catch (final Exception e) {
            throw IllegalArgumentExceptions.format(e, "Error in script engine [%s] evaluating script [%s].", engineName, script);
        }
    }
    
    static {
        INSTANCE = new ScriptStringLookup();
    }
}
