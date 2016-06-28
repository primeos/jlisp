package bar.f0o.jlisp.mappingsystem;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.ArrayList;

import bar.f0o.jlisp.lib.ControlPlane.ControlMessage;
import bar.f0o.jlisp.lib.ControlPlane.EncapsulatedControlMessage;
import bar.f0o.jlisp.lib.ControlPlane.MapRegister;
import bar.f0o.jlisp.lib.ControlPlane.MapRequest;
import bar.f0o.jlisp.lib.ControlPlane.Rec;

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
		ArrayList<byte[]> eids= new ArrayList<>();
		for(Rec rec: mapRequest.getRecs())
		{
			if(mapRequest.isaFlag())
			{
				byte[] proxy = MappingSystem.getMappings().getProxy(rec.getEidPrefix());
				if( proxy != null)
					forwardMappingRequest(rec.getEidPrefix(),proxy,mapRequest);
				else
					eids.add(rec.getEidPrefix());			
			}
			else{
				eids.add(rec.getEidPrefix());
			}

		}
		byte[] reply = MappingSystem.getMappings().getReply(eids, mapRequest.getNonce()).toByteArray();
		p = new DatagramPacket(reply, reply.length);
		try{
			p.setAddress(InetAddress.getByAddress(mapRequest.getItrRlocPairs().get(1)));
			p.setPort(4341);
			MappingSystem.sendPacket(p);
		} catch(IOException e) {
			e.printStackTrace();
		}
		
	}


	private void forwardMappingRequest(byte[] eidPrefix, byte[] proxy,MapRequest mapReq) {
			ArrayList<Rec> recs = new ArrayList<>();
			
			MapRequest req = new MapRequest(mapReq.isaFlag(), mapReq.ismFlag(), mapReq.ispFlag(), mapReq.isSmrBit(), mapReq.isPitrBit(), mapReq.isSmrInvoked(), mapReq.getNonce(), mapReq.getSourceEidAfi(), mapReq.getSourceEIDAddress(), mapReq.getItrRlocPairs(), recs, mapReq.getReply());
			byte[] request = req.toByteArray();
			DatagramPacket pack = new DatagramPacket(request, request.length);
			try {
				pack.setAddress(InetAddress.getByAddress(proxy));
				p.setPort(4341);
				MappingSystem.sendPacket(pack);
			} catch (IOException e) {
				e.printStackTrace();
			}
	}


	private void processRegister(MapRegister message) {
		// TODO Auto-generated method stub
		
	}

}
