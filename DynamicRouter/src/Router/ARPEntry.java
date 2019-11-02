package Router;

public class ARPEntry {
	byte[] ipAddress = new byte[4];
	byte[] ethernetAddress = new byte[6];
	byte check;
	
	public ARPEntry(byte[] ipAddress, byte[] ethernetAddress, byte check) {
		System.arraycopy(ipAddress, 0, this.ipAddress, 0, 4);
		System.arraycopy(ethernetAddress, 0, this.ethernetAddress, 0, 6);
		this.check = check;
	}
	
	public byte[] toByteArray() {
		byte[] byteArray = new byte[11];
		System.arraycopy(this.ipAddress, 0, byteArray, 0, 4);
		System.arraycopy(this.ethernetAddress, 0, byteArray, 4, 6);
		byteArray[10] = this.check;
		return byteArray;
	}
}
