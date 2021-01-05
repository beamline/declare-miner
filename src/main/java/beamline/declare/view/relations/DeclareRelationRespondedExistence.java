package beamline.declare.view.relations;

import beamline.graphviz.DotEdge;
import beamline.graphviz.DotNode;

public class DeclareRelationRespondedExistence extends DotEdge {

	public DeclareRelationRespondedExistence(DotNode source, DotNode target) {
		super(source, target);
		
		setOption("arrowhead", "none");
		setOption("arrowtail", "dot");
		setOption("label", "");
		setOption("dir", "both");
		setOption("color", "black");
	}
}
