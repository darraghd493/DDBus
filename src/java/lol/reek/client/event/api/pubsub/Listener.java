package lol.reek.client.event.api.pubsub;

@FunctionalInterface
public interface Listener<Event> {
    void call(final Event event);
}
