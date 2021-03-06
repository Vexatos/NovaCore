package nova.core.event;

import org.junit.Test;

import static nova.testutils.NovaAssertions.assertThat;

/**
 * @author Stan Hebben
 */
public class EventBusTest {
	@Test
	public void testEmpty() {
		EventBus<TestEvent> listenerList = new EventBus<>();
		TestEvent event = new TestEvent();
		listenerList.publish(event);

		assertThat(event.toString()).isEqualTo("");
	}

	@Test
	public void testInvocation() {
		EventBus<TestEvent> listenerList = new EventBus<>();
		listenerList.on().bind(new TestEventListener("A"));
		listenerList.on().bind(new TestEventListener("B"));

		TestEvent event = new TestEvent();
		listenerList.publish(event);

		assertThat(event.toString()).isEqualTo("AB");
	}

	@Test
	public void testOrdering() {
		EventBus<TestEvent> listenerList = new EventBus<>();
		listenerList.on().with(1).bind(new TestEventListener("A"));
		listenerList.on().with(1).bind(new TestEventListener("B"));
		listenerList.on().with(2).bind(new TestEventListener("C"));

		TestEvent event = new TestEvent();
		listenerList.publish(event);

		assertThat(event.toString()).isEqualTo("CAB");
	}

	@Test
	public void testRemovalByHandle() {
		EventBus<TestEvent> listenerList = new EventBus<>();
		listenerList.on().bind(new TestEventListener("A"));
		EventListenerHandle<TestEvent> handle = listenerList.on().bind(new TestEventListener("B"));
		handle.close();

		TestEvent event = new TestEvent();
		listenerList.publish(event);

		assertThat(event.toString()).isEqualTo("A");
	}

	@Test
	public void testRemovalByObject() {
		EventBus<TestEvent> listenerList = new EventBus<>();
		listenerList.on().bind(new TestEventListener("A"));

		TestEventListener listener = new TestEventListener("B");
		listenerList.on().bind(listener);
		listenerList.remove(listener);

		TestEvent event = new TestEvent();
		listenerList.publish(event);

		assertThat(event.toString()).isEqualTo("A");
	}
}
