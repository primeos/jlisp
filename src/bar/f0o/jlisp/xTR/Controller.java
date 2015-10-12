package bar.f0o.jlisp.xTR;

import java.io.IOException;
import java.net.DatagramSocket;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Controller {	
		
	private static ExecutorService poolSend = Executors.newFixedThreadPool(50);
	private static ExecutorService poolReceive = Executors.newFixedThreadPool(50);
	private static int fd;
	
	
	public Controller() throws IOException{
		//MS IP null
		new Thread(new InputListenerRaw()).start();
		new Thread(new InputListenerLISP()).start();
	}
	
	
	public static void main(String[] args) throws IOException{
		new Controller();
	}
	
	//Save own nonces send with echo request to another RLOC
	public static synchronized void saveNonceToRloc(byte[] rloc, long nonce){
		
	}
	
	//Get nonce that should be in a packet from another RLOC
	public static synchronized long getNonceEchoToRloc(byte[] rloc){
		return 0;
	}
		
	
	
	//Save echo requests from other RLOCs
	public static synchronized void saveNonceFromRloc(byte[] rloc, long nonce){
		
	}
	//Get echo request that has to be sent back
	public static synchronized long getNonceEchoFromRloc(byte[] rloc){
		return 0;
	}

	
	
	
	//Check if Other RLocs Version > saved one
	public static void checkSourceVersionNumber(short srcVersionNumber,byte[] otherRloc) {
		//If other RLOCs Version > then Map Request
	}
	
	//Check if own number > parameter
	public static void checkDestinationVersionNumber(short srcVersionNumber,byte[] otherRloc) {
		//if own number > srcVersionNumber  SMR
	}
	
	
	public static void addSendWorker(Runnable worker){
		poolSend.execute(worker);
	}
	
	public static void addReceiveWorker(Runnable worker){
		poolReceive.execute(worker);
	}


	

	public static int getFd() {
		return fd;
	}
	public static void setFd(int fd){
		Controller.fd = fd;
	}


	
	
}
