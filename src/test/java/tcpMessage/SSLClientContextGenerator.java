package tcpMessage;

import org.apache.mina.filter.ssl.KeyStoreFactory;
import org.apache.mina.filter.ssl.SslContextFactory;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.security.KeyStore;

/**
 * Created by Szymon on 20.08.2018.
 */
public class SSLClientContextGenerator {
    public SSLContext getSslContext() throws Exception {
        SSLContext sslContext = null;
       // ClassLoader classLoader = getClass().getClassLoader();
        File keyStoreFile = new File("src/test/java/tcpMessage/Keystore.jks");
        System.out.println(keyStoreFile.getAbsolutePath());

        File trustStoreFile = new File("src/test/java/tcpMessage/truststore.jks");

        if (keyStoreFile.exists() && trustStoreFile.exists()) {
            final KeyStoreFactory keyStoreFactory = new KeyStoreFactory();
            System.out.println("Url is: " + keyStoreFile.getAbsolutePath());
            keyStoreFactory.setDataFile(keyStoreFile);
            keyStoreFactory.setPassword("piwotomojepaliwo");

            final KeyStoreFactory trustStoreFactory = new KeyStoreFactory();
            trustStoreFactory.setDataFile(trustStoreFile);
            trustStoreFactory.setPassword("piwotomojepaliwo");

            final SslContextFactory sslContextFactory = new SslContextFactory();
            final KeyStore keyStore = keyStoreFactory.newInstance();
            sslContextFactory.setKeyManagerFactoryKeyStore(keyStore);

            final KeyStore trustStore = trustStoreFactory.newInstance();
            sslContextFactory.setTrustManagerFactoryKeyStore(trustStore);
            sslContextFactory.setKeyManagerFactoryKeyStorePassword("piwotomojepaliwo");
            sslContext = sslContextFactory.newInstance();
            System.out.println("SSL provider is: " + sslContext.getProvider());
        } else {
            System.out.println("Keystore or Truststore file does not exist");
        }
        return sslContext;
    }
}
