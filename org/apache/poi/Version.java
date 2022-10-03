package org.apache.poi;

public class Version
{
    private static final String VERSION_STRING = "4.1.2";
    private static final String RELEASE_DATE = "20200217";
    
    public static String getVersion() {
        return "4.1.2";
    }
    
    public static String getReleaseDate() {
        return "20200217";
    }
    
    public static String getProduct() {
        return "POI";
    }
    
    public static String getImplementationLanguage() {
        return "Java";
    }
    
    public static void main(final String[] args) {
        System.out.println("Apache " + getProduct() + " " + getVersion() + " (" + getReleaseDate() + ")");
    }
}
