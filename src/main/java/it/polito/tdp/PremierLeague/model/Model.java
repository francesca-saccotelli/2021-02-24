package it.polito.tdp.PremierLeague.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.PremierLeague.db.PremierLeagueDAO;

public class Model {
	
	PremierLeagueDAO dao;
	Map<Integer,Player>idMap;
	private Graph<Player,DefaultWeightedEdge>grafo;
	
	public Model() {
		dao=new PremierLeagueDAO();
		idMap=new HashMap<>();
		this.dao.listAllPlayers(idMap);
	}
	
	public List<Match>getMatch(){
		List<Match>match=new ArrayList<>(this.dao.listAllMatches());
		Collections.sort(match);
		return match;
	}
	
	public void creaGrafo(Match m) {
		grafo=new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		Graphs.addAllVertices(this.grafo,this.dao.getVertici(idMap, m));
		for(Adiacenza a:this.dao.getAdiacenze(idMap,m)) {
			if(this.grafo.containsVertex(a.getP1()) && this.grafo.containsVertex(a.getP2())) {
				if(a.getPeso()>=0) {
					Graphs.addEdgeWithVertices(this.grafo, a.getP1(), a.getP2(), a.getPeso());
				}else if(a.getPeso()<0) {
					Graphs.addEdgeWithVertices(this.grafo, a.getP2(), a.getP1(),((double)-1)*a.getPeso());
				}
			}
		}
	}
	public int vertexNumber() {
		return this.grafo.vertexSet().size();
	}
	
	public int edgeNumber() {
		return this.grafo.edgeSet().size();
	}
	
	public Graph<Player,DefaultWeightedEdge> getGrafo() {
		return this.grafo;
	}
	
	public TopPlayer getTopPlayer() {
		if(grafo==null) {
			return null;
		}
		double max=0.0;
		Player best=null;
		for(Player p:this.grafo.vertexSet()) {
			double sommaIn=0.0;
			double sommaOut=0.0;

			for(DefaultWeightedEdge edge : grafo.outgoingEdgesOf(p)) {
				sommaOut+=grafo.getEdgeWeight(edge);
				}
			for(DefaultWeightedEdge edge : grafo.incomingEdgesOf(p)) {
					sommaIn+=grafo.getEdgeWeight(edge);
				}
			double delta=sommaOut-sommaIn;
			if(delta>max) {
				best=p;
				max=delta;
			}
		}
		return new TopPlayer(best,max);
	}
}
