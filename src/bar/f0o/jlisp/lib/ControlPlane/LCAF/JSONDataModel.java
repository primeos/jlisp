/******************************************************************************
 * Copyright (c) 2015 by                                                      *
 * Andreas Stockmayer <stockmay@f0o.bar> and                                  *
 * Mark Schmidt <schmidtm@f0o.bar>                                            *
 *                                                                            *
 * This file (JSONDataModel.java) is part of jlisp.                               *
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
 *  JSON Data Model Type Address Format:

     0                   1                   2                   3
     0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |           AFI = 16387         |     Rsvd1     |     Flags     |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |   Type = 14   |    Rsvd2    |B|             2 + n             |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |           JSON length         | JSON binary/text encoding ... |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |              AFI = x          |       Optional Address ...    |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+

 */

public class JSONDataModel implements LCAFType {

	private boolean bBit;
	private short len;
	private short jsonLength;
	private byte[] jsonEncoding;
	private AfiType type;
	private byte[] optionalAddress;

	public JSONDataModel(DataInputStream stream) throws IOException {
		bBit = stream.readByte() != 0;
		len = (short) (stream.readShort() - 2);
		jsonLength = stream.readShort();
		jsonEncoding = new byte[jsonLength];
		stream.read(jsonEncoding);
		type = AfiType.fromInt(stream.readShort());
		optionalAddress = new byte[AfiType.length(type)];
		stream.read(optionalAddress);
	}

	public JSONDataModel(boolean bBit, byte[] jsonEncoding, AfiType type, byte[] optionalAddress) {
		super();
		this.bBit = bBit;
		this.jsonEncoding = jsonEncoding;
		this.jsonLength = (short) jsonEncoding.length;
		this.type = type;
		this.optionalAddress = optionalAddress;
		this.len = (short) (8 + jsonEncoding.length + optionalAddress.length);
	}

	public byte[] toByteArray() throws IOException {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		DataOutputStream stream = new DataOutputStream(byteStream);
		stream.writeByte((bBit ? 1 : 0));
		stream.writeShort(len + 2);
		stream.writeShort(jsonLength);
		stream.write(jsonEncoding);
		stream.writeInt(type.getVal());
		stream.write(optionalAddress);
		return byteStream.toByteArray();
	}
}
