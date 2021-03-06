/******************************************************************************
 * Copyright (c) 2015 by                                                      *
 * Andreas Stockmayer <stockmay@f0o.bar> and                                  *
 * Mark Schmidt <schmidtm@f0o.bar>                                            *
 *                                                                            *
 * This file (Rec.java) is part of jlisp.                                     *
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

package bar.f0o.jlisp.lib.ControlPlane;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;


/**
 *Rec including EID Prefix ans Mask length
 */
public class Rec {
        /*     0 1 2 3 4 5 6 7 0 1 2 3 4 5 6 7 0 1 2 3 4 5 6 7 0 1 2 3 4 5 6 7
         *  * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
            * |   Reserved    | EID mask-len  |        EID-Prefix-AFI         |
            * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
            * |                       EID-Prefix  ...                         |
            * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
       */

    private byte eidMaskLen, resevered = 0;
    private ControlMessage.AfiType eidPrefixAfi;
    private byte[]                 eidPrefix;

    @SuppressWarnings("unused")
    private Rec() {
    }

    /**
     * 
     * @param stream Bye Stream including the Rec
     * @throws IOException
     */
    public Rec(DataInputStream stream) throws IOException {
        this.resevered = stream.readByte();
        this.eidMaskLen = stream.readByte();

        this.eidPrefixAfi = ControlMessage.AfiType.fromInt(stream.readShort());
        byte[] buffer = new byte[ControlMessage.AfiType.length(this.eidPrefixAfi)];
        stream.read(buffer);
        this.eidPrefix = buffer;
    }

    /**
     * 
     * @param eidMaskLen Length of the Prefix 
     * @param eidPrefixAfi Afi Type of the Prefix
     * @param eidPrefix Raw prefix bytes
     */
    public Rec(byte eidMaskLen, ControlMessage.AfiType eidPrefixAfi, byte[] eidPrefix) {
        this.eidMaskLen = eidMaskLen;
        this.eidPrefixAfi = eidPrefixAfi;
        this.eidPrefix = eidPrefix;
    }

    /**
     * 
     * @return Raw byte Data of the Rec
     */
    public byte[] toByteArray() {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream stream = new DataOutputStream(byteStream);
        try {
            stream.writeByte(this.resevered);
            stream.writeByte(this.eidMaskLen);
            stream.writeShort(this.eidPrefixAfi.getVal());
            stream.write(this.eidPrefix);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteStream.toByteArray();
    }

    /**
     * Pretty printer
     */
    public String toString() {
        String res = "-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+\n";
        res += "|Reserved|EID-mask-len|EID-Prefix-Afi  |\n";
        res += String.format("|%8d|%12d|%16d|\n", this.resevered, this.eidMaskLen, eidPrefixAfi.getVal());
        res += "-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+\n";
        res += "|              Eid-Prefix              |\n";
        try {
            res += String.format("|%38s|\n", InetAddress.getByAddress(eidPrefix).getHostAddress());
        } catch (UnknownHostException e) {
            res += String.format("|%38s|", "Not IPv4 or IPv6");
        }
        res += "-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+\n";
        return res;
    }

    /**
     * 
     * @return length of the prefix
     */
    public byte getEidMaskLen() {
        return eidMaskLen;
    }

    /**
     * 
     * @return reserved space, not used yet
     */
    public byte getResevered() {
        return resevered;
    }

    /**
     * 
     * @return Afi Type of the EID
     */
    public ControlMessage.AfiType getEidPrefixAfi() {
        return eidPrefixAfi;
    }

    /**
     * 
     * @return Raw EID prefix
     */
    public byte[] getEidPrefix() {
        return eidPrefix;
    }


}
