package ara.util;

import ara.manet.communication.Emitter;
import ara.manet.detection.NeighborProtocolImpl;
import ara.manet.detection.ProbeMessage;
import ara.manet.positioning.PositionProtocol;
import ara.manet.positioning.PositionProtocolImpl;
import peersim.config.Configuration;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;

public class Initialisator implements Control {
	private static final String PAR_POSITION_PROTO = "position_pid";
	private static final String PAR_NEIGHBOR = "neighbor_pid";
	private static final String LOOP_EVENT = "LOOPEVENT";
	
	private final int positionPid, neighborPid;
	
	public Initialisator(String prefix) {
		positionPid = Configuration.getPid(prefix + "." + PAR_POSITION_PROTO);
		neighborPid = Configuration.getPid(prefix + "." + PAR_NEIGHBOR);
	}
	
	@Override
	public boolean execute() {
		for(int i = 0; i < Network.size(); i++) {
			Node node = Network.get(i);
			PositionProtocol position = (PositionProtocol)node.getProtocol(positionPid);
			position.initialiseCurrentPosition(node);
			PositionProtocolImpl pp = (PositionProtocolImpl)position;
			NeighborProtocolImpl np = (NeighborProtocolImpl)node.getProtocol(neighborPid);
			np.processEvent(node, neighborPid, new ProbeMessage(node.getID(), Emitter.ALL, neighborPid));
			pp.processEvent(node, positionPid, LOOP_EVENT);
		}
		return false;
	}
}

