package org.logstash.secret.store;

import org.junit.Test;

import static org.junit.Assert.*;

public class SecretStoreUtilTest {

    @Test
    public void testObfuscate(){
        char[] a = SecretStoreUtil.obfuscate("jakelandis".toCharArray());
        System.out.println(a);
        System.out.println(SecretStoreUtil.deObfuscate(a));
    }

}