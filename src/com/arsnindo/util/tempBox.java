package com.arsnindo.util;

import java.util.ArrayList;
import java.util.List;

public class tempBox {
	private String kategori;
	private List<String> label = new ArrayList<String>();
	private List<String> value = new ArrayList<String>();
	int boxType;
	
	public tempBox(String kategori, List<String> label2, List<String> value2, int type){
		this.kategori = kategori;
		this.label = label2;
		this.value = value2;
		this.boxType = type;
	}
	
	public String getKategori(){
		return this.kategori;
	}
	
	public List<String> getLabel(){
		return this.label;
	}
	
	public List<String> getValue(){
		return this.value;
	}
	
	public int getType(){
		return this.boxType;
	}
	
}
