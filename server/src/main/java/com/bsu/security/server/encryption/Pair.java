package com.bsu.security.server.encryption;

public class Pair {
    private long e;
    private long n;

    public Pair(long e, long n) {
        this.e = e;
        this.n = n;
    }

    public long getE() {
        return e;
    }

    public void setE(long e) {
        this.e = e;
    }

    public long getN() {
        return n;
    }

    public void setN(long n) {
        this.n = n;
    }

    @Override
    public String toString() {
        return "Pair{" +
                "e=" + e +
                ", n=" + n +
                '}';
    }
}
