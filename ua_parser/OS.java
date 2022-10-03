package ua_parser;

import java.util.Map;

public class OS
{
    public static final OS OTHER;
    public final String family;
    public final String major;
    public final String minor;
    public final String patch;
    public final String patchMinor;
    
    public OS(final String family, final String major, final String minor, final String patch, final String patchMinor) {
        this.family = family;
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.patchMinor = patchMinor;
    }
    
    public static OS fromMap(final Map<String, String> m) {
        return new OS(m.get("family"), m.get("major"), m.get("minor"), m.get("patch"), m.get("patch_minor"));
    }
    
    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof OS)) {
            return false;
        }
        final OS o = (OS)other;
        return ((this.family != null && this.family.equals(o.family)) || this.family == o.family) && ((this.major != null && this.major.equals(o.major)) || this.major == o.major) && ((this.minor != null && this.minor.equals(o.minor)) || this.minor == o.minor) && ((this.patch != null && this.patch.equals(o.patch)) || this.patch == o.patch) && ((this.patchMinor != null && this.patchMinor.equals(o.patchMinor)) || this.patchMinor == o.patchMinor);
    }
    
    @Override
    public int hashCode() {
        int h = (this.family == null) ? 0 : this.family.hashCode();
        h += ((this.major == null) ? 0 : this.major.hashCode());
        h += ((this.minor == null) ? 0 : this.minor.hashCode());
        h += ((this.patch == null) ? 0 : this.patch.hashCode());
        h += ((this.patchMinor == null) ? 0 : this.patchMinor.hashCode());
        return h;
    }
    
    @Override
    public String toString() {
        return String.format("{\"family\": %s, \"major\": %s, \"minor\": %s, \"patch\": %s, \"patch_minor\": %s}", (this.family == null) ? "\"\"" : ('\"' + this.family + '\"'), (this.major == null) ? "\"\"" : ('\"' + this.major + '\"'), (this.minor == null) ? "\"\"" : ('\"' + this.minor + '\"'), (this.patch == null) ? "\"\"" : ('\"' + this.patch + '\"'), (this.patchMinor == null) ? "\"\"" : ('\"' + this.patchMinor + '\"'));
    }
    
    static {
        OTHER = new OS("Other", null, null, null, null);
    }
}
