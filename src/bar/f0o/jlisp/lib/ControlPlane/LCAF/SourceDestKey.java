/******************************************************************************
 * Copyright (c) 2015 by                                                      *
 * Andreas Stockmayer <stockmay@f0o.bar> and                                  *
 * Mark Schmidt <schmidtm@f0o.bar>                                            *
 *                                                                            *
 * This file (SourceDestKey.java) is part of jlisp.                               *
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
 * 
   Source/Dest Key Canonical Address Format:

     0                   1                   2                   3
     0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |           AFI = 16387         |     Rsvd1     |     Flags     |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |   Type = 12   |     Rsvd2     |             4 + n             |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |            Reserved           |   Source-ML   |    Dest-ML    |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |              AFI = x          |         Source-Prefix ...     |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |              AFI = x          |     Destination-Prefix ...    |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 */

public class SourceDestKey implements LCAFType {

	private short len;
	private byte sourceMl;
	private byte destMl;
	private AfiType typeSource;
	private byte[] sourcePrefix;
	private AfiType typeDest;
	private byte[] destinationPrefix;

	public SourceDestKey(DataInputStream stream) throws IOException {
		stream.readByte();
		len = (short) (stream.readShort() - 4);
		stream.readShort();
		sourceMl = stream.readByte();
		destMl = stream.readByte();
		typeSource = AfiType.fromInt(stream.readShort());
		sourcePrefix = new byte[AfiType.length(typeSource)];
		stream.read(sourcePrefix);
		typeDest = AfiType.fromInt(stream.readShort());
		destinationPrefix = new byte[AfiType.length(typeDest)];
		stream.read(destinationPrefix);
	}

	public SourceDestKey(byte sourceMl, byte destMl, AfiType typeSource, byte[] sourcePrefix, AfiType typeDest,
			byte[] destinationPrefix) {
		super();
		this.sourceMl = sourceMl;
		this.destMl = destMl;
		this.typeSource = typeSource;
		this.sourcePrefix = sourcePrefix;
		this.typeDest = typeDest;
		this.destinationPrefix = destinationPrefix;
		this.len = (short) (8 + sourcePrefix.length + destinationPrefix.length);
	}

	@Override
	public byte[] toByteArray() throws IOException {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		DataOutputStream stream = new DataOutputStream(byteStream);
		stream.writeByte(0);
		stream.writeShort(len + 4);
		stream.writeShort(0);
		stream.writeByte(sourceMl);
		stream.writeByte(destMl);
		stream.writeShort(typeSource.getVal());
		stream.write(sourcePrefix);
		stream.writeShort(typeDest.getVal());
		stream.write(destinationPrefix);
		return byteStream.toByteArray();
	}
	
	public byte[] getRloc(){
		return null;
	}
}
