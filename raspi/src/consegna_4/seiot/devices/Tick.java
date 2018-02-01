package consegna_4.seiot.devices;

import consegna_4.seiot.common.Event;

public class Tick implements Event {
	
	private long time;
	
	public Tick(long time ){
		this.time = time;
	}
	
	public long getTime(){
		return time;
	}
}
