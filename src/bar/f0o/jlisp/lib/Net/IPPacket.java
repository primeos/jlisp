package bar.f0o.jlisp.lib.Net;

public abstract class IPPacket {

	public abstract byte[] toByteArray();
	public abstract void addPayload(IPPayload payload);

        public abstract void getSrcIP();
        public abstract void getDstIP();

        public abstract void getTTL();
        public abstract void setTTL(int ttl);

        public abstract void getToS();
        public abstract void setToS(int tos);

        public static IPPacket fromByteArray(byte[] packet) throws RuntimeExcption {
            if (packet.length < 20) //Minimum packet size of IPv4 is 20 bytes header + 0 bytes payload
                throw new RuntimeException("Payload too short for IP");
            if ((packet[0] >> 4) == 4)
                return new IPv4Packet(payload);
            else if ((packet[0] >> 4) == 6)
                return new IPv6Packet(payload);
            else
                throw new RuntimeException("Illegal IP version number");
        }
}
