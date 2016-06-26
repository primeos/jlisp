/******************************************************************************
 * Copyright (c) 2015 by                                                      *
 * Andreas Stockmayer <stockmay@f0o.bar> and                                  *
 * Mark Schmidt <schmidtm@f0o.bar>                                            *
 *                                                                            *
 * This file (GeoCoordinates.java) is part of jlisp.                          *
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
 *    Geo Coordinate LISP Canonical Address Format:

     0                   1                   2                   3
     0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |           AFI = 16387         |     Rsvd1     |     Flags     |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |   Type = 5    |     Rsvd2     |            12 + n             |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |N|     Latitude Degrees        |    Minutes    |    Seconds    |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |E|     Longitude Degrees       |    Minutes    |    Seconds    |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |                            Altitude                           |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |              AFI = x          |         Address  ...          |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 */

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
		length = (short) (stream.readShort() - 12);
		short lat = stream.readShort();
		north = (lat & 0b1000000000000000) != 0;
		latitude = (short) (lat & 0b0111111111111111);
		latMin = stream.readByte();
		latSec = stream.readByte();
		short lon = stream.readShort();
		east = (lon & 0b1000000000000000) != 0;
		longitude = (short) (lon & 0b0111111111111111);
		longMin = stream.readByte();
		longSec = stream.readByte();
		altitude = stream.readInt();
		type = AfiType.fromInt(stream.readShort());
		address = new byte[AfiType.length(type)];
		stream.read(address);
	}

	public GeoCoordinates(boolean north, short latitude, byte latMin, byte latSec, boolean east, short longitude,
			byte longMin, byte longSec, int altitude, AfiType type, byte[] address) {
		super();
		this.north = north;
		this.latitude = latitude;
		this.latMin = latMin;
		this.latSec = latSec;
		this.east = east;
		this.longitude = longitude;
		this.longMin = longMin;
		this.longSec = longSec;
		this.altitude = altitude;
		this.type = type;
		this.address = address;
		this.length = (short) (address.length + 2);
	}

	@Override
	public byte[] toByteArray() throws IOException {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		DataOutputStream stream = new DataOutputStream(byteStream);
		stream.writeByte(0);
		stream.writeShort(length + 12);
		short lat = (short) (north ? (latitude | 0b1000000000000000) : latitude);
		stream.writeShort(lat);
		stream.writeByte(latMin);
		stream.writeByte(latSec);
		short lon = (short) (east ? (longitude | 0b1000000000000000) : longitude);
		stream.writeShort(lon);
		stream.writeByte(longMin);
		stream.writeByte(longSec);
		stream.writeInt(altitude);
		stream.write(type.getVal());
		stream.write(address);
		return byteStream.toByteArray();
	}

	public byte[] getRloc(){
		return null;
	}
}
