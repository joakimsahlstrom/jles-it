package se.jsa.jles.it;

import se.jsa.jles.internal.util.Objects;

public class AddShoppingListItemEvent {

	private final Long shoppingListId;
	private final Long itemId;
	private final Long timestamp;
	private final String signature;

	public AddShoppingListItemEvent(Long shoppingListId, Long itemId, Long timestamp, String signature) {
		this.shoppingListId = Objects.requireNonNull(shoppingListId);
		this.itemId = Objects.requireNonNull(itemId);
		this.timestamp = Objects.requireNonNull(timestamp);
		this.signature = Objects.requireNonNull(signature);
	}

	public Long getShoppingListId() {
		return shoppingListId;
	}

	public Long getItemId() {
		return itemId;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public String getSignature() {
		return signature;
	}

	public SerializableEventV1 asSerializable() {
		return new SerializableEventV1(this);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof AddShoppingListItemEvent)) {
			return false;
		}
		AddShoppingListItemEvent other = (AddShoppingListItemEvent)obj;
		return shoppingListId.equals(other.shoppingListId)
				&& itemId.equals(other.itemId)
				&& timestamp.equals(other.timestamp)
				&& signature.equals(other.signature);
	}

	@Override
	public int hashCode() {
		return ((shoppingListId.hashCode() * 31 + itemId.hashCode()) * 31 + timestamp.hashCode()) * 31 + signature.hashCode();
	}

	public static class SerializableEventV1 {
		private Long shoppingListId;
		private Long itemId;
		private Long timestamp;
		private String signature;

		public SerializableEventV1() {
		}

		public SerializableEventV1(AddShoppingListItemEvent source) {
			this.shoppingListId = source.getShoppingListId();
			this.itemId = source.getItemId();
			this.timestamp = source.getTimestamp();
			this.signature = source.getSignature();
		}

		public Long getShoppingListId() {
			return shoppingListId;
		}

		public void setShoppingListId(Long shoppingListId) {
			this.shoppingListId = shoppingListId;
		}

		public Long getItemId() {
			return itemId;
		}

		public void setItemId(Long itemId) {
			this.itemId = itemId;
		}

		public Long getTimestamp() {
			return timestamp;
		}

		public void setTimestamp(Long timestamp) {
			this.timestamp = timestamp;
		}

		public String getSignature() {
			return signature;
		}

		public void setSignature(String signature) {
			this.signature = signature;
		}

		public AddShoppingListItemEvent asEvent() {
			return new AddShoppingListItemEvent(shoppingListId, itemId, timestamp, signature);
		}
	}

}
