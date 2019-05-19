package problems.qbf.solvers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import problems.qbf.solvers.GRASP_QBF;
import problems.qbfpt.log.Log;
import solutions.Solution;


/**
 * @author blenoclaus, rodrigofreitas, felipepavan 
 * 
 * Problem MAX_QBFPT using  Cost Perturbations 
 *
 */
public class GRASP_QBFPT_CP extends GRASP_QBFPT {
	
	private Map<Integer, Integer> frequency = new HashMap<>();

	public GRASP_QBFPT_CP(Double alpha, Integer iterations, String filename, Boolean bestImproving) throws IOException {
		super(alpha, iterations, filename, bestImproving);
	}
	
	private Integer getFrequency(Integer elem) {
		Integer freq = frequency.get(elem);
		if (freq == null) return Integer.MIN_VALUE;
		return freq;
	}
	private Integer putIncrease(Integer elem) {
		Integer value = frequency.get(elem);
		if (value == null) {
			frequency.put(elem, 1);
			return 1;
		}
		value = value + 1;
		frequency.put(elem, value);
		return value;
	}
	
	
	/*
	 * Essa função de pertubação de custos tem a idéia de dar prioridade as
	 * melhores elementos na primeira vez que são inseridos e a medida que 
	 * vão ficando muito tempo na solução, se começa a dar prioridade a 
	 * outros elementos com objetivo de diversificar a solução
	 * 
	 * */
	private Double perturbationFunction(Integer elem) {
		Double f = ObjFunction.evaluateInsertionCost(elem, incumbentSol);
		return f / (Integer.MAX_VALUE * getFrequency(elem));
	}
	
	private void updateFrequency() {
		incumbentSol.stream().forEach(elem -> putIncrease(elem));
	}
	
	@Override
	public Solution<Integer> localSearch() {
		Solution<Integer>  s = super.localSearch();
		updateFrequency();
		return s;
	}
	
	@Override
	public Solution<Integer> constructiveHeuristic() {
		CL = makeCL();
		RCL = makeRCL();
		incumbentSol = createEmptySol();
		incumbentCost = Double.POSITIVE_INFINITY;

		/* Main loop, which repeats until the stopping criteria is reached. */
		while (!constructiveStopCriteria()) {

			double maxCost = Double.NEGATIVE_INFINITY, minCost = Double.POSITIVE_INFINITY;
			incumbentCost = ObjFunction.evaluate(incumbentSol);
			updateCL();

			for (Integer c : CL) {
				Double deltaCost = ObjFunction.evaluateInsertionCost(c, incumbentSol);
				if (deltaCost < minCost)
					minCost = deltaCost;
				if (deltaCost > maxCost)
					maxCost = deltaCost;
			}

			for (Integer c : CL) {
				Double deltaCost = ObjFunction.evaluateInsertionCost(c, incumbentSol);
				if (deltaCost <= minCost + alpha * (maxCost - minCost)) {
					RCL.add(c);
				}
			}
			
			Double minPertubationCost = Double.MAX_VALUE;
			Integer candidate = null;
			for (Integer value : RCL) {
				Double functionPertuValue = perturbationFunction(value);
				if (minPertubationCost > functionPertuValue) {
					minPertubationCost = functionPertuValue;
					candidate = value;
				}
			}
			
			if (candidate != null) {
				CL.remove(candidate);
				incumbentSol.add(candidate);
			}
			ObjFunction.evaluate(incumbentSol);
			RCL.clear();
		}

		return incumbentSol;
	}
	
	@Override
	public Solution<Integer> solve() {
		return super.solve();
	}
	
	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		GRASP_QBF grasp;
		try {
			grasp = new GRASP_QBFPT_CP(0.2, 1000, "instances/qbf040", Boolean.TRUE);
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
