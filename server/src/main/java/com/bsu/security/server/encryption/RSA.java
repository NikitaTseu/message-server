package com.bsu.security.server.encryption;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class RSA {
    private long p;
    private long q;
    private long e;
    private long phi;
    private long n;

    public RSA(int p, int q) throws CryptException {
        this.p = p;
        this.q = q;
        start();
    }

    private void start() throws CryptException {
        setN();
        setE();
        setPhi();
    }

    private long composition() {
        return getP() * getQ();
    }

    private long eulerPQ() {
        return (getP() - 1) * (getQ() - 1);
    }

    private long findExpNum() throws CryptException {
        int num = 0;
        long euler = eulerPQ();
        for (long i = euler; i > 1; i -= 1) {
            if (isPrime(i) && gcd(i, euler) == 1) {
                return i;
            }
        }
        throw new CryptException("It's bad numbers q and p");
    }

    private static boolean isPrime(long n) {
        if (n <= 1) {
            return false;
        }
        for (int i = 2; i < Math.sqrt(n); i++) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }

    private long getP() {
        return p;
    }

    private void setP(long p) throws CryptException {
        if (isPrime(p)) {
            this.p = p;
        } else {
            throw new CryptException("p is not prime");
        }
    }

    private long getQ() {
        return q;
    }

    private void setQ(long q) throws CryptException {
        if (isPrime(q)) {
            this.q = q;
        } else {
            throw new CryptException("q is not prime");
        }

    }

    public Pair getOpenKey() throws CryptException {
        return new Pair(getE(), getN());
    }

    public Pair getSecretKey() {
        return new Pair(modInverse(getE(), getPhi()), getN());
    }

    public long gcd(long a, long b) {
        return b == 0 ? a : gcd(b, a % b);
    }

    private long modInverse(long a, long m) {
        a = a % m;
        for (long x = 1; x < m*2; x++)
            if ((a * x) % m == 1&&x!=getE())
                return x;
        return 1;
    }

    private long getE() {
        return e;
    }

    private void setE() throws CryptException {
        this.e = findExpNum();
    }

    private long getPhi() {
        return phi;
    }

    private void setPhi() {
        this.phi = eulerPQ();
    }

    private long getN() {
        return n;
    }

    private void setN() {
        this.n = composition();
    }
    public byte[] encrypt(Pair openKey, byte[] session){
        String s = Base64.getEncoder().encodeToString(session);
        char[] characterStream = s.toCharArray();
        List<BigInteger> encrypted = new ArrayList<>();

        for (char a: characterStream) {
            BigInteger b = new BigInteger(String.valueOf((int)a));
            encrypted.add( b.pow((int) openKey.getE()).mod(BigInteger.valueOf(openKey.getN())));
        }

        StringBuilder bigIntToString = new StringBuilder();
        for (BigInteger x: encrypted){
            bigIntToString.append(x.toString());
            bigIntToString.append(",");
        }
        return bigIntToString.toString().getBytes(StandardCharsets.UTF_8);
    }

    public byte[] decrypt(Pair closeKey, byte[] cipher){
        String bigIntToString = new String(cipher, StandardCharsets.UTF_8);
        String[] parsed = bigIntToString.split(",");
        List<BigInteger> list = new ArrayList<>();
        for(String s: parsed){
            list.add(new BigInteger(s));
        }

        StringBuilder decrypted = new StringBuilder();
        for (BigInteger a:list) {
            decrypted.append((char)(a.pow((int) closeKey.getE()).mod(BigInteger.valueOf(closeKey.getN()))).intValue());
        }
        return Base64.getDecoder().decode(decrypted.toString());
        //return decrypted.toString().getBytes(StandardCharsets.UTF_8);
    }

}
