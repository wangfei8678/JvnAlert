/**
 * 
 */
package com.jvn.wf;

/**
 * @author wang_fei_gn
 *
 */
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;

/**
 * プログラムがjavaでメール送信機能を実現します。使用するプロトコールはSMTP、ポート：25
 * ソケットで実現して、クライアントのソケットを開き、サーバに繋がる
 */
public class Email {
	/**
	 * MIMEメールインステンス
	 */
	private MimeMessage mimeMsg;
	/**
	 * メールを送信する用のSession
	 */
	private Session session;
	/**
	 * メールを送信する時の配置情報を記録する用のPropertiesインステンス
	 */
	private Properties props;
	/**
	 * 送信者のメールアドレス
	 */
	private String username;
	/**
	 * 送信者のパスワード
	 */
	private String password;
	/**
	 * 添付ファイルを実現するように
	 */
	private Multipart mp;
	/**
	 *  
	 */
	private int port = 465;
	/**
	 * パラメータの初期化
	 * @param smtp
	 * SMTPサーバのアドレス
	 */
	public Email(String smtp) {
		username = "";
		password = "";
		//メールサーバを設定する
		setSmtpHost(smtp);
		//メールを作る
		createMimeMessage();
	}
	/**
	 * メールを送信するホストを設置する
	 * @param hostName
	 */
	public void setSmtpHost(String hostName) {
		System.out.println("システムのプロパティを設置：smtp.gmail.com = " + hostName);
		if (props == null)
			props = System.getProperties();
		props.put("mail.smtp.host", hostName);
	}

	/**
	 * (这个Session类代表JavaMail 中的一个邮件session. 每一个基于
	 * JavaMail的应用程序至少有一个session但是可以有任意多的session。 在这个例子中,
	 * Session对象需要知道用来处理邮件的SMTP 服务器。
	 */
	public boolean createMimeMessage() {
		try {
			System.out.println("sessionインステンスを作る！");
			//Propertiesインステンスを通じて、sessionインステンスを作ると初期化
			session = Session.getDefaultInstance(props, null);
		} catch (Exception e) {
			System.err.println("sessionインステンスを作る時エラーが発生した！" + e);
			return false;
		}
//		System.out.println("MIMEメールインステンスを作る！");
		try {
			// sessionでMIMEメールインステンスを作る
			mimeMsg = new MimeMessage(session);
			mp = new MimeMultipart();
		} catch (Exception e) {
			System.err.println("MIMEメールインステンスを作る失敗！" + e);
			return false;
		}
		return true;
	}

	/**
	 * SMTPユーザのチェックを設置する
	 */
	public void setNeedAuth(boolean need) {
		System.out.println("mail.smtp.auth = " + need);
		if (props == null) {
			props = System.getProperties();
			props.put("mail.transport.protocol", "smtp");
	    	props.put("mail.smtp.port", port); 
		}
		if (need){
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.starttls.required", "true");
		}else
			props.put("mail.smtp.auth", "false");
	}

	/**
	 * ユーザーをチェックする時、ユーザー名とパスワードを設置する
	 */
	public void setNamePass(String name, String pass) {
		System.out.println("ユーザー名とパスワードを取得する");
		username = name;
		password = pass;
	}

	/**
	 * メールのタイトルを設置
	 * @param mailSubject
	 * @return
	 */
	public boolean setSubject(String mailSubject) {
		System.out.println("メールのタイトルを設置！");
		try {
			mimeMsg.setSubject(mailSubject);
		} catch (Exception e) {
			System.err.println("メールのタイトルを設置する時エラーが発生した！");
			return false;
		}
		return true;
	}

	/**
	 * メールの内容を設置する
	 * タイプがテキストまたHTMLを設置する、コートタイプがUTFー8を設置
	 * @param mailBody
	 * @return
	 */
	public boolean setBody(String mailBody) {
		try {
			System.out.println("メール内容のタイプを設置");
			BodyPart bp = new MimeBodyPart();
			bp.setContent(
					"<meta http-equiv=Content-Type content=text/html; charset=UTF-8>"
							+ mailBody, "text/html;charset=UTF-8");
			mp.addBodyPart(bp);
		} catch (Exception e) {
			System.err.println("メールの内容を設置する時エラーが発生した！" + e);
			return false;
		}
		return true;
	}

	/**
	 * ファイルを添付する
	 * @param filename
	 * ファイルのアドレス、ローカルアドレスだけ 
	 * @return
	 */
	public boolean addFileAffix(String filename) {
		System.out.println("ファイルを添付：" + filename);
		try {
			BodyPart bp = new MimeBodyPart();
			FileDataSource fileds = new FileDataSource(filename);
			bp.setDataHandler(new DataHandler(fileds));
			//添付するファイルの前にユーザー名を加える
			bp.setFileName(fileds.getName());
			// ファイルを添付
			mp.addBodyPart(bp);
		} catch (Exception e) {
			System.err.println("ファイルを添付する：" + filename + "发生错误！" + e);
			return false;
		}
		return true;
	}

	/**
	 * 送信者のメールアドレスを設置
	 * @param from
	 * 送信者のメールアドレス
	 * @return
	 */
	public boolean setFrom(String from) {
		System.out.println("送信者を設置！");
		try {
			mimeMsg.setFrom(new InternetAddress(from));
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * 受信者のメールアドレスを設置
	 * @param to
	 *　受信者のメールアドレス
	 * @return
	 */
	public boolean setTo(String to) {
		System.out.println("受信者を設置");
		if (to == null)
			return false;
		try {
			mimeMsg.setRecipients(javax.mail.Message.RecipientType.TO,
					InternetAddress.parse(to));
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * 添付ファイルを送信する
	 * @param copyto
	 * @return
	 */
	public boolean setCopyTo(String copyto) {
		System.out.println("添付ファイルを送信する");
		if (copyto == null)
			return false;
		try {
			mimeMsg.setRecipients(javax.mail.Message.RecipientType.CC,
					InternetAddress.parse(copyto));
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * メールを送信する
	 * @return
	 */
	public boolean sendout() {
		try {
			mimeMsg.setContent(mp);
			mimeMsg.saveChanges();
			System.out.println("メールを送信中....");
			Session mailSession = Session.getInstance(props, null);
			Transport transport = mailSession.getTransport("smtp");
			//メールサーバーを繋げるとユーザーをチェックする
			transport.connect((String) props.get("smtp.gmail.com"),
					username, password);
			transport.sendMessage(mimeMsg, mimeMsg
					.getRecipients(javax.mail.Message.RecipientType.TO));
			System.out.println("メールを送信成功！");
			transport.close();
		} catch (Exception e) {
			System.err.println("メールを送信失敗！" + e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}
}

