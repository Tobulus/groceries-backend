package remembrall.model;

public class RedisKeys {
    // sorted set storing keys in form '$listId-$userId' in order to remember pushes
    public static final String PUSH_NEW_ENTRY = "PUSH_NEW_ENTRY";
    // hash map storing for every user when he last opened a list in form '$listId-$user-id' => timestamp
    public static final String LAST_SEEN = "LAST_SEEN";
}
