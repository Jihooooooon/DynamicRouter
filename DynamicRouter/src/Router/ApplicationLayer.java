package Router;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.Date;
import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.JButton;
import java.awt.Label;
import javax.swing.JComboBox;
import java.awt.Font;
import javax.swing.JCheckBox;
import javax.swing.SwingConstants;

public class ApplicationLayer extends JFrame {

	private static final int WINDOW_WIDTH_SIZE = 780;
   private static final long serialVersionUID = 1L;
   private JPanel contentPane;
   private JTextField MyIPaddress;
   private JTextField MyIPaddress2;
   JComboBox<String> NIC_ComboBox;
   JComboBox<String> NIC_ComboBox2;
   private JTextField Mac_address;
   private JTextField Mac_address2;

   JButton myAddressSet_btn;
   JButton myAddressSet_btn2;
   JButton StaticRoutingAdd_btn;
   JButton StaticRoutingDelete_btn;
   JButton ARPCasheDel_btn;
   
   static JTable dynamicRoutingJTable;
   static DefaultTableModel routingTableModel;
   static JTable ARPCacheJTable;
   static DefaultTableModel ARPTableModel;
   
   private JTextField Destination;
   private JTextField Netmask;
   private JTextField Gateway;

   private JCheckBox CheckBoxUp;
   private JCheckBox CheckBoxGateway;
   private JCheckBox CheckBoxHost;

   static PacketDriverLayer m_PacketDriverLayer_1;
   static EthernetLayer m_EthernetLayer_1;
   static ARPLayer m_ARPLayer_1;
   static IPLayer m_IPLayer_1;
   static UDPLayer m_UDPLayer_1;
   static RIPLayer m_RIPLayer_1;
   
   static PacketDriverLayer m_PacketDriverLayer_2;
   static EthernetLayer m_EthernetLayer_2;
   static ARPLayer m_ARPLayer_2;
   static IPLayer m_IPLayer_2;
   static UDPLayer m_UDPLayer_2;
   static RIPLayer m_RIPLayer_2;
   
   static ApplicationLayer m_ApplicationLayer;
   static RoutingTable routingTable;
   static ArrayList<ARPEntry> ARPCacheTable;
   
   int[] adapterNumber = new int[2];
   
   private JTextField interface_box;
   private JButton btnAClass;
   private JButton btnBClass;
   private JButton btnCClass;
   
   protected static boolean tableRefreshBool = false;
   
   public static void main(String[] args) {
	   
	  
	   
      m_PacketDriverLayer_1 = new PacketDriverLayer("CPacketDriverLayer_1");
      m_EthernetLayer_1 = new EthernetLayer("CEthernetLayer_1");
      m_ARPLayer_1 = new ARPLayer("ARPLayer_1");
      m_IPLayer_1 = new IPLayer("IPLayer_1");
      m_UDPLayer_1 = new UDPLayer("UDPLayer_1");
      m_RIPLayer_1 = new RIPLayer("RIPLayer_1");
      
      m_PacketDriverLayer_2 = new PacketDriverLayer("CPacketDriverLayer_2");
      m_EthernetLayer_2 = new EthernetLayer("CEthernetLayer_2");
      m_ARPLayer_2 = new ARPLayer("ARPLayer_1");
      m_IPLayer_2 = new IPLayer("IPLayer_2");
      m_UDPLayer_2 = new UDPLayer("UDPLayer_2");
      m_RIPLayer_2 = new RIPLayer("RIPLayer_2");
      
      m_ApplicationLayer = new ApplicationLayer();
	  
      routingTable = new RoutingTable();
      m_RIPLayer_1.initRoutingTable(routingTable);
      m_RIPLayer_2.initRoutingTable(routingTable);
      m_IPLayer_1.initRoutingTable(routingTable);
      m_IPLayer_2.initRoutingTable(routingTable);
      
      ARPCacheTable = new ArrayList<ARPEntry>(255);
      m_ARPLayer_1.initARPCacheTable(ARPCacheTable);
      m_ARPLayer_2.initARPCacheTable(ARPCacheTable);
	  
	  
	  
      m_PacketDriverLayer_1.setUpperLayer(m_EthernetLayer_1);
      m_EthernetLayer_1.setUnderLayer(m_PacketDriverLayer_1);
      m_EthernetLayer_1.setUpperLayer(m_IPLayer_1);
      m_ARPLayer_1.setUnderLayer(m_EthernetLayer_1);
      m_ARPLayer_1.setUpperLayer(m_ApplicationLayer);
      m_IPLayer_1.setUnderLayer(m_ARPLayer_1);
      m_IPLayer_1.setUpperLayer(m_UDPLayer_1);
      m_UDPLayer_1.setUnderLayer(m_IPLayer_1);
      m_UDPLayer_1.setUpperLayer(m_RIPLayer_1);
      m_RIPLayer_1.setUnderLayer(m_UDPLayer_1);
      m_RIPLayer_1.setUpperLayer(m_ApplicationLayer);
      
      
      m_PacketDriverLayer_2.setUpperLayer(m_EthernetLayer_2);
      m_EthernetLayer_2.setUnderLayer(m_PacketDriverLayer_2);
      m_EthernetLayer_2.setUpperLayer(m_IPLayer_2);
      m_ARPLayer_2.setUnderLayer(m_EthernetLayer_2);
      m_ARPLayer_2.setUpperLayer(m_ApplicationLayer);
      m_IPLayer_2.setUnderLayer(m_ARPLayer_2);
      m_IPLayer_2.setUpperLayer(m_UDPLayer_2);
      m_UDPLayer_2.setUnderLayer(m_IPLayer_2);
      m_UDPLayer_2.setUpperLayer(m_RIPLayer_2);
      m_RIPLayer_2.setUnderLayer(m_UDPLayer_2);
      m_RIPLayer_2.setUpperLayer(m_ApplicationLayer);
      
      m_IPLayer_1.setOtherIPLayer(m_IPLayer_2);
      m_IPLayer_2.setOtherIPLayer(m_IPLayer_1);
      m_RIPLayer_1.setOtherRIPLayer(m_RIPLayer_2);
      m_RIPLayer_2.setOtherRIPLayer(m_RIPLayer_1);
      
      m_ARPLayer_1.initARPCacheTable(ARPCacheTable);
      m_ARPLayer_2.initARPCacheTable(ARPCacheTable);
      
      m_IPLayer_1.setInterfaceNumber(0);
      m_IPLayer_2.setInterfaceNumber(1);
      m_RIPLayer_1.setInterfaceNumber(0);
      m_RIPLayer_2.setInterfaceNumber(1);
     
      EventQueue.invokeLater(new Runnable() {
         public void run() {
            try {
               ApplicationLayer frame = new ApplicationLayer();
               frame.setVisible(true);
            } catch (Exception e) {
               e.printStackTrace();
            }
         }
      });
    
      ARP_Thread thread = new ARP_Thread();
      Thread object = new Thread(thread);
      object.start();
      
      Timer timer = new Timer();
     
      TimerTask periodicTask = new TimerTask() {
    	  @Override
    	  public void run() {
    		  long periodic = 0;
    		  while(true) {
    			  if (periodic == 1) {
    				  m_RIPLayer_1.sendRIP((byte)1);
    				  m_RIPLayer_2.sendRIP((byte)1);
    			  }
    			  if (periodic % 30 == 1) {	// 30초간 
    				  m_RIPLayer_1.sendRIP((byte)2);
    				  m_RIPLayer_2.sendRIP((byte)2);
    			  }
    			  for (int i = 0; i < ApplicationLayer.routingTable.size(); i++) {
    				  RoutingEntry entry = routingTable.get(i);
    				  int hopCount = entry.getMetric();
    				  long recentRefreshedTime = entry.getRefreshedTime() / 1000;
    				  long currentTime = System.currentTimeMillis() / 1000;
    				  long elapsedTime = currentTime - recentRefreshedTime;
    				  
    				  if (hopCount < 16 && elapsedTime > 180) {		// 만료 확인
    					  entry.setMetric(16);
    					  entry.setRefreshedTime();
    					  routingTable.set(i, entry);
    				  }
    				  else if (hopCount == 16 && elapsedTime > 120) {	// 폐경로 수집
    					  routingTable.remove(i);
    					  routingTableModel.removeRow(i);
    					  dynamicRoutingJTable.setModel(routingTableModel);
    					  routingTableModel.fireTableDataChanged();
    				  }
    			  }
    			  try {
						Thread.sleep(1000);
						if(tableRefreshBool) {
							periodic++;
						}
						else {
							periodic = 0;
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
    		  }
    	  }
      };
      
      timer.schedule(periodicTask, 0);
   }
   
   static class ARP_Thread implements Runnable {

		public ARP_Thread() {

		}

		@Override
		public void run() {
			byte[] ARP_Table_Data = new byte[11];
			byte[] ARP_IP = new byte[4];
			byte[] ARP_ETHERNET = new byte[6];
			byte ARP_STATE;
			while (true) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				for (int i = 0, j = 0; i < ARPCacheTable.size(); i++) {
					ARP_Table_Data = ARPCacheTable.get(i).toByteArray();
					System.arraycopy(ARP_Table_Data, 0, ARP_IP, 0, 4);
					System.arraycopy(ARP_Table_Data, 4, ARP_ETHERNET, 0, 6);
					ARP_STATE = ARP_Table_Data[10];
					
					String Mac_byte = byteArrayToHex(ARP_ETHERNET);
					if (Mac_byte.equals("ff:ff:ff:ff:ff:ff")) {
						Mac_byte = "??:??:??:??:??:??";
					}
					String ARP_Check = (ARP_STATE == 1) ? "complete" : "imcomplete";
						
					if (!byte2IP(ARP_IP).equals("0.0.0.0")) {
						ARPTableModel.setValueAt(byte2IP(ARP_IP), j, 0);
						ARPTableModel.setValueAt(Mac_byte, j, 1);
						ARPTableModel.setValueAt(ARP_Check, j, 2);
						j++;
						ARPCacheJTable.setModel(ARPTableModel);
						ARPTableModel.fireTableDataChanged();
					}
				}

			}
		}	
	}
   
   public static int byte2Int(byte[] src) {
      int s1 = src[0] & 0xFF;
      int s2 = src[1] & 0xFF;
      int s3 = src[2] & 0xFF;
      int s4 = src[3] & 0xFF;

      return ((s1 << 24) + (s2 << 16) + (s3 << 8) + (s4 << 0));
   }

   public static String byte2IP(byte[] src) {
      int s1 = src[0] & 0xFF;
      int s2 = src[1] & 0xFF;
      int s3 = src[2] & 0xFF;
      int s4 = src[3] & 0xFF;

      return s1 + "." + s2 + "." + s3 + "." + s4;
   }

   static String byteArrayToHex(byte[] a) {
      StringBuilder sb = new StringBuilder();
      for (final byte b : a)
         sb.append(String.format("%02x:", b & 0xff));
      sb.deleteCharAt(sb.length() - 1);
      return sb.toString();
   }
   
   synchronized public static void refreshTable() {
	   for (int i = 0; i < routingTable.size(); i++) {
		   RoutingEntry entry = routingTable.get(i);
		   routingTableModel.setValueAt(byte2IP(entry.getDestination()), i, 0);
		   routingTableModel.setValueAt(byte2IP(entry.getNetMask()), i, 1);
		   routingTableModel.setValueAt(byte2IP(entry.getGateway()), i, 2);
		   routingTableModel.setValueAt(entry.getFlag().toString(), i, 3);
		   routingTableModel.setValueAt(Integer.toString(entry.getInterface()), i, 4);
		   routingTableModel.setValueAt(Integer.toString(entry.getMetric()), i, 5);
		   routingTableModel.setValueAt(new Date(entry.getRefreshedTime()).toString(), i, 6);
	   }
	   dynamicRoutingJTable.setModel(routingTableModel);
	   routingTableModel.fireTableDataChanged();
   }

   @SuppressWarnings("serial")
   public class JEditorPaneLimit extends PlainDocument {
      private int limit;

      public JEditorPaneLimit(int limit) {
         super();
         this.limit = limit;
      }

      public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
         if (str == null)
            return;
         if (getLength() + str.length() <= limit)
            super.insertString(offset, str, attr);
      }
   }
   
   class setAddressListener implements ActionListener {
	   @Override
	   public void actionPerformed(ActionEvent e) {

         byte[] tempSourceEthernetAddress = new byte[6];
         byte[] tempSourceIPAddress = new byte[4];
         byte[] tempDestinIPAddress = new byte[4];

         if (e.getSource() == myAddressSet_btn) {
        	if(Mac_address.getText().length() == 0){
         		JOptionPane.showMessageDialog(null, "Interface 0의 Mac 주소를 12자리 입력해주세요.", "WARNING_MESSAGE",
 						JOptionPane.WARNING_MESSAGE);
         	} else if (MyIPaddress.getText().length() < 12){
         		JOptionPane.showMessageDialog(null, "Interface 0의 IP 주소를 12자리 입력해주세요.", "WARNING_MESSAGE",
 						JOptionPane.WARNING_MESSAGE);
         	} else {
         		String NIC_IPAddress = ApplicationLayer.getIPFromPcapAddr(
     				   m_PacketDriverLayer_1.getAdapterList().get(adapterNumber[0]).getAddresses().get(0).getAddr().toString()
     				   );
         		for (int i = 0; i < 4; i++) {
	                tempSourceIPAddress[i] = ((byte) Integer.parseInt(NIC_IPAddress.substring(i*3, (i+1)*3)));
	            }
	            for(int i = 0; i < 4; i++){
	                tempDestinIPAddress[i] = ((byte) Integer.parseInt(MyIPaddress.getText().substring(i*3, (i+1)*3)));
	            }
	            for (int i = 0, j = 0; i < 12; i += 2, j++) {
	                tempSourceEthernetAddress[j] = Integer.valueOf(Mac_address.getText().substring(i, i + 2), 16).byteValue();
	            }
	            m_RIPLayer_1.setDestinIpAddress(tempDestinIPAddress);
	            m_UDPLayer_1.setSourceIpAddress(tempSourceIPAddress);
	            m_IPLayer_1.setSourceIpAddress(tempSourceIPAddress);
	            m_IPLayer_1.setDestinIpAddress(tempDestinIPAddress);
	            m_ARPLayer_1.setSrcIPAddress(tempSourceIPAddress);
 	            m_ARPLayer_1.setSrcEthAddress(tempSourceEthernetAddress);
	            m_EthernetLayer_1.setSourceAddress(tempSourceEthernetAddress);
	            m_PacketDriverLayer_1.setAdapterNumber(adapterNumber[0]);
	            if(myAddressSet_btn.getText() == "Set"){
	            	myAddressSet_btn.setText("Reset");
	            	NIC_ComboBox.setEnabled(false);
	            	MyIPaddress.setEnabled(false);
	            	Mac_address.setEnabled(false);
	            	System.out.println("Interface 0 [" + adapterNumber[0] + "] 준비 완료!");
	            	if(myAddressSet_btn2.getText() == "Reset"){
		            	Destination.setEnabled(true);
		            	Netmask.setEnabled(true);
		            	Gateway.setEnabled(true);
		            	CheckBoxUp.setEnabled(true);
		            	CheckBoxGateway.setEnabled(true);
		            	CheckBoxHost.setEnabled(true);
		            	interface_box.setEnabled(true);
		            	StaticRoutingAdd_btn.setEnabled(true);
		            	StaticRoutingDelete_btn.setEnabled(true);
		            	btnAClass.setEnabled(true);
		            	btnBClass.setEnabled(true);
		            	btnCClass.setEnabled(true);
		            	tableRefreshBool = true;
	            	}
	            } else {
	            	myAddressSet_btn.setText("Set");
	            	NIC_ComboBox.setEnabled(true);
	            	MyIPaddress.setEnabled(true);
	            	Mac_address.setEnabled(true);
	            	Destination.setEnabled(false);
	            	Netmask.setEnabled(false);
	            	Gateway.setEnabled(false);
	            	CheckBoxUp.setEnabled(false);
	            	CheckBoxGateway.setEnabled(false);
	            	CheckBoxHost.setEnabled(false);
	            	interface_box.setEnabled(false);
	            	StaticRoutingAdd_btn.setEnabled(false);
	            	StaticRoutingDelete_btn.setEnabled(false);
	            	btnAClass.setEnabled(false);
	            	btnBClass.setEnabled(false);
	            	btnCClass.setEnabled(false);
	            	tableRefreshBool = false;
	            }
         	}
         } else if (e.getSource() == myAddressSet_btn2) {
        	if(Mac_address2.getText().length() == 0){
        		JOptionPane.showMessageDialog(null, "Interface 1의 Mac 주소를 12자리 입력해주세요.", "WARNING_MESSAGE",
						JOptionPane.WARNING_MESSAGE);
        	} else if (MyIPaddress2.getText().length() < 12){
        		JOptionPane.showMessageDialog(null, "Interface 1의 IP 주소를 12자리 입력해주세요.", "WARNING_MESSAGE",
						JOptionPane.WARNING_MESSAGE);
        	} else {
        		String NIC_IPAddress = ApplicationLayer.getIPFromPcapAddr(
      				   m_PacketDriverLayer_2.getAdapterList().get(adapterNumber[1]).getAddresses().get(0).getAddr().toString()
      				   );
          		for (int i = 0; i < 4; i++) {
 	                tempSourceIPAddress[i] = ((byte) Integer.parseInt(NIC_IPAddress.substring(i*3, (i+1)*3)));
 	            }
 	            for(int i = 0; i < 4; i++){
 	                tempDestinIPAddress[i] = ((byte) Integer.parseInt(MyIPaddress2.getText().substring(i*3, (i+1)*3)));
 	            }
 	            for (int i = 0, j = 0; i < 12; i += 2, j++) {
 	                tempSourceEthernetAddress[j] = Integer.valueOf(Mac_address2.getText().substring(i, i + 2), 16).byteValue();
 	            }
 	            m_RIPLayer_2.setDestinIpAddress(tempDestinIPAddress);
 	            m_UDPLayer_2.setSourceIpAddress(tempSourceIPAddress);
 	            m_IPLayer_2.setSourceIpAddress(tempSourceIPAddress);
 	            m_IPLayer_2.setDestinIpAddress(tempDestinIPAddress);
 	            m_ARPLayer_2.setSrcIPAddress(tempSourceIPAddress);
 	            m_ARPLayer_2.setSrcEthAddress(tempSourceEthernetAddress);
 	            m_EthernetLayer_2.setSourceAddress(tempSourceEthernetAddress);
 	            m_PacketDriverLayer_2.setAdapterNumber(adapterNumber[1]);
	            if(myAddressSet_btn2.getText() == "Set"){
	            	myAddressSet_btn2.setText("Reset");
	            	NIC_ComboBox2.setEnabled(false);
	            	MyIPaddress2.setEnabled(false);
	            	Mac_address2.setEnabled(false);
	            	System.out.println("Interface 1 [" + adapterNumber[1] + "] 준비 완료!");
	            	if(myAddressSet_btn.getText() == "Reset"){
		            	Destination.setEnabled(true);
		            	Netmask.setEnabled(true);
		            	Gateway.setEnabled(true);
		            	CheckBoxUp.setEnabled(true);
		            	CheckBoxGateway.setEnabled(true);
		            	CheckBoxHost.setEnabled(true);
		            	interface_box.setEnabled(true);
		            	StaticRoutingAdd_btn.setEnabled(true);
		            	StaticRoutingDelete_btn.setEnabled(true);
		            	btnAClass.setEnabled(true);
		            	btnBClass.setEnabled(true);
		            	btnCClass.setEnabled(true);
		            	tableRefreshBool = true;
	            	}
	            } else {
	            	myAddressSet_btn2.setText("Set");
	            	NIC_ComboBox2.setEnabled(true);
	            	MyIPaddress2.setEnabled(true);
	            	Mac_address2.setEnabled(true);
	            	Destination.setEnabled(false);
	            	Netmask.setEnabled(false);
	            	Gateway.setEnabled(false);
	            	CheckBoxUp.setEnabled(false);
	            	CheckBoxGateway.setEnabled(false);
	            	CheckBoxHost.setEnabled(false);
	            	interface_box.setEnabled(false);
	            	StaticRoutingAdd_btn.setEnabled(false);
	            	StaticRoutingDelete_btn.setEnabled(false);
	            	btnAClass.setEnabled(false);
	            	btnBClass.setEnabled(false);
	            	btnCClass.setEnabled(false);
	            	tableRefreshBool = false;
	            }
        	}
         } else if (e.getSource() == StaticRoutingAdd_btn) {
        	if(Destination.getText().length() < 12) {
				JOptionPane.showMessageDialog(null, "12자리 Destination IP 주소를 입력해주세요.", "WARNING_MESSAGE",
							JOptionPane.WARNING_MESSAGE);
			} else if(Netmask.getText().length() < 12) {
				JOptionPane.showMessageDialog(null, "12자리 Netmask IP 주소를 입력해주세요.", "WARNING_MESSAGE",
							JOptionPane.WARNING_MESSAGE);
			} else if(Gateway.getText().length() < 12) {
				JOptionPane.showMessageDialog(null, "12자리 Gateway IP 주소를 입력해주세요.", "WARNING_MESSAGE",
							JOptionPane.WARNING_MESSAGE);
			} else if(!CheckBoxUp.isSelected() && !CheckBoxGateway.isSelected() && !CheckBoxHost.isSelected()){
				JOptionPane.showMessageDialog(null, "Flag를 선택해주세요.", "WARNING_MESSAGE",
						JOptionPane.WARNING_MESSAGE);
			} else if(CheckBoxGateway.isSelected() && CheckBoxHost.isSelected()){
				JOptionPane.showMessageDialog(null, "Flag에서 Gateway와 Host를 동시에 선택할 수 없습니다.", "WARNING_MESSAGE",
						JOptionPane.WARNING_MESSAGE);
			} else if(interface_box.getText().length() < 1){
				JOptionPane.showMessageDialog(null, "Interface 이름을 입력해주세요.", "WARNING_MESSAGE",
						JOptionPane.WARNING_MESSAGE);
			} else {
	            Flag flag;
	            if(CheckBoxUp.isSelected() && CheckBoxGateway.isSelected())
	               flag = Flag.UG;
	            else if(CheckBoxUp.isSelected() && CheckBoxHost.isSelected())
	               flag = Flag.UH;
	            else if(CheckBoxUp.isSelected())
	               flag = Flag.U;
	            else if(CheckBoxGateway.isSelected())
	               flag = Flag.G;
	            else if(CheckBoxHost.isSelected())
	               flag = Flag.H;
	            else
	               flag = Flag.NONE;
	            byte[] tempDestination = new byte[4];
	            for(int i = 0; i < 4; i ++){
	            	tempDestination[i] = ((byte) Integer.parseInt(Destination.getText().substring(i*3, (i+1)*3)));
	            }
	             
	            byte[] tempNetmask = new byte[4];
	            for(int i = 0; i < 4; i ++){
	            	tempNetmask[i] = ((byte) Integer.parseInt(Netmask.getText().substring(i*3, (i+1)*3)));
	            }
	            
	            byte[] tempGateway = new byte[4];
	            for(int i = 0; i < 4; i ++){
	            	tempGateway[i] = ((byte) Integer.parseInt(Gateway.getText().substring(i*3, (i+1)*3)));
	            }
	            RoutingEntry tempEntry = new RoutingEntry();
	            tempEntry.setRoutingTable(tempDestination, tempNetmask, tempGateway, flag, 
		                   Integer.parseInt(interface_box.getText()));
	            routingTable.add(tempEntry);
	            ApplicationLayer.refreshTable();
	            
			}
         } else if (e.getSource() == StaticRoutingDelete_btn) {
        	if(routingTable.size() > 0){
        		int index = dynamicRoutingJTable.getSelectedRow();
	        	routingTable.remove(index);
	        	routingTableModel.removeRow(index);
        		dynamicRoutingJTable.setModel(routingTableModel);
        		routingTableModel.fireTableDataChanged();
        	}
         } else if (e.getSource() == btnAClass)	{
        	 Netmask.setText("255000000000");
         } else if (e.getSource() == btnBClass) {
        	 Netmask.setText("255255000000");
         } else if (e.getSource() == btnCClass) {
        	 Netmask.setText("255255255000");
         }
      }
   }

   public String get_MacAddress(byte[] byte_MacAddress) {

      String MacAddress = "";
      try {    	  
         for (int i = 0; i < m_PacketDriverLayer_1.getAdapterList().get(adapterNumber[0])
               .getHardwareAddress().length; i++) {
            MacAddress += String.format("%02X%s",
                  m_PacketDriverLayer_1.getAdapterList().get(adapterNumber[0]).getHardwareAddress()[i],
                  (i < MacAddress.length() - 1) ? "" : "");
         }
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }

      System.out.println("1번 현재 선택된 주소" + MacAddress);

      System.out.println(m_PacketDriverLayer_1.getAdapterList().get(adapterNumber[0]).getAddresses());

      return MacAddress;
   }

   public String get_MacAddress2(byte[] byte_MacAddress) {

      String MacAddress = "";
      try {
         for (int i = 0; i < m_PacketDriverLayer_2.getAdapterList().get(adapterNumber[1])
               .getHardwareAddress().length; i++) {
            MacAddress += String.format("%02X%s",
                  m_PacketDriverLayer_2.getAdapterList().get(adapterNumber[1]).getHardwareAddress()[i],
                  (i < MacAddress.length() - 1) ? "" : "");
         }
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }

      System.out.println("2번 현재 선택된 주소" + MacAddress);

      System.out.println(m_PacketDriverLayer_2.getAdapterList().get(adapterNumber[1]).getAddresses());

      return MacAddress;
   }

   public static String getIPFromPcapAddr(String addr) {
	   Pattern ipFormat = Pattern.compile("((25[0-5]|2[0-4][1-9]|1[0-9]{2}|[0-9]{1,2})\\.){3}(25[0-5]|2[0-4][1-9]|1[0-9]{2}|[0-9]{1,2})");
	   Matcher matcher = ipFormat.matcher(addr);
	   StringBuffer sb = new StringBuffer();
	   matcher.find();
	   String temp = matcher.group();
	   String[] ipArr = temp.split("\\.");
	   for (int i = 0; i < 4; i++) {
		   if (ipArr[i].length() < 3) {
			   ipArr[i] = new String(new char[3 - ipArr[i].length()]).replaceAll("\0", "0") + ipArr[i];
		   }
		   sb.append(ipArr[i]);
	   }
	   return sb.toString();
   }
   
   public ApplicationLayer() {
      setTitle(
            "Dynamic Router");
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setBounds(100, 100, WINDOW_WIDTH_SIZE, 730);
      contentPane = new JPanel();
      contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
      setContentPane(contentPane);
      contentPane.setLayout(null);
      
      JPanel StaticRoutingPanel = new JPanel();
      StaticRoutingPanel.setLayout(null);
      StaticRoutingPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"),
            "Dynamic Routing Table", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
      StaticRoutingPanel.setBounds(10, 10, WINDOW_WIDTH_SIZE - 40, 440);
      contentPane.add(StaticRoutingPanel);

      JPanel StaticRoutingeditorPanel = new JPanel();
      StaticRoutingeditorPanel.setLayout(null);
      StaticRoutingeditorPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
      StaticRoutingeditorPanel.setBounds(10, 15, WINDOW_WIDTH_SIZE - 60, 150);
      StaticRoutingPanel.add(StaticRoutingeditorPanel);

      String dynamicRoutTableColumnNames[] = {"Destination", "Subnet Mask", "Gateway", "Flag", "Interface", "Metric", "Recent Updated"};      
      routingTableModel = new DefaultTableModel(dynamicRoutTableColumnNames, 20);
      dynamicRoutingJTable = new JTable(routingTableModel);
      dynamicRoutingJTable.setSize(WINDOW_WIDTH_SIZE - 60, 150);
      dynamicRoutingJTable.setShowGrid(true);
      dynamicRoutingJTable.setGridColor(Color.GRAY);
      for (int i = 0; i < 3; i++) {
    	  dynamicRoutingJTable.getColumnModel().getColumn(i).setPreferredWidth(60);
      }
      for (int i = 3; i < 6; i++) {
    	  dynamicRoutingJTable.getColumnModel().getColumn(i).setPreferredWidth(10);
      }
      dynamicRoutingJTable.getColumnModel().getColumn(6).setPreferredWidth(150);
      
      JScrollPane temp = new JScrollPane(dynamicRoutingJTable);
      temp.setSize(WINDOW_WIDTH_SIZE - 60, 150);
      StaticRoutingeditorPanel.add(temp);
      
      
      StaticRoutingAdd_btn = new JButton("Add");
      StaticRoutingAdd_btn.setEnabled(false);
      StaticRoutingAdd_btn.addActionListener(new setAddressListener());
      StaticRoutingAdd_btn.setBounds(80, 370, 100, 30);
      StaticRoutingPanel.add(StaticRoutingAdd_btn);

      StaticRoutingDelete_btn = new JButton("Delete");
      StaticRoutingDelete_btn.setEnabled(false);
      StaticRoutingDelete_btn.addActionListener(new setAddressListener());
      StaticRoutingDelete_btn.setBounds(240, 370, 100, 30);
      StaticRoutingPanel.add(StaticRoutingDelete_btn);

      Label label_7 = new Label("Destination");
      label_7.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
      label_7.setAlignment(Label.CENTER);
      label_7.setBounds(25, 200, 70, 20);
      StaticRoutingPanel.add(label_7);

      Label label_8 = new Label("Netmask");
      label_8.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
      label_8.setAlignment(Label.CENTER);
      label_8.setBounds(25, 230, 70, 20);
      StaticRoutingPanel.add(label_8);

      Label label_9 = new Label("Gateway");
      label_9.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
      label_9.setAlignment(Label.CENTER);
      label_9.setBounds(25, 260, 70, 20);
      StaticRoutingPanel.add(label_9);

      Destination = new JTextField();
      Destination.setEnabled(false);
      Destination.setHorizontalAlignment(SwingConstants.CENTER);
      Destination.setColumns(10);
      Destination.setBounds(110, 200, 160, 20);
      Destination.setDocument(new JEditorPaneLimit(12));
      StaticRoutingPanel.add(Destination);

      Netmask = new JTextField();
      Netmask.setEnabled(false);
      Netmask.setHorizontalAlignment(SwingConstants.CENTER);
      Netmask.setColumns(10);
      Netmask.setBounds(110, 230, 160, 20);
      Netmask.setDocument(new JEditorPaneLimit(12));
      StaticRoutingPanel.add(Netmask);

      Gateway = new JTextField();
      Gateway.setEnabled(false);
      Gateway.setHorizontalAlignment(SwingConstants.CENTER);
      Gateway.setColumns(10);
      Gateway.setBounds(110, 260, 160, 20);
      Gateway.setDocument(new JEditorPaneLimit(12));
      StaticRoutingPanel.add(Gateway);

      Label label_10 = new Label("Flag");
      label_10.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
      label_10.setAlignment(Label.CENTER);
      label_10.setBounds(25, 290, 70, 20);
      StaticRoutingPanel.add(label_10);

      CheckBoxUp = new JCheckBox("UP");
      CheckBoxUp.setEnabled(false);
      CheckBoxUp.setHorizontalAlignment(SwingConstants.CENTER);
      CheckBoxUp.setBounds(95, 290, 50, 20);
      StaticRoutingPanel.add(CheckBoxUp);

      CheckBoxGateway = new JCheckBox("GateWay");
      CheckBoxGateway.setEnabled(false);
      CheckBoxGateway.setHorizontalAlignment(SwingConstants.CENTER);
      CheckBoxGateway.setBounds(145, 290, 80, 20);
      StaticRoutingPanel.add(CheckBoxGateway);

      CheckBoxHost = new JCheckBox("Host");
      CheckBoxHost.setEnabled(false);
      CheckBoxHost.setHorizontalAlignment(SwingConstants.CENTER);
      CheckBoxHost.setBounds(225, 290, 60, 20);
      StaticRoutingPanel.add(CheckBoxHost);

      Label label_11 = new Label("Interface");
      label_11.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
      label_11.setAlignment(Label.CENTER);
      label_11.setBounds(25, 320, 70, 20);
      StaticRoutingPanel.add(label_11);
      
      interface_box = new JTextField();
      interface_box.setEnabled(false);
      interface_box.setHorizontalAlignment(SwingConstants.CENTER);
      interface_box.setColumns(10);
      interface_box.setBounds(110, 320, 160, 20);
      interface_box.setDocument(new JEditorPaneLimit(10));
      StaticRoutingPanel.add(interface_box);
      
      btnAClass = new JButton("A Class");
      btnAClass.setEnabled(false);
      btnAClass.setBounds(305, 230, 80, 20);
      btnAClass.addActionListener(new setAddressListener());
      StaticRoutingPanel.add(btnAClass);
      
      btnBClass = new JButton("B Class");
      btnBClass.setEnabled(false);
      btnBClass.setBounds(305, 260, 80, 20);
      btnBClass.addActionListener(new setAddressListener());
      StaticRoutingPanel.add(btnBClass);
      
      btnCClass = new JButton("C Class");
      btnCClass.setEnabled(false);
      btnCClass.setBounds(305, 290, 80, 20);
      btnCClass.addActionListener(new setAddressListener());
      StaticRoutingPanel.add(btnCClass);
      
      Label label_12 = new Label("Macro");
      label_12.setAlignment(Label.CENTER);
      label_12.setBounds(310, 200, 70, 20);
      StaticRoutingPanel.add(label_12);

      JPanel AddressPanel = new JPanel();
      AddressPanel.setLayout(null);
      AddressPanel.setBorder(new TitledBorder(null, "Interface 0", TitledBorder.LEADING, TitledBorder.TOP, null, null));
      AddressPanel.setBounds(10, 450, 350, 110);
      contentPane.add(AddressPanel);

      Label label = new Label("MAC");
      label.setAlignment(Label.CENTER);
      label.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
      label.setBounds(10, 25, 50, 20);
      AddressPanel.add(label);

      Label label_2 = new Label("IP(Dest)");
      label_2.setAlignment(Label.CENTER);
      label_2.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
      label_2.setBounds(10, 65, 50, 20);
      AddressPanel.add(label_2);

      myAddressSet_btn = new JButton("Set");
      myAddressSet_btn.setBounds(220, 60, 100, 30);
      AddressPanel.add(myAddressSet_btn);
      myAddressSet_btn.addActionListener(new setAddressListener());

      MyIPaddress = new JTextField();
      MyIPaddress.setHorizontalAlignment(SwingConstants.CENTER);
      MyIPaddress.setBounds(60, 65, 130, 20);
      MyIPaddress.setDocument(new JEditorPaneLimit(12));
      MyIPaddress.setColumns(10);
      AddressPanel.add(MyIPaddress);

      NIC_ComboBox = new JComboBox<String>();
      NIC_ComboBox.setBounds(60, 25, 130, 20);
      AddressPanel.add(NIC_ComboBox);

      Mac_address = new JTextField();
      Mac_address.setHorizontalAlignment(SwingConstants.CENTER);
      Mac_address.setBounds(205, 25, 130, 20);
      Mac_address.setColumns(10);
      AddressPanel.add(Mac_address);

      JPanel AddressPanel2 = new JPanel();
      AddressPanel2.setLayout(null);
      AddressPanel2.setBorder(new TitledBorder(null, "Interface 1", TitledBorder.LEADING, TitledBorder.TOP, null, null));
      AddressPanel2.setBounds(10, 570, 350, 110);
      contentPane.add(AddressPanel2, BorderLayout.SOUTH);

      Label label_3 = new Label("MAC");
      label_3.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
      label_3.setAlignment(Label.CENTER);
      label_3.setBounds(10, 25, 50, 20);
      AddressPanel2.add(label_3);

      Label label_4 = new Label("IP(Dest)");
      label_4.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
      label_4.setAlignment(Label.CENTER);
      label_4.setBounds(10, 65, 50, 20);
      AddressPanel2.add(label_4);

      myAddressSet_btn2 = new JButton("Set");
      myAddressSet_btn2.setBounds(220, 60, 100, 30);
      AddressPanel2.add(myAddressSet_btn2);
      myAddressSet_btn2.addActionListener(new setAddressListener());

      MyIPaddress2 = new JTextField();
      MyIPaddress2.setHorizontalAlignment(SwingConstants.CENTER);
      MyIPaddress2.setColumns(10);
      MyIPaddress2.setBounds(60, 65, 130, 20);
      MyIPaddress2.setDocument(new JEditorPaneLimit(12));
      AddressPanel2.add(MyIPaddress2);

      NIC_ComboBox2 = new JComboBox<String>();
      NIC_ComboBox2.setBounds(60, 25, 130, 20);
      AddressPanel2.add(NIC_ComboBox2);

      Mac_address2 = new JTextField();
      Mac_address2.setHorizontalAlignment(SwingConstants.CENTER);
      Mac_address2.setColumns(10);
      Mac_address2.setBounds(205, 25, 130, 20);
      AddressPanel2.add(Mac_address2);
      
      for (int i = 0; m_PacketDriverLayer_1.getAdapterList().size() > i; i++) {
         NIC_ComboBox.addItem(m_PacketDriverLayer_1.getAdapterList().get(i).getDescription());
         NIC_ComboBox2.addItem(m_PacketDriverLayer_2.getAdapterList().get(i).getDescription());
      }

      NIC_ComboBox.addActionListener(new ActionListener() {
    	  @Override
         public void actionPerformed(ActionEvent e) {
            adapterNumber[0] = NIC_ComboBox.getSelectedIndex();         
            try {
               Mac_address.setText(get_MacAddress(
                     m_PacketDriverLayer_1.getAdapterList().get(adapterNumber[0]).getHardwareAddress()));
               MyIPaddress.setText(
            		   getIPFromPcapAddr(
            				   m_PacketDriverLayer_1.getAdapterList().get(adapterNumber[0]).getAddresses().get(0).getAddr().toString()
            				   ).substring(0, 9)
            		   );
            } catch (IOException e1) {
               e1.printStackTrace();
            }
         }
      });

      NIC_ComboBox2.addActionListener(new ActionListener() {
    	  @Override
          public void actionPerformed(ActionEvent e) {
            adapterNumber[1] = NIC_ComboBox2.getSelectedIndex();
            try {
               Mac_address2.setText(get_MacAddress2(
                     m_PacketDriverLayer_2.getAdapterList().get(adapterNumber[1]).getHardwareAddress()));
               MyIPaddress2.setText(
            		   getIPFromPcapAddr(
            				   m_PacketDriverLayer_2.getAdapterList().get(adapterNumber[1]).getAddresses().get(0).getAddr().toString()
            				   ).substring(0, 9)
            		   );
            } catch (IOException e1) {
               // TODO Auto-generated catch block
               e1.printStackTrace();
            }
         }
      });

      
      
      JPanel ARPcachePanel = new JPanel();
		ARPcachePanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "ARP Cache Table",
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		ARPcachePanel.setBounds(370, 450, 380, 230);
		contentPane.add(ARPcachePanel);
		ARPcachePanel.setLayout(null);

		JPanel ARPcacheEditorPanel = new JPanel();
		ARPcacheEditorPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		ARPcacheEditorPanel.setBounds(10, 15, 360, 170);
		ARPcachePanel.add(ARPcacheEditorPanel);
		ARPcacheEditorPanel.setLayout(null);

		
		String ARPTableColumnNames[] = {"IP Address", "Ethernet Address", "Check"};      
	    ARPTableModel = new DefaultTableModel(ARPTableColumnNames, 255);
	    ARPCacheJTable = new JTable(ARPTableModel);
	    ARPCacheJTable.setSize(360, 170);
	    ARPCacheJTable.setShowGrid(true);
	    ARPCacheJTable.setGridColor(Color.GRAY);
	    ARPCacheJTable.getColumnModel().getColumn(0).setPreferredWidth(90);
	    ARPCacheJTable.getColumnModel().getColumn(1).setPreferredWidth(140);
	    ARPCacheJTable.getColumnModel().getColumn(2).setPreferredWidth(70);
		
	    JScrollPane ARPJSPane = new JScrollPane(ARPCacheJTable);
	    ARPJSPane.setSize(360, 170);
		ARPcacheEditorPanel.add(ARPJSPane);
		
		ARPCasheDel_btn = new JButton("Item Delete");
		ARPCasheDel_btn.setEnabled(false);
		ARPCasheDel_btn.setBounds(140, 190, 100, 30);
		ARPCasheDel_btn.addActionListener(new setAddressListener());
		ARPcachePanel.add(ARPCasheDel_btn);
      
   }
}