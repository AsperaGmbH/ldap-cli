package com.usu.ldapcli.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.SystemUtils;

/**
 *
 * @author daniel
 */
public class LdapSystem {
	

    private static LdapSystem ldap;
    private static Properties ldapProperties;
    private static final Logger LOGGER = Logger.getLogger(LdapSystem.class.getName());
    public final static String PROJECT_NAME = "ldapcli";

    /**
     * Privater leerer Standardkonstruktor.
     */
    private LdapSystem() {
        ldapProperties = new Properties();

    }

    /**
     * Liefert eine statische Instanz f�r die Ressourcen.
     *
     * @return die Ressourcen.
     */
    public static synchronized LdapSystem getInstance() {
        if (ldap == null) {
        	ldap = new LdapSystem();
        	ldap.initialize();
        }
        return ldap;
    }

    public void initialize() {
        try {
            String fileName = null;
            String filePath = null;
            if (SystemUtils.IS_OS_WINDOWS) {
                fileName = System.getProperty("user.home") + "\\." + PROJECT_NAME + "\\ldap.properties";
                filePath = System.getProperty("user.home") + "\\." + PROJECT_NAME + "";
            } else {
                fileName = System.getProperty("user.home") + "/." + PROJECT_NAME + "/ldap.properties";
                filePath = System.getProperty("user.home") + "/." + PROJECT_NAME;
            }

            InputStream readInConfig = null;
            try {
                readInConfig = new FileInputStream(fileName);
            } catch (Exception e) {
                LOGGER.log(Level.FINER, "The config properties could not found. So it will be created!");
            }
            if (readInConfig == null) {
                boolean mkdirs = new File(filePath).mkdirs();
                File file = new File(fileName);
                FileOutputStream fileOut = new FileOutputStream(file);
                ldapProperties.put("domain", "local");
                ldapProperties.put("searchbase", "unknown");
                ldapProperties.put("hostname", "unknown");
                ldapProperties.put("port", "unknown");
                ldapProperties.put("security-principal", "unknown");
                ldapProperties.put("password", "unknown");
                ldapProperties.put("ssl", "false");
                
                ldapProperties.store(fileOut, "ldap-cli");
                fileOut.close();
            }
            ldap.ldapProperties.load(new FileInputStream(fileName));
        } catch (IOException ioex) {
            ldap = null;
            String msg = "Error during loading the properties";
            LOGGER.log(Level.SEVERE, ioex.getMessage(), ioex);
            throw new IllegalStateException(msg, ioex);
        }
    }

    public String getValue(String key) {
        return ldapProperties.getProperty(key);
    }

}
