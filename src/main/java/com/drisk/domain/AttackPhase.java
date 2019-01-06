package com.drisk.domain;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.drisk.domain.exceptions.SyntaxException;
import com.drisk.technicalservice.JsonHelper;
import com.google.gson.JsonObject;

public class AttackPhase extends Phase {

	public AttackPhase() {
		super(4);
	}

	@Override
	public void playPhase(JsonObject obj) {
		/*try {
			Territory territoryAttacker = new Territory(JsonHelper.getTerritoriesFromJson(obj).get(0));
			Territory territoryDefender = new Territory(JsonHelper.getTerritoriesFromJson(obj).get(1));
			int attackerTanks = Integer.parseInt(JsonHelper.getNumberOfTanksFromJson(obj).get(0));
			attackEnemyTerritory(territoryAttacker, territoryDefender, attackerTanks);
		} catch (SyntaxException e) {
			
		}*/
	}

	@Override
	public void nextPhase() {
		TurnManager.getInstance().setCurrentPhase(new TankMovementPhase());
	}

	public void attackEnemyTerritory(Territory territoryAttacker, Territory territoryDefender, int attackerTanks) {

		Player attacker = TurnManager.getInstance().getCurrentPlayer();
		Player defender = territoryDefender.findPlayer();

			int defenderTanks = territoryDefender.getNumberOfTanks();
			if (defenderTanks > 3) {
				defenderTanks = 3;
			}
			
			Integer[] dicesResult = new Integer[2];
			territoryAttacker.removeTanks(dicesResult[0]);
			territoryDefender.removeTanks(dicesResult[1]);

			if (territoryDefender.getNumberOfTanks() == 0) {
				TankManager tm = TankManager.getInstance();
				defender.removeTerritoryOwned(territoryDefender);
				attacker.addTerritoryOwned(territoryDefender);
				tm.placeTanks(territoryDefender, 1);
				tm.removeTanks(territoryAttacker, 1);
			}
		
	}
	
	public int[] rollDices(int attackerTanks, int defenderTanks) {

		Integer[] attackerDicesResults = new Integer[attackerTanks];
		Integer[] defenderDicesResults = new Integer[defenderTanks];
		Dice dice = new Dice();
		
		for(int i = 0; i < attackerDicesResults.length; ++i) {
			attackerDicesResults[i] = dice.extractNumber();
		}
		
		for(int i = 0; i < defenderDicesResults.length; ++i) {
			defenderDicesResults[i] = dice.extractNumber();
		}

		Arrays.sort(attackerDicesResults, Collections.reverseOrder());
		Arrays.sort(defenderDicesResults, Collections.reverseOrder());
		
		List<Integer[]> results = new LinkedList<>();
		results.add(attackerDicesResults);
		results.add(defenderDicesResults);
		return compareDices(results);
	
	}
	
	public int[] compareDices(List<Integer[]> results) {
		
		Integer[] attackerDicesResults = results.get(0);
		Integer[] defenderDicesResults = results.get(1);
		int attackerTanksLost = 0;
		int defenderTanksLost = 0;
		
		int numIterations = Math.min(attackerDicesResults.length, defenderDicesResults.length);
		
		for(int i = 0; i < numIterations; ++i) {
			if (attackerDicesResults[i] > defenderDicesResults[i]) {
				defenderTanksLost++;
			} else {
				attackerTanksLost++;
			}
		}
		
		int[] tanksLost = new int[2];
		tanksLost[0] = attackerTanksLost;
		tanksLost[1] = defenderTanksLost;
		
		return tanksLost;
	}
	
}
