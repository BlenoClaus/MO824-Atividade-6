package problems.qbf.solvers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import problems.qbf.solvers.GRASP_QBF;
import problems.qbfpt.triples.ForbiddenTriplesBuilder;
import solutions.Solution;

public class GRASP_QBFPT extends GRASP_QBF {
	
	private ForbiddenTriplesBuilder ftBuilder;
	protected Boolean bestImproving = Boolean.TRUE;

	public GRASP_QBFPT(Double alpha, Integer iterations, String filename) throws IOException {
		super(alpha, iterations, filename);
		this.ftBuilder= new ForbiddenTriplesBuilder(ObjFunction.getDomainSize());
	}
	
	public GRASP_QBFPT(Double alpha, Integer iterations, String filename, Boolean bestImproving) throws IOException {
		this(alpha, iterations, filename);
		this.bestImproving = bestImproving;
	}

	@Override
	public List<Integer> makeCL() {
		return super.makeCL();
	}

	@Override
	public List<Integer> makeRCL() {
		return super.makeRCL();
	}

	@Override
	public void updateCL() {
		if (!this.incumbentSol.isEmpty()) {
			List<Integer> forbiddenValues = new ArrayList<>();
			Integer lastElem = this.incumbentSol.get(this.incumbentSol.size()-1);
			for (int i = 0; i < this.incumbentSol.size()-1; i++) {
				forbiddenValues.addAll(ftBuilder.getForbiddenValues(this.incumbentSol.get(i)+1, lastElem+1));
			}
			for (Integer fv : forbiddenValues) {
				int index = CL.indexOf(fv-1);
				if (index >= 0) CL.remove(index);
			}
		}
	}

	@Override
	public Solution<Integer> createEmptySol() {
		return super.createEmptySol();
	}
	
	private void localSearchBestImproving() {
		Double minDeltaCost;
		Integer bestCandIn = null, bestCandOut = null;

		do {
			minDeltaCost = Double.POSITIVE_INFINITY;
			updateCL();
				
			// Evaluate insertions
			for (Integer candIn : CL) {
				double deltaCost = ObjFunction.evaluateInsertionCost(candIn, incumbentSol);
				if (deltaCost < minDeltaCost) {
					minDeltaCost = deltaCost;
					bestCandIn = candIn;
					bestCandOut = null;
				}
			}
			// Evaluate removals
			for (Integer candOut : incumbentSol) {
				double deltaCost = ObjFunction.evaluateRemovalCost(candOut, incumbentSol);
				if (deltaCost < minDeltaCost) {
					minDeltaCost = deltaCost;
					bestCandIn = null;
					bestCandOut = candOut;
				}
			}
			// Evaluate exchanges
			for (Integer candIn : CL) {
				for (Integer candOut : incumbentSol) {
					double deltaCost = ObjFunction.evaluateExchangeCost(candIn, candOut, incumbentSol);
					if (deltaCost < minDeltaCost) {
						minDeltaCost = deltaCost;
						bestCandIn = candIn;
						bestCandOut = candOut;
					}
				}
			}
			// Implement the best move, if it reduces the solution cost.
			if (minDeltaCost < -Double.MIN_VALUE) {
				if (bestCandOut != null) {
					incumbentSol.remove(bestCandOut);
					CL.add(bestCandOut);
				}
				if (bestCandIn != null) {
					incumbentSol.add(bestCandIn);
					CL.remove(bestCandIn);
				}
				ObjFunction.evaluate(incumbentSol);
			}
		} while (minDeltaCost < -Double.MIN_VALUE);
		
	}

	private void localSearchFirstImproving() {
		Double minDeltaCost;
		Integer bestCandIn = null, bestCandOut = null;

		minDeltaCost = Double.POSITIVE_INFINITY;
		updateCL();
			
		// Evaluate insertions
		for (Integer candIn : CL) {
			double deltaCost = ObjFunction.evaluateInsertionCost(candIn, incumbentSol);
			if (deltaCost < minDeltaCost) {
				minDeltaCost = deltaCost;
				bestCandIn = candIn;
				bestCandOut = null;
				break;
			}
		}
		// Evaluate removals
		for (Integer candOut : incumbentSol) {
			double deltaCost = ObjFunction.evaluateRemovalCost(candOut, incumbentSol);
			if (deltaCost < minDeltaCost) {
				minDeltaCost = deltaCost;
				bestCandIn = null;
				bestCandOut = candOut;
				break;
			}
		}
		// Evaluate exchanges
		for (Integer candIn : CL) {
			for (Integer candOut : incumbentSol) {
				double deltaCost = ObjFunction.evaluateExchangeCost(candIn, candOut, incumbentSol);
				if (deltaCost < minDeltaCost) {
					minDeltaCost = deltaCost;
					bestCandIn = candIn;
					bestCandOut = candOut;
					break;
				}
			}
		}
		// Implement the best move, if it reduces the solution cost.
		if (minDeltaCost < -Double.MIN_VALUE) {
			if (bestCandOut != null) {
				incumbentSol.remove(bestCandOut);
				CL.add(bestCandOut);
			}
			if (bestCandIn != null) {
				incumbentSol.add(bestCandIn);
				CL.remove(bestCandIn);
			}
			ObjFunction.evaluate(incumbentSol);
		}
		
	}
	
	@Override
	public Solution<Integer> localSearch() {
		if (bestImproving) {
			localSearchBestImproving();
		} else {
			localSearchFirstImproving();
		}
		return null;
	}

	@Override
	public Solution<Integer> solve() {
		/*Log.info("*********************************************");
		Log.info("Problema: MAX-QBFPT");
		Log.info("Instance: "+fileName);
		Log.info("Alpha: "+alpha);
		Log.info(bestImproving? "Best Improving" : "First Improving");*/
		return super.solve();
	}
	
	public static void main(String[] args)  {
		long startTime = System.currentTimeMillis();
		GRASP_QBF grasp;
		try {
			grasp = new GRASP_QBFPT(0.2, 1000, "instances/qbf040", Boolean.TRUE);
			Solution<Integer> bestSol = grasp.solve();
			System.out.println("maxVal = " + bestSol);
			long endTime   = System.currentTimeMillis();
			long totalTime = endTime - startTime;
			System.out.println("Time = "+(double)totalTime/(double)1000+" seg");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
