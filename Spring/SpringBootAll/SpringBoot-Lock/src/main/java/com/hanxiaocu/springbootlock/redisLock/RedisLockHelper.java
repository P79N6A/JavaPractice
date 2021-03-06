package com.hanxiaocu.springbootlock.redisLock;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.util.StringUtils;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * @desc:
 * @author: hanchenghai
 * @date: 2018/11/20 4:19 PM
 */
@Configuration
@AutoConfigureAfter(RedisAutoConfiguration.class)
public class RedisLockHelper {

	private static final String DELIMITER = ":";

	//线程池
	//org.apache.commons.lang3.concurrent.BasicThreadFactory
	private static final ScheduledExecutorService EXECUTOR_SERVICE = new ScheduledThreadPoolExecutor(10,
			new BasicThreadFactory.Builder().namingPattern("schedule-pool-%d").daemon(true).build());

	private final StringRedisTemplate stringRedisTemplate;

	public RedisLockHelper(StringRedisTemplate stringRedisTemplate) {
		this.stringRedisTemplate = stringRedisTemplate;
	}

	/**
	 * 获取锁 存在死锁风险
	 *
	 * @param lockKey
	 * @param value
	 * @param time
	 * @param unit
	 * @return
	 */
	public boolean tryLock(final String lockKey, final String value, final long time, final TimeUnit unit) {
		return stringRedisTemplate.execute((RedisCallback<Boolean>) connection -> {
			return connection.set(lockKey.getBytes(), value.getBytes(), Expiration.from(time, unit), RedisStringCommands.SetOption.SET_IF_ABSENT);
		});
	}

	/**
	 * 获取锁
	 *
	 * @param lockKey lockKey
	 * @param uuid    UUID
	 * @param timeout 超时时间
	 * @param unit    过期单位
	 * @return true or false
	 */
	public boolean lock(String lockKey, final String uuid, long timeout, final TimeUnit unit) {
		final long milliseconds = Expiration.from(timeout, unit).getExpirationTimeInMilliseconds();
		boolean success = stringRedisTemplate.opsForValue().setIfAbsent(lockKey, (System.currentTimeMillis() + milliseconds) + DELIMITER + uuid);
		if (success) {//当原来没有key 的时候，会设置成功
			stringRedisTemplate.expire(lockKey, timeout, TimeUnit.SECONDS);
		} else {
			String oldVal = stringRedisTemplate.opsForValue().getAndSet(lockKey, (System.currentTimeMillis() + milliseconds) + DELIMITER + uuid);
			final String[] oldValues = oldVal.split(Pattern.quote(DELIMITER));
			//老的已经过期了
			if (Long.parseLong(oldValues[0]) + 1 <= System.currentTimeMillis()) {
				return true;
			}
		}
		return success;
	}

	/**
	 * @see <a href="http://redis.io/commands/set">Redis Documentation: SET</a>
	 */
	public void unlock(String lockKey, String value) {
		unlock(lockKey, value, 0, TimeUnit.MILLISECONDS);
	}

	/**
	 * 延迟unlock
	 *
	 * @param lockKey   key
	 * @param uuid      client(最好是唯一键的)
	 * @param delayTime 延迟时间
	 * @param unit      时间单位
	 */
	public void unlock(final String lockKey, final String uuid, long delayTime, TimeUnit unit) {
		if (StringUtils.isEmpty(lockKey)) {
			return;
		}
		if (delayTime <= 0) {
			doUnlock(lockKey, uuid);
		} else {
			//加入定时任务去解锁
			EXECUTOR_SERVICE.schedule(() -> doUnlock(lockKey, uuid), delayTime, unit);
		}
	}

	/**
	 * @param lockKey key
	 * @param uuid    client(最好是唯一键的)
	 */
	private void doUnlock(final String lockKey, final String uuid) {
		String val = stringRedisTemplate.opsForValue().get(lockKey);
		final String[] values = val.split(Pattern.quote(DELIMITER));
		if (values.length <= 0) {
			return;
		}
		//只有自己解锁自己
		if (uuid.equals(values[1])) {
			stringRedisTemplate.delete(lockKey);
		}
	}
}
