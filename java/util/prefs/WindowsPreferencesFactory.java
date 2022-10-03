package java.util.prefs;

class WindowsPreferencesFactory implements PreferencesFactory
{
    @Override
    public Preferences userRoot() {
        return WindowsPreferences.getUserRoot();
    }
    
    @Override
    public Preferences systemRoot() {
        return WindowsPreferences.getSystemRoot();
    }
}
