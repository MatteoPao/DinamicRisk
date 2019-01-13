package com.drisk.domain.turn;

import java.util.LinkedList;
import java.util.List;

import com.drisk.domain.exceptions.RequestNotValidException;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public abstract class Phase {
	
	private int phaseId;
	private List<String> history;
	
	public Phase(int id) {
		phaseId = id;
		history = new LinkedList<>();
	}
	
	public final int getPhaseId() {
		return phaseId;
	}
	
	public final JsonArray getHistoryToJson() {
		JsonArray result = new JsonArray();
		for(String s : history)
			result.add(s);
		history.clear();
		return result;
	}
	
	public final void addMessage(String message) {
		history.add(message);
	}
	
	public abstract void nextPhase() throws RequestNotValidException;
	public abstract void playPhase(JsonObject obj) throws RequestNotValidException;
	public abstract void fromJson(JsonObject obj) throws RequestNotValidException;
	protected abstract void checkCondition() throws RequestNotValidException;

}
