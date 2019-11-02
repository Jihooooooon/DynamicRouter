package Router;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class UDPLayer extends BaseLayer {
	
	final static int UDP_HEADER_SIZE = 8;
	private byte[] sourceIPAddress = new byte[4];
	
	public UDPLayer(String layerName) {
		super(layerName);
	};
	
	void setSourceIpAddress(byte[] sourceAddress) {
		System.arraycopy(sourceAddress, 0, this.sourceIPAddress, 0, 4);
	}
	
	public void stringTo4byte(byte[] byteAddress, String stringAddress) {
		for(int i = 0; i < 4; i++){
			byteAddress[i] = ((byte) Integer.parseInt(stringAddress.substring(i*3, (i+1)*3)));
        }
	}
	
	private byte[] makeUDPPseudoHeader(byte[] udpDatagram, byte[] source_IP) {
		byte[] pseudoHeader = new byte[12];
		System.arraycopy(source_IP, 0, pseudoHeader, 0, 4);	// Sender IP copy
		for (int i = 4; i < 8; i++) {
			pseudoHeader[i] = -1;		// Destination IP is broadcast.
		}
		pseudoHeader[9] = 17;					// Protocol ID
		System.arraycopy(ByteBuffer.allocate(2).putShort((short)udpDatagram.length).array(), 0,
				pseudoHeader, 10, 2);			// udpDatagram length
		return pseudoHeader;
	}
	
	private byte[] getUDPCheckSum(byte[] udpDatagram) {
		byte[] tempDatagram = Arrays.copyOf(udpDatagram, udpDatagram.length);
		if (udpDatagram.length % 2 == 1) {
			 udpDatagram = Arrays.copyOf(udpDatagram, udpDatagram.length + 1);
		}
		short result = 0;
		for (int i = 0; i < tempDatagram.length; i += 2) {
			result += ByteBuffer.wrap(Arrays.copyOfRange(tempDatagram, i, i + 2)).getShort();
		}
		return ByteBuffer.allocate(2).putShort((short)~result).array();
	}
	
	// 완료
	public boolean sendRIP(byte[] ripMessage) {
		byte[] udpDatagram = new byte[UDP_HEADER_SIZE + ripMessage.length];
		byte[] portNumber = {0x02, 0x08};	// 520번 포트
		System.arraycopy(portNumber, 0, udpDatagram, 0, 2);
		System.arraycopy(portNumber, 0, udpDatagram, 2, 2);
		System.arraycopy(ByteBuffer.allocate(2).putShort((short) (8 + ripMessage.length)).array(), 0, udpDatagram, 4, 2);
		System.arraycopy(ripMessage, 0, udpDatagram, 8, ripMessage.length);
		
		byte[] pseudoAdded = new byte[udpDatagram.length + 12];	// pseudoHeader + udpDatagram.
		System.arraycopy(this.makeUDPPseudoHeader(udpDatagram, this.sourceIPAddress), 0, pseudoAdded, 0, 12);	// pseudoHeader copy
		System.arraycopy(udpDatagram, 0, pseudoAdded, 12, udpDatagram.length);	// udpDatagram copy
		
		System.arraycopy(this.getUDPCheckSum(pseudoAdded), 0, udpDatagram, 6, 2);	// UDP Checksum input.
		return ((IPLayer)this.underLayer).sendRIP(udpDatagram);
	}
	
	// 완료
	public boolean receiveUDPDatagram(byte[] udpDatagram, byte[] frame_srcIP) {
		byte[] pseudoAdded = new byte[udpDatagram.length + 12];
		System.arraycopy(this.makeUDPPseudoHeader(udpDatagram, frame_srcIP), 0, pseudoAdded, 0, 12);
		System.arraycopy(udpDatagram, 0, pseudoAdded, 12, udpDatagram.length);
		if (!Arrays.equals(this.getUDPCheckSum(pseudoAdded), new byte[2])) {	// Checksum 비교
			System.out.println("UDP checksum error " + ByteBuffer.wrap(this.getUDPCheckSum(pseudoAdded)).getShort());
			return false;
		}
		byte[] portNumber = {0x02, 0x08};	// 520번 포트
		if (Arrays.equals(Arrays.copyOfRange(udpDatagram, 0, 2), portNumber) &&			// 포트번호 520, 520일 때
				Arrays.equals(Arrays.copyOfRange(udpDatagram, 2, 4), portNumber)) {
			byte[] ripMessage = Arrays.copyOfRange(udpDatagram, UDP_HEADER_SIZE, udpDatagram.length);	// 헤더 분리
			return ((RIPLayer)this.upperLayer).receiveRIP(ripMessage);	// RIP 메세지를 상위 레이어로
		}
		return false;
	}
	
	
}
