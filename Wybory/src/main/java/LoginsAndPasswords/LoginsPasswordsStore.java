package LoginsAndPasswords;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class LoginsPasswordsStore {
	public int numberOfUsers = 100;	
	public String logins[] = new String[numberOfUsers+1];
	public byte salts[][] = new byte[numberOfUsers+1][];
	public byte encryptedPasswords[][] = new byte[numberOfUsers+1][];
	public LoginsPasswordsStore(String basicLoginName, String basicPassword) {
		for(int i=1; i<=numberOfUsers; i++)
		{
			logins[i] = new String(basicLoginName+i);
			try {
				salts[i] = PasswordEncryptionService.generateSalt();
				encryptedPasswords[i] = PasswordEncryptionService.getEncryptedPassword(
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
