package com.drisk.domain;

public class TerritoryCard implements Card, Comparable<TerritoryCard> {
	
	private Territory territory;
	private TerritoryCardSymbol symbol;
	
	
	public TerritoryCard(Territory territory, TerritoryCardSymbol simbol) {
		setTerritory(territory);
		setSymbol(simbol);
	}

	@Override
	public String toString() {
		return "TerritoryCard [territory=" + territory.toString() + ", symbol=" + symbol + "]";
	}

	public Territory getTerritory() {
		return territory;
	}


	public TerritoryCardSymbol getSymbol() {
		return symbol;
	}


	public void setTerritory(Territory territory) {
		this.territory = territory;
	}


	public void setSymbol(TerritoryCardSymbol simbol) {
		this.symbol = simbol;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TerritoryCard other = (TerritoryCard) obj;
		if (symbol != other.symbol)
			return false;
		if (territory == null) {
			if (other.territory != null)
				return false;
		} 
		else if (!territory.equals(other.territory))
			return false;
		return true;
	}


	@Override
	public int compareTo(TerritoryCard o) {
		return this.getSymbol().compareTo(o.getSymbol());	
	}
	
}
