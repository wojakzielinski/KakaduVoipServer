package tcpMessage;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioDatagramConnector;
import protocols.udp.KakaduVoiceCodecFactory;

import java.net.InetSocketAddress;

/**
 * Created by Szymon on 22.07.2018.
 */
public class UdpClient {
    private String ip;
    private int port;

    private static IoSession session;
    private NioDatagramConnector connector;

    public UdpClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
        connector = new NioDatagramConnector();
        connector.getSessionConfig().setReadBufferSize(4096);
        connector.getFilterChain().addLast("logger", new LoggingFilter());
        connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new KakaduVoiceCodecFactory()));
        connector.setHandler(new UdpClientHandler());
        ConnectFuture future = connector.connect(new InetSocketAddress(ip, port));
        future.awaitUninterruptibly();

        if (!future.isConnected()) {
            return;
        }
        session = future.getSession();
        session.getConfig().setUseReadOperation(true);
    }

    public void send(Object message) {
        session.write(message);
    }

    public void closeConnection() {
        connector.dispose();
    }

}
