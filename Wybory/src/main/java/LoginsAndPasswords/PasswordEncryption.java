package LoginsAndPasswords;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;


public class PasswordEncryption {
	public static boolean checkPassword(byte[] attemptedPassword, byte[] correctPassword, byte[] salt){
		return Arrays.equals(attemptedPassword, correctPassword);
	}

	public static byte[] getEncryptedPassword(String password, byte[] salt)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		String codingAlgorithm = "PBKDF2WithHmacSHA1";
		int iterationCount = 4000, keyLength = 160;
		KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterationCount, keyLength);
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(codingAlgorithm);

		return keyFactory.generateSecret(spec).getEncoded();
	}

	public static byte[] generateSalt() throws NoSuchAlgorithmException {
		SecureRandom rand = SecureRandom.getInstance("SHA1PRNG");
		byte[] salt = new byte[8];
		rand.nextBytes(salt);
		return salt;
	}
}
