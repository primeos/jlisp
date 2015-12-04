/******************************************************************************
 * Copyright (c) 2015 by                                                      *
 * Andreas Stockmayer <stockmay@f0o.bar> and                                  *
 * Mark Schmidt <schmidtm@f0o.bar>                                            *
 *                                                                            *
 * This file (Config.java) is part of jlisp.                                  *
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

public class Config {

	public static byte[] getMS(){
		byte[] ms = {(byte)134,2,11,(byte)173};
		return ms;
	}
	
	public static int getMTU() {
		return 1500;
	}

	public static String[] getEIDPrefix() {
		String [] eids = {"10.0.0.1/24"};
		return eids;
	}

	public static byte[][] getOwnRloc() {
		byte[][] rloc = {{(byte) 134,2,11,(byte)132}};
		return rloc;
	}

	public static boolean useV4() {
		return true;
	}
	
	public static boolean isRTR(){
		return false;
	}
}
