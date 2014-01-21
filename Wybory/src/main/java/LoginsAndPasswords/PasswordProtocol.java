package LoginsAndPasswords;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordProtocol {
	
	public static boolean vertify(BufferedReader input, BufferedWriter output, LoginsPasswordsStore loginsPasswordsStore,
			Integer AnEmptyIntToPlaceUserIdIn) throws IOException
	{
		int userId;
		String s;
		s=input.readLine();
		if(s==null || !s.equals("HELLO")){
			output.close();
			input.close();
			return false;
		}
		output.write("HELLO. WHO ARE YOU?\n");
		output.flush();
		while(true)
		{
			userId = -1;
			s=input.readLine();
			if(s==null){
				output.close();
				input.close();
				return false;
			}
	
			/*if(!s.matches("LOGIN: .+, PASS: .+")){
				output.write("PROTOCOL ERROR\n");
			}
			String login=s.replaceFirst("LOGIN: ", "").replaceFirst(",.+","");
			*/
			if(!s.matches("LOGIN: .*")){
				output.write("PROTOCOL ERROR\n");
			}
			String login=s.replaceFirst("LOGIN: ", "");
		
			for (int i=1; i<=loginsPasswordsStore.numberOfUsers; i++)
			{
				if(login.equals(loginsPasswordsStore.logins[i]))
				{
					userId = i;
					break;
				}
			}
			System.out.println(userId);
			if(userId==-1)
			{
				output.write("LOGIN DOES NOT EXIST\n");
				output.flush();
				continue;
			}
			for (int j=0; j<8; j++)
				output.write(new Byte(loginsPasswordsStore.salts[userId][j]).toString() + " ");
			output.write("\n");
			output.flush();	
			
			s = input.readLine();
			String pass=s.replaceFirst("PASS: ", "");
			Pattern pat = Pattern.compile("-?\\d+");
			Matcher mat = pat.matcher(pass); 
			System.out.println(mat.groupCount());
			System.out.println(pass);
			byte[] encPass = new byte[1000];
			int j;
			for (j=0; ; j++)
			{
				if(!mat.find())
					break;
				encPass[j] = new Byte(mat.group());
			}
			byte[] encPassCutToLength = new byte[j];
			for (int k=0; k<j; k++)
				encPassCutToLength[k] = encPass[k];
			
			if(PasswordEncryptionService.authenticate(encPassCutToLength, loginsPasswordsStore.encryptedPasswords[userId],
					loginsPasswordsStore.salts[userId]))
			{
				output.write("LOGIN OK\n");
				output.flush();
				break;
			}
			output.write("WRONG PASSWORD\n");
			output.flush();
			
		}
		return true;
		
	}
	
	
	
	
	public static boolean attempt(BufferedReader input, BufferedWriter output, Scanner sc) throws IOException
	{
		output.write("HELLO\n");
		output.flush();
		String s, t;
		s=input.readLine();
		System.out.println(s);
		if(!s.equals("HELLO. WHO ARE YOU?")){
			System.out.println("PROTOCOL FAILED");
			input.close();
			output.close();
			return false;
		}
		
		while(true)
		{
			System.out.println("login: ");
			s = sc.nextLine();
			output.write("LOGIN: " + s + "\n");
			output.flush();
			s = input.readLine();
			if(s.equals("LOGIN DOES NOT EXIST"))
			{
				System.out.println("LOGIN DOES NOT EXIST!");
				continue;
			}
			Pattern pat = Pattern.compile("-?\\d+");
			Matcher mat = pat.matcher(s); 
			byte[] salt = new byte[8];
			byte[] encPass = null;
			for (int j=0; j<8; j++)
			{
				if(!mat.find())
					throw new IOException("salt table too short");
				salt[j] = new Byte(mat.group());
			}
			
			System.out.println("password: ");
			s = sc.nextLine();			
			try {
				encPass = PasswordEncryptionService.getEncryptedPassword(s, salt);
			} catch (Exception e){
				e.printStackTrace();
			}
			output.write("PASS: ");
			for (int j=0; j<encPass.length; j++)
				output.write(new Byte(encPass[j]).toString() + " ");
			output.write("\n");
			output.flush();
			s = input.readLine();
			if(s.equals("LOGIN OK")){
				return true;
			}
			System.out.println("WRONG PASSWORD!");
		}
	}
}
