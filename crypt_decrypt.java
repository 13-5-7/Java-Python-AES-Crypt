import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.util.Base64;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.lang.*;
import java.util.Random;
import java.util.Formatter;

public class AESCrypt {

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final int IV_SIZE = 16;

    /**
     * 暗号化：IV(16byte) + 暗号文 をBase64で返す
     */
    public static String encrypt(String key, String plaintext) {
        try {
            // 強力な乱数生成器を使用
            byte initVector[] = new byte[IV_SIZE];
            (new Random()).nextBytes(initVector);
            IvParameterSpec iv = new IvParameterSpec(initVector);
        	
        	StringBuilder hexString = new StringBuilder();
        	for (byte b : initVector) {
            	hexString.append(String.format("%02x", b));
        	}

            // 鍵の生成 (常にUTF-8を使用)
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            // 暗号化実行
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            // IVと暗号文を結合
            byte[] cipherbytes = cipher.doFinal(plaintext.getBytes());
            byte[] messagebytes = new byte[initVector.length + cipherbytes.length];
            System.arraycopy(initVector, 0, messagebytes, 0, IV_SIZE);
            System.arraycopy(cipherbytes, 0, messagebytes, IV_SIZE, cipherbytes.length);

            return Base64.getEncoder().encodeToString(messagebytes);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * 復号：Base64文字列からIVを切り出し復号する
     */
    public static String decrypt(String key, String ciphertext) {
        try {
            byte[] cipherbytes = Base64.getDecoder().decode(ciphertext);

            // IVと暗号文に分割
            byte[] initVector = Arrays.copyOfRange(cipherbytes,0,IV_SIZE);
            byte[] messagebytes = Arrays.copyOfRange(cipherbytes,IV_SIZE,cipherbytes.length);

            // 2. 復号設定
            IvParameterSpec iv = new IvParameterSpec(initVector);
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            // 復号実行
            byte[] byte_array = cipher.doFinal(messagebytes);

            return new String(byte_array, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        //boolean encrypt = true;

        String key = "sampleKeyForAES_";  // 16, 24, 32文字である必要がある
        String message = "テストメッセージ";
        //String message = "xQbTk+9WVgx693s9nLsW9RCSzYJ917P8TN66O609pryLSN6UswEbxbOXkCascI3f";

        System.out.println(encrypt(key, message));
        //System.out.println(decrypt(key, message));

    }
}
