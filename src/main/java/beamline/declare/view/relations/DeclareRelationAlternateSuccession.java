package beamline.declare.view.relations;

import beamline.graphviz.DotEdge;
import beamline.graphviz.DotNode;

public class DeclareRelationAlternateSuccession extends DotEdge {

	public DeclareRelationAlternateSuccession(DotNode source, DotNode target) {
		super(source, target);
		
		setOption("arrowhead", "dotnormal");
		setOption("arrowtail", "dot");
		setOption("label", "");
		setOption("dir", "both");
		setOption("color", "black:black");
	}
}
