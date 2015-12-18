/*Copyright (C) 2013 Mark Ciecior

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along
    with this program; if not, write to the Free Software Foundation, Inc.,
    51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*/

package com.markciecior.snmp.devices;
import java.util.LinkedList;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;


public class DNSLookup {

	private static final String MX_ATTRIB = "MX";
	private static final String ADDR_ATTRIB = "A";
	private static final String PTR_ATTRIB = "PTR";
	@SuppressWarnings("unused")
	private static String[] MX_ATTRIBS = {MX_ATTRIB};
	@SuppressWarnings("unused")
	private static String[] ADDR_ATTRIBS = {ADDR_ATTRIB};
	 
	private InitialDirContext idc;
	 
	public DNSLookup(String myNS) throws NamingException {
	  Properties env = new Properties();
	  env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.dns.DnsContextFactory");
	  String nameserver = "dns://" + myNS;
	  env.put(Context.PROVIDER_URL, nameserver);
	  idc = new InitialDirContext(env);
	}	


public LinkedList<String> getRevName(String ipAddr) throws NamingException {
  LinkedList<String> nameList = new LinkedList<String>();
  String[] quads = ipAddr.split("\\.");
 
  ipAddr = "";
 
  for (int i = quads.length - 1; i >= 0; i--) {
    ipAddr += quads[i] + ".";
  }
 
  ipAddr += "in-addr.arpa.";
  try {
  Attributes attrs = idc.getAttributes(ipAddr, new String[] {PTR_ATTRIB});
  Attribute attr = attrs.get(PTR_ATTRIB);
  
  /*More than one PTR record could be returned, add them all to a LinkedList
  */
  if (attr != null) {
	  for (int i=0; i < attr.size(); i++){
		  nameList.add((String)attr.get(i));
	  }
  }
  
  /*If nothing is returned, return a blank LinkedList
	*/	  
  } catch (NamingException n){
	  LinkedList<String> blank = new LinkedList<String>();
	  blank.add("");
	  nameList = blank;
  }
 return nameList;
}

	
	
}
