package bar.f0o.jlisp.mappingsystem;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MappingSystem {

	//Socket for LISP Control Traffic
	private static DatagramSocket socket;
	//Threadpool
	private static ExecutorService pool = Executors.newFixedThreadPool(50);
	private static Mappings mappings = new Mappings();
	
	
	
	
	public static void main(String args[]) throws IOException{
		new MappingSystem();
		
	}
	
	
	public MappingSystem() throws IOException{
		 socket = new DatagramSocket(4342);
		 while(true){
				//Default MTU
				byte[] buf = new byte[1500];
				DatagramPacket p = new DatagramPacket(buf, buf.length);
				socket.receive(p);
				pool.execute(new MapWorker(p));
			}
	}
	
	public static void sendPacket(DatagramPacket p) throws IOException{
		socket.send(p);
	}
	
	public static Mappings getMappings(){
		return mappings;
	}
	
}
