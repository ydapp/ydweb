package net.yuan.nova.core.shiro;

import net.yuan.nova.core.shiro.vo.User;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PasswordHelper {

	private RandomNumberGenerator randomNumberGenerator = new SecureRandomNumberGenerator();

	@Value("${password.algorithmName}")
	private String algorithmName = "md5";
	@Value("${password.hashIterations}")
	private int hashIterations = 2;

	public void setRandomNumberGenerator(RandomNumberGenerator randomNumberGenerator) {
		this.randomNumberGenerator = randomNumberGenerator;
	}

	public void setAlgorithmName(String algorithmName) {
		this.algorithmName = algorithmName;
	}

	public void setHashIterations(int hashIterations) {
		this.hashIterations = hashIterations;
	}

	/**
	 * 对指定的用户分配加密盐并对密码进行加密
	 * 
	 * @param user
	 */
	public User encryptPassword(User user) {
		user.setSalt(randomNumberGenerator.nextBytes().toHex());
		String newPassword = encryptPassword(user.getPassword(), user.getSalt(), hashIterations);
		user.setPassword(newPassword);
		return user;
	}

	/**
	 * 加密
	 * 
	 * @param password
	 *            原始密码
	 * @param salt
	 *            盐
	 * @return
	 */
	public String encryptPassword(String password, String salt) {
		return encryptPassword(password, salt, hashIterations);
	}

	private String encryptPassword(String password, String salt, int hashIterations) {
		if (hashIterations <= 0) {
			hashIterations = this.hashIterations;
		}
		if (StringUtils.isBlank(salt)) {
			return new SimpleHash(algorithmName, password, null, hashIterations).toHex();
		}
		return new SimpleHash(algorithmName, password, ByteSource.Util.bytes(salt), hashIterations).toHex();
	}

}
