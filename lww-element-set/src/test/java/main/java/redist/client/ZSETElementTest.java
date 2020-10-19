package main.java.redist.client;

import java.sql.Timestamp;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Test;

public class ZSETElementTest {
	public static Timestamp getCurrentTimeStamp() {
		return new Timestamp(System.currentTimeMillis());
	}
	
	@Test
	public void testNew() {
		ZSETElement<String, Timestamp, Integer> zset = new ZSETElement<String, Timestamp, Integer>();
    	zset.New();
    	Assert.assertNotNull(zset.ZR);
    	Assert.assertNotNull(zset.ZA);
	}

	@Test
	public void testAdd() throws InterruptedException {
		ZSETElement<String,Timestamp, Integer> zset = new ZSETElement<String, Timestamp, Integer>();
    	zset.New();
    	
    	//Add elements to Set Z
    	zset.Add("abc",1);
    	zset.Add("xyz",2);
    	Timestamp oldTS = zset.ZA.get("abc");
    	
    	TimeUnit.SECONDS.sleep(1);
    	
    	//check the size of set Z
    	Assert.assertEquals(2, zset.ZA.size());
    	
    	//Add an existing element and size should remain same
    	zset.Add("abc",2);
    	Assert.assertEquals(2, zset.ZA.size());
    	
    	//Check if timestamp isupdated
    	Timestamp newTS = zset.ZA.get("abc");	
    	Assert.assertTrue(((newTS.compareTo(oldTS)) > 0));
    	
    	//Check if the score is updated as new value
    	int score = zset.ZS.get("abc");
    	Assert.assertEquals(2, score);
	}
	
	@Test
	public void testRemove() throws InterruptedException {
		ZSETElement<String, Timestamp, Integer> zset = new ZSETElement<String, Timestamp, Integer>();
    	zset.New();
    	
    	//Add elements to Set Z
    	zset.Add("abc",1);
    	zset.Add("xyz",1);
    	zset.Remove("abc");
    	
    	//The entry in Set ZR should be created
    	Assert.assertNotNull(zset.ZR.get("abc"));
    	Timestamp oldTS = zset.ZR.get("abc");
    	    	
    	TimeUnit.SECONDS.sleep(1);
    	
    	//Delete an element again should the timestamp in Set ZR
    	zset.Remove("abc");
    	Timestamp newTS = zset.ZR.get("abc");
    	
    	//check if timestamp is udpdate
    	Assert.assertTrue(((newTS.compareTo(oldTS)) > 0));
    	
    	// Check if element entry is removed from ZS
    	Assert.assertFalse(zset.ZS.containsKey("abc"));
	}
	
	@Test
	public void testExist() throws InterruptedException {
		ZSETElement<String,Timestamp, Integer> zset = new ZSETElement<String, Timestamp, Integer>();
    	zset.New();
    	
    	//should return false as Set ZA is empty
    	Assert.assertFalse(zset.Exists("abc"));
    	
    	//Should return true as element "abc" is added
    	zset.Add("abc",1);
    	Assert.assertTrue(zset.Exists("abc"));
    	
    	TimeUnit.SECONDS.sleep(1);
    	
    	//Should return false as element is now removed
    	zset.Remove("abc");
    	Assert.assertFalse(zset.Exists("abc"));
    	
    	//Should return true as element is now added back to Set Z
    	TimeUnit.SECONDS.sleep(1);
    	zset.Add("abc",2);
    	Assert.assertTrue(zset.Exists("abc"));
	}
	
	@Test
	public void testGet() throws InterruptedException {
		ZSETElement<String,Timestamp, Integer> zset = new ZSETElement<String, Timestamp, Integer>();
    	zset.New();
    	
    	//Add elements to Set Z and should return the size of Set Z as 2
    	zset.Add("abc", 1);
    	zset.Add("xyz", 2);
    	Assert.assertEquals(2, zset.Get().length);
    	
    	// Remove one element and size should be 1
    	zset.Remove("abc");
    	Assert.assertEquals(1, zset.Get().length);
    	
    	//Add new elements to Set Z and should return the size of Set Z as 1
    	zset.Add("aaa",5);
    	Assert.assertEquals(2, zset.Get().length);    	
	}

}
