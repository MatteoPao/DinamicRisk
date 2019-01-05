package com.drisk.domain;

public class AttackPhase extends Phase {

	public AttackPhase() {
		super(4);
	}

	@Override
	public void startPhase() {
		// TODO Auto-generated method stub

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

			int[] dicesResult = new Dice().rollDices(attackerTanks, defenderTanks);
			territoryAttacker.removeNumberOfTanks(dicesResult[0]);
			territoryDefender.removeNumberOfTanks(dicesResult[1]);

			if (territoryDefender.getNumberOfTanks() == 0) {
				TankManager tm = TankManager.getInstance();
				defender.removeTerritoryOwned(territoryDefender);
				attacker.addTerritoryOwned(territoryDefender);
				tm.placeTanks(territoryDefender, 1);
				tm.removeTanks(territoryAttacker, 1);
			}
		
	}
	
	public boolean checkAttackAvailability(Player attacker, Player defender, Territory territoryAttacker, Territory territoryDefender) {
		
		if (territoryAttacker != null && territoryDefender != null && !(attacker.equals(defender))) {
			boolean condition1 = territoryAttacker.getNumberOfTanks() > 1;
			boolean condition2 = territoryAttacker.getNeighbours().contains(territoryDefender);
			return condition1 && condition2;
		}
		
		return false;
		
	}
	
}
