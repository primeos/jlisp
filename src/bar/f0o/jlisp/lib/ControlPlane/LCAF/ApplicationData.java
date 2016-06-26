/******************************************************************************
 * Copyright (c) 2015 by                                                      *
 * Andreas Stockmayer <stockmay@f0o.bar> and                                  *
 * Mark Schmidt <schmidtm@f0o.bar>                                            *
 *                                                                            *
 * This file (ApplicationData.java) is part of jlisp.                         *
 *                                                                            *
 * jlisp is free software: you can redistribute it and/or modify              *
 * it under the terms of the GNU General Public License as published by       *
 * the Free Software Foundation, either version 3 of the License, or          *
 * (at your option) any later version.                                        *
 *                                                                            *
 * jlisp is distributed in the hope that it will be useful,                   *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.See the                *
 * GNU General Public License for more details.                               *
 *                                                                            *
 * You should have received a copy of the GNU General Public License          *
 * along with $project.name.If not, see <http://www.gnu.org/licenses/>.       *
 ******************************************************************************/
package bar.f0o.jlisp.lib.ControlPlane.LCAF;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import bar.f0o.jlisp.lib.ControlPlane.ControlMessage.AfiType;

/*
 * Application Data LISP Canonical Address Format:

     0                   1                   2                   3
     0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |           AFI = 16387         |     Rsvd1     |     Flags     |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |   Type = 4    |     Rsvd2     |            12 + n             |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |       IP TOS, IPv6 TC, or Flow Label          |    Protocol   |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |    Local Port (lower-range)   |    Local Port (upper-range)   |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |   Remote Port (lower-range)   |   Remote Port (upper-range)   |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |              AFI = x          |         Address  ...          |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+

   
 */

public class ApplicationData implements LCAFType {

	private short len;
	private int trafficClass;
	private byte protocol;
	private short localPortLower;
	private short localPortHigher;
	private short remotePortLower;
	private short remotePortHigher;
	private AfiType type;
	private byte[] locator;

	public ApplicationData(DataInputStream stream) throws IOException {
		stream.readByte();
		len = (short) (stream.readShort() - 12);
		int tc = stream.readInt();
		trafficClass = tc >> 8;
		protocol = (byte) (tc & 0x000000FF);
		localPortLower = stream.readShort();
		localPortHigher = stream.readShort();
		remotePortLower = stream.readShort();
		remotePortHigher = stream.readShort();
		type = AfiType.fromInt(stream.readShort());
		locator = new byte[AfiType.length(type)];
		stream.read(locator);

	}

	public ApplicationData(int trafficClass, byte protocol, short localPortLower, short localPortHigher,
			short remotePortLower, short remotePortHigher, AfiType type, byte[] locator) {
		super();
		this.trafficClass = trafficClass;
		this.protocol = protocol;
		this.localPortLower = localPortLower;
		this.localPortHigher = localPortHigher;
		this.remotePortLower = remotePortLower;
		this.remotePortHigher = remotePortHigher;
		this.type = type;
		this.locator = locator;
		this.len = (short) (2 + locator.length);
	}

	@Override
	public byte[] toByteArray() throws IOException {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		DataOutputStream stream = new DataOutputStream(byteStream);
		stream.writeByte(0);
		stream.writeShort(len + 12);
		stream.write((trafficClass | protocol));
		stream.writeShort(localPortLower);
		stream.writeShort(localPortHigher);
		stream.writeShort(remotePortLower);
		stream.writeShort(remotePortHigher);
		stream.writeShort(type.getVal());
		stream.write(locator);
		return byteStream.toByteArray();
	}
	
	public byte[] getRloc(){
		return null;
	}
}
