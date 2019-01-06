package com.drisk.domain;

import java.util.List;

import com.google.gson.JsonObject;

public class AssignTanksPhase extends Phase {

	public AssignTanksPhase() {
		super(2);
	}

	@Override
	public void playPhase(JsonObject obj) {
		// TODO Auto-generated method stub
	}

	@Override
	public void nextPhase() {
		TurnManager.getInstance().setCurrentPhase(new TankPlacementPhase());
	}
	
	public void assignTanks(Player player) {
		
		//Ogni giocatore avrà almeno un tank di base all'inizio del turno,
		//anche se dovesse avere meno di 3 territori.
		int numberTerritoriesOwned = player.getNumberOfTerritoriesOwned();
		int tanks;
		
		if (numberTerritoriesOwned / 3 < 1) {
			tanks = 1;
		} else {
			tanks = numberTerritoriesOwned / 3;
		}
		
		tanks += getTanksPerContinent(player);
		
		player.addAvailableTanks(tanks);
		
	}
	
	
	public int getTanksPerContinent (Player player) {
		
		int tanks = 0;
		List<Territory> territoriesOwned = player.getTerritoriesOwned();
		List<Continent> continents = MapManager.getInstance().getMapContinents();
		for(Continent c: continents) {
			List<Territory> territories = c.getTerritories();
			if (territoriesOwned.containsAll(territories)) {
				switch(c.getName()) {
					case "africa": 
						tanks += 3;
						break;
					case "asia":
						tanks += 7;
						break;
					case "australia":
					case "south america":
						tanks += 2;
						break;
					case "europe":
					case "north america":
						tanks += 5;
						break;
					default:
						tanks += 0;
				}
			}
		}	
		
		return tanks;
		
	}
	
}
