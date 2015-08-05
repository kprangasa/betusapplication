package com.lotus.event;

public enum Result {
	WIN, LOSE, DRAW, NONE;
//	private String resultType;
//
//	private Result(String resultType) {
//		this.resultType = resultType;
//	}
//	
//	public String getResultType() {
//		return resultType;
//	}
//
//	public void setResultType(String resultType) {
//		this.resultType = resultType;
//	}

	public static Result getResult(String result){
		for(Result resultType: Result.values() ){
			if(resultType.toString().equals(result)){
				return resultType;
			}
		}
		return null;
	}
}
