package com.feiyu.core.communicate;

import com.feiyu.core.communicate.config.SslConfig;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.security.x509.X509CertImpl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * @author zhufeifei 2023/9/13
 **/

public class SslHandlerProvider {
    private final static Logger LOG = LoggerFactory.getLogger(SslHandlerProvider.class);
    public static SslHandler getSslHandler(SslConfig config) {
        // 指定密钥仓库文件地址
        try (InputStream is = Files.newInputStream(Paths.get(SslHandlerProvider.class.getClassLoader().getResource(config.getJksPath()).getPath()));
             InputStream cerIs = Files.newInputStream(Paths.get(SslHandlerProvider.class.getClassLoader().getResource(config.getCerPath()).getPath()))) {
            // 新建一个KeyStore对象，从流中加载密钥仓库，密钥仓库类型为JKS
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(is, config.getKeyStorePassword().toCharArray());
            X509CertImpl cert = new X509CertImpl(cerIs);
            // 获取私钥
            PrivateKey privateKey = (PrivateKey) keyStore.getKey(config.getPrivateKeyAlias(), config.getPrivatePassword().toCharArray());
            // 获取证书
            X509Certificate certificate = (X509Certificate) keyStore.getCertificate(config.getCertAlias());
            // 通过SslContextBuilder构建SslContext
            SslContext sslContext = SslContextBuilder.forClient()
                    .keyManager(privateKey, "123456", certificate)
                    .trustManager(cert)
                    .build();
            // 通过SslContext创建新的SslHandler
            return sslContext.newHandler(PooledByteBufAllocator.DEFAULT);
        } catch (UnrecoverableKeyException | CertificateException | KeyStoreException | IOException | NoSuchAlgorithmException e) {
            LOG.error("ssl handler occur error.", e);
            throw new RuntimeException(e);
        }
    }

    
}
