package sun.rmi.transport.proxy;

final class CGIGethostnameCommand implements CGICommandHandler
{
    @Override
    public String getName() {
        return "gethostname";
    }
    
    @Override
    public void execute(final String s) {
        System.out.println("Status: 200 OK");
        System.out.println("Content-type: application/octet-stream");
        System.out.println("Content-length: " + CGIHandler.ServerName.length());
        System.out.println("");
        System.out.print(CGIHandler.ServerName);
        System.out.flush();
    }
}
