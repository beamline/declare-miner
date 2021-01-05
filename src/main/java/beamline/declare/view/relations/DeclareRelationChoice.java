package beamline.declare.view.relations;

import beamline.graphviz.DotEdge;
import beamline.graphviz.DotNode;

public class DeclareRelationChoice extends DotEdge {

	public DeclareRelationChoice(DotNode source, DotNode target) {
		super(source, target);
		
		setOption("arrowhead", "none");
		setOption("arrowtail", "none");
		setOption("label", "&#9826;");
		setOption("dir", "both");
		setOption("color", "black");
	}
}
