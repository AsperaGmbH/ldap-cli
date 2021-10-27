package com.usu.ldapcli.cmd;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import com.usu.ldapcli.util.LdapSystem;

public class LdapConnectionTestCommand implements CommandRunnable {
    private static final Logger LOGGER = Logger.getLogger(LdapConnectionTestCommand.class.getName());

    @Override
    public void run() {
        ping();
    }

    private void ping() {
        try {
            LdapSystem.getInstance().initialize();
            String hostname = LdapSystem.getInstance().getValue("hostname");
            String port = LdapSystem.getInstance().getValue("port");
            String securityPrincipal = LdapSystem.getInstance().getValue("security-principal");
            String password = LdapSystem.getInstance().getValue("password");
            boolean ssl = LdapSystem.getInstance().getValue("ssl").contains("true") ? true : false;
            Properties env = new Properties();
            StringBuilder sb = new StringBuilder();
            if (ssl) {
                sb.append("ldaps://");
            } else {
                sb.append("ldap://");
            }
            sb.append(hostname).append(":").append(port);
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.PROVIDER_URL, sb.toString());
            env.put(Context.SECURITY_PRINCIPAL, securityPrincipal);
            env.put(Context.SECURITY_CREDENTIALS, password);
            env.put("com.sun.jndi.ldap.read.timeout", "5000");
            env.put("com.sun.jndi.ldap.connect.timeout", "1000");
            LdapContext ctx = new InitialLdapContext(env, null);

            LOGGER.log(Level.INFO, "Connection on LDAP " + hostname + " was successful!");

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "#### Connection FAILED! ####");
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }
}
