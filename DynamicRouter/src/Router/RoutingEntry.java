package Router;

import java.nio.ByteBuffer;

public class RoutingEntry {
   private final static int RT_DES_SIZE = 4;
   private final static int RT_NETMASK_SIZE = 4;
   private final static int RT_GATEWAY_SIZE = 4;

   private byte[] RT_des_IP;
   private byte[] RT_netmask_IP;
   private byte[] RT_gateway_IP;
   private Flag RT_flag;
   private int RT_interface;
   private int RT_metric;
   private int RT_class;
   private long RT_lastRefresh;
   
   
   public RoutingEntry() {
      RT_des_IP = new byte[RT_DES_SIZE];
      RT_netmask_IP = new byte[RT_NETMASK_SIZE];
      RT_gateway_IP = new byte[RT_GATEWAY_SIZE];
      RT_flag = Flag.NONE;
      RT_interface = -1;
      RT_metric = 0;
      RT_lastRefresh = System.currentTimeMillis();
   }
   
   public void setRoutingTable(byte[] desIP, byte[] netmaskIP, byte[] gatewayIP, Flag flag, int interfaceNumber) {
      int netmaskCheck = 0;
      System.arraycopy(desIP, 0, RT_des_IP, 0, 4);
      System.arraycopy(netmaskIP, 0, RT_netmask_IP, 0, 4);
      System.arraycopy(gatewayIP, 0, RT_gateway_IP, 0, 4);
      RT_flag = flag;
      RT_interface = interfaceNumber;

      netmaskCheck = this.netmaskCheckClass(netmaskIP);
      
      if ((netmaskIP[3] & (byte)0xff) != 0) {
         RT_class = 3;
      } else if ((netmaskIP[2] & (byte)0xff) != 0) {
         RT_class = 2;
      } else if ((netmaskIP[1] & (byte)0xff) != 0) {	// 수정 필요
         RT_class = 1;
      } else {
         RT_class = 0;
      }

     if (desIP[netmaskCheck] == gatewayIP[netmaskCheck] || netmaskCheck == 0) {
         RT_metric = 0x01;
      } else {
         RT_metric = 0x02;
      }
     RT_lastRefresh = System.currentTimeMillis();
   }

   public byte[] getDestination() {
      return this.RT_des_IP;
   }

   public byte[] getNetMask() {
      return this.RT_netmask_IP;
   }

   public byte[] getGateway() {
      return this.RT_gateway_IP;
   }

   public Flag getFlag() {
      return this.RT_flag;
   }

   public int getInterface() {
      return this.RT_interface;
   }

   public int getMetric() {
      return this.RT_metric;
   }
   
   public int getClassNumber() {
      return this.RT_class;
   }
   
   public long getRefreshedTime() {
	   return this.RT_lastRefresh;
   }

   public void setMetric(int metric) {
	   this.RT_metric = metric;
   }
   
   public void setMetric(byte[] metric) {
	   this.RT_metric = ByteBuffer.wrap(metric).getInt();
   }
   
   private int netmaskCheckClass(byte[] netmaskIP) {
      int count = 0;
      for (int i = 0; i < RT_NETMASK_SIZE; i++) {
    	  if (netmaskIP[i] == (byte)0xff)
            count++;
      }
      return count;
   }

   public void setRefreshedTime() {
	   this.RT_lastRefresh = System.currentTimeMillis();
   }
}