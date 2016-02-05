package com.tc.websocket.valueobjects;

import java.util.ArrayList;

public class UniqueList<T> extends ArrayList<T> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7556419907075250540L;

	@Override
	public boolean add(T t){
		boolean b = true;
		if(!this.contains(t)){
			b = super.add(t);
		}
		return b;
	}

}
