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
import beamline.declare.miners.events.budgetlossycounting.BudgetLCReplayer;
import beamline.declare.model.DeclareModel;
import beamline.declare.model.SimplifiedDeclareModel;
import beamline.declare.view.DeclareModelView;

@ExposedMiner(
	name = "Declare Miner with Budget Lossy Counting",
	description = "This miner discovers a Declare model",
	configurationParameters = {
		@ExposedMinerParameter(name = "Budget size", type = MinerParameter.Type.INTEGER, defaultValue = "1000")
	},
	viewParameters = {
		@ExposedMinerParameter(name = "Number of constraints to show", type = MinerParameter.Type.INTEGER, defaultValue = "10"),
		@ExposedMinerParameter(name = "Update model frequency", type = MinerParameter.Type.INTEGER, defaultValue = "1000")
	}
)
public class DeclareMinerBudgetLossyCounting extends AbstractMiner {

	// configuration variables
	private int eventsReceived = 0;
	private double budgetSize = 1000;
	private static int CONSTRAINT_NUMBER = 11;
	private BudgetLCReplayer replayer;
	
	private int modelUpdateFrequency = 1000;
	private int constraintsToShow = 10;

	@Override
	public void configure(Collection<MinerParameterValue> collection) {
		for(MinerParameterValue v : collection) {
			if (v.getName().equals("Budget size") && v.getType() == MinerParameter.Type.INTEGER) {
				budgetSize = Integer.parseInt(v.getValue().toString());
			}
		}
		
		replayer = new BudgetLCReplayer((int) (budgetSize / CONSTRAINT_NUMBER));
	}

	@Override
	public void consumeEvent(String caseID, String activityName) {
		// statistics update
		eventsReceived++;

		// data structure update
		replayer.process(activityName, caseID);

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
