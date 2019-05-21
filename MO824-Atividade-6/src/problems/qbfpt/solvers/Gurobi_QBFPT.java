package problems.qbfpt.solvers;

import java.io.IOException;

import gurobi.GRB;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBQuadExpr;
import gurobi.GRBVar;
import problems.qbf.solvers.Gurobi_QBF;
import triple.forbidden.ForbiddenTriplesBuilder;
import triple.forbidden.Triple;

public class Gurobi_QBFPT extends Gurobi_QBF {

	private ForbiddenTriplesBuilder ftBuilder;

	public Gurobi_QBFPT(String filename) throws IOException {
		super(filename);
		ftBuilder = new ForbiddenTriplesBuilder(problem.size);
	}
	
	protected void populateNewModel(GRBModel model) throws GRBException {
    // variables
    x = new GRBVar[problem.size];

    for (int i = 0; i < problem.size; i++) {
        x[i] = model.addVar(0, 1, 0.0f, GRB.BINARY, "x[" + i + "]");
    }
    model.update();

    // objective functions
    GRBQuadExpr obj = new GRBQuadExpr();
    for (int i = 0; i < problem.size; i++) {
        for (int j = i; j < problem.size; j++) {
            obj.addTerm(problem.A[i][j], x[i], x[j]);
        }
    }
    
    model.setObjective(obj);
    model.update();
    
    GRBLinExpr expr;
    for (Triple triple : ftBuilder.getForbiddenTriple()) {
    	expr = new GRBLinExpr();
    	expr.addTerm(1, x[triple.getX()-1]);
    	expr.addTerm(1, x[triple.getY()-1]);
    	expr.addTerm(1, x[triple.getZ()-1]);
    	model.addConstr(expr, GRB.LESS_EQUAL, 2.0, String.valueOf("x_"+triple.getX()+","+triple.getY()+","+triple.getZ()));
    }

    // maximization objective function
    model.set(GRB.IntAttr.ModelSense, GRB.MAXIMIZE);
	}
	
	public static void main(String[] args) {

    // instance name
		Gurobi_QBFPT gurobi;
		try {
			gurobi = new Gurobi_QBFPT("instances/qbf020");
			env = new GRBEnv("mip_qbfpt_1.log");
			model = new GRBModel(env);
			
			// execution time in seconds 
			model.getEnv().set(GRB.DoubleParam.TimeLimit, 600.0);
			
			// generate the model
			gurobi.populateNewModel(model);
			
			// write model to file
			model.write("model_qbfpt_1.lp");
			
			model.optimize();
			
			System.out.println("\n\nZ* = " + model.get(GRB.DoubleAttr.ObjVal));
			
			System.out.print("X = [");
			for (int i = 0; i < gurobi.problem.size; i++) {
				System.out.print((gurobi.x[i].get(GRB.DoubleAttr.X) > 0.5 ? 1.0 : 0) + ", ");
			}
			System.out.print("]");
			
			model.dispose();
			env.dispose();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (GRBException e) {
			e.printStackTrace();
		}
		
	}

}
