import at.ac.tuwien.big.moea.SearchExperiment;
import at.ac.tuwien.big.moea.SearchResultManager;
import at.ac.tuwien.big.moea.experiment.executor.listener.SeedRuntimePrintListener;
import at.ac.tuwien.big.moea.print.IPopulationWriter;
import at.ac.tuwien.big.moea.print.ISolutionWriter;
import at.ac.tuwien.big.moea.search.algorithm.EvolutionaryAlgorithmFactory;
import at.ac.tuwien.big.moea.search.algorithm.LocalSearchAlgorithmFactory;
import at.ac.tuwien.big.moea.search.algorithm.provider.IRegisteredAlgorithm;
import at.ac.tuwien.big.moea.search.fitness.dimension.IFitnessDimension;
import at.ac.tuwien.big.momot.ModuleManager;
import at.ac.tuwien.big.momot.TransformationResultManager;
import at.ac.tuwien.big.momot.TransformationSearchOrchestration;
import at.ac.tuwien.big.momot.examples.emfrefactor.metric.NSUBEC;
import at.ac.tuwien.big.momot.problem.solution.TransformationSolution;
import at.ac.tuwien.big.momot.problem.unit.parameter.comparator.DefaultEObjectEqualityHelper;
import at.ac.tuwien.big.momot.problem.unit.parameter.comparator.IEObjectEqualityHelper;
import at.ac.tuwien.big.momot.search.algorithm.operator.mutation.TransformationPlaceholderMutation;
import at.ac.tuwien.big.momot.search.fitness.EGraphMultiDimensionalFitnessFunction;
import at.ac.tuwien.big.momot.search.fitness.IEGraphMultiDimensionalFitnessFunction;
import at.ac.tuwien.big.momot.search.fitness.dimension.AbstractEGraphFitnessDimension;
import at.ac.tuwien.big.momot.search.fitness.dimension.TransformationLengthDimension;
import at.ac.tuwien.big.momot.util.MomotUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.henshin.interpreter.EGraph;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.core.Population;
import org.moeaframework.core.operator.OnePointCrossover;
import org.moeaframework.core.operator.TournamentSelection;
import org.moeaframework.util.progress.ProgressListener;

@SuppressWarnings("all")
public class EMFRefactorSearch {
   protected final static String INITIAL_MODEL = "model/input/metamodel.ecore";

   protected final static int SOLUTION_LENGTH = 5;

   public static void initialization() {
      EcorePackage.eINSTANCE.eClass();
   }

   public static void main(final String... args) {
      initialization();
      final EMFRefactorSearch search = new EMFRefactorSearch();
      search.performSearch(INITIAL_MODEL, SOLUTION_LENGTH);
   }

   protected final String[] modules = new String[] {
         "transformation/refactorings/ecore/remove_empty_sub_eclass_all.henshin" };

   protected final String[] unitsToRemove = new String[] {
         "remove_empty_sub_eclass_all::removeEmptySubEClass::check_subetypes",
         "remove_empty_sub_eclass_all::removeEmptySubEClass::check_superetypes",
         "remove_empty_sub_eclass_all::removeEmptySubEClass::check_empty_eclass",
         "remove_empty_sub_eclass_all::removeEmptySubEClass::initialCheck",
         "remove_empty_sub_eclass_all::removeEmptySubEClass::check_preconditions",
         "remove_empty_sub_eclass_all::removeEmptySubEClass::execute",
         "remove_empty_sub_eclass_all::removeEmptySubEClass::remove" };

   protected final int populationSize = 50;

   protected final int maxEvaluations = 1000;

   protected final int nrRuns = 30;

   protected String baseName;

   protected IEObjectEqualityHelper _createEqualityHelper_() {
      return (left, right) -> _createEqualityHelperHelper_(left, right);
   }

   protected boolean _createEqualityHelperHelper_(final EObject left, final EObject right) {
      boolean _and = false;
      if(!(left instanceof ENamedElement)) {
         _and = false;
      } else {
         _and = right instanceof ENamedElement;
      }
      if(_and) {
         final String _name = ((ENamedElement) left).getName();
         final String _name_1 = ((ENamedElement) right).getName();
         return _name.equals(_name_1);
      }
      final DefaultEObjectEqualityHelper _defaultEObjectEqualityHelper = new DefaultEObjectEqualityHelper();
      return _defaultEObjectEqualityHelper.equals(left, right);
   }

   protected ProgressListener _createListener_0() {
      final SeedRuntimePrintListener _seedRuntimePrintListener = new SeedRuntimePrintListener();
      return _seedRuntimePrintListener;
   }

   protected IFitnessDimension<TransformationSolution> _createObjective_0(
         final TransformationSearchOrchestration orchestration) {
      final IFitnessDimension<TransformationSolution> dimension = _createObjectiveHelper_0();
      dimension.setName("SolutionLength");
      dimension.setFunctionType(at.ac.tuwien.big.moea.search.fitness.dimension.IFitnessDimension.FunctionType.Minimum);
      return dimension;
   }

   protected IFitnessDimension<TransformationSolution> _createObjective_1(
         final TransformationSearchOrchestration orchestration) {
      return new AbstractEGraphFitnessDimension("SubClasses",
            at.ac.tuwien.big.moea.search.fitness.dimension.IFitnessDimension.FunctionType.Minimum) {
         @Override
         protected double internalEvaluate(final TransformationSolution solution) {
            final EGraph graph = solution.execute();
            final EObject root = MomotUtil.getRoot(graph);
            return _createObjectiveHelper_1(solution, graph, root);
         }
      };
   }

   protected IFitnessDimension<TransformationSolution> _createObjectiveHelper_0() {
      final TransformationLengthDimension _transformationLengthDimension = new TransformationLengthDimension();
      return _transformationLengthDimension;
   }

   protected double _createObjectiveHelper_1(final TransformationSolution solution, final EGraph graph,
         final EObject root) {
      final NSUBEC subClassCalculator = new NSUBEC();
      final EClass _eClass = EcorePackage.Literals.ECLASS.eClass();
      final List<EObject> eClasses = graph.getDomain(_eClass, true);
      double subClasses = 0.0;
      for(final EObject eClass : eClasses) {
         {
            subClassCalculator.setContext(
                  Collections.<EObject> unmodifiableList(CollectionLiterals.<EObject> newArrayList(eClass)));
            final double _subClasses = subClasses;
            final double _calculate = subClassCalculator.calculate();
            subClasses = _subClasses + _calculate;
         }
      }
      return subClasses;
   }

   protected IRegisteredAlgorithm<NSGAII> _createRegisteredAlgorithm_0(
         final TransformationSearchOrchestration orchestration,
         final EvolutionaryAlgorithmFactory<TransformationSolution> moea,
         final LocalSearchAlgorithmFactory<TransformationSolution> local) {
      final TournamentSelection _tournamentSelection = new TournamentSelection(2);
      final OnePointCrossover _onePointCrossover = new OnePointCrossover(1.0);
      final TransformationPlaceholderMutation _transformationPlaceholderMutation = new TransformationPlaceholderMutation(
            0.15);
      final IRegisteredAlgorithm<NSGAII> _createNSGAIII = moea.createNSGAIII(_tournamentSelection, _onePointCrossover,
            _transformationPlaceholderMutation);
      return _createNSGAIII;
   }

   protected SearchExperiment<TransformationSolution> createExperiment(
         final TransformationSearchOrchestration orchestration) {
      final SearchExperiment<TransformationSolution> experiment = new SearchExperiment<>(orchestration, maxEvaluations);
      experiment.setNumberOfRuns(nrRuns);
      experiment.addProgressListener(_createListener_0());
      return experiment;
   }

   protected IEGraphMultiDimensionalFitnessFunction createFitnessFunction(
         final TransformationSearchOrchestration orchestration) {
      final IEGraphMultiDimensionalFitnessFunction function = new EGraphMultiDimensionalFitnessFunction();
      function.addObjective(_createObjective_0(orchestration));
      function.addObjective(_createObjective_1(orchestration));
      return function;
   }

   protected EGraph createInputGraph(final String initialGraph, final ModuleManager moduleManager) {
      final EGraph graph = moduleManager.loadGraph(initialGraph);
      return graph;
   }

   protected ModuleManager createModuleManager() {
      final ModuleManager manager = new ModuleManager();
      manager.addModules(modules);
      manager.removeUnits(unitsToRemove);
      return manager;
   }

   protected TransformationSearchOrchestration createOrchestration(final String initialGraph,
         final int solutionLength) {
      final TransformationSearchOrchestration orchestration = new TransformationSearchOrchestration();
      final ModuleManager moduleManager = createModuleManager();
      final EGraph graph = createInputGraph(initialGraph, moduleManager);
      orchestration.setModuleManager(moduleManager);
      orchestration.setProblemGraph(graph);
      orchestration.setSolutionLength(solutionLength);
      orchestration.setFitnessFunction(createFitnessFunction(orchestration));
      orchestration.setEqualityHelper(_createEqualityHelper_());

      final EvolutionaryAlgorithmFactory<TransformationSolution> moea = orchestration
            .createEvolutionaryAlgorithmFactory(populationSize);
      final LocalSearchAlgorithmFactory<TransformationSolution> local = orchestration
            .createLocalSearchAlgorithmFactory();
      orchestration.addAlgorithm("NSGAIII", _createRegisteredAlgorithm_0(orchestration, moea, local));

      return orchestration;
   }

   protected void deriveBaseName(final TransformationSearchOrchestration orchestration) {
      final EObject root = MomotUtil.getRoot(orchestration.getProblemGraph());
      if(root == null || root.eResource() == null || root.eResource().getURI() == null) {
         baseName = getClass().getSimpleName();
      } else {
         baseName = root.eResource().getURI().trimFileExtension().lastSegment();
      }
   }

   protected TransformationResultManager handleResults(final SearchExperiment<TransformationSolution> experiment) {
      final ISolutionWriter<TransformationSolution> solutionWriter = experiment.getSearchOrchestration()
            .createSolutionWriter();
      final IPopulationWriter<TransformationSolution> populationWriter = experiment.getSearchOrchestration()
            .createPopulationWriter();
      final TransformationResultManager resultManager = new TransformationResultManager(experiment);
      Population population;
      population = SearchResultManager.createApproximationSet(experiment, (String[]) null);
      System.out.println("- Save objectives of all algorithms to 'model/output/metamodel/referenceSet.pf'");
      SearchResultManager.saveObjectives("model/output/metamodel/referenceSet.pf", population);
      System.out.println("---------------------------");
      System.out.println("Objectives of all algorithms");
      System.out.println("---------------------------");
      System.out.println(SearchResultManager.printObjectives(population));

      population = SearchResultManager.createApproximationSet(experiment, (String[]) null);
      System.out.println("- Save models of all algorithms to 'model/output/metamodel/solutions/'");
      TransformationResultManager.saveModels("model/output/metamodel/solutions/", baseName, population);

      return resultManager;
   }

   public void performSearch(final String initialGraph, final int solutionLength) {
      final TransformationSearchOrchestration orchestration = createOrchestration(initialGraph, solutionLength);
      deriveBaseName(orchestration);
      printSearchInfo(orchestration);
      final SearchExperiment<TransformationSolution> experiment = createExperiment(orchestration);
      experiment.run();
      System.out.println("-------------------------------------------------------");
      System.out.println("Results");
      System.out.println("-------------------------------------------------------");
      handleResults(experiment);
   }

   public void printSearchInfo(final TransformationSearchOrchestration orchestration) {
      System.out.println("-------------------------------------------------------");
      System.out.println("Search");
      System.out.println("-------------------------------------------------------");
      System.out.println("InputModel:      " + INITIAL_MODEL);
      System.out.println("Objectives:      " + orchestration.getFitnessFunction().getObjectiveNames());
      System.out.println("NrObjectives:    " + orchestration.getNumberOfObjectives());
      System.out.println("Constraints:     " + orchestration.getFitnessFunction().getConstraintNames());
      System.out.println("NrConstraints:   " + orchestration.getNumberOfConstraints());
      System.out.println("Transformations: " + Arrays.toString(modules));
      System.out.println("Units:           " + orchestration.getModuleManager().getUnits());
      System.out.println("SolutionLength:  " + orchestration.getSolutionLength());
      System.out.println("PopulationSize:  " + populationSize);
      System.out.println("Iterations:      " + maxEvaluations / populationSize);
      System.out.println("MaxEvaluations:  " + maxEvaluations);
      System.out.println("AlgorithmRuns:   " + nrRuns);
      System.out.println("---------------------------");
   }
}
