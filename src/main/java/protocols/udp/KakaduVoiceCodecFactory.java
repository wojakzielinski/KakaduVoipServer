package protocols.udp;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

/**
 * Created by Szymon on 23.07.2018.
 */
public class KakaduVoiceCodecFactory implements ProtocolCodecFactory {

    public ProtocolEncoder getEncoder(IoSession ioSession) throws Exception {
        return new KakaduVoiceEncoder();
    }

    public ProtocolDecoder getDecoder(IoSession ioSession) throws Exception {
        return new KakaduVoiceDecoder();
    }
}