/******************************************************************************
 * Copyright (c) 2015 by                                                      *
 * Andreas Stockmayer <stockmay@f0o.bar> and                                  *
 * Mark Schmidt <schmidtm@f0o.bar>                                            *
 *                                                                            *
 * This file (NATTraversal.java) is part of jlisp.                            *
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
 * NAT-Traversal Canonical Address Format:

     0                   1                   2                   3
     0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |           AFI = 16387         |     Rsvd1     |     Flags     |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |   Type = 7    |     Rsvd2     |             4 + n             |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |       MS UDP Port Number      |      ETR UDP Port Number      |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |              AFI = x          |  Global ETR RLOC Address  ... |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |              AFI = x          |       MS RLOC Address  ...    |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |              AFI = x          | Private ETR RLOC Address  ... |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |              AFI = x          |      RTR RLOC Address 1 ...   |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |              AFI = x          |      RTR RLOC Address k ...   |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+

 */

public class NATTraversal implements LCAFType {

	private short len;
	private short msUDPNum = 4342;
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
		len = (short) (stream.readShort() - 4);
		msUDPNum = stream.readShort();
		etrUDPNum = stream.readShort();
		gerAfi = AfiType.fromInt(stream.readShort());
		globalETRRloc = stream.readShort();
		mrAfi = AfiType.fromInt(stream.readShort());
		msRLOC = stream.readShort();
		perAfi = AfiType.fromInt(stream.readShort());
		privateETRRloc = stream.readShort();
		int lengthLeft = len - 12;
		while (lengthLeft >= 0) {
			AfiType ntrAfi = AfiType.fromInt(stream.readShort());
			ntrRlocAfi.add(ntrAfi);
			byte[] ntrRlocTmp = new byte[AfiType.length(ntrAfi)];
			stream.read(ntrRlocTmp);
			ntrRloc.add(ntrRlocTmp);
			len -= (ntrRlocTmp.length + 2);
		}
	}
	/**
	 * 
	 * @param msUDPNum MS UDP Port number
	 * @param etrUDPNum ETR UDP Port number
	 * @param gerAfi Global ETR AFI
	 * @param globalETRRloc Global ETR Rloc
	 * @param mrAfi MS AFI
	 * @param msRLOC Global MS Rloc
	 * @param perAfi Private ETR AFI
	 * @param privateETRRloc Private ETF Rloc
	 * @param ntrRlocAfi List of NTR Afi types
	 * @param ntrRloc List of NTR Rlocs
	 */
	public NATTraversal(short msUDPNum, short etrUDPNum, AfiType gerAfi, short globalETRRloc, AfiType mrAfi,
			short msRLOC, AfiType perAfi, short privateETRRloc, ArrayList<AfiType> ntrRlocAfi,
			ArrayList<byte[]> ntrRloc) {
		super();
		this.msUDPNum = msUDPNum;
		this.etrUDPNum = etrUDPNum;
		this.gerAfi = gerAfi;
		this.globalETRRloc = globalETRRloc;
		this.mrAfi = mrAfi;
		this.msRLOC = msRLOC;
		this.perAfi = perAfi;
		this.privateETRRloc = privateETRRloc;
		this.ntrRlocAfi = ntrRlocAfi;
		this.ntrRloc = ntrRloc;
		this.len = (short) (ntrRlocAfi.size() * 2);
		for (byte[] rloc : ntrRloc)
			len += (short) rloc.length;
	}

	@Override
	public byte[] toByteArray() throws IOException {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		DataOutputStream stream = new DataOutputStream(byteStream);
		stream.writeByte(0);
		stream.writeShort(len + 4);
		stream.writeShort(msUDPNum);
		stream.writeShort(etrUDPNum);
		stream.writeShort(gerAfi.getVal());
		stream.writeShort(globalETRRloc);
		stream.writeShort(mrAfi.getVal());
		stream.writeShort(msRLOC);
		stream.writeShort(perAfi.getVal());
		stream.writeShort(privateETRRloc);
		for (int i = 0; i < ntrRlocAfi.size(); i++) {
			stream.write(ntrRlocAfi.get(i).getVal());
			stream.write(ntrRloc.get(i));
		}
		return byteStream.toByteArray();
	}

	public short getLen() {
		return len;
	}

	public short getMsUDPNum() {
		return msUDPNum;
	}

	public short getEtrUDPNum() {
		return etrUDPNum;
	}

	public AfiType getGerAfi() {
		return gerAfi;
	}

	public short getGlobalETRRloc() {
		return globalETRRloc;
	}

	public AfiType getMrAfi() {
		return mrAfi;
	}

	public short getMsRLOC() {
		return msRLOC;
	}

	public AfiType getPerAfi() {
		return perAfi;
	}

	public short getPrivateETRRloc() {
		return privateETRRloc;
	}

	public ArrayList<AfiType> getNtrRlocAfi() {
		return ntrRlocAfi;
	}

	public ArrayList<byte[]> getNtrRloc() {
		return ntrRloc;
	}

	
	public byte[] getRloc(){
		return null;
	}
}
