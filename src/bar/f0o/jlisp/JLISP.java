/******************************************************************************
 * Copyright (c) 2015 by                                                      *
 * Andreas Stockmayer <stockmay@f0o.bar> and                                  *
 * Mark Schmidt <schmidtm@f0o.bar>                                            *
 * *
 * This file (JLISP.java) is part of jlisp.                                   *
 * *
 * jlisp is free software: you can redistribute it and/or modify              *
 * it under the terms of the GNU General Public License as published by       *
 * the Free Software Foundation, either version 3 of the License, or          *
 * (at your option) any later version.                                        *
 * *
 * jlisp is distributed in the hope that it will be useful,                   *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.See the                *
 * GNU General Public License for more details.                               *
 * *
 * You should have received a copy of the GNU General Public License          *
 * along with $project.name.If not, see <http://www.gnu.org/licenses/>.       *
 ******************************************************************************/

package bar.f0o.jlisp;

import bar.f0o.jlisp.xTR.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;
import java.util.StringTokenizer;


public class JLISP {
    private final static int MS = 0;
    private final static int XTR = 1;
    private final static int RTR = 2;
    private final static int PXTR = 3;
    private final static int NTR = 5;
    private final static int HAXTR = 7;
    private final static int HARTR = 8;

    private static Config config;

    /**
     * 	Returns config of current instance of JLISP
     * @return Config of current instance
     */
    public static Config getConfig() {
        return JLISP.config;
    }

    /**
     * 
     * @param args Config file and foreground flag
     */
    public static void main(String[] args) {
        if (args.length == 0 || args.length > 2) {
            System.out.println("usage: java JLISP <config_path> [-f]");
            System.exit(-1);
        } else {
            boolean foreground = args.length == 2 ? args[1].equalsIgnoreCase("-f"): false;
            config = new Config();
            try {
                config.load(new FileInputStream(args[0]));
                int component = config.getComponent();
                LISPComponent lisp = null;
                switch (component) {
                    case JLISP.MS:
                        //TODO lisp = new MS();
                        break;
                    case JLISP.XTR:
                        lisp = new XTR();
                        break;
                    case JLISP.RTR:
                        lisp = new RTR();
                        break;
                    case JLISP.PXTR:
                        lisp = new PXTR();
                        break;
                    case JLISP.NTR:
                        lisp = new NTR();
                        break;
                    case JLISP.HAXTR:
                        lisp = new HAXTR();
                        break;
                    case JLISP.HARTR:
                        lisp = new HARTR();
                        break;
                    default:
                        System.err.println("no such component");
                        System.exit(-1);
                }
                if (foreground) {
                    String cmd = "";
                    Scanner in = new Scanner(System.in);
                    while (!(cmd = in.nextLine()).equalsIgnoreCase("quit")) {
                        if (cmd.equalsIgnoreCase("help"))
                            System.out.printf("Supported commands are:\n\t%s\n\t%s\n\t%s", "show config", "write config", "set <key> <value>");
                        if (cmd.equalsIgnoreCase("show config")) JLISP.getConfig().list(System.out);
                        if (cmd.equalsIgnoreCase("write config"))
                            JLISP.getConfig().store(new FileOutputStream(args[0]));//, "JLISP config\napp.component: ms=0, xtr=1");
                        if (cmd.toLowerCase().startsWith("set ")) {
                            StringTokenizer tk = new StringTokenizer(cmd, " ");
                            if (tk.countTokens() != 3) break;
                            tk.nextToken();
                            JLISP.getConfig().setProperty(tk.nextToken(), tk.nextToken());
                        }
                    }
                }
                lisp.start();
            } catch (FileNotFoundException e) {
                System.err.println("config file not found");
                System.exit(-1);
            } catch (IOException e) {
                System.err.println("cannot read config file");
                System.exit(-1);
            } catch (ParserConfigurationException e) {
                System.err.println("cannot pase cfg");
                System.exit(-1);
            } catch (SAXException e) {
                System.err.println("cannot pase cfg");
                System.exit(-1);
            }
        }
    }
}
