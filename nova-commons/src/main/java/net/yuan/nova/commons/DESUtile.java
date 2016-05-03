package net.yuan.nova.commons;

import java.io.UnsupportedEncodingException;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

public class DESUtile {

	// 向量
	private static byte[] iv1;
	
	static {
		try {
			iv1 = "12345678".getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	// 算法名称
	public static final String KEY_ALGORITHM = "DES";
	/**
	 * 加密/解密算法/工作模式/填充方式
	 * */
	public static final String CIPHER_ALGORITHM = "DES/CBC/PKCS5Padding"; // "DES/ECB/NoPadding";

	/**
	 * 密钥
	 * 
	 * @return
	 * @throws Exception
	 */
	public static byte[] initkey(String key) throws Exception {
		return key.getBytes();
	}

	/**
	 * 加密
	 * 
	 * @param data
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static byte[] encrypt(byte[] data,String key) throws Exception {
		// 密钥
		Key k = toKey(initkey(key));
		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
		// 用密匙初始化Cipher对象
		IvParameterSpec param = new IvParameterSpec(iv1);
		// 初始化，设置为解密模式
		cipher.init(Cipher.ENCRYPT_MODE, k, param);

		return cipher.doFinal(data);
	}

	/**
	 * 解密
	 * 
	 * @param data
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static byte[] decrypt(byte[] data,String key) throws Exception {
		// 欢迎密钥
		Key k = toKey(initkey(key));
		// 实例化
		// Cipher对象实际完成加密操作
		// Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		// 若采用NoPadding模式，data长度必须是8的倍数
		// Cipher cipher = Cipher.getInstance("DES/CBC/NoPadding");
		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);

		// 用密匙初始化Cipher对象
		IvParameterSpec param = new IvParameterSpec(iv1);
		// 初始化，设置为解密模式
		cipher.init(Cipher.DECRYPT_MODE, k, param);
		// 执行操作
		return cipher.doFinal(data);
	}

	/**
	 * 转换密钥
	 * 
	 * @param key
	 *            二进制密钥
	 * @return Key 密钥
	 * */
	private static Key toKey(byte[] key) throws Exception {
		// 实例化Des密钥
		DESKeySpec dks = new DESKeySpec(key);
		// 实例化密钥工厂
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(KEY_ALGORITHM);
		// 生成密钥
		SecretKey secretKey = keyFactory.generateSecret(dks);
		return secretKey;
	}

}
