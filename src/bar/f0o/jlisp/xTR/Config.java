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
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

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
        String[] eids = {"127.0.0.1/8"};
        try{
            Node n = config.getElementsByTagName("eids").item(0);
            int c = n.getChildNodes().getLength();
            eids = new String[c];
            for(int i = 0; i < c; i++){
                Element eid = ((Element)n.getChildNodes().item(i));
                eids[i] = eid.getAttribute("address")+"/"+eid.getAttribute("prefix");
            }
        } catch (Exception e){}
        return eids;
    }

    public void list(OutputStream out){

    }

    public void setProperty(String key, String value){

    }

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
