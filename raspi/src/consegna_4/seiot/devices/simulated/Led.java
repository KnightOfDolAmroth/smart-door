package consegna_4.seiot.devices.simulated;

import java.io.IOException;

import consegna_4.seiot.devices.Light;


public class Led implements Light {
	//private final int pinNum;
	private final String id;

	public Led(int pinNum, String id){
		//this.pinNum = pinNum;
		this.id = id;
		try {
			System.out.println("[LED "+id+"] installed on pin "+pinNum);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Led(int pinNum){
		this(pinNum,"green");
	}	
	
	@Override
	public synchronized void switchOn() throws IOException {
		System.out.println("[LED "+id+"] ON");
	}

	@Override
	public synchronized void switchOff() throws IOException {
		System.out.println("[LED "+id+"] OFF");
	}
		
}
