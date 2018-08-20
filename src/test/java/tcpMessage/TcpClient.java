package tcpMessage;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.filter.ssl.SslFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import protocols.tcp.KakaduCodecFactory;
import tcpMessage.controllers.ClientPanelController;

import javax.net.ssl.SSLContext;
import java.net.InetSocketAddress;

public class TcpClient {
    private String ip;
    private int port;

    private static IoSession session;
    private IoConnector connector;

    public TcpClient(String ip, int port, ClientPanelController clientPanelController) throws Exception {
        this.ip = ip;
        this.port = port;
        connector = new NioSocketConnector();
        connector.getSessionConfig().setReadBufferSize(2048);

        SSLContext sslContext = new SSLClientContextGenerator().getSslContext();
        System.out.println("SSLContext protocol is: " + sslContext.getProtocol());

        SslFilter sslFilter = new SslFilter(sslContext);
        sslFilter.setUseClientMode(true);
        connector.getFilterChain().addFirst("sslFilter", sslFilter);

        connector.getFilterChain().addLast("logger", new LoggingFilter());
        connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new KakaduCodecFactory()));
        TcpClientHandler handler = new TcpClientHandler();
        handler.setClientPanelController(clientPanelController);
        connector.setHandler(handler);
        ConnectFuture future = connector.connect(new InetSocketAddress(ip, port));
        future.awaitUninterruptibly();

        if (!future.isConnected()) {
            return;
        }
        session = future.getSession();
        session.getConfig().setUseReadOperation(true);
    }

    public void send(Object message){
        session.write(message);
    }

    public void closeConnection() {
        connector.dispose();
    }

}
