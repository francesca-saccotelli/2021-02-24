package it.polito.tdp.PremierLeague.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.PremierLeague.model.Action;
import it.polito.tdp.PremierLeague.model.Adiacenza;
import it.polito.tdp.PremierLeague.model.Match;
import it.polito.tdp.PremierLeague.model.Player;
import it.polito.tdp.PremierLeague.model.Team;

public class PremierLeagueDAO {
	
	public void listAllPlayers(Map<Integer,Player>idMap){
		String sql = "SELECT * FROM Players";
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				if(!idMap.containsKey(res.getInt("PlayerID"))) {
				Player p = new Player(res.getInt("PlayerID"), res.getString("Name"));
				idMap.put(p.getPlayerID(), p);
				}
			}
			conn.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
	
	public List<Team> listAllTeams(){
		String sql = "SELECT * FROM Teams";
		List<Team> result = new ArrayList<Team>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Team team = new Team(res.getInt("TeamID"), res.getString("Name"));
				result.add(team);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Action> listAllActions(){
		String sql = "SELECT * FROM Actions";
		List<Action> result = new ArrayList<Action>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				
				Action action = new Action(res.getInt("PlayerID"),res.getInt("MatchID"),res.getInt("TeamID"),res.getInt("Starts"),res.getInt("Goals"),
						res.getInt("TimePlayed"),res.getInt("RedCards"),res.getInt("YellowCards"),res.getInt("TotalSuccessfulPassesAll"),res.getInt("totalUnsuccessfulPassesAll"),
						res.getInt("Assists"),res.getInt("TotalFoulsConceded"),res.getInt("Offsides"));
				
				result.add(action);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Match> listAllMatches(){
		String sql = "SELECT m.MatchID, m.TeamHomeID, m.TeamAwayID, m.teamHomeFormation, m.teamAwayFormation, m.resultOfTeamHome, m.date, t1.Name, t2.Name   "
				+ "FROM Matches m, Teams t1, Teams t2 "
				+ "WHERE m.TeamHomeID = t1.TeamID AND m.TeamAwayID = t2.TeamID";
		List<Match> result = new ArrayList<Match>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				
				Match match = new Match(res.getInt("m.MatchID"), res.getInt("m.TeamHomeID"), res.getInt("m.TeamAwayID"), res.getInt("m.teamHomeFormation"), 
							res.getInt("m.teamAwayFormation"),res.getInt("m.resultOfTeamHome"), res.getTimestamp("m.date").toLocalDateTime(), res.getString("t1.Name"),res.getString("t2.Name"));
				
				
				result.add(match);

			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Player>getVertici(Map<Integer,Player>idMap,Match m){
		String sql="SELECT DISTINCT p.PlayerID,p.Name "
				+ "FROM players AS p,actions AS a,matches AS m "
				+ "WHERE p.PlayerID=a.PlayerID AND "
				+ "a.MatchID=m.MatchID AND m.MatchID=?";
		List<Player>vertici=new ArrayList<Player>();
		try {
			Connection conn = DBConnect.getConnection() ;
			PreparedStatement st = conn.prepareStatement(sql) ;
			st.setInt(1,m.getMatchID());
		    ResultSet res = st.executeQuery() ;
		    while(res.next()) {
		    	if(idMap.containsKey(res.getInt("p.PlayerID"))) {
		    		vertici.add(idMap.get(res.getInt("p.PlayerID")));
		    	}
		    }
		    conn.close();
		    return vertici;
		}catch(SQLException e) {
			e.printStackTrace();
			return null ;
		}
	}
	public List<Adiacenza>getAdiacenze(Map<Integer,Player>idMap,Match m){
		String sql="SELECT a1.PlayerID,a2.PlayerID, "
				+ "((a1.TotalSuccessfulPassesAll+a1.Assists)/a1.TimePlayed)-((a2.TotalSuccessfulPassesAll+a2.Assists)/a2.TimePlayed) AS peso "
				+ "FROM actions AS a1,actions AS a2,matches AS m "
				+ "WHERE a1.PlayerID>a2.PlayerID AND "
				+ "m.MatchID=a1.MatchID AND m.MatchID=a2.MatchID "
				+ "AND a1.TeamID!=a2.TeamID AND m.MatchID=? "
				+ "GROUP BY a1.PlayerID,a2.PlayerID";
		List<Adiacenza>adiacenze=new ArrayList<Adiacenza>();
		try {
			Connection conn = DBConnect.getConnection() ;
			PreparedStatement st = conn.prepareStatement(sql) ;
			st.setInt(1,m.getMatchID());
		    ResultSet res = st.executeQuery() ;
		    while(res.next()) {
		    	adiacenze.add(new Adiacenza(idMap.get(res.getInt("a1.PlayerID")),idMap.get(res.getInt("a2.PlayerID")),res.getDouble("peso")));
		    }
		    conn.close();
		    return adiacenze;
		}catch(SQLException e) {
			e.printStackTrace();
			return null ;
		}
	}
}
