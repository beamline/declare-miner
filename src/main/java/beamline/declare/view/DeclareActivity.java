package beamline.declare.view;

import beamline.graphviz.DotNode;

public class DeclareActivity extends DotNode {

	protected DeclareActivity(String label) {
		super(label, null);
		
		setOption("fontname", "arial");
		setOption("fontsize", "10");
		setOption("fillcolor", "white");
		setOption("shape", "rec");
		setOption("style", "filled");
		setOption("color", "black");
	}
	
	@Override
	public int hashCode() {
		return getLabel().hashCode();
	}

	@Override
	public boolean equals(Object object) {
		return getLabel().equals(object);
	}
}
