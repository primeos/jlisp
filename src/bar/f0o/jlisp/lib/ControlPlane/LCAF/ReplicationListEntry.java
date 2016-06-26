/******************************************************************************
 * Copyright (c) 2015 by                                                      *
 * Andreas Stockmayer <stockmay@f0o.bar> and                                  *
 * Mark Schmidt <schmidtm@f0o.bar>                                            *
 *                                                                            *
 * This file (ReplicationListEntry.java) is part of jlisp.                               *
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
import java.util.ArrayList;

import bar.f0o.jlisp.lib.ControlPlane.ControlMessage.AfiType;

/*
 * Replication List Entry Address Format:

     0                   1                   2                   3
     0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |           AFI = 16387         |     Rsvd1     |     Flags     |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |   Type = 13   |    Rsvd2      |             4 + n             |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |              Rsvd3            |     Rsvd4     |  Level Value  |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |              AFI = x          |           RTR/ETR #1 ...      |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |              Rsvd3            |     Rsvd4     |  Level Value  |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |              AFI = x          |           RTR/ETR  #n ...     |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+

 */

public class ReplicationListEntry implements LCAFType {

	private short len;
	private ArrayList<Byte> levelValue = new ArrayList<>();
	private ArrayList<AfiType> trType = new ArrayList<>();
	private ArrayList<byte[]> tr = new ArrayList<>();

	public ReplicationListEntry(DataInputStream stream) throws IOException {
		stream.readByte();
		len = (short) (stream.readShort() - 4);
		int lenTmp = len;
		while (lenTmp > 0) {
			stream.readShort();
			stream.readByte();
			levelValue.add(stream.readByte());
			AfiType type = AfiType.fromInt(stream.readShort());
			byte[] actualTr = new byte[AfiType.length(type)];
			stream.read(actualTr);
			trType.add(type);
			tr.add(actualTr);
		}
	}

	public ReplicationListEntry(ArrayList<Byte> levelValue, ArrayList<AfiType> trType, ArrayList<byte[]> tr) {
		super();
		this.levelValue = levelValue;
		this.trType = trType;
		this.tr = tr;
		this.len = (short) (6 * tr.size());
		for (byte[] rloc : tr)
			len += (short) rloc.length;
	}

	@Override
	public byte[] toByteArray() throws IOException {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		DataOutputStream stream = new DataOutputStream(byteStream);
		stream.writeByte(0);
		stream.writeShort(len + 4);
		for (int i = 0; i < tr.size(); i++) {
			stream.writeShort(0);
			stream.writeByte(0);
			stream.writeByte(levelValue.get(i));
			stream.writeShort(trType.get(i).getVal());
			stream.write(tr.get(i));
		}
		return byteStream.toByteArray();
	}
	
	public byte[] getRloc(){
		return null;
	}
}
