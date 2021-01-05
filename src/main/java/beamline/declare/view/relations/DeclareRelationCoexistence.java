package beamline.declare.view.relations;

import beamline.graphviz.DotEdge;
import beamline.graphviz.DotNode;

public class DeclareRelationCoexistence extends DotEdge {

	public DeclareRelationCoexistence(DotNode source, DotNode target) {
		super(source, target);
		
		setOption("arrowhead", "dot");
		setOption("arrowtail", "dot");
		setOption("label", "");
		setOption("dir", "both");
		setOption("color", "black");
	}
}
