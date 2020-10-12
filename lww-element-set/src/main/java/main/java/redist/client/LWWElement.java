package main.java.redist.client;

import java.sql.Timestamp;
import java.util.*;

/**
 * Hello world!
 *
 */
public class LWWElement<E,T> 
{
	Map<E,T> ZA;
	Map<E,T> ZR;
	Timestamp tmpTS;

	//NEW() -> Z
	public void New(){
		ZA = new HashMap<>();
		ZR = new HashMap<>();
	}
	
	//ADD(Z,e,t)
	public void Add(E element, Timestamp t){
		if( ZA.containsKey(element)){
			tmpTS = (Timestamp) ZA.get(element);
			if(tmpTS.compareTo(t) < 0) {
				ZA.put(element, (T) t);
			}
		} else {
			ZA.put(element, (T) t);
		}
	}
	
	//Remove(Z,e,t)
	public void Remove(E element, Timestamp t){
		if( ZR.containsKey(element)){
			tmpTS = (Timestamp) ZR.get(element);
			if(tmpTS.compareTo(t) < 0) {
				ZR.put(element, (T) t);
			}			
		} else {
			ZR.put(element, (T) t);
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
	
	public E[] Get(){
		List<E> elementList = new ArrayList<>();
		ZA.forEach((e,t) ->{
			if(ZR.containsKey(e)) {			
				tmpTS = (Timestamp) ZR.get(e);
				if(((Timestamp) t).compareTo(tmpTS)>1) {
					elementList.add(e);					
				} 
			} else {
				elementList.add(e);
			}	
		});
		return (E[]) elementList.toArray();
	}
	
	public static Timestamp getCurrentTimeStamp() {
		return new Timestamp(System.currentTimeMillis());
	}
	
    public static void main( String[] args )
    {	
    	LWWElement<Object,Timestamp> lww = new LWWElement<Object, Timestamp>();
    	lww.New();
    	lww.Add("abc", getCurrentTimeStamp());
    	lww.Add("xyz", getCurrentTimeStamp());
    }
}
