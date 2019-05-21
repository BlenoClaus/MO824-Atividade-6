package problems.qbfpt.solvers;

public class Reporter {
	
	static String[] INSTANCES = {
			"instances/qbf020",
			"instances/qbf040",
			"instances/qbf060",
			"instances/qbf080",
			"instances/qbf100",
			"instances/qbf200",
			"instances/qbf400",
		};
	
	static final double time = 30 * 60;
	
	private static final String createName(String prefix, String instance) {
		return prefix+"_"+instance.replace("instances/", "");
	}
	
	
	public static void main(String[] args) {
	
//		Gurobi_QBFPT.run(createName("model1-int-qua",INSTANCES[0]), INSTANCES[0], time);
//		Gurobi_QBFPT.run(createName("model1-int-qua",INSTANCES[1]), INSTANCES[1], time);
//		Gurobi_QBFPT.run(createName("model1-int-qua",INSTANCES[2]), INSTANCES[2], time);
//		Gurobi_QBFPT.run(createName("model1-int-qua",INSTANCES[3]), INSTANCES[3], time);
//		Gurobi_QBFPT.run(createName("model1-int-qua",INSTANCES[4]), INSTANCES[4], time);
//		Gurobi_QBFPT.run(createName("model1-int-qua",INSTANCES[5]), INSTANCES[5], time);
//		Gurobi_QBFPT.run(createName("model1-int-qua",INSTANCES[6]), INSTANCES[6], time);	
//		Gurobi_Linear_QBFPT.run(createName("model2-lin-mist",INSTANCES[0]), INSTANCES[0], time);
//		Gurobi_Linear_QBFPT.run(createName("model2-lin-mist",INSTANCES[1]), INSTANCES[1], time);
		Gurobi_Linear_QBFPT.run(createName("model2-lin-mist",INSTANCES[2]), INSTANCES[2], time);
//		Gurobi_Linear_QBFPT.run(createName("model2-lin-mist",INSTANCES[3]), INSTANCES[3], time);
//		Gurobi_Linear_QBFPT.run(createName("model2-lin-mist",INSTANCES[4]), INSTANCES[4], time);
//		Gurobi_Linear_QBFPT.run(createName("model2-lin-mist",INSTANCES[5]), INSTANCES[5], time);
//		Gurobi_Linear_QBFPT.run(createName("model2-lin-mist",INSTANCES[6]), INSTANCES[6], time);
					
				
	}
}
