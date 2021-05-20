import java.io.*;
import java.net.*;
import java.util.*;

class Server{
    ServerSocket server = null;
	Socket client = null;

	public static void main(String[] arg){
		Server s = new Server();
		s.doConnections();
	}

	public void doConnections(){
		try{
			server = new ServerSocket(8888);
			while(true){
				client = server.accept();
				ClientThread ct = new ClientThread(client);
				ct.start();
			}
		}
		catch(Exception e){
			System.out.println("ERROR : Client connection failed");
		}
	}
}	

class ClientThread extends Thread{
    public Socket client = null;
	public DataInputStream dis = null;
	public DataOutputStream dos = null;
	public FileInputStream fis = null;
	public FileOutputStream fos = null;
	public BufferedReader br = null;
	public File file = null;

	public ClientThread(Socket c){	
		try{
		    client = c;
			System.out.println("A new client connected.\n");
	        dis = new DataInputStream(c.getInputStream());
		    dos = new DataOutputStream(c.getOutputStream());
		}
		catch(Exception e){	
			System.out.println("ERROR : Creating server-client data streams failed.");
		}
	}

	public void run(){ 
		while(true){
			try{
				String input = dis.readUTF();
				String filename = "", filedata ="";
				byte[] data;
				
				if(input.equals("FILE_SEND_FROM_CLIENT")){
					System.out.println("Client sending data to server");

					filename = dis.readUTF();
					filedata = dis.readUTF();
					System.out.println("Client creating following file on server : " + filename);
					fos = new FileOutputStream(filename);
					fos.write(filedata.getBytes());
					System.out.println(filename + " successfully created on server by client.");
					System.out.println("____________________________________________________________________\n");

					fos.close();
				}

				else if(input.equals("DOWNLOAD_FILE")){
					System.out.println("Client requested downloading file from server.");
					System.out.println("Sending available file names to client....");

					File directoryPath = new File("C:\\Users\\Pranjal Rane\\Documents\\College-TY\\Computer Network and Technologies\\Course Project\\FTP Server");
					File filesList[] = directoryPath.listFiles();
					int n = 0;

					for(File file : filesList)
						n++;
					dos.writeUTF(String.valueOf(n));

					int count = 0;
					for(File file : filesList) {
						dos.writeUTF(file.getName());
						String name = file.getName();
						if (name.equals("Server.java") || name.equals("Server.class") || name.equals("ClientThread.class"))
							continue;
						else	
							count++;
					}

					if (count == 0){
						System.out.println("ERROR : No files on server to share.");
						System.out.println("____________________________________________________________________\n");
					}

					else {
						System.out.println("Available file names succesfully sent to client.");
						
						filename = dis.readUTF();
						System.out.println("Client requested " + filename + " from server.");
						System.out.println("Sending " +  filename + " to client....");
						file = new File(filename);
						
						if(file.isFile()){
							fis = new FileInputStream(file);
							data = new byte[fis.available()];
							fis.read(data);

							filedata = new String(data);
							fis.close();
							dos.writeUTF(filedata);
							
							System.out.println("File successfully sent.");
							System.out.println("____________________________________________________________________\n");
						}
						else{
							System.out.println("ERROR : Client requested invalid/unavailable file.");
							dos.writeUTF(""); // NO FILE FOUND
						}
					}
				}	

				else if (input.equals("DELETE_FILE")){
					System.out.println("Client requested deleting file from server.");
					System.out.println("Waiting for users to enter password...");

					String password = dis.readUTF();
					if(password.equals("ty06allowed")){
						dos.writeUTF("__");
						System.out.println("Sending available file names to client....");

						File directoryPath = new File("C:\\Users\\Pranjal Rane\\Documents\\College-TY\\Computer Network and Technologies\\Course Project\\FTP Server");
						File filesList[] = directoryPath.listFiles();

						int n = 0;
						for(File file : filesList)
							n++;

						dos.writeUTF(String.valueOf(n));

						int count = 0;
						for(File file : filesList) {
							dos.writeUTF(file.getName());

							String name = file.getName();
							if (name.equals("Server.java") || name.equals("Server.class") || name.equals("ClientThread.class"))
								continue;

							else	
								count++;
						}

						if (count == 0){
							System.out.println("ERROR : No files on server to delete.");
							System.out.println("____________________________________________________________________\n");
						}

						else {
							System.out.println("Available file names succesfully sent to client.");
						
							filename = dis.readUTF();
							System.out.println("Client requested " + filename + " to be deleted from server.");
							System.out.println("Deleting " +  filename + " from server");

							file = new File(filename);
							if(file.isFile()){
								file.delete();
								dos.writeUTF("__");
								System.out.println("File successfully deleted.");
								System.out.println("____________________________________________________________________\n");
							}

							else{
								System.out.println("ERROR : Client requested invalid/unavailable file.");
								dos.writeUTF("");
							}
						}						
					}

					else{
						dos.writeUTF("");
						System.out.println("ERROR : User Authentication Failed");
					}					
				}

				else{
					System.out.println("ERROR : Invalid input from client");
				}
			}
			catch(Exception e){

			}	
	    }
	}
}
