package com.yuelinghui.personal.maframe.util.crypto;

import java.math.BigInteger;
import java.util.Random;

/**
 * An unofficial implementation of the RC4 cipher algorithm.
 */
public class RC4 {

    private final byte[] key;
    private final byte[] state;
    private int x;
    private int y;

    /**
     * Constructs a new RC4 object with a randomly generated encryption key.
     */
    public RC4() {
        this.state = new byte[256];
        this.key = new BigInteger(2048, new Random()).toByteArray();
        reset();
    }

    /**
     * Constructs a new RC4 object with the specified encryption key. The key
     * can be at most 256 bytes in length.
     *
     * @param key the encryption key.
     */
    public RC4(byte[] key) {
        this.state = new byte[256];
        int length = Math.min(256, key.length);
        byte[] keyCopy = new byte[length];
        System.arraycopy(key, 0, keyCopy, 0, length);
        this.key = keyCopy;
        reset();
    }

    /**
     * Resets the cipher to start encrypting a new stream of data.
     */
    public void reset() {
        for (int i = 0; i < 256; i++) {
            state[i] = (byte) i;
        }
        int j = 0;
        for (int i = 0; i < 256; i++) {
            j = (j + state[i] + key[i % key.length]) & 0xff;
            byte temp = state[i];
            state[i] = state[j];
            state[j] = temp;
        }

        x = 0;
        y = 0;
    }

    /**
     * Crypts the data from the input array to the output array.
     *
     * @param input  The source data.
     * @param output The array to store the crpyted data, which must be as long as
     *               the input data.
     */
    public void crypt(byte[] input, byte[] output) {
        for (int i = 0; i < input.length; i++) {
            x = (x + 1) & 0xff;
            y = (state[x] + y) & 0xff;

            byte temp = state[x];
            state[x] = state[y];
            state[y] = temp;

            output[i] = (byte) ((input[i] ^ state[(state[x] + state[y]) & 0xff]));
        }
    }

    /***
     * @see 外部调用
     ***/
    public byte[] crypt(byte[] byteE) {
        byte[] byteFina = new byte[byteE.length];
        try {
            crypt(byteE, byteFina);
        } catch (Exception e) {
        } finally {
        }
        return byteFina;
    }
}