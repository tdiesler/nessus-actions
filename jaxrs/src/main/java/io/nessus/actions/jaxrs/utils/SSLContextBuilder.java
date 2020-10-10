package io.nessus.actions.jaxrs.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStore.PasswordProtection;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.nessus.common.AssertArg;
import io.nessus.common.AssertState;

public class SSLContextBuilder {

	static final Logger LOG = LoggerFactory.getLogger(SSLContextBuilder.class);
	
	private String keystoreType = KeyStore.getDefaultType();
	private char[] keystorePassword = "changeit".toCharArray();
	private Path keystorePath;
	private KeyMaterial privKeyMaterial;
	private KeyMaterial certMaterial;
	private KeyMaterial pemMaterial;
	
	static class KeyMaterial {
		final String alias;
		final Path path;
		KeyMaterial(String alias, Path path) {
			this.alias = alias;
			this.path = path;
		}
	}
	
	public SSLContextBuilder keystorePath(Path keysPath) {
		this.keystorePath = keysPath;
		return this;
	}
	
	public SSLContextBuilder addPem(String alias, Path pemPath) {
		this.pemMaterial = new KeyMaterial(alias, pemPath);
		return this;
	}
	
	public SSLContextBuilder addCertificate(String alias, Path certPath) {
		this.certMaterial = new KeyMaterial(alias, certPath);
		return this;
	}
	
	public SSLContextBuilder addPrivateKey(String alias, Path privKeyPath) {
		this.privKeyMaterial = new KeyMaterial(alias, privKeyPath);
		return this;
	}
	
	public SSLContextBuilder keystoreType(String keysType) {
		this.keystoreType = keysType;
		return this;
	}
	
	public SSLContextBuilder keystorePassword(String keysPassword) {
		this.keystorePassword = keysPassword.toCharArray();
		return this;
	}
	
    public SSLContext build() throws IOException, GeneralSecurityException {
        
        KeyStore keyStore = loadKeyStore(keystorePath, keystoreType, keystorePassword);

        SSLContext sslContext;
        try {
        	
            KeyManager[] keyManagers = buildKeyManagers(keyStore, keystorePassword);
            TrustManager[] trustManagers = buildTrustManagers(keyStore);
            
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagers, trustManagers, null);
        }
        catch (NoSuchAlgorithmException | KeyManagementException ex) {
            throw new IOException("Unable to create and initialise the SSLContext", ex);
        }

        return sslContext;
    }

    private KeyStore loadKeyStore(Path keysPath, String keysType, char[] keysPassword) throws IOException, GeneralSecurityException {
		AssertArg.notNull(keysPath, "Null keysPath");
		AssertArg.notNull(keysType, "Null keysType");
		AssertArg.notNull(keysPassword, "Null keysPassword");
    	
    	KeyStore keystore = KeyStore.getInstance(keysType);
    	
    	if (keysPath.toFile().isFile()) {
    		
    		LOG.info("Loading keystore file: {}", keysPath);
            
    		try (FileInputStream fis = new FileInputStream(keysPath.toFile())) {
                keystore.load(fis, keysPassword);
            }
            
    	} else {
    		
    		LOG.info("Creating keystore ...");
            
            keystore.load(null, keysPassword);
            
            Certificate cert = null;
            
            if (pemMaterial != null) {
            	
            	Path pemPath = pemMaterial.path;
            	String pemAlias = pemMaterial.alias;
            	
        		LOG.info("Reading pem material: {}", pemPath);
        		
            	cert = readCertificate(pemPath);
            	if (cert != null) {
                    keystore.setCertificateEntry(pemAlias, cert);
            	}
            	
            	RSAPrivateKey privKey = readPrivateKey(pemPath);
            	if (privKey != null) {
            		PrivateKeyEntry keyEntry = new PrivateKeyEntry(privKey, new Certificate[] { cert });
                    keystore.setEntry(pemAlias, keyEntry, new PasswordProtection(keysPassword));
            	}
            }
            
            if (certMaterial != null) {
            	
            	Path crtPath = certMaterial.path;
            	String crtAlias = certMaterial.alias;
            	
        		LOG.info("Reading certificate material: {}", crtPath);
        		
            	cert = readCertificate(crtPath);
            	AssertState.notNull(cert, "Null certificate");
            	
                keystore.setCertificateEntry(crtAlias, cert);
            }
            
            if (privKeyMaterial != null) {
            	
            	Path privKeyPath = privKeyMaterial.path;
            	String privAlias = privKeyMaterial.alias;
            	
        		LOG.info("Reading private key material: {}", privKeyPath);
        		
            	RSAPrivateKey privKey = readPrivateKey(privKeyPath);
            	AssertState.notNull(privKey, "Null private key");
            	AssertState.notNull(cert, "Null certificate");
            	
        		PrivateKeyEntry keyEntry = new PrivateKeyEntry(privKey, new Certificate[] { cert });
                keystore.setEntry(privAlias, keyEntry, new PasswordProtection(keysPassword));
            }
            
    		LOG.info("Storing keystore file: {}", keysPath);
    		
            try (FileOutputStream fos = new FileOutputStream(keysPath.toFile())) {
            	keystore.store(fos, keysPassword);
            }    	
    	}
    	
        return keystore;
    }

    private Certificate readCertificate(Path pemPath) throws IOException, GeneralSecurityException {
    	
	    String content = readPemContent(pemPath, "CERTIFICATE");
	    if (content.length() == 0)
	    	return null;
	    
	    byte[] decoded = Base64.getDecoder().decode(content);
	    InputStream bais = new ByteArrayInputStream(decoded);
	 
    	CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
        return certFactory.generateCertificate(bais);
	}

    @SuppressWarnings("unused")
	private RSAPublicKey readPublicKey(Path pemPath) throws IOException, GeneralSecurityException {
    	
	    String content = readPemContent(pemPath, "PUBLIC KEY");
	    if (content.length() == 0)
	    	return null;
	 
	    byte[] decoded = Base64.getDecoder().decode(content);
	 
	    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
	    X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoded);
	    return (RSAPublicKey) keyFactory.generatePublic(keySpec);
	}

    private RSAPrivateKey readPrivateKey(Path pemPath) throws IOException, GeneralSecurityException {
    	
	    String content = readPemContent(pemPath, "PRIVATE KEY");
	    if (content.length() == 0)
	    	return null;
	 
	    byte[] decoded = Base64.getDecoder().decode(content);
	 
	    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
	    PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);
	    return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
	}
    
	private String readPemContent(Path pemPath, String type) throws IOException {

	    String beginMarker = "-----BEGIN " + type + "-----";
	    String endMarker = "-----END " + type + "-----";
		boolean readContent = false;
	    
    	InputStream bais = new ByteArrayInputStream(Files.readAllBytes(pemPath));
    	BufferedReader br = new BufferedReader(new InputStreamReader(new BufferedInputStream(bais)));
	    StringWriter sw = new StringWriter();
	    
    	String line = br.readLine();
    	while (line != null) {
    		
    		if (!readContent && line.equals(beginMarker)) {
    			readContent = true;
    		}
    		
    		else if (readContent && line.equals(endMarker)) {
    			readContent = false;
    		}
    		
    		else if (readContent) {
    			sw.write(line);
    		}
    		
    		line = br.readLine();
    	}
    	
	    String content = sw.toString();
		return content;
	}

    private KeyManager[] buildKeyManagers(final KeyStore keyStore, char[] keysPassword) throws GeneralSecurityException  {
        String keyAlgorithm = KeyManagerFactory.getDefaultAlgorithm();
		KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(keyAlgorithm);
        keyManagerFactory.init(keyStore, keysPassword);
        KeyManager[] keyManagers = keyManagerFactory.getKeyManagers();
        return keyManagers;
    }

    private TrustManager[] buildTrustManagers(final KeyStore trustStore) throws IOException, GeneralSecurityException {
        String trustAlgorithm = KeyManagerFactory.getDefaultAlgorithm();
		TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(trustAlgorithm);
        trustManagerFactory.init(trustStore);
        TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
        return trustManagers;
    }
}