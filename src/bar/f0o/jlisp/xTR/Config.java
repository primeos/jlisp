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

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class Config {
    private org.w3c.dom.Document config;

    public void load(FileInputStream cfg) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        config = builder.parse(cfg);
    }

    public void store(FileOutputStream cfg){
    }

    public int getComponent(){
        int type = -1;
        try{
            type = Integer.parseInt(config.getDocumentElement().getAttribute("component"));
        } catch(Exception e){}
        return type;
    }

    public String[] getEIDs(){
    	List<String> eids = new ArrayList<>();
    	
        try{
            Node n = config.getDocumentElement().getElementsByTagName("eids").item(0);
            Node nc = n.getFirstChild();
            
            
            do {
            	if(nc.getNodeType() == Node.ELEMENT_NODE) {
            		Element eid = (Element) nc;
            		eids.add(eid.getAttribute("address")+"/"+eid.getAttribute("prefix"));
            	}
            	nc = nc.getNextSibling();
            } while(nc != null);
        } catch (Exception e){e.printStackTrace();}
        if(eids.size() == 0) {
        	eids.add("127.0.0.1/8");
        }
        return eids.toArray(new String[eids.size()]);
    }

    public void list(OutputStream out){

    }

    public void setProperty(String key, String value){

    }

	public byte[] getMS(){
		byte[] ms = {(byte)134,2,11,(byte)173};
        try{
            Element n = (Element) config.getElementsByTagName("ms").item(0);
            ms = InetAddress.getByName(n.getAttribute("address")).getAddress();
        } catch (Exception e){}
        return ms;
	}

    public String getMSPasswd(){
        String passwd = "";
        try{
            Element n = (Element) config.getElementsByTagName("ms").item(0);
            passwd = n.getAttribute("passwd");
        } catch (Exception e){}
        return passwd;
    }

	public int getMTU() {
		return 1500;
	}

	public Rloc[] getRlocs() {
    	List<Rloc> rlocs = new ArrayList<>();
    	
        try{
            Node n = config.getDocumentElement().getElementsByTagName("rlocs").item(0);
            Node nc = n.getFirstChild();
            
            
            do {
            	if(nc.getNodeType() == Node.ELEMENT_NODE) {
            		Element rloc = (Element) nc;
            		rlocs.add(new Rloc(rloc.getAttribute("address"), rloc.getAttribute("prio"), rloc.getAttribute("weight")));
            	}
            	nc = nc.getNextSibling();
            } while(nc != null);
        } catch (Exception e){e.printStackTrace();}
        if(rlocs.size() == 0) {
        	rlocs.add(new Rloc("127.0.0.1", "1", "225"));
        }
        return rlocs.toArray(new Rloc[rlocs.size()]);
	}

	public static boolean useV4() {
		return true;
	}
	
	public static boolean isRTR(){
		return false;
	}
	
	public static void main(String...strings) {
		Config config = new Config();
		try {
			config.load(new FileInputStream("/home/schmidtm/workspace/jlisp/cfg/jlisp.cfg"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("getComponent: " + config.getComponent());
		System.out.println("getEIDs: " + config.getEIDs());
		System.out.println("getMS: " + config.getMS());
		System.out.println("getMSPasswd: " + config.getMSPasswd());
		System.out.println("getMTU: " + config.getMTU());
		System.out.println("getRlocs: " + config.getRlocs());
		System.out.println("useV4: " + config.useV4());
		System.out.println("isRTR: " + config.isRTR());
	}

    public class Rloc {
        private byte[] address;
        private int prio;
        private int weight;

        public Rloc(String address, String prio, String weight) {
            try {
				this.address = InetAddress.getByName(address).getAddress();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
            this.prio = Integer.parseInt(prio);
            this.weight = Integer.parseInt(weight);
        }

        public byte[] getAddress() {
            return this.address;
        }

        public int getPrio() {
            return this.prio;
        }

        public int getWeight() {
            return this.weight;
        }
    }
}
