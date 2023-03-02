package pro.dd493.ddbus.api.pubsub.bus;

public interface Bus<Event> {
    void register(final Object listener);
    void unregister(final Object listener);
    void post(final Event event);
}
