package com.drisk.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.gson.JsonObject;

public class MatchManagerTest {
	
	@Before
	public void init() {
		for(int i = 1; i <= 6; i++)
			MatchManager.getInstance().joinGame("player" + i);
	}
	
	@Test
	public void joinGameTest() {
		assertTrue(MatchManager.getInstance().isMatchFull());

	}	

	@Test 
	public void colorPlayersTest() {
		Color[] colors = Color.values();
		boolean exist;
		List<Player> list = MatchManager.getInstance().getPlayers();
		for(Color c : colors) {
			exist = false;
			for(Player p : list)
				if(c.equals(p.getColor()))
					exist = true;
			if(!exist)
				fail();
		}
			
	}
	
	@Test
	public void areThereAtLeastTwoPlayersTest() {
		assertTrue(MatchManager.getInstance().areThereAtLeastTwoPlayers());
	}
	
	@Test
	public void isEveryoneReadyTest() {
		assertFalse(MatchManager.getInstance().isEveryoneReady());
		for (Player p : MatchManager.getInstance().getPlayers())
			p.setReady(true);
		assertTrue(MatchManager.getInstance().isEveryoneReady());
	}
	
	@Test
	public void isMatchStartedTest() {
		assertFalse(MatchManager.getInstance().isMatchStarted());
	}
	
	@Test
	public void toJsonTest() {
		JsonObject obj = MatchManager.getInstance().toJson();
		assertEquals(6, obj.getAsJsonArray("players").size());
	}
	
}
