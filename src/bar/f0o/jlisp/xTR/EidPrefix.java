/******************************************************************************
 * Copyright (c) 2015 by                                                      *
 * Andreas Stockmayer <stockmay@f0o.bar> and                                  *
 * Mark Schmidt <schmidtm@f0o.bar>                                            *
 *                                                                            *
 * This file (EidPrefix.java) is part of jlisp.                               *
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

package bar.f0o.jlisp.xTR;

public class EidPrefix {

	private byte[] prefix;
	private int prefixLength;
	private long added;	
	
	public EidPrefix(byte[] prefix, int prefixLength) {
		super();
		this.prefix = prefix;
		this.prefixLength = prefixLength;
		this.added = System.currentTimeMillis();
	}

	public boolean match(byte[] eid){
		int actual = 0;
		while(prefixLength - actual >=8){
			if(prefix[actual/8] != eid[actual/8])
				return false;
			actual+=8;
		}
		byte prefixTmp = prefix[prefixLength/8];
		byte eidTmp = eid[prefixLength/8];
		int bytes = prefixLength -actual;

		for(int i=0;i<bytes;i++){
			if((prefixTmp&(0b10000000>>i)) != (eidTmp&(0b10000000>>i)))
				return false;
		}
		return true;
	}
	
	public int getPrefixLength(){
		return this.prefixLength;
	}
	
	
}
