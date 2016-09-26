package com.tc.utils;

public class NameValue<T> {
	
	private String name;
	private T value;
	
	public NameValue(String name, T value){
		this.name = name;
		this.value = value;
	}
	
	public NameValue(){}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public T getValue() {
		return value;
	}
	public void setValue(T value) {
		this.value = value;
	}
	
	@Override
	public boolean equals(Object o){
		boolean b = false;
		if(o instanceof NameValue){
			b = this.toString().equals(o.toString());
		}
		return b;
	}
	
	@Override
	public String toString(){
		return name + "." + String.valueOf(value);
	}
	
	@Override
	public int hashCode(){
		return this.toString().hashCode();
	}
	
	

}
