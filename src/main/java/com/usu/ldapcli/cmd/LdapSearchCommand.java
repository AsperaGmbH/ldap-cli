package com.usu.ldapcli.cmd;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.PagedResultsControl;
import javax.naming.ldap.PagedResultsResponseControl;

import org.apache.commons.lang.StringUtils;

import com.usu.ldapcli.util.LdapSystem;

public class LdapSearchCommand implements CommandRunnable {
    private static final Logger LOGGER = Logger.getLogger(LdapSearchCommand.class.getName());
    private static final int PAGE_SIZE = 1000;

    @Override
    public void run() {
        search();
    }

    private void search() {
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
            String userName = CommandContext.getInstance().nextArgument();
            String searchBaseOverride = CommandContext.getInstance().nextArgument();
            getAllUsers(env, userName, searchBaseOverride);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "#### Connection FAILED! ####");
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

    }

    private static SearchResult getAllUsers(Properties env, String sAMAccountName, String searchBaseOverride) {
        try {
            LdapContext ctx = new InitialLdapContext(env, null);
            long now = System.currentTimeMillis();
            byte[] cookie = null;
            ctx.setRequestControls(new Control[] { new PagedResultsControl(PAGE_SIZE, Control.CRITICAL) });
            int userCount = 0;
            do {

                NamingEnumeration<SearchResult> results = null;
                SearchControls sc = new SearchControls();
                sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
                String searchBase = StringUtils.isNotEmpty(searchBaseOverride) ? searchBaseOverride
                        : LdapSystem.getInstance().getValue("searchbase");
                if (sAMAccountName != null && sAMAccountName.equals("*")) {
                    results = ctx.search(searchBase, "(&(objectClass=user)(!(objectClass=computer)))", sc);
                } else {
                    results = ctx.search(searchBase, "(&(objectClass=user)(sAMAccountName={0}))",
                            new Object[] { sAMAccountName }, sc);
                }

                while (results != null && results.hasMore()) {
                    SearchResult entry = (SearchResult) results.next();
                    if (entry.getAttributes().get("sAMAccountName").contains(sAMAccountName)) {
                        System.out.println(entry.getName());
                        System.out.println(entry.getAttributes().get("sAMAccountName"));
                        System.out.println(entry.getAttributes().get("givenName"));
                        long runtime = System.currentTimeMillis() - now;
                        userCount++;
                        LOGGER.info("Query " + runtime + " ms executed time: usercount selected users => " + userCount);
                        LOGGER.info("### LDAP Search query was successful. ####");
                        return entry;
                    } else {
                        System.out.println("\n----- User count: " + userCount + " ---------");
                        System.out.println(entry.getName());
                        System.out.println(entry.getAttributes().get("sAMAccountName"));
                        System.out.println(entry.getAttributes().get("givenName"));
                        userCount++;
                    }
                }
                Control[] controls = ctx.getResponseControls();
                if (controls != null) {
                    for (int i = 0; i < controls.length; i++) {
                        if (controls[i] instanceof PagedResultsResponseControl) {
                            PagedResultsResponseControl prrc = (PagedResultsResponseControl) controls[i];
                            cookie = prrc.getCookie();
                        }
                    }
                } else {
                    System.out.println("No controls were sent from the server");
                }
                // Re-activate paged results
                ctx.setRequestControls(new Control[] { new PagedResultsControl(PAGE_SIZE, cookie, Control.CRITICAL) });
            } while (cookie != null);
            ctx.close();
            long runtime = System.currentTimeMillis() - now;
            LOGGER.info("\n\nQuery " + runtime + " ms executed time: usercount selected users => " + userCount);
            LOGGER.info("### LDAP Search query was successful. ####");
        } catch (NamingException | IOException e) {
            LOGGER.log(Level.SEVERE, "PagedSearch failed.", e);
        }
        return null;
    }
}
