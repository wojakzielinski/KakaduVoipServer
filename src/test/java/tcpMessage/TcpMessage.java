package tcpMessage;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

public class TcpMessage {
    private static String HOSTNAME = "localhost";
    private static int PORT = 9123;

    public static void main(String[] args) throws IOException, InterruptedException
    {
        IoConnector connector = new NioSocketConnector();
        connector.getSessionConfig().setReadBufferSize(2048);

        connector.getFilterChain().addLast("logger", new LoggingFilter());
        connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"))));

        connector.setHandler(new MinaClientHandler("Hello Server"));
        ConnectFuture future = connector.connect(new InetSocketAddress(HOSTNAME, PORT));
        future.awaitUninterruptibly();

        if (!future.isConnected())
        {
            return;
        }
        IoSession session = future.getSession();
        session.getConfig().setUseReadOperation(true);
        session.write("Message to server");
        System.out.println(session.read().getMessage());

        System.out.println("After Writing");
        connector.dispose();

    }
}
