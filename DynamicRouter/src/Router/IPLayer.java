package Router;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class IPLayer extends BaseLayer {
	final static int IP_HEAD_SIZE = 20;

	byte[] ip_sourceIP = new byte[4];
	byte[] ip_destinationIP = new byte[4];
	byte[] ip_dataBuffer;		// 데이터 크기가 1500 이상인 경우 사용
	private byte[] broadcast = {-1, -1, -1, -1};
	
	private int interfaceNumber;
	IPLayer otherIPLayer;
	RoutingTable routingTable;

	public IPLayer(String layerName) {
		super(layerName);
	}
	
	void setOtherIPLayer(IPLayer ipLayer) {
		otherIPLayer = ipLayer;
	}

	void setInterfaceNumber(int number) {
		interfaceNumber = number;
	}

	void initRoutingTable(RoutingTable routingTable) {
		this.routingTable = routingTable;
	}

	void setSourceIpAddress(byte[] sourceIPAddress) {
		System.arraycopy(sourceIPAddress, 0, this.ip_sourceIP, 0, 4);
	}
	
	void setDestinIpAddress(byte[] destinIPAddress) {
		System.arraycopy(destinIPAddress, 0, this.ip_destinationIP, 0, 4);
	}

	private byte[] shortToByteArray(short shortNum) {
		return ByteBuffer.allocate(2).putShort(shortNum).array();
	}
	
	public static int byteToInt(byte[] src) {	 
        int s1 = src[0] & 0xFF;
        int s2 = src[1] & 0xFF;
        return ((s1 << 8) + (s2 << 0));
    }
	
	private byte[] getIPCheckSum(byte[] ipDatagram) {
		byte[] tempDatagram = Arrays.copyOf(ipDatagram, ipDatagram.length);
		if (ipDatagram.length % 2 == 1) {
			 tempDatagram = Arrays.copyOf(ipDatagram, ipDatagram.length + 1);
		}
		short result = 0;
		for (int i = 0; i < tempDatagram.length; i += 2) {
			result += ByteBuffer.wrap(Arrays.copyOfRange(tempDatagram, i, i + 2)).getShort();
		}
		return ByteBuffer.allocate(2).putShort((short)~result).array();
	}
	
	boolean sendRIP(byte[] udpDatagram) {
		byte[] ipDatagram = new byte[IP_HEAD_SIZE + udpDatagram.length];
		ipDatagram[0] = 0x45;
		System.arraycopy(this.shortToByteArray((short) (IP_HEAD_SIZE + udpDatagram.length)), 0, ipDatagram, 2, 2);	// totlen
		ipDatagram[9] = 17;		// protocol, from UDP
		System.arraycopy(this.ip_sourceIP, 0, ipDatagram, 12, 4);
		System.arraycopy(this.broadcast, 0, ipDatagram, 16, 4);
		System.arraycopy(udpDatagram, 0, ipDatagram, 20, udpDatagram.length);
		
		System.arraycopy(this.getIPCheckSum(ipDatagram), 0, ipDatagram, 10, 2);	// Checksum create and copy
		return ((ARPLayer)this.underLayer).send(ipDatagram, this.ip_destinationIP);
	}
	
	//미완료
	boolean receiveIPDatagram(byte[] ipDatagram) {
		if (!Arrays.equals(this.getIPCheckSum(ipDatagram), new byte[2])) {
			System.out.print("체크섬에러");
			return false;
		}
		byte protocol = ipDatagram[9];
		byte[] data_srcIP = Arrays.copyOfRange(ipDatagram, 12, 16);
		byte[] data_destIP = Arrays.copyOfRange(ipDatagram, 16, 20);
		if (Arrays.equals(this.ip_sourceIP, data_srcIP)) { // 내가 보낸걸 다시 받은 경우
			return false;
	    }
	    if (!(Arrays.equals(this.broadcast, data_destIP)	// (도착지가 브로드캐스트이거나
	    		  || Arrays.equals(this.ip_sourceIP, data_destIP)))	{//내가 받아야할 대상이 아닌경우 
	         return false;
	    }
		if (protocol == (byte)1) {			// ICMP (ping)
			byte[] frame_destinationIP = Arrays.copyOfRange(ipDatagram, 16, 20);

			for (int i = 0; i < routingTable.size(); i++) {
				byte[] destination = routingTable.get(i).getDestination();
				byte[] netMask = routingTable.get(i).getNetMask();
				
				boolean destFound = true;
				for (int j = 0; j < 4; j++) {
					if (destination[j] != (netMask[j] & frame_destinationIP[j])) {
						destFound = false;
					}
				}
				if (destFound) {
					int destInterfaceNum = routingTable.get(i).getInterface();
					if (destInterfaceNum != this.interfaceNumber) {
						return ((EthernetLayer)this.otherIPLayer.getUnderLayer()).sendIPDatagram(ipDatagram, data_destIP, 0);
					}
				}
			}
			return true;
		}
		else if (protocol == (byte)17) {		// UDP
			byte[] udpDatagram = Arrays.copyOfRange(ipDatagram, IP_HEAD_SIZE, ipDatagram.length);
			return ((UDPLayer)this.upperLayer).receiveUDPDatagram(udpDatagram, data_srcIP);
		}
		else {
			return false;
		}
	}
	
	boolean receiveARPMessage(byte[] datagram) {
		if (Arrays.equals(this.ip_sourceIP, Arrays.copyOfRange(datagram, 24, 28))) {
			return ((ARPLayer) this.getUnderLayer()).ARP_reply_send(datagram);
		}
		
		for (int i = 0; i < routingTable.size(); i++) {
			byte[] destination = routingTable.get(i).getDestination();
			byte[] netMask = routingTable.get(i).getNetMask();
			boolean destFound = true;
			for (int j = 0; j < 4; j++) {
				if (destination[j] != (netMask[j] & datagram[j + 24])) {
					destFound = false;
					break;
				}
			}
			if (destFound) {
				if (interfaceNumber != routingTable.get(i).getInterface()) {
					((ARPLayer) this.getUnderLayer()).ARP_reply_send(datagram);
					((ARPLayer) otherIPLayer.getUnderLayer()).ARP_request_send(routingTable.get(i).getGateway());
				} else {
					((ARPLayer) this.getUnderLayer()).ARP_reply_send(datagram);
				}
				return true;
			}
		}
		return false;
	}
}