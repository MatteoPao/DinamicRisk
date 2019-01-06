package com.drisk.domain;

import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.drisk.domain.exceptions.SyntaxException;
import com.drisk.technicalservice.JsonHelper;
import com.google.gson.JsonObject;

public class GameManager {
	
	private List<Player> players;
	private static GameManager instance;
	
	private GameManager() {
		players = new LinkedList<>();
	}
	
	public static GameManager getInstance() {
		if (instance == null)
			instance = new GameManager();
		return instance;
	}
	
	public static void destroy() {
		instance = null;
	}

	public void initGame(JsonObject gameConfig, List<Player> players) throws SyntaxException, FileNotFoundException {
		initPlayers(players);
		initMap(gameConfig);
		initCards(gameConfig);
		initPlayersMission();
		initPlayersTerritories();
		initTanks();
		initPlaceTanks();
	}
	
	public void startGame() {
		TurnManager.getInstance().initTurn();
	}
	
	private void initMap(JsonObject gameConfig) throws SyntaxException, FileNotFoundException {
		MapManager.getInstance().createMap(gameConfig);
	}

	private void initPlayers(List<Player> players) {
		this.players = players;
	}
	
	public void initCards(JsonObject gameConfig) {
		CardManager.getInstance().initTerritoryCards();
		CardManager.getInstance().initMissionCards(Difficulty.valueOf(JsonHelper.difficultyFromJson(gameConfig).toUpperCase()));
		
		CardManager.getInstance().shuffleDeck(CardManager.getInstance().getTerritoryCards());
		CardManager.getInstance().shuffleDeck(CardManager.getInstance().getMissionCards());
	}
	
	public void initPlayersMission() {
		boolean singleMission = true; //temporaneamente impostiamo una missione comune a tutti
		
		if(singleMission) {
			MissionCard mission = (MissionCard) CardManager.getInstance().getMissionCards().get(0);
			for(Player p : players) 
				p.setMission(mission);
		} else {
			
		}
	}
	
	public void initPlayersTerritories() {
		List<Territory> territories = MapManager.getInstance().getTerritories();
		Collections.shuffle(territories);
		
		for (int i = 0; i < territories.size(); ++i) {
			players.get(i % players.size()).addTerritoryOwned(territories.get(i));
		}
	}

	public void initTanks() {
		TankManager.getInstance().initTanks(getPlayers());
	}
	
	public void initPlaceTanks() {
		for(Player p : players)
			for(Territory t : p.getTerritoriesOwned())
				TankManager.getInstance().placeTanks(t, p.placeTanks(1));
	}
	
	public boolean checkWin(Player currentPlayer) {
		return currentPlayer.getMissionCard().checkWin();
	}
	
	public boolean checkLoss() {
		//da implementare TODO
		return false;
	}

	public List<Player> getPlayers() {
		return players;
	}
	
	public void newTurn(Player oldPlayer) {
		int currentPlayerPositionInPlayers = players.indexOf(oldPlayer);
		if(currentPlayerPositionInPlayers == players.size() - 1)
			TurnManager.getInstance().setCurrentPlayer(players.get(0));
		else
			TurnManager.getInstance().setCurrentPlayer(players.get(currentPlayerPositionInPlayers + 1));
	}
	
	// this method allow to check, in the initial phase, if all player have placed all own tanks
	public boolean areAllTanksPlaced() {
		for(Player p : players)
			if(p.getAvailableTanks() != 0)
				return false;
		return true;
	}
	
	public Player findPlayerByColor(Color color) {
		for(Player p : players)
			if(p.getColor().equals(color))
				return p;
		return null;
	}
	
	private String getStringColorOfCurrentPlayer() {
		Player currentPlayerInTurn = TurnManager.getInstance().getCurrentPlayer();
		if(currentPlayerInTurn != null)
			return currentPlayerInTurn.getColor().toString();
		else
			return null;
	}
	
	private Integer getCurrentPhaseId() {
		Phase phase = TurnManager.getInstance().getCurrentPhase();
		if(phase != null)
			return phase.getPhaseId();
		else
			return null;
	}
	
	public JsonObject toJson() {
		return JsonHelper.gameManagerToJson(getStringColorOfCurrentPlayer(), getCurrentPhaseId(), players);
	}
	
}