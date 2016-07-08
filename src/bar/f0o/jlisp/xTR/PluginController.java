package bar.f0o.jlisp.xTR;

import java.util.ArrayList;

import bar.f0o.jlisp.lib.ControlPlane.ControlMessage;
import bar.f0o.jlisp.lib.DataPlane.DataMessage;

public class PluginController {

	private static ArrayList<Plugin> registeredPlugins = new ArrayList<>();

	
	public static void addPlugin(Plugin p){
		registeredPlugins.add(p);
	}
	
	public static byte[] sendRawData(byte[] data){
		for(Plugin p: registeredPlugins)
			data = p.sendRawData(data);
		return data;
	}
	
	public static DataMessage sendLispData(DataMessage data){
		for(Plugin p: registeredPlugins)
			data = p.sendLispData(data);
		return data;
	}
	
	public static DataMessage receiveLispData(DataMessage data){
		for(Plugin p: registeredPlugins)
			data = p.receiveLispData(data);
		return data;
	}
	
	public static byte[] receiveRawData(byte[] data){
		for(Plugin p: registeredPlugins)
			data = p.receiveRawData(data);
		return data;
	}
	
	public static ControlMessage sendControlMessage(ControlMessage data){
		for(Plugin p: registeredPlugins)
			data = p.sendControlMessage(data);
		return data;
	}
	
	public static ControlMessage receiveControlMessage(ControlMessage data){
		for(Plugin p: registeredPlugins)
			data = p.receiveControlMessage(data);
		return data;
	}
	
	
}

