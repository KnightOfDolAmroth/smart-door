package consegna_4.seiot.common;

public abstract class ReactiveAgent extends BasicEventLoopController {
	
	protected ReactiveAgent(int size){
		super(size);
	}

	protected ReactiveAgent(){
	}
	
	protected boolean sendMsgTo(ReactiveAgent agent, Msg m){
		MsgEvent ev = new MsgEvent(m,this);
		return agent.notifyEvent(ev);
	}
}
