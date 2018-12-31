package com.drisk.domain;

import java.util.LinkedList;
import java.util.List;

public class MatchManager {

	private static MatchManager instance;
	private boolean matchStarted;
	private boolean matchFull;
	private List<Color> colorsAvailablesList;
	private List<Player> players;
	
	private MatchManager() {
		matchStarted = false;
		matchFull = false;
		colorsAvailablesList = createColorAvailableList();
		players = new LinkedList<>();
	}
	
	public boolean isMatchStarted() {
		return matchStarted;
	}
	
	public boolean isMatchFull() {
		return matchFull;
	}
	
	public List<Player> getPlayers() {
		return players;
	}

	public void joinGame(String nickName) {
		if(!isMatchFull()) {
			Color color = findFreeColor();
			Player player = new Player(color, nickName);
			addPlayer(player);
		}
	}
	
	public void exitGame(String nickName) {
		for (Player p : players)
			if (p.getNickname().equals(nickName)) {
				Color playerColor = p.getColor();
				colorsAvailablesList.add(playerColor);
				removePlayer(p);
			}
	}
	
	
	public void setPlayerReady(String nickname, boolean ready) {
		for (Player p : players)
			if (p.getNickname().equals(nickname))
				p.setReady(ready);
	}
	
	public boolean isEveryoneReady() {
		boolean everyoneReady = true;
		for (Player p : players)
			if (!p.isReady())
				everyoneReady = false;
		return everyoneReady;
	}
	
	public void startGame() {
		matchStarted = true;
		GameManager.getInstance().initGameTemplate();
	}
	
	private void addPlayer(Player player) {
		if(!players.contains(player))
			players.add(player);
		if(players.size() >= 6)
			matchFull = true;
	}
	
	private void removePlayer(Player player) {
		if(players.contains(player))
			players.remove(player);
		if(players.size() < 6)
			matchFull = false;
	}

	private Color findFreeColor() {
		return colorsAvailablesList.remove(0);
	}
	
	private List<Color> createColorAvailableList() {
		Color[] colors = Color.values();
		List<Color> colorsList = new LinkedList<>();
		for(int i = 0; i < colors.length; ++i)
			colorsList.add(colors[i]);
		return colorsList;
	}
	
	public static MatchManager getInstance() {
		if(instance == null)
			instance = new MatchManager();
		return instance;
	}
	
}
