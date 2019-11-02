package Router;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class RoutingTable extends ArrayList<RoutingEntry> {

	private static final long serialVersionUID = 1L;
	IPAscend ascend = new IPAscend();
	
	
	public RoutingTable() {
		super(20);
	}
	
	@Override
	synchronized public boolean add(final RoutingEntry entry) {
		boolean success = super.add(entry);
		Collections.sort(this, ascend);
		ApplicationLayer.refreshTable();
		return success;
	}
	
	@Override
	synchronized public RoutingEntry set(final int index, final RoutingEntry entry) {
		RoutingEntry result = super.set(index, entry);
		Collections.sort(this, ascend);
		ApplicationLayer.refreshTable();
		return result;
	}
	
	@Override
	synchronized public RoutingEntry get(final int index) {
		return super.get(index);
	}
	
	@Override
	synchronized public RoutingEntry remove(final int index) {
		RoutingEntry result = super.remove(index);
		Collections.sort(this, ascend);
		ApplicationLayer.refreshTable();
		return result;
	}
	
	class IPAscend implements Comparator<RoutingEntry> {
		@Override
		public int compare(RoutingEntry a, RoutingEntry b) {
			for (int i = 0; i < 4; i++) {
				int tempA = (int)a.getDestination()[i], tempB = (int)b.getDestination()[i];
				int aValue = tempA >= 0 ? tempA : tempA + 256;
				int bValue = tempB >= 0 ? tempB : tempB + 256;
				if (aValue != bValue) {
					return aValue - bValue;
				}
			}
			return 0;
		}
	}
}
