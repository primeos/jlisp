package bar.f0o.jlisp.lib.tools;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

public abstract class Sender {


	public Sender(String dst, int dstPort){

		byte[] ligBytes = sendPacket();
		DatagramPacket ligPacket;
		try {
			ligPacket = new DatagramPacket(ligBytes, ligBytes.length, InetAddress.getByName(dst), 4342);
			DatagramSocket sock = new DatagramSocket(60573);
			sock.send(ligPacket);
			byte[] answer = new byte[128];
			DatagramPacket ligAnswer = new DatagramPacket(answer, answer.length);;
			sock.receive(ligAnswer);
			sock.close();
			DataInputStream answerStream = new DataInputStream(new ByteArrayInputStream(answer));
			receivePacket(answerStream);

		} catch (Exception e) {
			e.printStackTrace();
		}


	}

	protected abstract byte[] sendPacket();

	protected abstract void receivePacket(DataInputStream str);

}
