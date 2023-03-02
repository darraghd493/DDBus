# DDBus
A basic event bus made by [darraghd493](https://github.com/darraghd493).

## Usage
```java
// Create a new bus
EventBus<type> bus = new EventBus<>();

// Create a listener
@EventTarget(priority = EventPriority.NORMAL) // Set normal by default.
function onEvent(type event) {
    // Do something
}

// Register a listener
bus.register(this);

// Post an event
bus.post(new type());

// Unregister a listener
bus.unregister(this);

// Unregister all listeners
bus.unregisterAll();
```
