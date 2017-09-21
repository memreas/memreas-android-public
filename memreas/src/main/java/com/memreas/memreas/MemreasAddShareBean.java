package com.memreas.memreas;

public class MemreasAddShareBean  extends MemreasShareBean {

	private static MemreasAddShareBean instance;
	private boolean recreateable=false;

	private MemreasAddShareBean () {
	}

	public static MemreasAddShareBean getInstance() {
		if (instance == null) {
			instance = new MemreasAddShareBean();
		}
		return instance;
	}
	
	public void onDoneResetShare() {
		instance = null;
	}

	public boolean isRecreateable() {
		return recreateable;
	}

	public void setRecreateable(boolean recreateable) {
		this.recreateable = recreateable;
	}

}
