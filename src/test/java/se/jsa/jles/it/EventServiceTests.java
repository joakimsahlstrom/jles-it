package se.jsa.jles.it;

import static se.jsa.jles.it.AsyncAssert.assertEqualsEventually;

import java.util.LinkedList;

import org.junit.Test;

import se.jsa.jles.EventQuery2;
import se.jsa.jles.EventStore;
import se.jsa.jles.EventStoreConfigurer;
import se.jsa.jles.eh.EventFeedReader;
import se.jsa.jles.eh.EventService;
import se.jsa.jles.it.AsyncAssert.ValueRetriever;

public class EventServiceTests {

	static class MyEventFeedReader implements EventFeedReader {
		private final LinkedList<Object> eventQueue = new LinkedList<Object>();
		final Class<?> eventType;

		public MyEventFeedReader(Class<?> eventType) {
			this.eventType = eventType;
		}

		@Override
		public void onNewEvent(Object event) {
			this.eventQueue.offer(event);
		}

		public Object getEvent() {
			return eventQueue.isEmpty() ? null : eventQueue.poll();
		}

		public LinkedList<Object> getEvents() {
			return eventQueue;
		}

		public Integer getNumEvents() {
			return eventQueue.size();
		}
	}

	private final EventStore eventStore = EventStoreConfigurer.createMemoryOnlyConfigurer().testableEventDefinitions().configure();
	private final EventService eventService = new EventService(eventStore);

	@Test
	public void receivesMatchingEvents() throws Exception {
		final MyEventFeedReader subscription = new MyEventFeedReader(AddShoppingListItemEvent.class);

		final AddShoppingListItemEvent expectedEvent = createEvent(1);
		eventStore.write(expectedEvent);
		eventService.register(subscription, EventQuery2.select(AddShoppingListItemEvent.class));

		assertEqualsEventually(expectedEvent, getNextReceivedEvent(subscription), 100);
	}

	@Test
	public void receivesMatchingEventsAddedLater() throws Exception {
		final MyEventFeedReader subscription = new MyEventFeedReader(AddShoppingListItemEvent.class);

		final AddShoppingListItemEvent expectedEvent1 = createEvent(1);
		final AddShoppingListItemEvent expectedEvent2 = createEvent(1);
		eventStore.write(expectedEvent1);
		eventService.register(subscription, EventQuery2.select(AddShoppingListItemEvent.class).where("ShoppingListId").is(1L));
		eventStore.write(expectedEvent2);

		assertEqualsEventually(expectedEvent1, getNextReceivedEvent(subscription), 100);
		assertEqualsEventually(expectedEvent2, getNextReceivedEvent(subscription), 100);
	}

	@Test
	public void receivesOnlyMatchingEvents() throws Exception {
		final MyEventFeedReader subscription = new MyEventFeedReader(AddShoppingListItemEvent.class);

		final AddShoppingListItemEvent expectedEvent1 = createEvent(1);
		final AddShoppingListItemEvent unExpectedEvent = createEvent(2);
		final AddShoppingListItemEvent expectedEvent2 = createEvent(1);
		eventStore.write(expectedEvent1);
		eventService.register(subscription, EventQuery2.select(AddShoppingListItemEvent.class).where("ShoppingListId").is(1L));
		eventStore.write(unExpectedEvent);
		eventStore.write(expectedEvent2);

		assertEqualsEventually(expectedEvent1, getNextReceivedEvent(subscription), 100);
		assertEqualsEventually(expectedEvent2, getNextReceivedEvent(subscription), 100);
	}

	@Test
	public void canUseComplexQueries() throws Exception {
		final MyEventFeedReader subscription = new MyEventFeedReader(AddShoppingListItemEvent.class);

		final AddShoppingListItemEvent expectedEvent1 = createEvent(2);
		final AddShoppingListItemEvent unExpectedEvent = createEvent(3);
		final AddShoppingListItemEvent expectedEvent2 = createEvent(1);
		eventStore.write(expectedEvent1);
		eventService.register(subscription, EventQuery2.select(AddShoppingListItemEvent.class).where("ShoppingListId").in(1L, 2L));
		eventStore.write(unExpectedEvent);
		eventStore.write(expectedEvent2);

		assertEqualsEventually(expectedEvent1, getNextReceivedEvent(subscription), 100000);
		assertEqualsEventually(expectedEvent2, getNextReceivedEvent(subscription), 100000);
	}

	private ValueRetriever getNextReceivedEvent(final MyEventFeedReader subscription) {
		return new ValueRetriever() { @Override public Object get() { return subscription.getEvent(); } };
	}

	private long itemId = 0;
	private AddShoppingListItemEvent createEvent(long listId) {
		return new AddShoppingListItemEvent(listId, itemId++, System.currentTimeMillis(), "sign");
	}

}
