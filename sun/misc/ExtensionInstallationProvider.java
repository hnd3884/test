package sun.misc;

public interface ExtensionInstallationProvider
{
    boolean installExtension(final ExtensionInfo p0, final ExtensionInfo p1) throws ExtensionInstallationException;
}
