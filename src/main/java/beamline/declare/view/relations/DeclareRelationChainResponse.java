package beamline.declare.view.relations;

import beamline.graphviz.DotEdge;
import beamline.graphviz.DotNode;

public class DeclareRelationChainResponse extends DotEdge {

	public DeclareRelationChainResponse(DotNode source, DotNode target) {
		super(source, target);
		
		setOption("arrowhead", "normal");
		setOption("arrowtail", "dot");
		setOption("label", "");
		setOption("dir", "both");
		setOption("color", "black:black:black");
	}
}
