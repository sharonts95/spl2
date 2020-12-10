package bgu.spl.mics;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

	private ConcurrentHashMap<MicroService, LinkedBlockingQueue<Message>> registerQ;// register Q of Microservices
	private ConcurrentHashMap<Class<? extends Message>, ConcurrentLinkedQueue<MicroService>> typeQ; //
	private ConcurrentHashMap<Event, Future> futureQ;


	private static class mbHolder{
		private static MessageBusImpl instance= new MessageBusImpl();
	}

	private MessageBusImpl(){
		this.registerQ=new ConcurrentHashMap<>();
		this.typeQ=new ConcurrentHashMap<>();
		this.futureQ=new ConcurrentHashMap<>();
	}

	public static MessageBusImpl getInstance(){
		return mbHolder.instance;
	}


	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		typeQ.putIfAbsent(type, new ConcurrentLinkedQueue<MicroService>());
		typeQ.get(type).add(m);
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		typeQ.putIfAbsent(type, new ConcurrentLinkedQueue<MicroService>());
		typeQ.get(type).add(m);
    }

	@Override @SuppressWarnings("unchecked")
	public <T> void complete(Event<T> e, T result) {
		if (futureQ.get(e)!=null)
			futureQ.get(e).resolve(result);
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		if(typeQ.get(b.getClass())!=null) {
			for (MicroService m : typeQ.get(b.getClass()))
				registerQ.get(m).add(b);
		}
	}

	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		if(e!=null && typeQ.containsKey(e.getClass()) && !typeQ.get(e.getClass()).isEmpty()){
			synchronized (typeQ){
				Future<T> future = new Future<>();
				MicroService m=typeQ.get(e.getClass()).poll();
				registerQ.get(m).add(e);
				typeQ.get(e.getClass()).add(m);
				futureQ.putIfAbsent(e, future);
				return future;
			}
		}
        return null;
	}

	@Override
	public void register(MicroService m) {
		registerQ.putIfAbsent(m, new LinkedBlockingQueue<Message>());
	}

	@Override
	public void unregister(MicroService m) {
		synchronized (m){
			registerQ.remove(m);
			for(Map.Entry<Class<? extends Message>, ConcurrentLinkedQueue<MicroService>> entry : typeQ.entrySet()){
				if(entry.getValue().contains(m))
					entry.getValue().remove(m);
			}
		}
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		if (registerQ.containsKey(m))
			return registerQ.get(m).take();
		return null;
	}
}
