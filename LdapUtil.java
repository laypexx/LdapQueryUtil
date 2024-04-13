package com.your.project;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

public class LdapUtil {

    private final String ldapUrl = "ldap://0.0.0.0:389";

    //TODO replace "LDAP NAME" with firstname + lastname of ldap account
    private final String secrurityPrincipal = "CN\\=LDAP NAME,OU\\=User,OU\\=GFL,DC\\=G,DC\\=FL,DC\\=local";

    //TODO replace "password" with your ldap passwor
    private final String ldapCredentials = "password";
    
    private final String ldapSearchBase = "OU=User,OU=GFL,DC=A,DC=BC,DC=DE";


    public Set<LdapUser> getAllLDapUsers() throws NamingException {

        DirContext ldapContext = getDirContext();

        String base = this.ldapSearchBase;
        String filter = "(mail=*)";

        SearchControls sc = new SearchControls();
        sc.setSearchScope(SearchControls.SUBTREE_SCOPE);

        NamingEnumeration results = ldapContext.search(base, filter, sc);

        Set<LdapUser> ldapUserSet = new HashSet<LdapUser>();

        while (results.hasMore()) {
            SearchResult sr = (SearchResult) results.next();
            Attributes attrs = sr.getAttributes();

            LdapUser newUser = new LdapUser();

            if (attrs.get("mail") != null) {
                newUser.setEmail(attrs.get("mail").get().toString().toLowerCase());
            }

            if (attrs.get("sAMAccountName") != null) {
                newUser.setUsername(attrs.get("sAMAccountName").get().toString());
            }

            if (attrs.get("department") != null) {
                newUser.setTeam(attrs.get("department").get().toString());
            }

            if (attrs.get("name") != null) {
                newUser.setFullname(attrs.get("name").get().toString());
            }
            ldapUserSet.add(newUser);
        }
        return ldapUserSet;
    }

    private DirContext getDirContext() throws NamingException {
        Hashtable<String, String> env = new Hashtable<String, String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory"); //generally this one
        env.put(Context.PROVIDER_URL, this.ldapUrl);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, this.secrurityPrincipal);
        env.put(Context.SECURITY_CREDENTIALS, this.ldapCredentials);

        return new InitialDirContext(env);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class LdapUser {
        private String fullname;
        private String email;
        private String username;
        private String team;
    }
}
