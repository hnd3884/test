package sun.rmi.transport.proxy;

final class CGIPingCommand implements CGICommandHandler
{
    @Override
    public String getName() {
        return "ping";
    }
    
    @Override
    public void execute(final String s) {
        System.out.println("Status: 200 OK");
        System.out.println("Content-type: application/octet-stream");
        System.out.println("Content-length: 0");
        System.out.println("");
    }
}
