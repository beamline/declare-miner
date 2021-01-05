package beamline.declare.view.relations;

import beamline.graphviz.DotEdge;
import beamline.graphviz.DotNode;

public class DeclareRelationChainPrecedence extends DotEdge {

	public DeclareRelationChainPrecedence(DotNode source, DotNode target) {
		super(source, target);
		
		setOption("arrowhead", "dotnormal");
		setOption("arrowtail", "none");
		setOption("label", "");
		setOption("dir", "both");
		setOption("color", "black:black:black");
	}
}
