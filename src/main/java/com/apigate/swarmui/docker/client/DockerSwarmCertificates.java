package com.apigate.swarmui.docker.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.ssl.SSLContexts;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.spotify.docker.client.DockerCertificates;
import com.spotify.docker.client.DockerCertificatesStore;
import com.spotify.docker.client.exceptions.DockerCertificateException;

public class DockerSwarmCertificates implements DockerCertificatesStore {

	  public static final String DEFAULT_CA_CERT_NAME = "ca.pem";
	  public static final String DEFAULT_CLIENT_CERT_NAME = "cert.pem";
	  public static final String DEFAULT_CLIENT_KEY_NAME = "key.pem";

	  private static final char[] KEY_STORE_PASSWORD = "docker!!11!!one!".toCharArray();
	  private static final Logger log = LoggerFactory.getLogger(DockerCertificates.class);

	  private final SSLContext sslContext;

	  public DockerSwarmCertificates(String caPem, String certPem, String keyPem) throws DockerCertificateException {
	    this(new Builder().dockerCertPath(caPem, certPem, keyPem));
	  }

	  private DockerSwarmCertificates(final Builder builder) throws DockerCertificateException {
	    if ((builder.caPem == null) || (builder.certPem == null)
	        || (builder.keyPem == null)) {
	      throw new DockerCertificateException(
	          "caPem, certPem, and keyPem must all be specified");
	    }

	    try {

	      final PrivateKey clientKey = readPrivateKey(builder.keyPem);
	      final List<Certificate> clientCerts = readCertificates(builder.certPem);

	      final KeyStore keyStore = newKeyStore();
	      keyStore.setKeyEntry("key", clientKey, KEY_STORE_PASSWORD,
	              clientCerts.toArray(new Certificate[clientCerts.size()]));

	      final List<Certificate> caCerts = readCertificates(builder.caPem);

	      final KeyStore trustStore = newKeyStore();
	      for (Certificate caCert : caCerts) {
	        X509Certificate crt = (X509Certificate) caCert;
	        String alias = crt.getSubjectX500Principal()
	                .getName();
	        trustStore.setCertificateEntry(alias, caCert);
	      }

	      this.sslContext = builder.sslContextFactory
	          .newSslContext(keyStore, KEY_STORE_PASSWORD, trustStore);
	    } catch (DockerCertificateException e) {
	      throw e;
	    } catch (CertificateException
	        | IOException
	        | NoSuchAlgorithmException
	        | InvalidKeySpecException
	        | KeyStoreException
	        | UnrecoverableKeyException
	        | KeyManagementException e) {
	      throw new DockerCertificateException(e);
	    }
	  }

	  private KeyStore newKeyStore() throws CertificateException, NoSuchAlgorithmException,
	      IOException, KeyStoreException {
	    final KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
	    keyStore.load(null);
	    return keyStore;
	  }

	  private PrivateKey readPrivateKey(String keyPem) throws IOException, InvalidKeySpecException,
	      NoSuchAlgorithmException, DockerCertificateException {
		  
		  try(InputStream is = new ByteArrayInputStream(keyPem.getBytes());
		  InputStreamReader reader = new InputStreamReader(is); 
				  PEMParser pemParser = new PEMParser(reader)) {
//	    try (BufferedReader reader = Files.newBufferedReader(isr, Charset.defaultCharset());
//	         PEMParser pemParser = new PEMParser(isr)) {

	      final Object readObject = pemParser.readObject();

	      if (readObject instanceof PEMKeyPair) {
	        PEMKeyPair clientKeyPair = (PEMKeyPair) readObject;
	        return generatePrivateKey(clientKeyPair.getPrivateKeyInfo());
	      } else if (readObject instanceof PrivateKeyInfo) {
	        return generatePrivateKey((PrivateKeyInfo) readObject);
	      }

	      throw new DockerCertificateException("Can not generate private key ");
	    }
	  }

	  private static PrivateKey generatePrivateKey(PrivateKeyInfo privateKeyInfo) throws IOException,
	          InvalidKeySpecException, NoSuchAlgorithmException {
	    final PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(privateKeyInfo.getEncoded());
	    final KeyFactory kf = KeyFactory.getInstance("RSA");
	    return kf.generatePrivate(spec);
	  }

	  private List<Certificate> readCertificates(String certPem) throws CertificateException, IOException {
	    try (InputStream inputStream = new ByteArrayInputStream(certPem.getBytes())) {
	      final CertificateFactory cf = CertificateFactory.getInstance("X.509");
	      return new ArrayList<>(cf.generateCertificates(inputStream));
	    }
	  }

	  public SSLContext sslContext() {
	    return this.sslContext;
	  }

	  public HostnameVerifier hostnameVerifier() {
	    return NoopHostnameVerifier.INSTANCE;
	  }

	  public interface SslContextFactory {
	    SSLContext newSslContext(KeyStore keyStore, char[] keyPassword, KeyStore trustStore)
	        throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException,
	        KeyManagementException;
	  }

	  private static class DefaultSslContextFactory implements SslContextFactory {
	    @Override
	    public SSLContext newSslContext(KeyStore keyStore, char[] keyPassword, KeyStore trustStore)
	        throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException,
	        KeyManagementException {
	      return SSLContexts.custom()
	              .loadTrustMaterial(trustStore, null)
	              .loadKeyMaterial(keyStore, keyPassword)
	              .build();
	    }
	  }

	  public static Builder builder() {
	    return new Builder();
	  }

	  public static class Builder {

	    private SslContextFactory sslContextFactory = new DefaultSslContextFactory();
	    
	    private String caPem;
	    private String certPem;
	    private String keyPem;
	    
	    public Builder dockerCertPath(String caPem, String certPem, String keyPem) {
	      this.caPem = caPem;
	      this.certPem = certPem;
	      this.keyPem = keyPem;
	      
	      return this;
	    }

	    public Builder sslFactory(final SslContextFactory sslContextFactory) {
	      this.sslContextFactory = sslContextFactory;
	      return this;
	    }

	    public Optional<DockerCertificatesStore> build() throws DockerCertificateException {
	      if (this.caPem == null || this.certPem == null || this.keyPem == null) {
	        log.debug("caPem, certPem or keyPem not specified, not using SSL");
	        return Optional.absent();
	      } else if (this.caPem != null && this.certPem != null
	                 && this.keyPem != null) {
	        return Optional.of((DockerCertificatesStore) new DockerSwarmCertificates(this));
	      } else {
	        log.debug("{}, {} or {} does not exist, not using SSL", this.caPem, this.certPem,
	                  this.keyPem);
	        return Optional.absent();
	      }
	    }
	  }
}
