/******************************************************************************
 * Copyright (c) 2015 by                                                      *
 * Andreas Stockmayer <stockmay@f0o.bar> and                                  *
 * Mark Schmidt <schmidtm@f0o.bar>                                            *
 *                                                                            *
 * This file (MapReply.java) is part of jlisp.                                *
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
import java.util.ArrayList;


/**
 * Map Reply
 * 0 1 2 3 4 5 6 7 0 1 2 3 4 5 6 7 0 1 2 3 4 5 6 7 0 1 2 3 4 5 6 7
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |Type=2 |P|E|S|          Reserved               | Record Count  |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                         Nonce . . .                           |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                         . . . Nonce                           |
 * +-> +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |   |                          Record TTL                           |
 * |   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * R   | Loc Count | EID mask-len  | ACT |A|      Reserved         |
 * e   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * c   | Rsvd  |  Map-Version Number   |       EID-Prefix-AFI          |
 * o   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * r   |                          EID-Prefix                           |
 * d   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |  /|    Priority   |    Weight     |  M Priority   |   M Weight    |
 * | L +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * | o |        Unused Flags     |L|p|R|           Loc-AFI             |
 * | c +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |  \|                             Loc                           |
 * +-> +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * <p/>
 * <p/>
 * <p/>
 * <p/>
 * S-Bit 1: (For further study)
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |    AD Type    |       Authentication Data Content . . .       |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 */
/**
 * Map Reply Message format as defined in RFC6830
 *
 */
public class MapReply extends ControlMessage {

    /**
     * P: probe bit: set if response to locator reachability probe
     * E: Site is enabled for echo-nonce-locator-reachability algorithm
     * S: Security bit if set additional Security information is Attached at the end
     * Record Count: Number of records in this reply
     * Nonce: 64bit Value from Map-Request or 24 bit value set in a data probe packet
     */


    private boolean pBit, eBit, sBit;
    private long  nonce;
    private byte  recordCount;
    private short reserved;
    private ArrayList<Record> records = new ArrayList<>();

    //optional
    private byte adType;
    private int  adContent;
    
    /**
     * 
     * @param stream Raw Byte stream including the Map Reply
     * @throws IOException
     */
    public MapReply(DataInputStream stream) throws IOException {
    	this(stream,stream.readByte());
    }

    /**
     * 
     * @param stream Byte Stream containing the Map Reply without the first byte
     * @param version the missing first byte
     * @throws IOException
     */
    public MapReply(DataInputStream stream,byte version) throws IOException {
    	this.type = 2;
        byte flags = version;
        this.sBit = (flags & 1) != 0;
        this.eBit = (flags & 2) != 0;
        this.pBit = (flags & 4) != 0;
        this.reserved = stream.readShort();
        this.recordCount = stream.readByte();
        this.nonce = stream.readLong();
        for (int i = 0; i < this.recordCount; i++)
            records.add(new Record(stream));
        if (sBit) {
            int ad = stream.readInt();
            adType = (byte) (ad >> 24);
            adContent = ad & 0x00FFFFFF;
        }
    }

    
    /**
     * 
     * @param pBit Probe bit
     * @param eBit Echo nonce locator reachability flag
     * @param sBit Security bit
     * @param nonce 64 bit Nonce
     * @param records Records included in the Map reply
     */
    public MapReply(boolean pBit, boolean eBit, boolean sBit, long nonce, ArrayList<Record> records) {
        this.sBit = sBit;
        this.eBit = eBit;
        this.pBit = pBit;
        this.recordCount = (byte) records.size();
        this.nonce = nonce;
        this.records = records;
    }

    public byte[] toByteArray() {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream stream = new DataOutputStream(byteStream);
        try {
            byte flagsTypeTmp = 32; //Type = 2
            if (this.sBit)
                flagsTypeTmp |= 2;
            if (this.eBit)
                flagsTypeTmp |= 4;
            if (this.pBit)
                flagsTypeTmp |= 8;
            stream.writeByte(flagsTypeTmp);
            stream.writeShort(this.reserved);
            stream.writeByte(this.recordCount);
            stream.writeLong(this.nonce);
            for (Record r : records)
                stream.write(r.toByteArray());
            if (sBit) {
                stream.writeInt(((adType << 24) | adContent));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteStream.toByteArray();
    }

    @Override
    /**
     * Pretty printer for the message
     */
    public String toString() {
        String ret = "MapReply [pBit=" + pBit + ", eBit=" + eBit + ", sBit=" + sBit
                     + ", nonce=" + nonce + ", recordCount=" + recordCount
                     + ", reserved=" + reserved + ", records=";
        for (Record rec : records)
            ret += "{" + rec.toString() + "}";
        ret += ", adType=" + adType + ", adContent=" + adContent + "]";
        return ret;
    }

    //Getter


    /**
     * 
     * @return Probe bit
     */
    public boolean ispBit() {
        return pBit;
    }

    /**
     * 
     * @return Echo nonce locator reachability
     */
    public boolean iseBit() {
        return eBit;
    }
    
    /**
     * 
     * @return security bit
     */
    public boolean issBit() {
        return sBit;
    }

    /**
     * 
     * @return 64bit Nonce
     */
    public long getNonce() {
        return nonce;
    }

    /**
     * 
     * @return Number of records included
     */
    public byte getRecordCount() {
        return recordCount;
    }

    /**
     * 
     * @return reserved Bytes, not used yet
     */
    public short getReserved() {
        return reserved;
    }

    /**
     * 
     * @return The records included in the Map Reply
     */
    public ArrayList<Record> getRecords() {
        return records;
    }

    /**
     * 
     * @return Authentificationtype Data, not yet used
     */
    public byte getAdType() {
        return adType;
    }

    /**
     * 
     * @return Authentification Data, not yet used
     */
    public int getAdContent() {
        return adContent;
    }


}
