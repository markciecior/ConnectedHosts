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
import jcifs.netbios.NbtAddress;


public class NBTLookup {
	
	public NBTLookup(){
		
	}
	
	public String getNBTName(String addr){
		NbtAddress[] nbtArray;
		String nbtName = "";
		try {
		nbtArray = NbtAddress.getAllByAddress(addr, 0, "");
		/*More than one NBT name is usually returned, we only care about the first one (it's the machine name)
		*/		
			if (!(nbtArray[0] == null)){
				nbtName = (nbtArray[0].getHostName());
			}
		} catch (Exception u){
			nbtName = "";
		}
		return nbtName;
	}
}
