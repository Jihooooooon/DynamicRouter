package Router;

import java.util.Arrays;

public class EthernetLayer extends BaseLayer {
	final static int ETHERNET_MAX_SIZE = 1514;
	final static int ETHERNET_HEAD_SIZE = 14;
	final static int ETHERNET_MAX_DATA = ETHERNET_MAX_SIZE - ETHERNET_HEAD_SIZE;

	byte[] sourceAddress;
	byte[] ethernetData;
	byte[] broadcast = {-1, -1, -1, -1, -1, -1};
	byte[] ethernet_dataBuffer;		// 데이터 크기가 1500 이상인 경우 사용
	
	public EthernetLayer(String layerName) {
		super(layerName);
		resetHeader();
	}

	void resetHeader() {
		sourceAddress = new byte[6];
		ethernetData = new byte[ETHERNET_MAX_DATA];
	}

	void setSourceAddress(byte[] sourceAddress) {
		for (int i = 0; i < 6; i++) {
		   this.sourceAddress[i] = sourceAddress[i];
		}
	}
	
	boolean sendIPDatagram(byte[] ipDatagram, byte[] destEthernetAddress, int type) {
		int length = ipDatagram.length;
		byte[][] frameType = {{0x08, 0x00}, {0x08, 0x06}};
		byte[] ethernetPacket = new byte[ETHERNET_HEAD_SIZE + ipDatagram.length];
		System.arraycopy(destEthernetAddress, 0, ethernetPacket, 0, 6);		// RIP인 경우
		System.arraycopy(this.sourceAddress, 0, ethernetPacket, 6, 6);
		System.arraycopy(frameType[type], 0, ethernetPacket, 12, 2);
		for (int i = 0; i < length; i++) {
			ethernetPacket[ETHERNET_HEAD_SIZE + i] = ipDatagram[i];
		}
		return ((PacketDriverLayer) this.getUnderLayer()).send(ethernetPacket, ethernetPacket.length);
	}
	// 완료
	boolean receive(byte[] data) {
      byte[] data_destinationMAC = new byte[6];
      byte[] data_sourceMAC = new byte[6];
      System.arraycopy(data, 0, data_destinationMAC, 0, 6);
      System.arraycopy(data, 6, data_sourceMAC, 0, 6);
      if (java.util.Arrays.equals(this.sourceAddress, data_sourceMAC)) {
         return false;
      }
      if (!(java.util.Arrays.equals(this.broadcast, data_destinationMAC)	// (도착지가 브로드캐스트이거나
    		  || java.util.Arrays.equals(this.sourceAddress, data_destinationMAC)))	{//내가 받아야할 대상이 아닌경우 
    	  return false;
      }
      
      byte[] dataFrame = new byte[data.length - ETHERNET_HEAD_SIZE];
      dataFrame = Arrays.copyOfRange(data, ETHERNET_HEAD_SIZE, data.length);
      if (data[12] == 8 && data[13] == 0) {
    	  return ((IPLayer) this.getUpperLayer()).receiveIPDatagram(dataFrame);
      }
      else if (data[12] == 8 && data[13] == 6) {
    	  return ((IPLayer) this.getUpperLayer()).receiveARPMessage(dataFrame);
      }
      return true;
   }
}