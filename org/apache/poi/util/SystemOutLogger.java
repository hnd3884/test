package org.apache.poi.util;

public class SystemOutLogger implements POILogger
{
    private static final String LEVEL_STRINGS_SHORT = "?D?I?W?E?F?";
    private String _cat;
    
    @Override
    public void initialize(final String cat) {
        this._cat = cat;
    }
    
    @Override
    public void _log(final int level, final Object obj1) {
        this._log(level, obj1, null);
    }
    
    @SuppressForbidden("uses printStackTrace")
    @Override
    public void _log(final int level, final Object obj1, final Throwable exception) {
        if (!this.check(level)) {
            return;
        }
        System.out.println("[" + this._cat + "]" + "?D?I?W?E?F?".charAt(Math.min("?D?I?W?E?F?".length() - 1, level)) + " " + obj1);
        if (exception != null) {
            exception.printStackTrace(System.out);
        }
    }
    
    @Override
    public boolean check(final int level) {
        int currentLevel;
        try {
            currentLevel = Integer.parseInt(System.getProperty("poi.log.level", "5"));
        }
        catch (final SecurityException e) {
            currentLevel = 1;
        }
        return level >= currentLevel;
    }
}
