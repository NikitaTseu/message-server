package com.bsu.security.server;

import com.bsu.security.server.encryption.AES;
import com.bsu.security.server.encryption.CryptException;
import com.bsu.security.server.encryption.Pair;
import com.bsu.security.server.encryption.RSA;

import java.util.Random;

public class EncryptionService {
    private byte[] sessionKey;
    private RSA rsa;

    {
        try {
            rsa = new RSA(73, 79);
        } catch (CryptException e) {
            e.printStackTrace();
        }
    }

    public EncryptionService(){
        sessionKey = new byte[16];
        new Random().nextBytes(sessionKey);
    }

    public byte[] getSessionKey() {
        return sessionKey;
    }

    public byte[] encrypt(String strToEncrypt)
    {
        return AES.encrypt(strToEncrypt, sessionKey);
    }

    public String decrypt(byte[] encryptedIvTextBytes)
    {
        return AES.decrypt(encryptedIvTextBytes, sessionKey);
    }

    public byte[] encryptRSA(Pair publicKey, byte[] key){
        return this.rsa.encrypt(publicKey, key);
    }
}

