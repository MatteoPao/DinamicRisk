package com.drisk.domain;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.drisk.domain.exceptions.SyntaxException;
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
		
	//"template" perchè posso inizializzare il gioco sia attraverso il database
	//con una mappa predefinita, sia inizializzando una mappa nuova passata come
	//json dal client. Quindi in realtà ci saranno due implementaizoni diverse.
	public void initGame(JsonObject gameConfig, List<Player> players) throws SyntaxException {
		initPlayers(players);
		initMap(gameConfig);
		initCards();
		initPlayersMission();
		initPlayersTerritories();
		initTanks();
		initPlaceTanks();
	}
	
	private void initMap(JsonObject gameConfig) throws SyntaxException {
		Map.getInstance().createMap(gameConfig);
	}

	private void initPlayers(List<Player> players) {
		this.players = players;
	}
	
	public void initCards() {
		CardManager.getInstance().initTerritoryCards();
		CardManager.getInstance().initMissionCards("easy");
		
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
		List<Territory> territories = Map.getInstance().getTerritories();
		Collections.shuffle(territories);
		
		for (int i = 0; i < territories.size(); ++i) {
			players.get(i % players.size()).addTerritoryOwned(territories.get(i));
		}
	}

	public void initTanks() {
		TankManager.getInstance().initTanks(getPlayers());
	}
	
	public void initPlaceTanks() {
		//TODO
	}
	
	public boolean checkWin(Player currentPlayer) {
		List<Territory> territories = Map.getInstance().getTerritories();
		int totalNumberOfTerritories = territories.size();
		
		int currentPlayerNumberOfTerritories = currentPlayer.getNumberOfTerritoriesOwned();
		double playerTerritoriesRate = (double) currentPlayerNumberOfTerritories / totalNumberOfTerritories;
		return playerTerritoriesRate >= (double) 2 / 3;	
	}
	
	public boolean checkLoss() {
		//da implementare TODO
		return false;
	}

	public List<Player> getPlayers() {
		return players;
	}
	
}