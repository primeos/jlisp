package bar.f0o.jlisp.xTR;

import bar.f0o.jlisp.lib.ControlPlane.ControlMessage;
import bar.f0o.jlisp.lib.DataPlane.DataMessage;

public interface Plugin {

	public byte[] sendRawData(byte[] data);
	
	public DataMessage sendLispData(DataMessage data);
	
	public DataMessage receiveLispData(DataMessage data);
	
	public byte[] receiveRawData(byte[] data);
	
	public ControlMessage sendControlMessage(ControlMessage data);
	
	public ControlMessage receiveControlMessage(ControlMessage data);
	
}
