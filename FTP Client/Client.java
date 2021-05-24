import java.io.*;
import java.net.*;
import java.util.*;

public class Client{
	public Socket client = null;
	public DataInputStream dis = null;
	public DataOutputStream dos = null;
	public FileInputStream fis = null;
	public FileOutputStream fos = null;
	public BufferedReader br = null;
	public String inputFromUser = "";
	
	public static void main(String[] args){
		Client c = new Client();
		c.doConnections();	
	}
	
	public void doConnections(){
		try{
		  	InputStreamReader isr = new InputStreamReader(System.in);
		  	br = new BufferedReader(isr);
		  	client = new Socket("127.0.0.1", 8888);
		  	dis = new DataInputStream(client.getInputStream());
		  	dos = new DataOutputStream(client.getOutputStream());
		}
		catch(Exception e){
		  	System.out.println("ERROR : Connection to Server failed.");
			System.exit(0);
		}
		
		while(true){
			try{
				System.out.println("Please Make a Choice : \n1. send file \n2. receive file \n3. delete file \nYour Choice: ");
				inputFromUser = br.readLine();
				int i = Integer.parseInt(inputFromUser);
				switch(i){
					case 1: sendFile(); break;
					case 2: receiveFile(); break;
					case 3: deleteFile(); break;
					default: System.out.println("Invalid Option!");
				} 
			}
			catch(Exception e){
				System.out.println("ERROR: Unknown error occured.");
			}
		}	
	}

	public void sendFile() {
		try{
			String filename="", filedata="";		
			File file;
			byte[] data;
			
			System.out.println("\nEnter the filename: ");
			filename = br.readLine();
			file = new File(filename);
			System.out.println("Sending the following file to server : " + filename);
			
			if(file.isFile()){	
				fis = new FileInputStream(file);
				data = new byte[fis.available()];

				fis.read(data);
				fis.close();

				filedata = new String(data);

				dos.writeUTF("FILE_SEND_FROM_CLIENT");
				dos.writeUTF(filename);		
				dos.writeUTF(filedata);

				System.out.println("File Successful sent to server.");
				System.out.println("____________________________________________________________________\n");
			}

			else{
				System.out.println("ERROR: File not found on client.");
			}
		}
		catch(Exception e){
			System.out.println("ERROR: Connection error occured.");
			System.exit(0);
		}		
	}

	public void receiveFile(){
		try{
			System.out.println("\nRequesting file download from server...");
			dos.writeUTF("DOWNLOAD_FILE");

			int n = Integer.parseInt(dis.readUTF());
			
			String filename = "", filedata = "";	
			ArrayList<String> validFiles = new ArrayList<String>();

			for (int i = 0; i < n; i++){
				String name = dis.readUTF();
				if (name.equals("Server.java") || name.equals("Server.class") || name.equals("ClientThread.class"))
					continue;

				else{
					validFiles.add(name);
				}	
			}

			if (validFiles.size() > 0){
				System.out.println("Files available on server : ");
				for (int i = 0; i < validFiles.size(); i++)
					System.out.println(validFiles.get(i));
				

				System.out.println("Enter the filename from above list : ");
				filename = br.readLine();
				dos.writeUTF(filename);
				filedata = dis.readUTF();

		    	if(filedata.equals("")){
				  	System.out.println("Requested File is unavailable.");
					  return;
				}

				else{
					fos = new FileOutputStream(filename);
					fos.write(filedata.getBytes());
					fos.close();
				}	

				System.out.println("File successfully received from server.");
				System.out.println("____________________________________________________________________\n");
			}

			else{
				System.out.println("There are no files on server.");
				System.out.println("____________________________________________________________________\n");		
			}
		}
		catch(Exception e){
			System.out.println("ERROR: Connection error occured.");
		}
	}

	public void deleteFile(){
		try {
			System.out.println("\nRequesting file deletion from server...");
			dos.writeUTF("DELETE_FILE");

			System.out.println("\nEnter the password to continue deletion : ");
			String password = br.readLine();
			dos.writeUTF(password);

			String auth = dis.readUTF();

			if (auth.equals("")){
				System.out.println("Incorrect Password. \nTry Again.");
			}

			else {
				System.out.println("Authentication Successful \n");
				
				int n = Integer.parseInt(dis.readUTF());
				String filename = "", result = "";	
				ArrayList<String> validFiles = new ArrayList<String>();

				for (int i = 0; i < n; i++){
					String name = dis.readUTF();
					if (name.equals("Server.java") || name.equals("Server.class") || name.equals("ClientThread.class"))
						continue;

					else{
						validFiles.add(name);
					}	
				}

				if (validFiles.size() > 0){
					System.out.println("Files available on server : ");
					for (int i = 0; i < validFiles.size(); i++)
						System.out.println(validFiles.get(i));

					System.out.println("\nEnter the filename to delete from above list : ");
					filename = br.readLine();
					dos.writeUTF(filename);
					result = dis.readUTF();

					if(result.equals("")){
						System.out.println("Reuested File is not present on server.");
		  			}

		  			else{
						System.out.println("File successfully deleted.");
		  			}
				}

				else
					System.out.println("There are no files on server.");
					System.out.println("____________________________________________________________________\n");		
			}	
		} 
		
		catch (Exception e) {
			System.out.println("ERROR: Connection error occured.");
			System.exit(0);
		}
	}
}
