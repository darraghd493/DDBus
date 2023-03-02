package pro.dd493.ddbus.api.pubsub;

@FunctionalInterface
public interface Listener<Event> {
    void call(final Event event);
}
