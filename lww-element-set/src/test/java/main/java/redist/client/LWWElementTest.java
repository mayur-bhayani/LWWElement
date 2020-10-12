package main.java.redist.client;

import java.sql.Timestamp;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Test;

public class LWWElementTest {

	public static Timestamp getCurrentTimeStamp() {
		return new Timestamp(System.currentTimeMillis());
	}
	
	@Test
	public void testNew() {
		LWWElement<Object,Timestamp> lww = new LWWElement<Object, Timestamp>();
    	lww.New();
    	Assert.assertNotNull(lww.ZR);
    	Assert.assertNotNull(lww.ZA);
	}

	@Test
	public void testAdd() throws InterruptedException {
		LWWElement<Object,Timestamp> lww = new LWWElement<Object, Timestamp>();
    	lww.New();
    	
    	//Add elements to Set Z
    	lww.Add("abc", getCurrentTimeStamp());
    	lww.Add("xyz", getCurrentTimeStamp());
    	Timestamp oldTS = lww.ZA.get("abc");
    	
    	TimeUnit.SECONDS.sleep(1);
    	
    	//check the size of set Z
    	Assert.assertEquals(2, lww.ZA.size());
    	
    	//Add an existing element and size should remain same
    	lww.Add("abc", getCurrentTimeStamp());
    	Assert.assertEquals(2, lww.ZA.size());
    	
    	//Check if timestamp isupdated
    	Timestamp newTS = lww.ZA.get("abc");	
    	Assert.assertTrue(((newTS.compareTo(oldTS)) > 0));
	}
	
	@Test
	public void testRemove() throws InterruptedException {
		LWWElement<Object,Timestamp> lww = new LWWElement<Object, Timestamp>();
    	lww.New();
    	
    	//Add elements to Set Z
    	lww.Add("abc", getCurrentTimeStamp());
    	lww.Add("xyz", getCurrentTimeStamp());
    	lww.Remove("abc", getCurrentTimeStamp());
    	
    	//The entry in Set ZR should be created
    	Assert.assertNotNull(lww.ZR.get("abc"));
    	Timestamp oldTS = lww.ZR.get("abc");
    	    	
    	TimeUnit.SECONDS.sleep(1);
    	
    	//Delete an element again should the timestamp in Set ZR
    	lww.Remove("abc", getCurrentTimeStamp());
    	Timestamp newTS = lww.ZR.get("abc");
    	
    	//check if timestamp is udpdate
    	Assert.assertTrue(((newTS.compareTo(oldTS)) > 0));   	
	}
	
	@Test
	public void testExist() throws InterruptedException {
		LWWElement<Object,Timestamp> lww = new LWWElement<Object, Timestamp>();
    	lww.New();
    	
    	//should return false as Set ZA is empty
    	Assert.assertFalse(lww.Exists("abc"));
    	
    	//Should return true as element "abc" is added
    	lww.Add("abc", getCurrentTimeStamp());
    	Assert.assertTrue(lww.Exists("abc"));
    	
    	TimeUnit.SECONDS.sleep(1);
    	
    	//Should return false as element is now removed
    	lww.Remove("abc", getCurrentTimeStamp());
    	Assert.assertFalse(lww.Exists("abc"));
    	
    	//Should return true as element is now added back to Set Z
    	TimeUnit.SECONDS.sleep(1);
    	lww.Add("abc", getCurrentTimeStamp());
    	Assert.assertTrue(lww.Exists("abc"));
	}
	
	@Test
	public void testGet() throws InterruptedException {
		LWWElement<Object,Timestamp> lww = new LWWElement<Object, Timestamp>();
    	lww.New();
    	
    	//Add elements to Set Z and should return the size of Set Z as 2
    	lww.Add("abc", getCurrentTimeStamp());
    	lww.Add("xyz", getCurrentTimeStamp());
    	Assert.assertEquals(2, lww.Get().length);
    	
    	// Remove one element and size should be 1
    	lww.Remove("abc", getCurrentTimeStamp());
    	Assert.assertEquals(1, lww.Get().length);
    	
    	//Add new elements to Set Z and should return the size of Set Z as 1
    	lww.Add("aaa", getCurrentTimeStamp());
    	Assert.assertEquals(2, lww.Get().length);
    	
    	
    	
    	
	}
}
