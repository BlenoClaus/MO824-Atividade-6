package metaheuristics.grasp;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import problems.Evaluator;
import solutions.Solution;

/**
 * Abstract class for metaheuristic GRASP (Greedy Randomized Adaptive Search
 * Procedure). It consider a minimization problem.
 *
 * @author ccavellucci, fusberti
 * @param <E> Generic type of the element which composes the solution.
 */
public abstract class AbstractGRASP<E> {

    /**
     * flag that indicates whether the code should print more information on
     * screen
     */
    public static boolean verbose = true;

    /**
     * a random number generator
     */
    static Random rng = new Random(0);

    /**
     * the objective function being optimized
     */
    protected Evaluator<E> ObjFunction;

    /**
     * the GRASP greediness-randomness parameter
     */
    protected Double alpha;

    /**
     * the best solution cost
     */
    protected Double bestCost;

    /**
     * the incumbent solution cost
     */
    protected Double incumbentCost;

    /**
     * the best solution
     */
    protected Solution<E> bestSol;

    /**
     * the incumbent solution
     */
    protected Solution<E> incumbentSol;

    /**
     * tempo para execução do GRASP.
     */
    protected Integer tempoExecucao;

    /**
     * Quantidade de iterações sem melhora para considerar a convergễncia do
     * GRASP.
     */
    protected Integer iteraConvengencia;
    
    protected List<Integer> alvos;

    /**
     * the Candidate List of elements to enter the solution.
     */
    protected ArrayList<E> CL;

    /**
     * the Restricted Candidate List of elements to enter the solution.
     */
    protected ArrayList<E> RCL;

    /**
     * Creates the Candidate List, which is an ArrayList of candidate elements
     * that can enter a solution.
     *
     * @return The Candidate List.
     */
    public abstract ArrayList<E> makeCL();

    /**
     * Creates the Restricted Candidate List, which is an ArrayList of the best
     * candidate elements that can enter a solution. The best candidates are
     * defined through a quality threshold, delimited by the GRASP
     * {@link #alpha} greedyness-randomness parameter.
     *
     * @return The Restricted Candidate List.
     */
    public abstract ArrayList<E> makeRCL();

    /**
     * Updates the Candidate List according to the incumbent solution
     * {@link #incumbentSol}. In other words, this method is responsible for
     * updating which elements are still viable to take part into the solution.
     */
    public abstract void updateCL();

    /**
     * Creates a new solution which is empty, i.e., does not contain any
     * element.
     *
     * @return An empty solution.
     */
    public abstract Solution<E> createEmptySol();

    /**
     * The GRASP local search phase is responsible for repeatedly applying a
     * neighborhood operation while the solution is getting improved, i.e.,
     * until a local optimum is attained.
     *
     * @return An local optimum solution.
     */
    public abstract Solution<E> localSearch();

    /**
     * Constructor for the AbstractGRASP class.
     *
     * @param objFunction The objective function being minimized.
     * @param alpha The GRASP greediness-randomness parameter (within the range
     * [0,1])
     */
    public AbstractGRASP(Evaluator<E> objFunction, Double alpha, Integer tempoExecucao, List<Integer> alvos) {
        this.ObjFunction = objFunction;
        this.alpha = alpha;
        this.tempoExecucao = tempoExecucao;
        this.alvos = alvos;
    }

    /**
     * The GRASP constructive heuristic, which is responsible for building a
     * feasible solution by selecting in a greedy-random fashion, candidate
     * elements to enter the solution.
     *
     * @return A feasible solution to the problem being minimized.
     */
    public Solution<E> constructiveHeuristic() {

        incumbentSol = createEmptySol();
        incumbentCost = Double.POSITIVE_INFINITY;

        CL = makeCL();
        RCL = makeRCL();


        /* Main loop, which repeats until the stopping criteria is reached. */
        while (!constructiveStopCriteria()) {

            double maxCost = Double.NEGATIVE_INFINITY, minCost = Double.POSITIVE_INFINITY;
            incumbentCost = ObjFunction.evaluate(incumbentSol);
            updateCL();

            if (CL.isEmpty()) {
                break;
            }

            /*
            * Explore all candidate elements to enter the solution, saving the
            * highest and lowest cost variation achieved by the candidates.
             */
            for (E c : CL) {
                Double deltaCost = ObjFunction.evaluateInsertionCost(c, incumbentSol);

                if (deltaCost < minCost) {
                    minCost = deltaCost;
                }

                if (deltaCost > maxCost) {
                    maxCost = deltaCost;
                }
            }

            /*
            * Among all candidates, insert into the RCL those with the highest
            * performance using parameter alpha as threshold.
             */
            for (E c : CL) {
                Double deltaCost = ObjFunction.evaluateInsertionCost(c, incumbentSol);

                if (deltaCost <= minCost + alpha * (maxCost - minCost)) {
                    RCL.add(c);
                }
            }

            /* Choose a candidate randomly from the RCL */
            int rndIndex = rng.nextInt(RCL.size());
            E inCand = RCL.get(rndIndex);
            CL.remove(inCand);
            incumbentSol.add(inCand);
            ObjFunction.evaluate(incumbentSol);
            RCL.clear();

        }

        return incumbentSol;
    }

    /**
     * The GRASP mainframe. It consists of a loop, in which each iteration goes
     * through the constructive heuristic and local search. The best solution is
     * returned as result.
     *
     * @return The best feasible solution obtained throughout all iterations.
     */
    public Solution<E> solve() {
        long tempoInicial;
        int iteracao;
        bestSol = createEmptySol();
        int iteracoesSemMelhora = 0;
        List<Integer> alvos_ = new ArrayList<>(this.alvos);

        tempoInicial = System.currentTimeMillis();
        iteracao = 1;
        
        verificarAlvos(alvos_, 0, tempoInicial, tempoInicial);
        
        while ((((System.currentTimeMillis() - tempoInicial) / 1000D) / 60D) <= this.tempoExecucao && !alvos_.isEmpty()) {
            iteracao++;
            iteracoesSemMelhora++;

            constructiveHeuristic();
            localSearch();

            if (bestSol.cost > incumbentSol.cost) {
                bestSol = new Solution<E>(incumbentSol);
                iteracoesSemMelhora = 0;

                if (verbose) {
                    System.out.println("(Iter. " + iteracao + ", Temp. " + ((System.currentTimeMillis() - tempoInicial) / 1000D) + "s) BestSol = " + bestSol);
                }
            }

            verificarAlvos(alvos_, iteracao, tempoInicial, System.currentTimeMillis());
        }
        
        // Caso não tenha chegado em algum alvo
        for (Integer alvo : alvos_) {
            System.out.println("Alvo: [" + alvo + "]");
        }

        return bestSol;
    }

    /**
     * A standard stopping criteria for the constructive heuristic is to repeat
     * until the incumbent solution improves by inserting a new candidate
     * element.
     *
     * @return true if the criteria is met.
     */
    public Boolean constructiveStopCriteria() {
        return (incumbentCost > incumbentSol.cost) ? false : true;
    }
    
    private void verificarAlvos(List<Integer> alvos_, int g, long tempoInicial, long tempoAtual) {
        for (int i = 0; i < alvos_.size(); i++) {
            Integer alvo = alvos_.get(i);
            
            if (Math.abs(this.bestSol.cost) >= alvo) {
                System.out.println("Alvo: [" + alvo + "] (Ite. " + g + ", Temp. " + ((tempoAtual - tempoInicial) / 1000D) + "s) BestSol = " + bestSol);
                alvos_.remove(i);
                i--;
            }
        }
    }

}
