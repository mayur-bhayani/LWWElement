package main.java.redist.client;

import java.io.IOException;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.*;

public class ZSETElement<E,T,S>  {
	Map<E,T> ZA;
	Map<E,T> ZR;
	Map<E,S> ZS;
	Timestamp tmpTS;
	Timestamp currentTS;

	//NEW() -> Z
	public void New(){
		ZA = new HashMap<>();
		ZR = new HashMap<>();
		ZS = new HashMap<>();
	}
	
	//ADD(Z,e,t)
	public void Add(E element, Integer score){
		currentTS = getCurrentTimeStamp();
		ZS.put(element, (S) score);
		if( ZA.containsKey(element)){
			tmpTS = (Timestamp) ZA.get(element);
			if(tmpTS.compareTo(currentTS) < 0) {
				ZA.put(element, (T) currentTS);
			}
		} else {
			ZA.put(element, (T) currentTS);			
		}
	}
	
	//Remove(Z,e,t)
	public void Remove(E element){
		currentTS = getCurrentTimeStamp();
		if( ZR.containsKey(element)){
			tmpTS = (Timestamp) ZR.get(element);
			if(tmpTS.compareTo(currentTS) < 0) {
				ZR.put(element, (T) currentTS);
			}			
		} else {
			ZR.put(element, (T) currentTS);
		}
		if( ZS.containsKey(element)){
			ZS.remove(element);
		}
	}
	
	// 
	public boolean Exists(E element) {
		boolean elementExist = false;
		if( ZA.containsKey(element)){
			tmpTS = (Timestamp) ZA.get(element);
			if( ZR.containsKey(element)){
				if( tmpTS.compareTo((Timestamp) ZR.get(element)) > 0) {
					elementExist = true;
				} else {
					elementExist = false;
				}
			} else {
				elementExist = true;
			}
		}
		return elementExist;
	}
	
	public Object[] Get(){		
		List<String> elementList = new ArrayList<>();
		ZA.forEach((e,t) ->{
			String memberWithScores = "";
			if(ZR.containsKey(e)) {			
				tmpTS = (Timestamp) ZR.get(e);
				if(((Timestamp) t).compareTo(tmpTS)>1) {
					memberWithScores = e + "->" + ZS.get(e);
					elementList.add(memberWithScores);					
				} 
			} else {
				memberWithScores = e + "->" + ZS.get(e);
				elementList.add(memberWithScores);
			}	
		});
		return (Object[]) elementList.toArray();
	}
	
	public static Timestamp getCurrentTimeStamp() {
		return new Timestamp(System.currentTimeMillis());
	}
	
    public static void main( String[] args )
    {	
    	ZSETElement<String, Timestamp, Integer> zset = new ZSETElement<String, Timestamp, Integer>();
    	zset.New();
    	zset.Add("abc",1);
    	zset.Add("xyz",2);
    	zset.Add("abc",5);
    	zset.Remove("xyz");
    	Object[] elementlist = zset.Get();
    	for(Object s:elementlist) 
    		System.out.println(s);
    }
}
