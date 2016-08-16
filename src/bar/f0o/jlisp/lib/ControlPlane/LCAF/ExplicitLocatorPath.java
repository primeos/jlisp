/******************************************************************************
 * Copyright (c) 2015 by                                                      *
 * Andreas Stockmayer <stockmay@f0o.bar> and                                  *
 * Mark Schmidt <schmidtm@f0o.bar>                                            *
 *                                                                            *
 * This file (ExplicitLocatorPath.java) is part of jlisp.                     *
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
import java.util.Arrays;

import bar.f0o.jlisp.JLISP;
import bar.f0o.jlisp.lib.ControlPlane.ControlMessage.AfiType;
import bar.f0o.jlisp.xTR.Config;
import bar.f0o.jlisp.xTR.Config.Rloc;

/*
 *  Explicit Locator Path (ELP) Canonical Address Format:

     0                   1                   2                   3
     0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |           AFI = 16387         |     Rsvd1     |     Flags     |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |   Type = 10   |     Rsvd2     |               n               |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |           Rsvd3         |L|P|S|           AFI = x             |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |                         Reencap Hop 1  ...                    |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |           Rsvd3         |L|P|S|           AFI = x             |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |                         Reencap Hop k  ...                    |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+

 */

public class ExplicitLocatorPath implements LCAFType {

	private short n;
	private ArrayList<Boolean> lBits = new ArrayList<>();
	private ArrayList<Boolean> pBits = new ArrayList<>();
	private ArrayList<Boolean> sBits = new ArrayList<>();
	private ArrayList<AfiType> afiTypes = new ArrayList<>();
	private ArrayList<byte[]> reencapHops = new ArrayList<>();

	public ExplicitLocatorPath(DataInputStream stream) throws IOException {
		stream.readByte();
		n = stream.readShort();
		int len = n;
		while (len > 0) {
			short flags = stream.readShort();
			lBits.add((flags & 0b0000000000000100) != 0);
			pBits.add((flags & 0b0000000000000010) != 0);
			sBits.add((flags & 0b0000000000000001) != 0);
			AfiType type = AfiType.fromInt(stream.readShort());
			afiTypes.add(type);
			byte[] hop = new byte[AfiType.length(type)];
			stream.read(hop);
			reencapHops.add(hop);
			len -= (4 + hop.length);
		}
	}

	/**
	 * 
	 * @param lBits
	 *            List of L Flags
	 * @param pBits
	 *            List of P Flags
	 * @param sBits
	 *            List of S Flags
	 * @param afiTypes
	 *            List of AFI types
	 * @param reencapHops
	 *            List of the Locators for reencapsulation
	 */
	public ExplicitLocatorPath(ArrayList<Boolean> lBits, ArrayList<Boolean> pBits, ArrayList<Boolean> sBits,
			ArrayList<AfiType> afiTypes, ArrayList<byte[]> reencapHops) {
		super();
		this.lBits = lBits;
		this.pBits = pBits;
		this.sBits = sBits;
		this.afiTypes = afiTypes;
		this.reencapHops = reencapHops;
		this.n = (short) (4 * reencapHops.size());
		for (byte[] rloc : reencapHops)
			n += (short) rloc.length;
	}

	@Override
	public byte[] toByteArray() throws IOException {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		DataOutputStream stream = new DataOutputStream(byteStream);
		stream.writeByte(0);
		stream.writeShort(n);
		for (int i = 0; i < reencapHops.size(); i++) {
			short flags = 0;
			flags = (short) (lBits.get(i) ? (flags | 0b0000000000000100) : flags);
			flags = (short) (pBits.get(i) ? (flags | 0b0000000000000010) : flags);
			flags = (short) (sBits.get(i) ? (flags | 0b0000000000000001) : flags);
			stream.writeShort(flags);
			stream.writeShort(afiTypes.get(i).getVal());
			stream.write(reencapHops.get(i));
		}
		return byteStream.toByteArray();
	}

	public short getN() {
		return n;
	}

	public ArrayList<Boolean> getlBits() {
		return lBits;
	}

	public ArrayList<Boolean> getpBits() {
		return pBits;
	}

	public ArrayList<Boolean> getsBits() {
		return sBits;
	}

	public ArrayList<AfiType> getAfiTypes() {
		return afiTypes;
	}

	public ArrayList<byte[]> getReencapHops() {
		return reencapHops;
	}

	public byte[] getRloc() {
		Config.Rloc[] rlocs = JLISP.getConfig().getRlocs();
		if (JLISP.getConfig().isRTR()) {
			for (Rloc rloc : rlocs) {
				int index = -1;
				for(int i=0;i<reencapHops.size();i++){
					if(Arrays.equals(reencapHops.get(i), rloc.getAddress()))
					{
						index = i;
						break;
					}
				}
				if(index >=0)
					return index < this.reencapHops.size()-1 ? this.reencapHops.get(index + 1) : null;
				
			}
		}
		return this.reencapHops.get(0);
	}

}
