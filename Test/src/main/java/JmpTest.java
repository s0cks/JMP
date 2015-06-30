import io.github.s0cks.jmp.server.CPUProfiler;
import io.github.s0cks.jmp.server.JmpServer;
import io.github.s0cks.jmp.server.MemorySpy;
import org.apache.log4j.PropertyConfigurator;

public final class JmpTest{
    public static void main(String... args)
    throws Exception{
        PropertyConfigurator.configure(System.class.getResource("/log4j.properties"));
        JmpServer.instance().start();
        MemorySpy.instance().start();
        CPUProfiler.instance().start();
        while(true){}
    }
}