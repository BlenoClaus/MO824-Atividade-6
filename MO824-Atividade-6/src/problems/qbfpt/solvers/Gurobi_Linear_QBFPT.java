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

public class Gurobi_Linear_QBFPT extends Gurobi_QBF {

	private ForbiddenTriplesBuilder ftBuilder;
	private GRBVar[][] w;

	public Gurobi_Linear_QBFPT(String filename) throws IOException {
		super(filename);
		ftBuilder = new ForbiddenTriplesBuilder(problem.size);
	}
	
	protected void populateNewModel(GRBModel model) throws GRBException {
    // variables
    x = new GRBVar[problem.size];
    w = new GRBVar[problem.size][problem.size];

    for (int i = 0; i < problem.size; i++) {
        x[i] = model.addVar(0, 1, 0.0f, GRB.BINARY, "x[" + i + "]");
        for (int j = 0; j < problem.size; j++) {
        	w[i][j] = model.addVar(0, 1, 0.0f, GRB.BINARY, "w["+i+","+j+"]");
        }
    }
    model.update();

    // objective functions
    GRBQuadExpr obj = new GRBQuadExpr();
    for (int i = 0; i < problem.size; i++) {
        for (int j = i; j < problem.size; j++) {
            obj.addTerm(problem.A[i][j], w[i][j]);
        }
    }
    
    model.setObjective(obj);
    model.update();
    
    GRBLinExpr expr1;
    for (Triple triple : ftBuilder.getForbiddenTriple()) {
    	expr1 = new GRBLinExpr();
    	expr1.addTerm(1, x[triple.getX()-1]);
    	expr1.addTerm(1, x[triple.getY()-1]);
    	expr1.addTerm(1, x[triple.getZ()-1]);
    	model.addConstr(expr1, GRB.LESS_EQUAL, 2.0, String.valueOf("x_"+triple.getX()+","+triple.getY()+","+triple.getZ()));
    }
    
    model.update();
    
    GRBLinExpr expr2, expr3;
    for (int i = 0; i < problem.size; i++) {
      for (int j = i; j < problem.size; j++) {
      	expr2 = new GRBLinExpr();
      	expr2.addTerm(1, w[i][j]);
      	model.addConstr(expr2, GRB.LESS_EQUAL, x[i], String.valueOf("(i)w_"+i+","+j+"<=x_"+i));
      	model.addConstr(expr2, GRB.LESS_EQUAL, x[j], String.valueOf("(j)w_"+i+","+j+"<=x_"+j));
      	expr3 = new GRBLinExpr();
      	expr3.addTerm(-1, w[i][j]);
      	expr3.addTerm(1, x[i]);
      	expr3.addTerm(1, x[j]);      	
      	model.addConstr(expr3, GRB.LESS_EQUAL, 1, String.valueOf("-w_"+i+","+j+"+x_"+i+","+j+"+x_"+i+","+j+"<=1"));
      }
    }
    model.update();
    

    // maximization objective function
    model.set(GRB.IntAttr.ModelSense, GRB.MAXIMIZE);
	}
	
	public static void run(String logName, String instance, double time) {
			Gurobi_Linear_QBFPT gurobi;
			try {
				gurobi = new Gurobi_Linear_QBFPT(instance);
				env = new GRBEnv(logName+".log");
				model = new GRBModel(env);
				model.getEnv().set(GRB.DoubleParam.TimeLimit, time);
				gurobi.populateNewModel(model);
				model.write(logName+".lp");
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
	
	public static void main(String[] args) {

    // instance name
		Gurobi_Linear_QBFPT gurobi;
		try {
			gurobi = new Gurobi_Linear_QBFPT("instances/qbf040");
			env = new GRBEnv("mip_qbfpt_2.log");
			model = new GRBModel(env);
			
			// execution time in seconds 
			model.getEnv().set(GRB.DoubleParam.TimeLimit, 600.0);
			
			// generate the model
			gurobi.populateNewModel(model);
			
			// write model to file
			model.write("model_qbfpt_2.lp");
			
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
