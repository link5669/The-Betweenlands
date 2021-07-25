package thebetweenlands.common.world.gen.dungeon.layout.topology.graph.grammar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;

public class Graph {
	private final Set<Node> nodes = new HashSet<>();
	private final Map<String, List<Node>> typeMap = new HashMap<>();
	private final Map<String, List<Node>> tagMap = new HashMap<>();

	private boolean mutated = false;

	int nodeIdCounter = 0;
	int edgeIdCounter = 0;

	public Graph copy() {
		Graph graph = new Graph();
		graph.merge(this, true);
		return graph;
	}

	public Map<Node, Node> merge(Graph graph) {
		return this.merge(graph, false);
	}

	public Map<Node, Node> merge(Graph graph, boolean mergeTags) {
		Map<Node, Node> merge = new HashMap<>();

		for(Node node : graph.nodes) {
			merge.put(node, this.addNode(node.getType(), mergeTags ? node.getTag() : null));
		}

		Set<Edge> edges = new HashSet<>();

		for(Node node : graph.nodes) {
			for(Edge edge : node.getEdges()) {
				if(edges.add(edge)) {
					Node left = merge.get(edge.getLeft());
					Node right = merge.get(edge.getRight());
					left.connect(right, edge.getType(), edge.isBidirectional());
				}
			}
		}

		return merge;
	}

	public Node addNode(String type) {
		return this.addNode(type, null);
	}

	public Node addNode(String type, @Nullable String tag) {
		Node node = new Node(this, type, tag);

		this.nodes.add(node);

		List<Node> typeNodes = this.typeMap.get(type);
		if(typeNodes == null) {
			this.typeMap.put(type, typeNodes = new ArrayList<>());
		}
		typeNodes.add(node);

		if(tag != null) {
			List<Node> tagNodes = this.tagMap.get(tag);
			if(tagNodes == null) {
				this.tagMap.put(tag, tagNodes = new ArrayList<>());
			}
			tagNodes.add(node);
		}

		return node;
	}

	public void removeNode(Node node) {
		this.nodes.remove(node);

		this.typeMap.remove(node.getType());

		String tag = node.getTag();
		if(tag != null) {
			this.tagMap.remove(tag);
		}

		for(Edge edge : node.getEdges()) {
			edge.getOther(node).removeEdge(edge);
		}
	}

	public Set<Node> getNodes() {
		return this.nodes;
	}

	public List<Node> getNodesByType(@Nullable String type) {
		List<Node> nodes = this.typeMap.get(type);
		if(nodes == null) {
			nodes = Collections.emptyList();
		}
		return nodes;
	}

	public List<Node> getNodesByTag(@Nullable String tag) {
		List<Node> nodes = this.tagMap.get(tag);
		if(nodes == null) {
			nodes = Collections.emptyList();
		}
		return nodes;
	}

	public Set<String> getTypes() {
		return this.typeMap.keySet();
	}

	public Set<String> getTags() {
		return this.tagMap.keySet();
	}

	void setMutated(boolean mutated) {
		this.mutated = mutated;
	}

	public boolean isMutated() {
		return this.mutated;
	}

	@Override
	public String toString() {
		ToStringHelper str = MoreObjects.toStringHelper(this);
		str.add("nodes", this.getNodes().size());
		for(Node node : this.getNodes()) {
			for(Edge edge : node.getEdges()) {
				if(edge.isBidirectional() || edge.getLeft() == node) {
					str.addValue(node.getType() + " (" + node.getID() + ") --(" + edge.getType() + ")--> " + edge.getOther(node).getType() + " (" + edge.getOther(node).getID() + ")");
				}
			}
		}
		return str.toString();
	}
}
