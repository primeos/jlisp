package bar.f0o.jlisp.xTR;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class InputListenerLISP implements Runnable{

	private DatagramSocket receiver;
	
	public InputListenerLISP() throws SocketException {
		receiver =  new DatagramSocket(4341);
	}
	
	@Override
	public void run() {
		while(true){
			byte[] buf = new byte[Controller.getMTU()];
			DatagramPacket p = new DatagramPacket(buf, buf.length);
			try {
				receiver.receive(p);
				Controller.addReceiveWorker(new ETRWorker(p));
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}

}
