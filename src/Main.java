import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Random;
import java.util.ResourceBundle;

public class Main {
    private static ResourceBundle bundle = ResourceBundle.getBundle("config");
    private static BigInteger r = new BigInteger(bundle.getString("public_key"));
    private static BigInteger e = new BigInteger(bundle.getString("encrypt_key"));
    private static BigInteger d = new BigInteger(bundle.getString("decrypt_key"));
    public static void main(String[] args) {
        String m = "明天凌晨7点，高地33号开始突袭。";
        byte[] x = encrypt(m.getBytes(Charset.forName("UTF-8")));
        m = decrypt(x);
        System.out.println(m);
    }

    public static String decrypt(byte[] data) {

        BigInteger x = new BigInteger(data);

        BigInteger m = x.modPow(d, r);
        System.out.println("decrypt_key : " + d);

        //undo what we did in encrypt(byte[] data)
        int length = m.toByteArray().length;
        byte[] copy = new byte[length - 1];
        System.arraycopy(m.toByteArray(), 1, copy, 0, length-1);

        return new String(copy, Charset.forName("UTF-8"));
    }

    public static byte[] encrypt(byte[] data) {

        // since m can't be negative,
        // we need to process it before converting it to BigInteger.
        byte[] copy = new byte[data.length + 1];
        copy[0] = 127;
        System.arraycopy(data, 0, copy, 1, data.length);

        BigInteger m = new BigInteger(copy);
        BigInteger x = m.modPow(e, r);

        System.out.println("public_key : " + r);
        System.out.println("encrypt_key : " + e);

        System.out.println("plain_message : " + m);
        System.out.println("encrypted_message : " + x);
        return x.toByteArray();
    }

    /**
     * Generate all keys to be used in RSA encryption and
     * @param bitLength recommended bit length of public key is 1024 or 2048.
     * @return
     */
    public static String keyGenerator(int bitLength) {
        StringBuilder builder = new StringBuilder();

        BigInteger p1 = BigInteger.probablePrime(bitLength / 2, new Random());
        builder.append("p1=" + p1.toString() + '\n');

        BigInteger p2 = BigInteger.probablePrime(bitLength / 2, new Random());
        builder.append("p2=" + p2.toString() + '\n');

        BigInteger r = p1.multiply(p2);
        builder.append("public_key=" + r.toString() + '\n');

        BigInteger phi = p1.subtract(BigInteger.ONE).multiply(p2.subtract(BigInteger.ONE));
        builder.append("phi=" + phi.toString() + '\n');


        BigInteger e = new BigInteger("65537");
        builder.append("encrypt_key=" + e.toString() + '\n');

        BigInteger d = solve(phi, e);
        builder.append("decrypt_key=" + d.toString());

        return builder.toString();

    }

    /**
     * An implementation of extended euclidean algorithm
     *
     * Credit to {@link http://www.sanfoundry.com/java-program-extended-euclid-algorithm/}
     * @param a input value, a > b
     * @param b input value, b < a
     * @return q, where ap + bq = gcd(a, b);
     *
     */
    public static BigInteger solve(BigInteger a, BigInteger b)
    {
        BigInteger x = BigInteger.ZERO;
        BigInteger y = BigInteger.ONE;
        BigInteger lastx = BigInteger.ONE;
        BigInteger lasty = BigInteger.ZERO;
        BigInteger temp = BigInteger.ZERO;
        while (!b.equals(BigInteger.ZERO))
        {
            BigInteger q = a.divide(b);
            BigInteger r = a.remainder(b);

            a = b;
            b = r;

            temp = x;
            x = lastx.subtract(q.multiply(x));
            lastx = temp;

            temp = y;
            y = lasty.subtract(q.multiply(y));
            lasty = temp;
        }
        // System.out.println("Roots  x : "+ lastx +" y :"+ lasty);
        return lasty;
    }
}
