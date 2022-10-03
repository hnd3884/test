package java.net;

class Parts
{
    String path;
    String query;
    String ref;
    
    Parts(String path) {
        final int index = path.indexOf(35);
        this.ref = ((index < 0) ? null : path.substring(index + 1));
        path = ((index < 0) ? path : path.substring(0, index));
        final int lastIndex = path.lastIndexOf(63);
        if (lastIndex != -1) {
            this.query = path.substring(lastIndex + 1);
            this.path = path.substring(0, lastIndex);
        }
        else {
            this.path = path;
        }
    }
    
    String getPath() {
        return this.path;
    }
    
    String getQuery() {
        return this.query;
    }
    
    String getRef() {
        return this.ref;
    }
}
