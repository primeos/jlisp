/******************************************************************************
 * Copyright (c) 2015 by                                                      *
 * Andreas Stockmayer <stockmay@f0o.bar> and                                  *
 * Mark Schmidt <schmidtm@f0o.bar>                                            *
 *                                                                            *
 * This file (KeyValueAddressPair.java) is part of jlisp.                     *
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
 * Key/Value Pair Address Format:

     0                   1                   2                   3
     0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |           AFI = 16387         |     Rsvd1     |     Flags     |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |   Type = 15   |     Rsvd2     |               n               |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |              AFI = x          |       Address as Key ...      |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |              AFI = x          |       Address as Value ...    |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+

 */

public class KeyValueAddressPair implements LCAFType {

	private short len;
	private AfiType keyType;
	private byte[] key;
	private AfiType valueType;
	private byte[] value;

	public KeyValueAddressPair(DataInputStream stream) throws IOException {
		stream.readByte();
		len = stream.readShort();
		keyType = AfiType.fromInt(stream.readShort());
		key = new byte[AfiType.length(keyType)];
		stream.read(key);
		valueType = AfiType.fromInt(stream.readShort());
		value = new byte[AfiType.length(valueType)];
		stream.read(value);
	}

	public KeyValueAddressPair(AfiType keyType, byte[] key, AfiType valueType, byte[] value) {
		super();
		this.keyType = keyType;
		this.key = key;
		this.valueType = valueType;
		this.value = value;
		this.len = (short) (4 + key.length + value.length);
	}

	public byte[] toByteArray() throws IOException {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		DataOutputStream stream = new DataOutputStream(byteStream);
		stream.writeByte(0);
		stream.writeShort(len);
		stream.writeShort(keyType.getVal());
		stream.write(key);
		stream.writeShort(valueType.getVal());
		stream.write(value);
		return byteStream.toByteArray();
	}

	public byte[] getRloc(){
		return null;
	}
}
