package Router;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class RIPLayer extends BaseLayer {
	
	static class RIPEntry {
		byte[] addressFamilyIdentifier = {0, 2};
		byte[] routeTag = {0, 1};
		byte[] ipAddress = new byte[4];
		byte[] subnetMask = new byte[4];
		byte[] nextHop = new byte[4];
		int metric = 0;
		
		public byte[] toByteArray() {
			byte[] result = new byte[20];
			System.arraycopy(addressFamilyIdentifier, 0, result, 0, 2);
			System.arraycopy(routeTag, 0, result, 2, 2);
			System.arraycopy(ipAddress, 0, result, 4, 4);
			System.arraycopy(subnetMask, 0, result, 8, 4);
			System.arraycopy(nextHop, 0, result, 12, 4);
			System.arraycopy(ByteBuffer.allocate(4).putInt(metric).array(), 0, result, 16, 4);
			return result;
		}
		
		public void setEntry(byte[] ipAddress, byte[] subnetMask, byte[] nextHop, byte[] metric) {
			System.arraycopy(ipAddress, 0, this.ipAddress, 0, 4);
			System.arraycopy(subnetMask, 0, this.subnetMask, 0, 4);
			System.arraycopy(nextHop, 0, this.nextHop, 0, 4);
			this.metric = ByteBuffer.wrap(metric).getInt();
		}
		
		public static RIPEntry makeRIPFromByteArray(byte[] byteArray) {
			RIPEntry result = new RIPEntry();
			System.arraycopy(byteArray, 4, result.ipAddress, 0, 4);
			System.arraycopy(byteArray, 8, result.subnetMask, 0, 4);
			System.arraycopy(byteArray, 12, result.nextHop, 0, 4);
			result.metric = ByteBuffer.wrap(Arrays.copyOfRange(byteArray, 16, 20)).getInt();
			return result;
		}
		
	};
	
	RoutingTable routingTable;
	
	private int interfaceNumber;
	RIPLayer otherRIPLayer;
	
	RIPEntry thisLayerEntry;
	
	private byte[] destinIPAddress = new byte[4];
	private byte[] subnetMask = {-1, -1, -1, 0};
	
	public RIPLayer(String layerName) {
		super(layerName);
	}

	void initRoutingTable(RoutingTable routingTable) {
		this.routingTable = routingTable;
	}
	
	void setInterfaceNumber(int number) {
		interfaceNumber = number;
	}
	
	void setDestinIpAddress(byte[] destinAddress) {
		System.arraycopy(destinAddress, 0, this.destinIPAddress, 0, 4);
		this.setThisAdapterToRoutingTable();
	}
	
	void setOtherRIPLayer(RIPLayer other) {
		otherRIPLayer = other;
	}

	protected void setThisAdapterToRoutingTable() {
		thisLayerEntry = new RIPEntry();
		System.arraycopy(this.makeNetworkAddress(), 0, thisLayerEntry.ipAddress, 0, 4);
		System.arraycopy(this.subnetMask, 0, thisLayerEntry.subnetMask, 0, 4);
	
		RoutingEntry routingEntry = new RoutingEntry();
		routingEntry.setRoutingTable(thisLayerEntry.ipAddress
				, this.subnetMask, this.destinIPAddress, Flag.G, interfaceNumber);
		
		for (int i = 0; i < routingTable.size(); i++) {
			if (Arrays.equals(routingTable.get(i).getDestination(), this.thisLayerEntry.ipAddress)) {
				routingTable.set(i, routingEntry);
				return;
			}
		}
		routingTable.add(routingEntry);
		ApplicationLayer.refreshTable();
	}
	
	private byte[] makeNetworkAddress() {
		byte[] result = new byte[4];
		for (int i = 0; i < 4; i++) {
			result[i] = (byte) (this.destinIPAddress[i] & this.subnetMask[i]);
		}
		return result;
	}
	
	protected RIPEntry routingEntryToRIPEntry(RoutingEntry routingEntry) {
		RIPEntry result = new RIPEntry();
		System.arraycopy(routingEntry.getDestination(), 0, result.ipAddress, 0, 4);
		System.arraycopy(routingEntry.getNetMask(), 0, result.subnetMask, 0, 4);
		System.arraycopy(routingEntry.getGateway(), 0, result.nextHop, 0, 4);
		result.metric = routingEntry.getMetric();
		return result;
	}
	
	synchronized protected boolean sendRIP(byte command) {
		Send_Thread thread = new Send_Thread(this, command);
	      Thread object = new Thread(thread);
	      object.start();
	      try {
	          object.join(1);
	       } catch (InterruptedException e) {
	          e.printStackTrace();
	       }
	      return true;
	}
	
	private void updateRoutingTable(final RIPEntry entry) {
		RoutingEntry tempEntry = new RoutingEntry();
		tempEntry.setRoutingTable(entry.ipAddress, entry.subnetMask, entry.nextHop, Flag.UG, this.interfaceNumber);
		tempEntry.setMetric(entry.metric);
		for (int i = 0; i < routingTable.size(); i++) {
			if (Arrays.equals(routingTable.get(i).getDestination(), entry.ipAddress)) { // 목적지가 같은 경우
				if (Arrays.equals(routingTable.get(i).getGateway(), entry.nextHop)) {	// 다음 홉이 같은 경우
					routingTable.set(i, tempEntry);
				}
				else if (routingTable.get(i).getMetric() > entry.metric) {		// 
					routingTable.set(i, tempEntry);
				}
				return;
			}
		}
		routingTable.add(tempEntry);
	}
	
	protected boolean receiveRIP(byte[] ripMessage) {
		byte command = ripMessage[0];
		int entryLength = (ripMessage.length - 4) / 20;
		if (command == 1) {
			this.sendRIP((byte)2);
		}
		else if (command == 2) {
			for (int i = 0; i < entryLength; i++) {
				RIPEntry tempEntry = RIPEntry.makeRIPFromByteArray(Arrays.copyOfRange(ripMessage, 4 + i * 20, 4 + (i + 1) * 20));
				tempEntry.metric += 1;
				if (tempEntry.metric > 15
						|| Arrays.equals(tempEntry.ipAddress, this.thisLayerEntry.ipAddress)
						|| Arrays.equals(tempEntry.ipAddress, this.otherRIPLayer.thisLayerEntry.ipAddress)) {
					continue;
				}
				this.updateRoutingTable(tempEntry);
				ApplicationLayer.refreshTable();
			}
		}
		else if (command == (byte)3) {	// triggered update;
		
		}
		return true;
	}
}

class Send_Thread implements Runnable {
	   RIPLayer ripLayer;
	   byte command;
	   
	   public Send_Thread(RIPLayer thislayer, byte command) {
	      this.ripLayer = thislayer;
	      this.command = command;
	   }

	   @Override
	   public void run() {
		   byte[] ripMessage = new byte[4 + 20 * (ripLayer.routingTable.size() - 1)];
			ripMessage[0] = command;
			ripMessage[1] = 2;
			ripMessage[2] = 0;
			ripMessage[3] = 0;
			ripLayer.setThisAdapterToRoutingTable();
			for (int i = 0, j = 0; i < ripLayer.routingTable.size(); i++) {
				if (Arrays.equals(ripLayer.routingTable.get(i).getDestination(), ripLayer.thisLayerEntry.ipAddress)) {
					continue;
				}
				System.arraycopy(ripLayer.routingEntryToRIPEntry(ripLayer.routingTable.get(i)).toByteArray(), 0, ripMessage, 4 + 20 * j, 20);
				j++;
			}
			((UDPLayer) ripLayer.getUnderLayer()).sendRIP(ripMessage);
	   }
	}
