package pro.dd493.ddbus.bus;

import pro.dd493.ddbus.api.pubsub.bus.Bus;
import lombok.Getter;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;

public class EventBus<Event> implements Bus<Event> {
    protected final Map<Type, List<CallSite<Event>>> callSites;

    public EventBus() {
        callSites = new HashMap<>();
    }

    @Override
    public void register(final Object subscriber) {
        for (final Method method : subscriber.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(EventTarget.class)) {
                final Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length != 1)
                    throw new IllegalArgumentException("Method " + method.getName() + " has @EventTarget annotation, but requires " + parameterTypes.length + " arguments.  Event handler methods must require a single argument.");

                final Type eventType = parameterTypes[0];
                final byte priority = method.getAnnotation(EventTarget.class).priority();

                final CallSite<Event> callSite = new CallSite<>(subscriber, method, priority);
                List<CallSite<Event>> sites = callSites.get(eventType);

                if (sites == null) {
                    sites = new ArrayList<>();
                    callSites.put(eventType, sites);
                }

                sites.add(callSite);
                sites.sort(Comparator.comparingInt(site1 -> site1.getPriority()));
            }
        }
    }

    @Override
    public void unregister(final Object subscriber) {
        for (final List<CallSite<Event>> sites : callSites.values()) {
            final Iterator<CallSite<Event>> iterator = sites.iterator();
            while (iterator.hasNext()) {
                final CallSite<Event> site = iterator.next();
                if (site.getSource() == subscriber) iterator.remove();
            }
        }
    }

    @Override
    public void post(final Event event) {
        final List<CallSite<Event>> sites = callSites.get(event.getClass());
        if (sites != null) {
            final List<CallSite<Event>> sitesCopy = new ArrayList<>(sites);
            try {
                for (final CallSite<Event> site : sitesCopy) site.invoke(event);
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Thread getThread() {
        return Thread.currentThread();
    }

    public static class CallSite<E> {
        @Getter
        protected final Object source;
        @Getter protected final Method target;
        @Getter protected final byte priority;

        public CallSite(final Object owner, final Method target, final byte priority) {
            this.source = owner;
            this.target = target;
            this.priority = priority;
        }

        public void invoke(final E event) {
            try {
                target.invoke(source, event);
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }
}
