package net.yuan.nova.commons;

import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 邮箱发送
 * 
 * @author dengxiaoming
 *
 */
public class EmailSendUtils {

	protected final static Logger logger = LoggerFactory.getLogger(EmailSendUtils.class);

	private final static String MAIL_HOST_KEY = "mail.host";
	private final static String MAIL_FROM_KEY = "mail.from";
	private final static String MAIL_FROM_NAME_KEY = "mail.fromName";
	private final static String MAIL_USERNAME_KEY = "mail.username";
	private final static String MAIL_PASSWORD_KEY = "mail.password";

	private final static String charset = "UTF-8";

	/**
	 * @param to
	 *            发给谁
	 * @param subject
	 *            主题
	 * @param content
	 *            内容
	 * @throws EmailException
	 */
	public static void send(String to, String subject, String content) throws EmailException {
		send(to, subject, content, null);
	}

	/**
	 * @param to
	 *            发给谁
	 * @param subject
	 *            主题
	 * @param content
	 *            内容
	 * @param emailAttachments
	 *            附件
	 * @throws EmailException
	 */
	public static void send(String to, String subject, String content, List<EmailAttachment> emailAttachments)
			throws EmailException {
		if (to == null || to.length() == 0) {
			logger.warn("收件人不能为空");
			return;
		}
		String[] _to = new String[1];
		_to[0] = to;
		send(_to, subject, content, emailAttachments);
	}

	/**
	 * 多附件邮箱发送
	 * 
	 * @param to
	 *            发给哪些人
	 * @param subject
	 *            主题
	 * @param content
	 *            内容
	 * @throws EmailException
	 */
	public static void send(String[] to, String subject, String content) throws EmailException {
		send(to, subject, content, null);
	}

	/**
	 * 多附件邮箱发送给多人
	 * 
	 * @param to
	 *            发给哪些人
	 * @param subject
	 *            主题
	 * @param content
	 *            内容
	 * @throws EmailException
	 */
	public static void send(String[] to, String subject, String content, List<EmailAttachment> emailAttachments)
			throws EmailException {
		if (to == null || to.length == 0) {
			logger.warn("收件人不能为空");
			return;
		}
		HtmlEmail email = getHtmlEmail();
		// 添加附件
		if (emailAttachments != null) {
			for (EmailAttachment emailAttachment : emailAttachments) {
				email.attach(emailAttachment);
			}
		}
		// 添加收件人
		for (int i = 0; i < to.length; i++) {
			String _to = to[i];
			if (_to == null || _to.length() == 0)
				email.addTo(to[i]);
		}
		email.setSubject(subject);
		email.setHtmlMsg(content);
		email.send();
	}

	/**
	 * 发送邮件
	 * 
	 * @return
	 * @throws EmailException
	 */
	private static HtmlEmail getHtmlEmail() throws EmailException {

		// 如果需要代理则添加代理访问
		if (Boolean.parseBoolean(SystemConstant.getProperty("proxy.enable", "false"))) {
			logger.info("需要设置代理访问网络");
			String hostname = SystemConstant.getProperty("proxy.hostname");
			String portStr = SystemConstant.getProperty("proxy.port");
			if (StringUtils.isNotBlank(hostname) && StringUtils.isNotBlank(portStr)) {
				logger.info("当前使用的代理是：{}:{}", hostname, portStr);
				Properties props = System.getProperties();
				props.setProperty("http.proxySet", "true");
				props.setProperty("http.proxyHost", hostname);
				props.setProperty("http.proxyPort", portStr);
			} else {
				logger.warn("代理配置有误，请留意");
			}
		}

		String host = SystemConstant.getProperty(MAIL_HOST_KEY); // 主机名
		logger.debug("mail.host:{}", host);
		String from = SystemConstant.getProperty(MAIL_FROM_KEY); // 发件人
		logger.debug("mail.from:{}", from);
		String fromName = SystemConstant.getProperty(MAIL_FROM_NAME_KEY); // 发件人名称
		logger.debug("mail.fromName:{}", fromName);
		String username = SystemConstant.getProperty(MAIL_USERNAME_KEY); // 用户名
		logger.debug("mail.username:{}", username);
		String password = SystemConstant.getProperty(MAIL_PASSWORD_KEY); // 用户密码
		logger.debug("mail.password:{}", password);
		HtmlEmail email = new HtmlEmail();
		email.setHostName(host);
		email.setFrom(from, fromName);
		email.setAuthentication(username, password);
		email.setCharset(charset);
		return email;
	}
}