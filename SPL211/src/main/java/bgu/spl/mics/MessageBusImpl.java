package bgu.spl.mics;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

	//each key represented by different microservice and the value is a queue of the messages that this microservice can handle with.
	private ConcurrentHashMap<MicroService, LinkedBlockingQueue<Message>> registerQ;
	//each key represented by the class of an event or broadcast and the value is a queue of the microservices that subscribed to it.
	private ConcurrentHashMap<Class<? extends Message>, ConcurrentLinkedQueue<MicroService>> typeQ;
	// each key represented by event and the value is the Future object that bond to it.
	private ConcurrentHashMap<Event, Future> futureQ;


	private static class mbHolder{
		private static MessageBusImpl instance= new MessageBusImpl();
	}

	private MessageBusImpl(){
		this.registerQ=new ConcurrentHashMap<>();
		this.typeQ =new ConcurrentHashMap<>();
		this.futureQ=new ConcurrentHashMap<>();
	}

	public static MessageBusImpl getInstance(){
		return mbHolder.instance;
	}


	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
			typeQ.putIfAbsent(type, new ConcurrentLinkedQueue<>());
			typeQ.get(type).add(m);
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
			typeQ.putIfAbsent(type, new ConcurrentLinkedQueue<>());
			typeQ.get(type).add(m);
    }

	@Override @SuppressWarnings("unchecked")
	public <T> void complete(Event<T> e, T result) {
		if (futureQ.get(e)!=null)
			futureQ.get(e).resolve(result);
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		synchronized (typeQ) {
			if (typeQ.get(b.getClass()) != null) {
				for (MicroService m : typeQ.get(b.getClass()))
					registerQ.get(m).add(b);
			}
		}
	}

	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		synchronized (typeQ) {
			if (e != null && typeQ.containsKey(e.getClass()) && !typeQ.get(e.getClass()).isEmpty()) {
				Future<T> future = new Future<>();
				MicroService m = typeQ.get(e.getClass()).poll();
				futureQ.putIfAbsent(e, future);
				registerQ.get(m).add(e);
				// implements "round-robin" by polling the microservice from the queue and add it to the end of the queue.
				typeQ.get(e.getClass()).add(m);
				return future;
			}
		}
		// when there is no microservice that subscribe to this event.
        return null;
	}

	@Override
	public void register(MicroService m) {
		registerQ.putIfAbsent(m, new LinkedBlockingQueue<>());
	}

	@Override
	public void unregister(MicroService m) {
		synchronized (typeQ) {
			for (Map.Entry<Class<? extends Message>, ConcurrentLinkedQueue<MicroService>> entry : typeQ.entrySet()) {
				if (entry.getValue().contains(m))
					entry.getValue().remove(m);
			}
		}
		registerQ.remove(m);
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		Message message = null;
		try {
			message = registerQ.get(m).take();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
		return message;
	}
}
