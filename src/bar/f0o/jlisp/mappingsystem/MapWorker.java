package bar.f0o.jlisp.mappingsystem;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;

import bar.f0o.jlisp.lib.ControlPlane.ControlMessage;
import bar.f0o.jlisp.lib.ControlPlane.EncapsulatedControlMessage;
import bar.f0o.jlisp.lib.ControlPlane.MapRegister;
import bar.f0o.jlisp.lib.ControlPlane.MapRequest;

public class MapWorker implements Runnable {

	private DatagramPacket p;
	
	public MapWorker(DatagramPacket p) {
		this.p = p;
	}


	@Override
	public void run() {
		//Get Datainputstream from Datagrampacket
		byte[] rec = new byte[p.getLength()];
		System.arraycopy(p.getData(), 0, rec,0, rec.length);
		DataInputStream answerStream = new DataInputStream(new ByteArrayInputStream(rec));
	
		try {
			ControlMessage message = ControlMessage.fromStream(answerStream);
			if(message instanceof MapRegister)
				processRegister((MapRegister)message);
			else if(message instanceof EncapsulatedControlMessage)
				processRequest((MapRequest)(((EncapsulatedControlMessage) message).getMessage()));
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		
		
	}


	private void processRequest(MapRequest mapRequest) {
		
	}


	private void processRegister(MapRegister message) {
		// TODO Auto-generated method stub
		
	}

}
