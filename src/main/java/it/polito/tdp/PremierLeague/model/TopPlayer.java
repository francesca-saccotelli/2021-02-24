package it.polito.tdp.PremierLeague.model;

public class TopPlayer {

	private Player p;
	private double peso;
	public TopPlayer(Player p, double peso) {
		super();
		this.p = p;
		this.peso = peso;
	}
	public Player getP() {
		return p;
	}
	public void setP(Player p) {
		this.p = p;
	}
	public double getPeso() {
		return peso;
	}
	public void setPeso(double peso) {
		this.peso = peso;
	}
	@Override
	public String toString() {
		return p.getPlayerID()+" - "+p.getName()+", delta efficienza= "+this.peso;
	}
	
}
