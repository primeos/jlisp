package bar.f0o.jlisp.lib.ControlPlane.LCAF;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import bar.f0o.jlisp.lib.ControlPlane.ControlMessage.AfiType;

public class GeoCoordinates implements LCAFType {

	private short length;
	private boolean north;
	private short latitude;
	private byte latMin;
	private byte latSec;
	private boolean east;
	private short longitude;
	private byte longMin;
	private byte longSec;
	private int altitude;
	private AfiType type;
	private byte[] address;
	
	public GeoCoordinates(DataInputStream stream) throws IOException {
		stream.readByte();
		length = (short)(stream.readShort()-12);
	    short lat = stream.readShort();
		north = (lat&0b1000000000000000) != 0;
		latitude = (short)(lat&0b0111111111111111);
		latMin = stream.readByte();
		latSec = stream.readByte();
		short lon = stream.readShort();
		east = (lon&0b1000000000000000) != 0;
		longitude = (short)(lon&0b0111111111111111);
		longMin = stream.readByte();
		longSec = stream.readByte();
		altitude = stream.readInt();
		type = AfiType.fromInt(stream.readShort());
		address = new byte[AfiType.length(type)];
		stream.read(address);
	}
	
	@Override
	public byte[] toByteArray() throws IOException {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		DataOutputStream stream = new DataOutputStream(byteStream);
		stream.writeByte(0);
		stream.writeShort(length+12);
		short lat = (short)(north?(latitude|0b1000000000000000):latitude);
		stream.writeShort(lat);
		stream.writeByte(latMin);
		stream.writeByte(latSec);
		short lon = (short)(east?(longitude|0b1000000000000000):longitude);
		stream.writeShort(lon);
		stream.writeByte(longMin);
		stream.writeByte(longSec);
		stream.writeInt(altitude);
		stream.write(type.getVal());
		stream.write(address);
		return byteStream.toByteArray();
	}

}
