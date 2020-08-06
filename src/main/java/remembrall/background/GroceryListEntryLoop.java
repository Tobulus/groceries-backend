package remembrall.background;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import remembrall.model.RedisKeys;
import remembrall.service.PushService;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class GroceryListEntryLoop {

    @Autowired
    private StringRedisTemplate redis;

    @Autowired
    private PushService pushService;

    @Scheduled(fixedDelayString = "PT1M")
    public void pushNewEntries() {
        long endTimestamp = System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(5);
        long startTimestamp = System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(30);
        Set<String> keys =
                redis.opsForZSet().rangeByScore(RedisKeys.PUSH_NEW_ENTRY, startTimestamp, endTimestamp, 0, 100);

        if (keys != null && !keys.isEmpty()) {
            keys.forEach(key -> {
                String[] userAndList = key.split("-");
                Long groceryListId = Long.valueOf(userAndList[0]);
                Long userId = Long.valueOf(userAndList[1]);

                pushService.sendEntryPush(userId, groceryListId);

                redis.opsForZSet().remove(RedisKeys.PUSH_NEW_ENTRY, key);
            });
        }
    }
}
