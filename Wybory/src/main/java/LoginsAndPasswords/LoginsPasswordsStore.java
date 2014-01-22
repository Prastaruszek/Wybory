package LoginsAndPasswords;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class LoginsPasswordsStore {
	public int numberOfUsers = 100;	
	public String logins[] = new String[numberOfUsers];
	public byte salts[][] = new byte[numberOfUsers][];
	public byte encryptedPasswords[][] = new byte[numberOfUsers][];
	public LoginsPasswordsStore(String basicLoginName, String basicPassword) {
		for(int i=0; i<=numberOfUsers-1; i++)
		{
			logins[i] = new String(basicLoginName+i);
			try {
				salts[i] = PasswordEncryption.generateSalt();
				encryptedPasswords[i] = PasswordEncryption.getEncryptedPassword(
						basicPassword + i, salts[i]);
			}
			catch (NoSuchAlgorithmException e){
				e.printStackTrace();
			}
			catch (InvalidKeySpecException e){
				e.printStackTrace();
			}	
		}
	}

}
