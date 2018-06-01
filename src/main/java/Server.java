import handler.TcpServerHandler;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioDatagramAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import protocols.KakaduCodecFactory;

import java.net.InetSocketAddress;
import java.util.ResourceBundle;

public class Server {
    public static void main(String[] args) throws Exception {
        ResourceBundle serverProperties = ResourceBundle.getBundle("server");
        createTcpServer(serverProperties);
        createUdpServer(serverProperties);
    }

    private static void createTcpServer(ResourceBundle serverProperties) throws Exception {
        IoAcceptor acceptor = new NioSocketAcceptor();

        acceptor.getFilterChain().addLast("logger", new LoggingFilter());
        acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new KakaduCodecFactory()));

        acceptor.setHandler(new TcpServerHandler());

        acceptor.getSessionConfig().setReadBufferSize(2048);
        acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);

        int tcpPort = Integer.parseInt(serverProperties.getString("server.tcp.port"));
        acceptor.bind(new InetSocketAddress(tcpPort));

    }

    private static void createUdpServer(ResourceBundle serverProperties) throws Exception {
//        NioDatagramAcceptor acceptor = new NioDatagramAcceptor();
//        acceptor.setHandler(new MemoryMonitorHandler(this));
    }

}