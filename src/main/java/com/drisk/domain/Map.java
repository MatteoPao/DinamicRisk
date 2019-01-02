package com.drisk.domain;

import java.util.LinkedList;
import java.util.List;

import com.drisk.technicalservice.JsonHelper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class Map {

	private static Map instance;
	private String difficulty;
	private List<Continent> continents;
	
	private Map() {
		continents = new LinkedList<>();
	}

	public static Map getInstance() {
		if (instance == null)
			instance = new Map();
		return instance;
	}
	
	public String getDifficulty() {
		return difficulty;
	}
	
	public void createMap(JsonObject gameConfig) {
		setDifficulty(JsonHelper.difficultyFromJson(gameConfig));
		createContinents(JsonHelper.getContinentsFromJson(gameConfig));
		createTerritories(JsonHelper.getMembershipFromJson(gameConfig));
		createNeighbours(JsonHelper.getNeighbourhoodFromJson(gameConfig));
	}
	
	private void createContinents(List<String> continentsNames) {
		for(String continentName : continentsNames) {
			Continent c = new Continent(continentName);
			addContinent(c);
		}	
	}
	
	private void createTerritories(java.util.Map<String, List<String>> relation) {
		for(java.util.Map.Entry<String, List<String>> entry : relation.entrySet()) {
			Continent c = findContinentByName(entry.getKey());
			for(String territoryName : relation.get(c.getName())) {
				Territory t = new Territory(territoryName);
				c.addTerritory(t);
			}
		}
	}
	
	private void createNeighbours(java.util.Map<String, List<String>> relation) {
		for(java.util.Map.Entry<String, List<String>> entry : relation.entrySet()) {
			Territory t = findTerritoryByName(entry.getKey());
			for(String neighbourName : relation.get(t.getName())) {
				Territory neighbour = findTerritoryByName(neighbourName);
				t.addNeighbour(neighbour);
				neighbour.addNeighbour(t);
			}
		}
	}
	
	private void addContinent(Continent continent) {
		if(!continents.contains(continent))
			continents.add(continent);
	}
	
	public Continent findContinentByName(String continentName) {
		for(Continent c : continents)
			if(c.getName().equals(continentName))
				return c;
		return null;
	}
	
	public Territory findTerritoryByName(String territoryName) {
		for(Continent c : continents)
			for(Territory t : c.getTerritories())
				if(t.getName().equals(territoryName))
					return t;
		return null;
	}
	
	private void setDifficulty(String difficulty) {
		this.difficulty = difficulty;
	}
	
	public List<Territory> getTerritories() {
		List<Territory> territories = new LinkedList<>();
		for(Continent c : continents)
			territories.addAll(c.getTerritories());
		return territories;
	}

	public List<Continent> getContinents() {
		return continents;
	}
	
	public JsonObject toJson1() {
		JsonObject jsonMap = new JsonObject();
		jsonMap.addProperty("difficulty", difficulty);
		JsonArray arrayContinents = new JsonArray();
		for(Continent c : continents)
			arrayContinents.add(c.toJson());
		jsonMap.add("continents", arrayContinents);
		return jsonMap;
	}
	
	public JsonObject toJson() {
		return JsonHelper.mapToJson(difficulty, continents, getTerritories());
	}
	
}
