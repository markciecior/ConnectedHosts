/*Copyright (C) 2015 Mark Ciecior

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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.text.DefaultCaret;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
 
public class DeviceCollector extends JPanel {

	private static final long serialVersionUID = 1L;
	protected static final String textFieldString = "JTextField";
    protected static final String passwordFieldString = "JPasswordField";
    protected static final String ftfString = "JFormattedTextField";
    protected static final String buttonString = "JButton";
    protected static final String runProject = "runProject";
    protected static final String cancelProject = "cancelProject";
    protected static final String saveARPButton = "saveARPButton";
    protected static final String saveOutputButton = "saveOutputButton";
    protected static final String newArpTableString = "getNewArpTable";
    protected static final String oldArpTableString = "useSavedArpTable";
    protected static final String chooseArpTableString = "chooseArpTable";
    protected static final String clearTextString = "clearText";
    protected static boolean USE_SAVED_ARP_TABLE = false;
    protected static final int TEXT_FIELD_WIDTH = 20;
    
    protected static final String licenseText = "Copyright (C) 2015 Mark Ciecior\r\n\r\n" +

											    "This program is free software; you can redistribute it and/or modify\r\n" +
											    "it under the terms of the GNU General Public License as published by\r\n" +
											    "the Free Software Foundation; either version 2 of the License, or\r\n" +
											    "(at your option) any later version.\r\n\r\n" +
											    
											    "This program is distributed in the hope that it will be useful,\r\n" +
											    "but WITHOUT ANY WARRANTY; without even the implied warranty of\r\n" +
											    "MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\r\n" +
											    "GNU General Public License for more details.\r\n\r\n" +
											
											    "You should have received a copy of the GNU General Public License along\r\n" +
											    "with this program; if not, write to the Free Software Foundation, Inc.,\r\n" +
											    "51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.\r\n\r\n" +
											    
											    "http://www.gnu.org/licenses/gpl.html\r\n\r\n" +
											    
											    "This program makes use of the jCIFS and SNMP4j APIs.\r\n" +
											    "Their licenses can be found below:\r\n\r\n" +
											    "SNMP4j: http://www.snmp4j.org/LICENSE-2_0.txt\r\n" +
											    "jCIFS: http://www.gnu.org/licenses/lgpl-2.1.txt\r\n" +
											    "Apache POI: http://www.apache.org/licenses/LICENSE-2.0.txt";
    
    protected static final String aboutText = "Mark's Device Collector\r\n" +
    										  "Version 1.2 (18 December 2015)\r\n" +
    										  "by Mark Ciecior, CCIE #28274\r\n" +
    										  "www.github.com/markciecior/ConnectedHosts";
    
    protected static final String helpText = "1) Enter the address/hostname and SNMP v2c community string of the access switch whose connected hosts you want to find.\r\n" +
    										 "2) Enter a comma-separated list of the interface names that should not be queried.  It's best to include uplinks so time isn't\r\n" +
    										 "    wasted querying MAC/ARP tables for devices that aren't directly connected.\r\n" +
    										 "3) Enter the address/hostname and SNMP v2c community string of the switch that contains ARP entries for devices attached to the\r\n" +
    										 "    access switch.  This will usually be the router or L3 switch containing the gateway addresses.\r\n" +
    										 "4) Choose whether you want to search for DNS and/or NetBIOS names in addition to the IP addresses of connected devices\r\n" +
    										 "5) Choose whether to use a saved ARP table or not.  If this is the first time running the application, leave this set to No.\r\n" +
    										 "    If you have multiple access switches to query it often  makes sense to cache the ARP table of the core switch and refer to a\r\n" +
    										 "    saved copy instead of polling the ARP table every time.\r\n" +
    										 "6) Click the green GO! button.\r\n" +
    										 "7) Watch the output in the center pane.\r\n" +
    										 "8) Once the status field shows 'Discovery completed' you can save the output to a XLSX file.\r\n" +
    										 "9) If you plan to query other access switches (and use the same L3 device's ARP table), click 'Save ARP Table' and the saved\r\n" +
    										 "    copy will be placed in your temp directory.\r\n" +
    										 "10) Clear the output and start again!\r\n";
    
    private String ACCESS_ADDR;
    private String ACCESS_SNMP;
    private String GATEWAY_ADDR;
    private String GATEWAY_SNMP;
    private String UPLINK;
    private String NAMESERVER;
    
    String ARP_TABLE_PATH = System.getProperty("java.io.tmpdir");
    String CSV_OUTPUT_PATH = System.getProperty("java.io.tmpdir");
 
    protected JLabel actionLabel;
    
    static JFrame frame;
    
    JTextField textFieldAccessAddr;
    JTextField textFieldAccessSNMP;
    JTextField textFieldGatewayAddr;
    JTextField textFieldGatewaySNMP;
    JTextField textFieldNameserver;
    JTextField textFieldUplink;
    
    JTextArea textArea;
    
    JLabel statusLabel;
    
    JPanel saveControlsPane;
    
    JCheckBox enableDNS;
    JCheckBox enableNBT;
    
    JRadioButton newArpTable;
    JRadioButton oldArpTable;
    JButton chooseArpTable;
    JTextField arpTablePath;
    
    JButton saveARP;
    
    @SuppressWarnings("rawtypes")
	HashMap ARPTable;
    DiscoverWorker dw;
    
    LinkedList<String> vlanList = new LinkedList<String>();
    final SNMPManager man = new SNMPManager();
    
    
/*    class VlanWorker extends SwingWorker<LinkedList<String>, Void> {
    	private String myAccessAddr;
    	private String myAccessSNMP;
    	
    	public VlanWorker(String addr, String community){
    		myAccessAddr = addr;
    		myAccessSNMP = community;
    	}
    
		public LinkedList<String> doInBackground() {
			LinkedList<String> retVal = new LinkedList<String>();
			retVal = man.getVlans(myAccessAddr, myAccessSNMP);
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return retVal;
		}
		
		public void done() {
			try {
				vlanList = get();
			} catch (InterruptedException ignore) {}
			catch (ExecutionException e) {
				String why = null;
				Throwable cause= e.getCause();
				if (cause != null) {
					why = cause.getMessage();
				} else {
					why = e.getMessage();
				}
				System.err.println("Error getting VLAN List: " + why);
			}
		}
    }*/
    
    
    /*This is the SwingWorker class that does all the heavy work in the background
    */
    class DiscoverWorker extends SwingWorker<Void, String> {
    	private String myAccessAddr;
    	private String myAccessSNMP;
    	private String myGatewayAddr;
    	private String myGatewaySNMP;
    	private String myNS;
    	private String uplinkPortName;
    	private boolean dnsEnabled;
    	private boolean nbtEnabled;
    	
    	public DiscoverWorker(String addr, String community, String gwaddr, String gwcommunity, String ns, String uplink, boolean dns, boolean nbt){
    		myAccessAddr = addr;
    		myAccessSNMP = community;
    		myGatewayAddr = gwaddr;
    		myGatewaySNMP = gwcommunity;
    		myNS = ns;
    		uplinkPortName = uplink;
    		dnsEnabled = dns;
    		nbtEnabled = nbt;
    	}
    
		@SuppressWarnings({ "rawtypes", "unchecked" })
		protected Void doInBackground() {
			Void retVal = null;
			
			String myVLANAccessSNMP = "";
	    	HashMap MACTable = new HashMap();
	    	
	    	try {
	    		man.start();
	    			    		
	    		actionLabel.setText("Discovering VLANs");
	    		//Testing
	    		if (isCancelled()){ return retVal; }
	    		/*First we need to gather all the active VLANs on the access switch
	    		*/
	    		LinkedList<String> vlanList = man.getVlans(myAccessAddr, myAccessSNMP);
	    		
	    		actionLabel.setText("VLANs discovered");
	    		
//	    		MACTable = open("/home/mark/Desktop/arp/mac_table.txt");
	        	
	        	/*Then go through each VLAN and gather the connected MAC addresses.
	        	This requires looking up four separate tables and combining the info into one
	        	Then put the info from every VLAN into a final HashMap
	        	*/
	    		for (int a=0; a < vlanList.size(); a++){
	    			/*Ignore these Cisco-specific system VLANs
	    			*/
	    			if (vlanList.get(a).equals("1002") ||
	    				vlanList.get(a).equals("1003") ||
	    				vlanList.get(a).equals("1004") ||
	    				vlanList.get(a).equals("1005"))
	    			{
	    				continue;
	    			}
	    			actionLabel.setText("Discovering MACs in VLAN " + vlanList.get(a));
	    			myVLANAccessSNMP = myAccessSNMP + "@" + vlanList.get(a);
	    			if (isCancelled()) {return retVal;}
	    			HashMap macCrazyId = man.getMACToCrazyId(myAccessAddr, myVLANAccessSNMP);
	        		if (isCancelled()) {return retVal;}
	        		HashMap crazyIdBPort = man.getCrazyIdToBridgePort(myAccessAddr, myVLANAccessSNMP);
	        		if (isCancelled()) {return retVal;}
	        		HashMap bPortIndex = man.getBridgePortToIfIndex(myAccessAddr, myVLANAccessSNMP);
	        		if (isCancelled()) {return retVal;}
	        		HashMap indexName = man.getIfIndexToIfName(myAccessAddr, myVLANAccessSNMP);
	        		HashMap tempMACTable = man.getMACToIfName(macCrazyId, crazyIdBPort, bPortIndex, indexName);
	        		MACTable.putAll(tempMACTable);
	    		}
	        	
	    		
	    		actionLabel.setText("MACs discovered");
	    		//save(MACTable, "/home/mark/Desktop/arp/mac_table.txt");
	    		
	    		/*We don't want to waste time looking up information about MACs that are connected to different
	    		switches (but show up in the CAM table b/c of broadcast frames).  The user can exclude MACs
	    		learned on specific interfaces by entering a comma-separated list which we parse here
	    		*/
	    		String[] uplinkPortList;
	    		uplinkPortList = uplinkPortName.split(",");
	    		for (int j=0; j < uplinkPortList.length; j++){
	    			MACTable.values().removeAll(Collections.singleton(uplinkPortList[j].trim()));
	    		}

	    		/*The user has the option of using a saved ARP table.  This can save time if running this
	    		program against a lot of access layer switches that use the same L3 switch for their gateway
	    		*/
	    		ARPTable = new HashMap();
	    		actionLabel.setText("Discovering ARP Table");
	    		if (isCancelled()){ return retVal; }
	    		if (USE_SAVED_ARP_TABLE){
	    			ARPTable = open(arpTablePath.getText());
	    		} else {
	    				ARPTable = man.getArpTable(myGatewayAddr, myGatewaySNMP);
	    		}
	    		actionLabel.setText("ARP Table discovered");

	    		/*This HashMap holds the MACs->IPs of the devices connected to just the specified access switch
	    		*/
	    		HashMap connectedMAC_IPs = man.getConnectedIPs(MACTable, ARPTable);
	    		
	    		
	    		Iterator it = MACTable.entrySet().iterator();
	        	String myIP = "";
	        	String myName = "";
	        	String myNbtName = "";
	        	DNSLookup dns = new DNSLookup(myNS); 
	        	NBTLookup nbt = new NBTLookup();
	        	
	        	//Go through each connected MAC address, then each of its IP addresses, and look for DNS/NetBIOS names
	        	while (it.hasNext()){
	        		if (isCancelled()){ return retVal; }
	        		Map.Entry pairs = (Map.Entry)it.next();
	        		String myMAC = (String)pairs.getKey();
	        		String myPort = (String)pairs.getValue();
	        		actionLabel.setText("Discovering " + myMAC + " on port " + myPort);
	        		LinkedList<String> myIPList = (LinkedList<String>)connectedMAC_IPs.get(myMAC);
	        		LinkedList<String> myNameList = null;
	        		
	        		//Every MAC address has a LinkedList of IP addresses associated with it, go through all of them
	        		for (int i=0; i < myIPList.size(); i++){
	        			//If the List of IP addresses is empty, just return blank spaces for the CSV file
	        			if (myIPList.get(i) == null) {
	        				myIP = "";
	        				myName = "";
	        				String out = myMAC + "," + myPort + "," + myIP + "," + myName;// + "," + myNbtName;
	        				publish(out);
	        			} else {
	        			//If there are IP addresses associated with this MAC, get the DNS/NBT names (if the user wants to)
	        			myIP = myIPList.get(i);
	        			if (dnsEnabled){
	        				myNameList = dns.getRevName(myIP);
	        			} else {
	        				LinkedList<String> tmp = new LinkedList<String>();
	        				tmp.add("");
	        				myNameList = tmp;
	        			}
	        			if (nbtEnabled) {
	        				myNbtName = nbt.getNBTName(myIP);
	        			} else {
	        				myNbtName = "";
	        			}
	        			
	        			//By now all the DNS/NBT names have been learned, we need to put the lists together
	        			// so each type of name is printed on its own line in the final output
	        			if (myNameList.get(0).equals("") && myNbtName.equals("")){
	        			// Both DNS and NBT are empty, do nothing
	        				
	        			} else if (!myNameList.get(0).equals("") && myNbtName.equals("")) {
	        			// DNS non-empty, NBT empty, keep the DNS list the same
	        				
	        			} else if (myNameList.get(0).equals("") && !myNbtName.equals("")) {
	        			// DNS empty, NBT non-empty, put the NBT name as the first (and only) entry in the DNS list
	        				myNameList.set(0, myNbtName);	        				
	        			} else {
	        			// Both DNS and NBT non-empty, add the NBT name to the end of the DNS list
	        				myNameList.add(myNbtName);	        				
	        			}
	        			
	        			//Now we have a complete list of DNS/NBT names
	        			// Iterate through them and print them alongside the MAC/interface/IP
	        			for (int k=0; k < myNameList.size(); k++){
	        				String out = myMAC + "," + myPort + "," + myIP + "," + myNameList.get(k);
	        				
	        				//Publish this info so the Event Dispatch Thread can update the text area
	        				publish(out);
	        			} //print all hostnames (DNS and NBT)
	        		} //if there's an IP in the ARP table
	        	} // Loop through all IPs
	    		} //Loop through all connected MACs
	        	actionLabel.setText("Discovery Complete!");
	        	} catch (Exception error){
	        		System.out.println("error in doInBackground");
	        		error.printStackTrace();
	        		//System.exit(1);
	        	}
			
			return retVal;
		}
		
		
		/*Run in the Event Dispatch Thread, this takes the output from the Worker thread and updates the text area
		*/
    	@Override
		protected void process(List<String> mac) {
			Iterator<String> iter = mac.iterator();
			while (iter.hasNext()) {
				textArea.setText(textArea.getText() + iter.next() + System.getProperty("line.separator"));
				iter.remove();
			}
		}

		public void done() {
			
	        	        
			if (isCancelled()) {
//				System.out.println("Done - Stopped by user");
			} else {
//				System.out.println("Done - Finished on its own");

				
			}
		}
    }
    
    public DeviceCollector() {
    	
    	    	//Try and pull the last values of the text fields from a config file so we can re-populate the GUI
    	try {
    		ACCESS_ADDR = Config.getSetting("accessSwitchAddr");
    		if (ACCESS_ADDR == "null") { ACCESS_ADDR = ""; }
    		
    		ACCESS_SNMP = Config.getSetting("accessSwitchSNMP");
    		if (ACCESS_SNMP == "null") { ACCESS_SNMP = ""; }
    		
    		GATEWAY_ADDR = Config.getSetting("gatewaySwitchAddr");
    		if (GATEWAY_ADDR == "null") { GATEWAY_ADDR = ""; }
    		
    		GATEWAY_SNMP = Config.getSetting("gatewaySwitchSNMP");
    		if (GATEWAY_SNMP == "null") { GATEWAY_SNMP = ""; }
    		
    		UPLINK = Config.getSetting("uplink");
    		if (UPLINK == "null") { UPLINK = ""; }
    		
    		NAMESERVER = Config.getSetting("nameserver");
    		if (NAMESERVER == "null") { NAMESERVER = ""; }
    		
    	} catch (Exception e) {
    		
    	}
    	//Don't wait forever for NBNS names of machines that might not be Windows
    	jcifs.Config.setProperty("jcifs.netbios.retryTimeout", "1000");
    	jcifs.Config.setProperty("jcifs.netbios.retryCount", "1");
        setLayout(new BorderLayout());
        
        textFieldAccessAddr = new JTextField(TEXT_FIELD_WIDTH);
        textFieldAccessAddr.setActionCommand(textFieldString);
        textFieldAccessAddr.setText(ACCESS_ADDR);
        textFieldAccessSNMP = new JTextField(TEXT_FIELD_WIDTH);
        textFieldAccessSNMP.setActionCommand(textFieldString);
        textFieldAccessSNMP.setText(ACCESS_SNMP);
        textFieldUplink = new JTextField(TEXT_FIELD_WIDTH);
        textFieldUplink.setActionCommand(textFieldString);
        textFieldUplink.setText(UPLINK);
        textFieldGatewayAddr = new JTextField(TEXT_FIELD_WIDTH);
        textFieldGatewayAddr.setActionCommand(textFieldString);
        textFieldGatewayAddr.setText(GATEWAY_ADDR);
        textFieldGatewaySNMP = new JTextField(TEXT_FIELD_WIDTH);
        textFieldGatewaySNMP.setActionCommand(textFieldString);
        textFieldGatewaySNMP.setText(GATEWAY_SNMP);
        textFieldNameserver = new JTextField(TEXT_FIELD_WIDTH);
        textFieldNameserver.setActionCommand(textFieldString);
        textFieldNameserver.setText(NAMESERVER);
        
        newArpTable = new JRadioButton("No");
        newArpTable.setSelected(true);
        newArpTable.setActionCommand(newArpTableString);
        newArpTable.addActionListener(new GetNewArpTableListener());
        oldArpTable = new JRadioButton("Yes");
        oldArpTable.setActionCommand(oldArpTableString);
        oldArpTable.addActionListener(new UseSavedArpTableListener());
        ButtonGroup group = new ButtonGroup();
        group.add(newArpTable);
        group.add(oldArpTable);
        JPanel radioPanel = new JPanel(new GridLayout(1, 0));
        radioPanel.add(newArpTable);
        radioPanel.add(oldArpTable);
        
        
        JLabel textFieldLabelSavedArpTable = new JLabel("Use Saved ARP Table?:");
        
        chooseArpTable = new JButton("Choose");
        chooseArpTable.setEnabled(false);
        chooseArpTable.setActionCommand(chooseArpTableString);
        chooseArpTable.addActionListener(new ChooseArpTableListener());
        arpTablePath = new JTextField(TEXT_FIELD_WIDTH);
        
        enableDNS = new JCheckBox("Enable DNS Lookup?");
        enableDNS.setSelected(false);
        
        enableNBT = new JCheckBox("Enable NetBIOS Lookup?");
        enableNBT.setSelected(false);
        
        JButton buttonStart = new JButton("GO!");
        buttonStart.setActionCommand(runProject);
        buttonStart.addActionListener(new StartDiscoveryListener());
        buttonStart.setBackground(Color.GREEN);
        
        saveARP = new JButton("Save ARP Table");
        saveARP.setActionCommand(saveARPButton);
        saveARP.addActionListener(new SaveButtonListener());
        
        JButton saveOutput = new JButton("Save Output as XLSX");
        saveOutput.setActionCommand(saveOutputButton);
        saveOutput.addActionListener(new SaveOutputListener());
        
        JButton buttonStop = new JButton("STOP!");
        buttonStop.setActionCommand(cancelProject);
        buttonStop.addActionListener(new StopDiscoveryListener());
        buttonStop.setBackground(Color.RED);
        
        JButton clearText = new JButton("Clear Output");
        clearText.setActionCommand(clearTextString);
        clearText.addActionListener(new ClearTextFieldListener());
        
        JLabel textFieldLabelAccessAddr = new JLabel("Access Switch Name/IP:");
        textFieldLabelAccessAddr.setLabelFor(textFieldAccessAddr);
        
        JLabel textFieldLabelAccessSNMP = new JLabel("Access Switch SNMP String:");
        textFieldLabelAccessSNMP.setLabelFor(textFieldAccessSNMP);
        
        JLabel textFieldLabelUplink = new JLabel("Access Switch Uplink Port(s) [comma-separated]:");
        textFieldLabelUplink.setLabelFor(textFieldUplink);
                
        JLabel textFieldLabelGatewayAddr = new JLabel("Gateway Switch Name/IP:");
        textFieldLabelGatewayAddr.setLabelFor(textFieldGatewayAddr);
        
        JLabel textFieldLabelGatewaySNMP = new JLabel("Gateway Switch SNMP String:");
        textFieldLabelGatewaySNMP.setLabelFor(textFieldGatewaySNMP);
        
        JLabel textFieldLabelNameserver = new JLabel("Nameserver:");
        textFieldLabelNameserver.setLabelFor(textFieldNameserver);
        
        JLabel textFieldLabelButton = new JLabel();
        textFieldLabelButton.setLabelFor(buttonStart);
        
        JLabel textFieldLabelsaveARP = new JLabel();
        textFieldLabelsaveARP.setLabelFor(saveARP);
        
        actionLabel = new JLabel();
 
        //Lay out the text controls and the labels.
        JPanel textControlsPane = new JPanel();
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
 
        textControlsPane.setLayout(gridbag);
 
        JLabel[] labels = {textFieldLabelAccessAddr, textFieldLabelAccessSNMP, textFieldLabelUplink, textFieldLabelGatewayAddr, textFieldLabelGatewaySNMP, textFieldLabelNameserver};
        JComponent[] textFields = {textFieldAccessAddr, textFieldAccessSNMP, textFieldUplink, textFieldGatewayAddr, textFieldGatewaySNMP, textFieldNameserver};
        JComponent[] boxes = {enableDNS, enableNBT};
        addLabelTextRows(labels, textFields, boxes, gridbag, textControlsPane);
        
        c.gridwidth = 2;
        c.fill = GridBagConstraints.NONE;      //reset to default
        c.gridy = 6;
        textControlsPane.add(enableDNS, c);
        c.fill = GridBagConstraints.NONE;      //reset to default
        c.gridy = 6;
        textControlsPane.add(enableNBT, c);
 
        textControlsPane.setBorder(
                BorderFactory.createCompoundBorder(
                                BorderFactory.createTitledBorder("Switch Information"),
                                BorderFactory.createEmptyBorder(5,5,5,5)));

        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;      //reset to default
        c.gridy = 7;
        textControlsPane.add(textFieldLabelSavedArpTable, c);
        c.fill = GridBagConstraints.NONE;      //reset to default
        c.gridy = 7;
        textControlsPane.add(radioPanel, c);
        c.fill = GridBagConstraints.NONE;      //reset to default
        c.gridy = 7;
        textControlsPane.add(chooseArpTable, c);
        c.fill = GridBagConstraints.NONE;      //reset to default
        c.gridy = 7;
        textControlsPane.add(arpTablePath, c);
        
        c.fill = GridBagConstraints.NONE;      //reset to default
        c.gridy = 8;
        
        c.gridwidth = 2;
        c.fill = GridBagConstraints.NONE;      //reset to default
        c.weightx = 0.0;                       //reset to default
        c.gridy = 9;
        textControlsPane.add(buttonStart, c);
        c.fill = GridBagConstraints.NONE;      //reset to default
        c.weightx = 1.0;                       //reset to default
        c.gridy = 9;
        textControlsPane.add(buttonStop, c);
        
        textArea = new JTextArea();
        DefaultCaret caret = (DefaultCaret) textArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        textArea.setFont(new Font("Serif", Font.PLAIN, 16));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane areaScrollPane = new JScrollPane(textArea);
        areaScrollPane.setVerticalScrollBarPolicy(
                        JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        areaScrollPane.setPreferredSize(new Dimension(250, 250));
        areaScrollPane.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createCompoundBorder(
                                BorderFactory.createTitledBorder("Connected Hosts"),
                                BorderFactory.createEmptyBorder(5,5,5,5)),
                areaScrollPane.getBorder()));
        
        saveControlsPane = new JPanel();
        GridBagLayout gridbag2 = new GridBagLayout();
        GridBagConstraints d = new GridBagConstraints();
        saveControlsPane.setLayout(gridbag2);
        
        JPanel buttonControlsPane = new JPanel();
        buttonControlsPane.add(buttonStart);
        buttonControlsPane.add(buttonStop);
        
        d.gridy = 1;
        saveControlsPane.add(actionLabel, d);
        d.gridy = 2;
        saveControlsPane.add(buttonControlsPane, d);
        //saveControlsPane.add(buttonStart, d);
        //saveControlsPane.add(buttonStop, d);
        d.gridy = 3;
        saveControlsPane.add(saveOutput, d);
        d.gridy = 4;
        saveControlsPane.add(saveARP, d);
        d.gridy = 5;
        saveControlsPane.add(clearText, d);
        
        d.anchor = GridBagConstraints.PAGE_END; //bottom of space
        saveControlsPane.setBorder(
                BorderFactory.createCompoundBorder(
                                BorderFactory.createTitledBorder("Control Buttons"),
                                BorderFactory.createEmptyBorder(5,5,5,5)));
        
        JPanel rightPane = new JPanel(new GridLayout(3,1));
        rightPane.add(textControlsPane);
        rightPane.add(areaScrollPane);
        //rightPane.add(splitPane);
        rightPane.add(saveControlsPane);
        /*rightPane.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder("Styled Text"),
                        BorderFactory.createEmptyBorder(5,5,5,5)));*/
        
 
        //Put everything together.
        /*JPanel leftPane = new JPanel(new BorderLayout());
        leftPane.add(textControlsPane, 
                     BorderLayout.PAGE_START);
        leftPane.add(areaScrollPane,
                     BorderLayout.PAGE_END);
 
        add(leftPane, BorderLayout.CENTER);*/
        add(rightPane, BorderLayout.CENTER);
        //add(menuBar, BorderLayout.NORTH);
    }
 
    private static JMenuBar createMenu() {
    	JMenuBar menuBar;
        JMenu fileMenu, helpMenu;
        JMenuItem fileExit, helpHowTo, helpLicense, helpAbout;
        
        menuBar = new JMenuBar();
        fileMenu = new JMenu("File");
        helpMenu = new JMenu("Help");
        fileExit = new JMenuItem("Exit");
        helpHowTo = new JMenuItem("How To");
        helpLicense = new JMenuItem("License");
        helpAbout = new JMenuItem("About");
        
        fileExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.ALT_MASK));
        fileExit.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e) {
        		System.exit(1);
        	}
        });
        
        helpHowTo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, ActionEvent.ALT_MASK));
        helpHowTo.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e) {
        		JOptionPane.showMessageDialog(frame, helpText, "How to Use", JOptionPane.PLAIN_MESSAGE);
        	}
        });
        
        helpLicense.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.ALT_MASK));
        helpLicense.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e) {
        		JOptionPane.showMessageDialog(frame, licenseText, "License", JOptionPane.PLAIN_MESSAGE);
        	}
        });
        
        helpAbout.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.ALT_MASK));
        helpAbout.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e) {
        		JOptionPane.showMessageDialog(frame, aboutText, "About Mark's Device Collector", JOptionPane.PLAIN_MESSAGE);
        	}
        });

        fileMenu.add(fileExit);
        helpMenu.add(helpHowTo);
        helpMenu.add(helpLicense);
        helpMenu.add(helpAbout);
        menuBar.setVisible(true);
        
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        
        return menuBar;

    }
    
    private void addLabelTextRows(JLabel[] labels,
                                  JComponent[] components,
                                  JComponent[] boxes,
                                  GridBagLayout gridbag,
                                  Container container) {
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.WEST;
        int numLabels = labels.length;
 
        for (int i = 0; i < numLabels; i++) {
            //c.gridwidth = GridBagConstraints.RELATIVE; //next-to-last
            c.fill = GridBagConstraints.NONE;      //reset to default
            c.weightx = 0.0;                       //reset to default
            c.gridy = i;
            c.gridwidth = 2;
            container.add(labels[i], c);
 
            //c.gridwidth = GridBagConstraints.REMAINDER;     //end row
            c.fill = GridBagConstraints.HORIZONTAL;
            c.weightx = 1.0;
            c.gridy = i;
            c.gridwidth = 2;
            container.add(components[i], c);
            
        }
    }
 
    class SaveButtonListener implements ActionListener {
    	public void actionPerformed(ActionEvent e){
    		if (ARPTable != null && ARPTable.size() != 0) {
	        	try {
	        		String path = System.getProperty("file.separator") + "arp_table.txt";
	        		save(ARPTable, ARP_TABLE_PATH + path);
	        		arpTablePath.setText(ARP_TABLE_PATH + path);
	        		oldArpTable.setSelected(true);
	        		chooseArpTable.setEnabled(true);
	        	} catch (Exception f){
	        		f.printStackTrace();
	        	}
        	} else {
        		JOptionPane.showMessageDialog(frame,"You haven't retrieved an ARP table yet.", "Message from Mark", JOptionPane.WARNING_MESSAGE);
        	}
    	}
    }
    
    class SaveOutputListener implements ActionListener {
    	public void actionPerformed(ActionEvent e){
        	String path = System.getProperty("java.io.tmpdir");
    		FileDialog fDialog = new FileDialog(frame, "Save", FileDialog.SAVE);
    		fDialog.setFile("devices.xlsx");
    		fDialog.setVisible(true);
            path = fDialog.getDirectory() + fDialog.getFile();

            // Create array of rows from the textArea output
	        String[] multiRow = textArea.getText().split(System.getProperty("line.separator"));
	        
	        // Create workbook to be written to disk
			XSSFWorkbook myWorkbook = new XSSFWorkbook ();
	        XSSFSheet mySheet = myWorkbook.createSheet();

	        // Add the headers to the first row
	        Row titleRow = mySheet.createRow(0);
	        Cell titleCell = titleRow.createCell(0);
	        titleCell.setCellValue("MAC Address");
	        titleCell = titleRow.createCell(1);
	        titleCell.setCellValue("Interface");
	        titleCell = titleRow.createCell(2);
	        titleCell.setCellValue("IP Address");
	        titleCell = titleRow.createCell(3);
	        titleCell.setCellValue("DNS/NetBIOS Name");
	        
	        // Add auto filter to each column
	        mySheet.setAutoFilter(CellRangeAddress.valueOf("A1:D1"));
	        
	        // Go through each row, break up each field by comma, and insert into the sheet
	        int rownum = 1;
	        for (int j = 0; j < multiRow.length; j++) {
	        	Row row = mySheet.createRow(rownum++);
	        	int cellnum = 0;
	        	String[] thisRow = multiRow[j].split(",");
	        	for (int k = 0; k < thisRow.length; k++) {
	        		Cell cell = row.createCell(cellnum++);
	        		cell.setCellValue(thisRow[k]);
	        	}
	        }
	        
	        // Resize each column
	        int m = 0;
				while (m < 5) {
					mySheet.autoSizeColumn(m);
					m++;
			}
		
	     
	        // open an OutputStream to save written data into XLSX file
	        File myFile = new File(path);
	        FileOutputStream os = null;
			try {
				os = new FileOutputStream(myFile);
			} catch (FileNotFoundException f) {
				f.printStackTrace();
			}
	        try {
				myWorkbook.write(os);
			} catch (IOException f) {
				f.printStackTrace();
			}
	        try {
				myWorkbook.close();
			} catch (IOException f) {
				f.printStackTrace();
			}
    	}
    }
    
    class GetNewArpTableListener implements ActionListener {
    	public void actionPerformed(ActionEvent e) {
    		chooseArpTable.setEnabled(false);
        	USE_SAVED_ARP_TABLE = false;
    	}
    }
    
    class UseSavedArpTableListener implements ActionListener {
    	public void actionPerformed(ActionEvent e) {
    		chooseArpTable.setEnabled(true);
        	USE_SAVED_ARP_TABLE = true;
    	}
    }
    
    class ChooseArpTableListener implements ActionListener {
    	public void actionPerformed(ActionEvent e) {
    		JFileChooser fc = new JFileChooser();
        	fc.setCurrentDirectory(new File(ARP_TABLE_PATH));
        	int returnVal = fc.showOpenDialog(null);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                arpTablePath.setText(file.toString());
            }
    	}
    }
    
    class ClearTextFieldListener implements ActionListener {
    	public void actionPerformed (ActionEvent e) {
    		textArea.setText("");
        	actionLabel.setText("Output Text Cleared");
    	}
    }
    
    class StopDiscoveryListener implements ActionListener {
    	public void actionPerformed(ActionEvent e) {
    		if (dw != null) {
        		try {
        			dw.cancel(true);
        			actionLabel.setText("Collection Stopped");
        		} catch (Exception f) {
        			System.out.println(f.getMessage());
        			System.out.println(f.getCause());
        		}
        	}
    	}
    }
    
    class StartDiscoveryListener implements ActionListener {
    	
    	public void actionPerformed(ActionEvent e) {
    	
	    	String myAccessAddr = textFieldAccessAddr.getText();
	    	String myAccessSNMP = textFieldAccessSNMP.getText();
	    	String myGatewayAddr = textFieldGatewayAddr.getText();
	    	String myGatewaySNMP = textFieldGatewaySNMP.getText();
	    	String myNS = textFieldNameserver.getText();
	    	String uplinkPortName = textFieldUplink.getText();
	    	boolean dnsEnabled = enableDNS.isSelected();
	    	boolean nbtEnabled = enableNBT.isSelected();
    	
    		Config.setSetting("accessSwitchAddr", textFieldAccessAddr.getText());
        	Config.setSetting("accessSwitchSNMP", textFieldAccessSNMP.getText());
        	Config.setSetting("gatewaySwitchAddr", textFieldGatewayAddr.getText());
        	Config.setSetting("gatewaySwitchSNMP", textFieldGatewaySNMP.getText());
        	Config.setSetting("uplink", textFieldUplink.getText());
        	Config.setSetting("nameserver", textFieldNameserver.getText());
        	
        	dw = new DiscoverWorker(myAccessAddr, myAccessSNMP, myGatewayAddr, myGatewaySNMP, myNS, uplinkPortName, dnsEnabled, nbtEnabled);
        	dw.execute();
    	}
    }
    
    /*I used this method to save the MACTable and ARPTable HashMaps so I could use them later for testing
      This makes it faster to see GUI changes and not have to wait for an actual SNMP poll across the network
      This is used along with the open() method below to retrieve the results from a saved file.
    */  
    @SuppressWarnings("rawtypes")
	public void save(HashMap savedMap, String path) throws NotSerializableException{
    	try {
    		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path));
    		oos.writeObject(savedMap);
    		oos.flush();
    		oos.close();
    	} catch (Exception e){
    		e.printStackTrace();
    	}
    }
    
    @SuppressWarnings("rawtypes")
	public HashMap open(String path){
    	HashMap table = new HashMap();
    	
    	try {
    		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path));
    		table = (HashMap)ois.readObject();
    		ois.close();
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	
    	return table;
    }
    
    /*This is also used for testing to see what's in the HashMaps I've created.
    */
    @SuppressWarnings("rawtypes")
	public void printHashMap(HashMap myMap){
    	Iterator iter = myMap.entrySet().iterator();
    	
    	while (iter.hasNext()){
    		Map.Entry pairs = (Map.Entry)iter.next();
    		String myKey = (String) pairs.getKey();
    		Object myValue = pairs.getValue();
    		System.out.println(myKey + " : " + myValue);
    	}
    	
    }
   
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event dispatch thread.
     * @throws Exception 
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        frame = new JFrame("Mark's Device Collector");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        //Add the File/Help menu bar
        frame.setJMenuBar(createMenu());
        //Add content to the window.
        frame.add(new DeviceCollector());

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
 
    public static void main(String[] args) {
        //Schedule a job for the event dispatching thread:
        //creating and showing this application's GUI.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                 //Turn off metal's use of bold fonts
        UIManager.put("swing.boldMetal", Boolean.FALSE);
			createAndShowGUI();
            }
        });
    }
}
