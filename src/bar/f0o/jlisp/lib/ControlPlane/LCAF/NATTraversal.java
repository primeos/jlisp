package bar.f0o.jlisp.lib.ControlPlane.LCAF;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import bar.f0o.jlisp.lib.ControlPlane.ControlMessage.AfiType;

public class NATTraversal implements LCAFType {

	private short len;
	private short msUDPNum=4342;
	private short etrUDPNum;
	private AfiType gerAfi;
	private short globalETRRloc;
	private AfiType mrAfi;
	private short msRLOC;
	private AfiType perAfi;
	private short privateETRRloc;
	private ArrayList<AfiType> ntrRlocAfi = new ArrayList<>();
	private ArrayList<byte[]> ntrRloc = new ArrayList<>();
	
	public NATTraversal(DataInputStream stream) throws IOException {
		stream.readByte();
		len = (short)(stream.readShort()-4);
		msUDPNum = stream.readShort();
		etrUDPNum = stream.readShort();
		gerAfi = AfiType.fromInt(stream.readShort());
		globalETRRloc = stream.readShort();
		mrAfi = AfiType.fromInt(stream.readShort());
		msRLOC = stream.readShort();
		perAfi = AfiType.fromInt(stream.readShort());
		privateETRRloc = stream.readShort();
		int lengthLeft = len-12;
		while(lengthLeft >=0){
			AfiType ntrAfi = AfiType.fromInt(stream.readShort());
			ntrRlocAfi.add(ntrAfi);
			byte[] ntrRlocTmp = new byte[AfiType.length(ntrAfi)];
			stream.read(ntrRlocTmp);
			ntrRloc.add(ntrRlocTmp);
			len -= (ntrRlocTmp.length + 2);	
		}
	}
	
	@Override
	public byte[] toByteArray() throws IOException {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		DataOutputStream stream = new DataOutputStream(byteStream);
		stream.writeByte(0);
		stream.writeShort(len+4);
		stream.writeShort(msUDPNum);
		stream.writeShort(etrUDPNum);
		stream.writeShort(gerAfi.getVal());
		stream.writeShort(globalETRRloc);
		stream.writeShort(mrAfi.getVal());
		stream.writeShort(msRLOC);
		stream.writeShort(perAfi.getVal());
		stream.writeShort(privateETRRloc);
		for(int i=0;i<ntrRlocAfi.size();i++){
			stream.write(ntrRlocAfi.get(i).getVal());
			stream.write(ntrRloc.get(i));
		}
		return byteStream.toByteArray();
	}

}
