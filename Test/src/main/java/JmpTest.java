import io.github.s0cks.jmp.server.JmpServer;
import org.apache.log4j.PropertyConfigurator;

public final class JmpTest{
    public static void main(String... args)
    throws Exception{
        PropertyConfigurator.configure(System.class.getResource("/log4j.properties"));
        JmpServer server = JmpServer.instance();
        server.start();
        System.in.read();
    }
}