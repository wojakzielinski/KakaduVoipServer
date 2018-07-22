package tcpMessage;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.ReadFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import protocols.KakaduCodecFactory;
import tcpMessage.controllers.ClientPanelController;

import java.net.InetSocketAddress;

public class TcpClient {
    private String ip;
    private int port;

    private static IoSession session;
    private IoConnector connector;

    public TcpClient(String ip, int port, ClientPanelController clientPanelController) {
        this.ip = ip;
        this.port = port;
        connector = new NioSocketConnector();
        connector.getSessionConfig().setReadBufferSize(2048);

        connector.getFilterChain().addLast("logger", new LoggingFilter());
        connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new KakaduCodecFactory()));
        MinaClientHandler handler = new MinaClientHandler();
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
