package util;

import common.MailAttribute;
import model.MailInfo;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.*;
import java.sql.Date;
import java.util.Calendar;
import java.text.SimpleDateFormat;

/**
 * Created by YanJun on 2016/4/1.
 */
public class MailUtil {
    public static Session getMailSession(MailInfo mail){
        java.util.Properties props = new java.util.Properties();
        props.setProperty(MailAttribute.MAIL_SMTP_AUTH, "true");
        props.setProperty(MailAttribute.MAIL_SMTP_HOST, mail.getSmtpHost());
        props.setProperty(MailAttribute.MAIL_SMTP_PORT,mail.getSmtpPort());
        props.setProperty(MailAttribute.MAIL_TRANS_PROTOCOL, mail.getProtocol());
        Session mailSession = Session.getDefaultInstance(props);
        return mailSession;
    }

    public static Multipart prepareMultipart(File reportFile) throws MessagingException, IOException {
        BodyPart html = new MimeBodyPart();
        StringBuffer messageTestBuffer = new StringBuffer();
        if(reportFile.exists()){
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(new FileInputStream(reportFile)));
            while(true){
                String line = br.readLine();
                if(line == null){
                    break;
                }else{
                    messageTestBuffer.append(line);
                }
            }
        }else{
            messageTestBuffer.append("report file path is wrong");
        }
        html.setContent(messageTestBuffer.toString(),MailAttribute.HTML_TYPE);
        Multipart mp = new MimeMultipart();
        mp.addBodyPart(html);
        return mp;
    }

    public static BodyPart prepareMultiAttachement(File attachmentFile) throws MessagingException {
        BodyPart attach=new MimeBodyPart();
        DataSource ds=new FileDataSource(attachmentFile);
        attach.setDataHandler(new DataHandler(ds));
        attach.setFileName(ds.getName());
        return attach;
    }

    public static BodyPart prepareMultiImage(File imageFile,String name) throws MessagingException {
        MimeBodyPart image=new MimeBodyPart();
        DataSource ds=new FileDataSource(imageFile);

        image.setDataHandler(new DataHandler(ds));
        image.setFileName(ds.getName());
        image.setContentID(name);
        return image;
    }

    public static Message prepareMimeMessage(MailInfo mailInfo, Session mailSession) throws MessagingException {
        InternetAddress fromAddress = new InternetAddress(mailInfo.getFromEmailAddress());
        InternetAddress toAddress = new InternetAddress(mailInfo.getToEmailAddress());
        Message message = new MimeMessage(mailSession);
        message.setFrom(fromAddress);
        message.addRecipient(javax.mail.Message.RecipientType.TO, toAddress);
        message.setSentDate(new java.util.Date());
        return message;
    }

    public static String getFormatterDateTime(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        return formatter.format(date);
    }
}
