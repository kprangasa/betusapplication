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

	public static SportsCategory getBycode(String code) {
		for (SportsCategory sports : SportsCategory.values()) {
			if (sports.sportsCategory.equals(code)) {
				return sports;
			}
		}

		return null;
	}
}
