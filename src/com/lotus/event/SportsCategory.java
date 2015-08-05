package com.lotus.event;



public enum SportsCategory {
	FOOT("football"), TENN("tennis"), BASK("basketball"), HORS("Horse racing"), BOXI("boxing");
	private String sportsCategory;

	private SportsCategory(String sportsCategory){
		this.sportsCategory = sportsCategory;
		
	}

	public String getSportsCategory() {
		return sportsCategory;
	}

	public void setSportsCategory(String sportsCategory) {
		this.sportsCategory = sportsCategory;
	}

	public static SportsCategory getSportsCode(String sportsCode){
		for(SportsCategory sportsCategory: SportsCategory.values()){
			if(sportsCategory.toString().equals(sportsCode)){
				return sportsCategory;
			}
		}
		return null;
	}
}
