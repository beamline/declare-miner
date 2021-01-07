package beamline.declare.miners;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import beamline.core.miner.AbstractMiner;
import beamline.core.web.annotations.ExposedMiner;
import beamline.core.web.annotations.ExposedMinerParameter;
import beamline.core.web.miner.models.MinerParameter;
import beamline.core.web.miner.models.MinerParameterValue;
import beamline.core.web.miner.models.MinerView;
import beamline.core.web.miner.models.MinerView.Type;
import beamline.core.web.miner.models.notifications.RefreshViewNotification;
import beamline.declare.miners.events.lossycounting.LCReplayer;
import beamline.declare.model.DeclareModel;
import beamline.declare.model.SimplifiedDeclareModel;
import beamline.declare.view.DeclareModelView;

@ExposedMiner(
	name = "Declare Miner with Lossy Counting",
	description = "This miner discovers a Declare model",
	configurationParameters = {
		@ExposedMinerParameter(name = "Maximal error", type = MinerParameter.Type.DOUBLE, defaultValue = "0.005")
	},
	viewParameters = {
		@ExposedMinerParameter(name = "Number of constraints to show", type = MinerParameter.Type.INTEGER, defaultValue = "10"),
		@ExposedMinerParameter(name = "Update model frequency", type = MinerParameter.Type.INTEGER, defaultValue = "1000")
	}
)
public class DeclareMinerLossyCounting extends AbstractMiner {

	// configuration variables
	private int eventsReceived = 0;
	private double maximalError = 0.005;
	private LCReplayer replayer = new LCReplayer();
	private int bucketWidth;
	
	private int modelUpdateFrequency = 1000;
	private int constraintsToShow = 10;

	@Override
	public void configure(Collection<MinerParameterValue> collection) {
		for(MinerParameterValue v : collection) {
			if (v.getName().equals("Maximal error") && v.getType() == MinerParameter.Type.DOUBLE) {
				maximalError = Double.parseDouble(v.getValue().toString());
			}
		}
		
		bucketWidth = (int)(1.0 / maximalError);
	}

	@Override
	public void consumeEvent(String caseID, String activityName) {
		// statistics update
		eventsReceived++;
		int currentBucket = (int)((double)eventsReceived / (double)bucketWidth);

		// data structure update
		replayer.addObservation(caseID, currentBucket);
		replayer.process(activityName, caseID);

		// events cleanup
		if (eventsReceived % bucketWidth == 0) {
			replayer.cleanup(currentBucket);
		}

		// incrementally update the model
		if (eventsReceived == 1 || eventsReceived % modelUpdateFrequency == 0) {
			notifyToClients(new RefreshViewNotification());
		}

	}

	@Override
	public List<MinerView> getViews(Collection<MinerParameterValue> collection) {
		for(MinerParameterValue v : collection) {
			if (v.getName().equals("Number of constraints to show")) {
				constraintsToShow = Integer.parseInt(v.getValue().toString());
			}
			if (v.getName().equals("Update model frequency")) {
				modelUpdateFrequency = Integer.parseInt(v.getValue().toString());
			}
		}
		
		Pair<SimplifiedDeclareModel, DeclareModel> model = getModel();
		
		List<MinerView> views = new ArrayList<>();
		views.add(new MinerView("Graphical representation", new DeclareModelView(model.getLeft()).toString(), Type.GRAPHVIZ));
		views.add(new MinerView("Textual representation", model.getRight().toHTMLString(), Type.RAW));
		return views;
	}

	public Pair<SimplifiedDeclareModel, DeclareModel> getModel() {
		DeclareModel model = replayer.getModel();
		DeclareModel filteredModel;
		if (model.hasTraces()) {
			filteredModel = DeclareModel.filterOnTraceSupport(model, 1.0);
		} else {
			filteredModel = DeclareModel.filterOnFulfillmentRatio(model, 1.0);
		}
		DeclareModel filteredSmallModel = DeclareModel.getTopConstraints(filteredModel, constraintsToShow);

		SimplifiedDeclareModel simplifiedModel = new SimplifiedDeclareModel();
		simplifiedModel.addConstraintsFromModel(filteredSmallModel);

		return Pair.of(simplifiedModel, filteredSmallModel);
	}
}
