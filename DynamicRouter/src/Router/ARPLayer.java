package Router;

import java.util.ArrayList;
import java.util.Arrays;

public class ARPLayer extends BaseLayer {
	final static int ARP_MAX_SIZE = 28;
	final static int ARP_IP_SIZE = 4;
	final static int ARP_ETH_SIZE = 6;
	final static int ARP_STATE_SIZE = 1;
	final static int ARP_TABLE_SIZE = ARP_IP_SIZE + ARP_ETH_SIZE + ARP_STATE_SIZE;
	final static int ARP_DEVICE_NAME = 10;
	
	byte[] sourceIPAddress = new byte[4];
	byte[] sourceMACAddress = new byte[6];
	byte[] destIPAddress = new byte[4];
	
	byte[] broadcast = {-1, -1, -1, -1, -1, -1};
	
	ArrayList<ARPEntry> ARPCacheTable;
	
	public ARPLayer(String layerName) {
		super(layerName);
	}
	
	void initARPCacheTable(ArrayList<ARPEntry> arpCacheTable) {
		this.ARPCacheTable = arpCacheTable;
	}

	void setARPCacheTable(byte[] IP_Address, byte[] Ether_Address, byte state) {
		int index = findARPCacheTable(IP_Address);
		if (Arrays.equals(IP_Address, this.sourceIPAddress))
			return;
		if (index == -1) {
			ARPCacheTable.add(new ARPEntry(IP_Address, Ether_Address, state));
		} else {
			ARPCacheTable.set(index, new ARPEntry(ARPCacheTable.get(index).ipAddress, Ether_Address, state));
		}	
	}

	ARPEntry getARPCacheTable(byte[] IP_Address) {
		int index = findARPCacheTable(IP_Address);
		return (index != -1) ? ARPCacheTable.get(index) : null;
	}

	private int findARPCacheTable(byte[] IP_Address) {
		for (int i = 0; i < ARPCacheTable.size(); i++) {
			if (Arrays.equals(IP_Address, ARPCacheTable.get(i).ipAddress)) {
				return i;
			}
		}
		return -1;
	}

	boolean send(byte[] send_ip_data, byte[] dest_ip_address) {
		if ((findARPCacheTable(dest_ip_address) == -1)) {
			ARP_request_send(dest_ip_address);
			while (true) {
				try {
					Thread.sleep(10000);
					if (findARPCacheTable(dest_ip_address) != -1 && ARPCacheTable.get(findARPCacheTable(dest_ip_address)).check != 0) {
						byte[] dest_mac_address = Arrays.copyOf(getARPCacheTable(dest_ip_address).ethernetAddress, 6);
						return ((EthernetLayer) this.getUnderLayer()).sendIPDatagram(send_ip_data, dest_mac_address, 0);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		else {
			byte[] dest_mac_address = Arrays.copyOf(getARPCacheTable(dest_ip_address).ethernetAddress, 6);
			return ((EthernetLayer) this.getUnderLayer()).sendIPDatagram(send_ip_data, dest_mac_address, 0);
		}
	}

	boolean ARP_request_send(byte[] dest_ip_address) {
		byte[] ARPData = new byte[ARP_MAX_SIZE];
		ARPData[0] = 0;				ARPData[1] = 1;
		ARPData[2] = (byte)8;		ARPData[3] = 0;
		ARPData[4] = 6;				ARPData[5] = 4;
		ARPData[6] = 0;				ARPData[7] = 1;

		System.arraycopy(this.sourceMACAddress, 0, ARPData, 8, 6);
		System.arraycopy(this.sourceIPAddress, 0, ARPData, 14, 4);
		System.arraycopy(dest_ip_address, 0, ARPData, 24, 4);

		if (findARPCacheTable(dest_ip_address) == -1) {
			setARPCacheTable(dest_ip_address, broadcast, (byte) 0);
		}
		return ((EthernetLayer) this.getUnderLayer()).sendIPDatagram(ARPData, broadcast, 1);
	}

	boolean ARP_reply_send(byte[] data) {
		byte[] receive_sender_Ethernet = new byte[6];
		byte[] receive_sender_IP = new byte[4];
		byte[] receive_target_Ethernet = new byte[6];
		byte[] receive_target_IP = new byte[4];

		System.arraycopy(data, 8, receive_sender_Ethernet, 0, 6);
		System.arraycopy(data, 14, receive_sender_IP, 0, 4);
		System.arraycopy(data, 18, receive_target_Ethernet, 0, 6);
		System.arraycopy(data, 24, receive_target_IP, 0, 4);

		setARPCacheTable(receive_sender_IP, receive_sender_Ethernet, (byte) 1);

		if (Arrays.equals(this.sourceIPAddress, receive_sender_IP)) {		// 내가 보낸 것을 다시 받은 경우
			return false;
		}

		if (data[6] == 0 && data[7] == 1) {
			if (Arrays.equals(receive_sender_Ethernet, receive_target_Ethernet)) {	// 보낸 사람과 받는 사람의 주소?
				if (findARPCacheTable(receive_sender_IP) != -1) {
					setARPCacheTable(receive_sender_IP, receive_sender_Ethernet, (byte) 1);
					return true;
				}
			}
			data[6] = 0;
			data[7] = 2;
			System.arraycopy(this.sourceMACAddress, 0, data, 8, 6);
			System.arraycopy(receive_target_IP, 0, data, 14, 4);
			System.arraycopy(receive_sender_Ethernet, 0, data, 18, 6);
			System.arraycopy(receive_sender_IP, 0, data, 24, 4);
			return ((EthernetLayer) this.getUnderLayer()).sendIPDatagram(data, receive_sender_Ethernet, 1);
		}
		else if (data[6] == 0 && data[7] == 2) {
			System.arraycopy(data, 8, receive_sender_Ethernet, 0, 6);
			System.arraycopy(data, 14, receive_sender_IP, 0, 4);
			if (findARPCacheTable(receive_sender_IP) != -1) {
				setARPCacheTable(receive_sender_IP, receive_sender_Ethernet, (byte) 1);
				return true;
			}
		}
		return true;
	}

	public void setSrcIPAddress(byte[] src_IPAddress) {
		System.arraycopy(src_IPAddress, 0, this.sourceIPAddress, 0, 4);
	}
	
	public void setDstIPAddress(byte[] dst_IPAddress) {
		System.arraycopy(dst_IPAddress, 0, this.destIPAddress, 0, 4);
	}

	public void setSrcEthAddress(byte[] src_EthAddress) {
		System.arraycopy(src_EthAddress, 0, this.sourceMACAddress, 0, 6);
	}
	
	public void ARPTable_reset() {
		ARPCacheTable.clear();
	}

	public void ARPTable_delete() {
		if (this.ARPCacheTable.size() > 0) {
			ARPCacheTable.remove(ARPCacheTable.size() - 1);
		}
	}

	public void ARPTable_IP_delete(byte[] ip) {
		int index = findARPCacheTable(ip);
		if (index != -1) {
			ARPCacheTable.remove(index);
		}
	}
}