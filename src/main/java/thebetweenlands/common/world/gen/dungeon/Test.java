package thebetweenlands.common.world.gen.dungeon;

import java.util.Random;

import thebetweenlands.common.world.gen.dungeon.layout.LayoutGenerator;
import thebetweenlands.common.world.gen.dungeon.layout.criteria.LoggingCriterion;
import thebetweenlands.common.world.gen.dungeon.layout.criteria.PathPercentageCriterion;
import thebetweenlands.common.world.gen.dungeon.layout.criteria.PhaseLimitCriterion;
import thebetweenlands.common.world.gen.dungeon.layout.criteria.RetryCriterion;
import thebetweenlands.common.world.gen.dungeon.layout.grid.Grid;
import thebetweenlands.common.world.gen.dungeon.layout.grid.Link;
import thebetweenlands.common.world.gen.dungeon.layout.pathfinder.SimplePathfinder;
import thebetweenlands.common.world.gen.dungeon.layout.postprocessor.CompactionPostprocessor;
import thebetweenlands.common.world.gen.dungeon.layout.topology.RandomWalkTopology;
import thebetweenlands.common.world.gen.dungeon.layout.topology.graph.grammar.Edge;
import thebetweenlands.common.world.gen.dungeon.layout.topology.graph.grammar.Grammar;
import thebetweenlands.common.world.gen.dungeon.layout.topology.graph.grammar.Graph;
import thebetweenlands.common.world.gen.dungeon.layout.topology.graph.grammar.GraphPrinter;
import thebetweenlands.common.world.gen.dungeon.layout.topology.graph.grammar.Mutator;
import thebetweenlands.common.world.gen.dungeon.layout.topology.graph.grammar.Node;

public class Test {
	public static Test TEST = new Test();

	public Grid grid = new Grid(new Random(), 16);

	public RandomWalkTopology topology = new RandomWalkTopology();
	public CompactionPostprocessor postprocessor = new CompactionPostprocessor();
	public SimplePathfinder pathfinder = new SimplePathfinder();

	private boolean isShrinking = false;
	private int counter = 0;

	public static Link invalidLink = null;

	public void init() {
		System.out.println("---------------------------- Generate ----------------------------");

		this.isShrinking = false;
		this.counter = 0;

		this.grid = new Grid(new Random(), 8);

		this.isShrinking = false;
		LayoutGenerator.sequence()
		.watchdog(() -> new PhaseLimitCriterion(9))
		.watchdog(() -> new LoggingCriterion())
		.topology(() -> this.topology)
		.criterion(() -> new RetryCriterion(3, false, true))
		.postprocessor(() -> this.postprocessor)
		.criterion(() -> new RetryCriterion(3, false, true))
		.pathfinder(() -> this.pathfinder)
		.criterion(() -> new PathPercentageCriterion(0.5f, 3, true))
		.finish()
		.generate(this.grid, new Random());
	}

	public void step() {
		this.grid.clearMeta(this.pathfinder);

		/*this.isShrinking = false;
		this.grid.resolve(new Random());
		this.postprocessor.init(this.grid, new Random(), num -> 0);*/

		/*this.isShrinking = false;
		this.grid.resolve(new Random());
		this.postprocessor.init(this.grid, new Random(), num -> 0);
		this.postprocessor.process();*/

		/*if(!this.isShrinking) {
			if(!this.grid.resolveIteration(new Random())) {
				this.isShrinking = true;
				this.counter = 0;
			}
		} else {
			if(this.counter == 0) this.postprocessor.init(this.grid, new Random(), num -> 0);
			System.out.println((++this.counter) + " " + this.postprocessor.shrinkStep(new Random(), 1));
		}*/

		if(!this.isShrinking) {
			this.grid.resolve();
			this.isShrinking = true;
			this.counter = 0;
		} else {
			if(this.counter == 0) this.postprocessor.init(this.grid, new Random(), num -> 0);
			System.out.println((++this.counter) + " " + this.postprocessor.compactIteration(new Random(), 1));
			if(this.counter > 20) {
				this.isShrinking = false;
			}
		}

		/*if(!this.isShrinking) {
			this.grid.resolve(new Random());
			this.isShrinking = true;
		} else {
			this.postprocessor.init(this.grid, new Random(), num -> 0);
			this.postprocessor.process();
			this.isShrinking = false;
		}*/
	}

	public void graph() {
		Graph graph = new Graph();
		graph.addNode("S"); //Axiom

		Grammar grammar = Grammar.builder()
				//Start rule
				.rule(1, lhs -> {
					lhs.addNode("S");
				}, rhs -> {
					Node e = rhs.addNode("e");
					Node C = rhs.addNode("C");
					Node G = rhs.addNode("G");
					Node bm = rhs.addNode("bm");
					Node iq = rhs.addNode("iq");
					Node ti = rhs.addNode("ti");
					Node CF = rhs.addNode("CF");
					Node g = rhs.addNode("g");

					e.chain(C).chain(G).chain(bm, "double")
					.chain(iq, "double").chain(ti, "double")
					.chain(CF, "double").chain(g);
				})
				//Create Final Chain
				.rule(1, lhs -> {
					Node CF = lhs.addNode("CF", "start");
					Node g = lhs.addNode("g", "end");
					CF.connect(g);
				}, rhs -> {
					Node C = rhs.addNode("C", "start");
					Node H1 = rhs.addNode("H");
					Node G = rhs.addNode("G");
					Node lf = rhs.addNode("lf");
					Node bl = rhs.addNode("bl");
					Node g = rhs.addNode("g", "end");
					Node t = rhs.addNode("t");
					Node kf = rhs.addNode("kf");
					Node H2 = rhs.addNode("H");

					C.connect(H1);
					C.chain(G).chain(lf, "double")
					.chain(bl, "double").chain(g, "double");
					C.chain(t).chain(kf, "double").chain(lf);
					t.connect(H2);
				})
				//Create Linear Chain 1
				.rule(1, lhs -> {
					Node C = lhs.addNode("C", "start");
					Node G = lhs.addNode("G", "end");
					C.connect(G);
				}, rhs -> {
					Node CL1 = rhs.addNode("CL", "start");
					Node CL2 = rhs.addNode("CL");
					Node CL3 = rhs.addNode("CL", "end");

					CL1.chain(CL2, "double").chain(CL3, "double");
				})
				//Create Linear Chain 2
				.rule(1, lhs -> {
					Node C = lhs.addNode("C", "start");
					Node G = lhs.addNode("G", "end");
					C.connect(G);
				}, rhs -> {
					Node CL1 = rhs.addNode("CL", "start");
					Node CL2 = rhs.addNode("CL");
					Node CL3 = rhs.addNode("CL");
					Node CL4 = rhs.addNode("CL", "end");

					CL1.chain(CL2, "double").chain(CL3, "double")
					.chain(CL4, "double");
				})
				//Create Linear Chain 3
				.rule(1, lhs -> {
					Node C = lhs.addNode("C", "start");
					Node G = lhs.addNode("G", "end");
					C.connect(G);
				}, rhs -> {
					Node CL1 = rhs.addNode("CL", "start");
					Node CL2 = rhs.addNode("CL");
					Node CL3 = rhs.addNode("CL");
					Node CL4 = rhs.addNode("CL");
					Node CL5 = rhs.addNode("CL", "end");

					CL1.chain(CL2, "double").chain(CL3, "double")
					.chain(CL4, "double").chain(CL5, "double");
				})
				//Create Linear Chain 4
				.rule(1, lhs -> {
					lhs.addNode("CL", "start");
				}, rhs -> {
					rhs.addNode("t", "start");
				})
				//Create Linear Chain 5
				.rule(1, lhs -> {
					lhs.addNode("CL", "start");
				}, rhs -> {
					Node t1 = rhs.addNode("t", "start");
					Node t2 = rhs.addNode("t");
					Node ib = rhs.addNode("ib", "start");

					t1.chain(t2, "double").chain(ib, "double");
				})
				//Create Linear Chain 6
				.rule(1, lhs -> {
					lhs.addNode("CL", "start");
				}, rhs -> {
					rhs.addNode("ts", "start");
				})
				//Create Linear Chain 7
				.rule(1, lhs -> {
					Node CL1 = lhs.addNode("CL", "start");
					Node CL2 = lhs.addNode("CL", "end");

					CL1.connect(CL2, "double");
				}, rhs -> {
					Node k = rhs.addNode("k", "start");
					Node l = rhs.addNode("l", "end");

					k.connect(l, "double");
				})
				//Create Linear Chain 8
				.rule(1, lhs -> {
					Node CL1 = lhs.addNode("CL", "start");
					Node CL2 = lhs.addNode("CL", "end");

					CL1.connect(CL2, "double");
				}, rhs -> {
					Node k = rhs.addNode("k", "start");
					Node l = rhs.addNode("l");
					Node CL = rhs.addNode("CL", "end");

					k.chain(l, "double").chain(CL, "double");
				})
				//Resolve hook 1
				.rule(1, lhs -> {
					lhs.addNode("H", "start");
				}, rhs -> {
					rhs.addNode("n", "start");
				})
				//Resolve hook 2
				.rule(1, lhs -> {
					lhs.addNode("H", "start");
				}, rhs -> {
					Node t = rhs.addNode("t", "start");
					Node ib = rhs.addNode("ib", "start");

					t.connect(ib, "double");
				})
				//Resolve hook 3
				.rule(1, lhs -> {
					lhs.addNode("H", "start");
				}, rhs -> {
					Node ts = rhs.addNode("ts", "start");
					Node ib = rhs.addNode("ib", "start");

					ts.connect(ib, "double");
				})
				//Create Parallel Chain 1
				.rule(1, lhs -> {
					Node C = lhs.addNode("C", "start");
					Node G = lhs.addNode("G", "end");

					C.connect(G);
				}, rhs -> {
					Node CP = rhs.addNode("CP", "start");
					Node G = rhs.addNode("G", "end");

					CP.connect(G);
				})
				//Create Parallel Chain 2
				.rule(1, lhs -> {
					Node CP = lhs.addNode("CP", "start");
					Node G = lhs.addNode("G", "end");

					CP.connect(G);
				}, rhs -> {
					Node F = rhs.addNode("F", "start");
					Node km1 = rhs.addNode("km");
					Node km2 = rhs.addNode("km");
					Node km3 = rhs.addNode("km");
					Node lm = rhs.addNode("lm", "end");

					F.chain(km1).chain(lm);
					F.chain(km2).chain(lm);
					F.chain(km3).chain(lm);
				})
				//Create Parallel Chain 3
				.rule(1, lhs -> {
					Node F = lhs.addNode("F", "start");
					Node km = lhs.addNode("km", "end");

					F.connect(km);
				}, rhs -> {
					Node F = rhs.addNode("F", "start");
					Node k = rhs.addNode("k");
					Node l = rhs.addNode("l");
					Node km = rhs.addNode("km", "end");
					Node H = rhs.addNode("H");

					F.chain(k).chain(l).chain(km, "double");
					l.connect(H, "double");
				})
				//Create Parallel Chain 4
				.rule(1, lhs -> {
					Node F = lhs.addNode("F", "start");
					Node km = lhs.addNode("km", "end");

					F.connect(km);
				}, rhs -> {
					Node F = rhs.addNode("F", "start");
					Node t = rhs.addNode("t");
					Node km = rhs.addNode("km", "end");

					F.chain(t).chain(km, "double");
				})
				//Create Parallel Chain 5
				.rule(1, lhs -> {
					Node F = lhs.addNode("F", "start");
					Node km = lhs.addNode("km", "end");

					F.connect(km);
				}, rhs -> {
					Node F = rhs.addNode("F", "start");
					Node ts = rhs.addNode("ts");
					Node km = rhs.addNode("km", "end");

					F.chain(ts).chain(km, "double");
				})
				//Create Parallel Chain 6
				.rule(1, lhs -> {
					Node F = lhs.addNode("F", "start");
					Node k = lhs.addNode("k", "end");

					F.connect(k);
				}, rhs -> {
					Node F = rhs.addNode("F", "start");
					Node t = rhs.addNode("t");
					Node k = rhs.addNode("k", "end");

					F.chain(t).chain(k, "double");
				})
				//Create Parallel Chain 7
				.rule(1, lhs -> {
					Node F = lhs.addNode("F", "start");
					Node k = lhs.addNode("k", "end");

					F.connect(k);
				}, rhs -> {
					Node F = rhs.addNode("F", "start");
					Node ts = rhs.addNode("ts");
					Node k = rhs.addNode("k", "end");

					F.chain(ts).chain(k, "double");
				})
				//Create Parallel Chain 8
				.rule(1, lhs -> {
					lhs.addNode("F", "start");
				}, rhs -> {
					Node n = rhs.addNode("n", "start");
					Node H1 = rhs.addNode("H", "start");
					Node H2 = rhs.addNode("H");

					n.connect(H1);
					n.connect(H2);
				})
				.build();

		Mutator mutator = Mutator.builder()
				.addMutation(grammar, true)
				.build();

		long start = System.nanoTime();
		int n = mutator.mutate(graph, new Random(), 50);
		System.out.println("Run time: " + (((System.nanoTime() - start) % 10000000000L) / 1000000.0f) + "ms");
		System.out.println("Mutated in: " + n + " steps");
		System.out.println("Nodes: " + graph.getNodes().size());
		System.out.println("Graph: \n" + GraphPrinter.toEdgeListString(graph));
		System.out.println("Tree: \n" + GraphPrinter.toSpanningTreeString(graph.getNodesByType("e").get(0)));
	}
}
