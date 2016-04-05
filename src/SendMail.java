import common.MailAttribute;
import model.MailInfo;
import util.MailUtil;

import javax.mail.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SendMail {

    public static void main(String[] args) {

        MailInfo mailInfo = new MailInfo();
        mailInfo.setSmtpHost(MailAttribute.DEFAULT_SMTP_HOST);
        mailInfo.setFromEmailAddress(MailAttribute.DEFAULT_SMTP_FROM);
        mailInfo.setToEmailAddress(MailAttribute.DEFAULT_SMTP_TO);
        mailInfo.setUserName(MailAttribute.USERNAME);
        mailInfo.setPassword(MailAttribute.PASSWORD);
        mailInfo.setSmtpPort(MailAttribute.MAIL_SMTP_PORT);
        mailInfo.setProtocol(MailAttribute.MAIL_TRANS_PROTOCOL);
        mailInfo.setSubject(MailAttribute.SUBJECT + " " + MailUtil.getFormatterDateTime());
        String fileName = null;
        List<File> attachFileList = new ArrayList<File>();
        if(args.length > 0){
            fileName = args[0];
            String attachFileStr = args[1];
            String[] attachFileNames = attachFileStr.split(",");
            for(String name:attachFileNames){
                if(name != null && !"".equals(name)){
                    attachFileList.add(new File(name));
                }
            }
        }else{
            System.out.println("Report File not found");
            return;
        }
        File htmlReportFile = new File(fileName);
        try {
            System.out.println("------");
            attachFileList.add(new File(fileName));
            SendMail.sendMessage(mailInfo,htmlReportFile,attachFileList);
        } catch (javax.mail.MessagingException exc) {
            exc.printStackTrace();
        } catch (java.io.UnsupportedEncodingException exc) {
            exc.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendMessage(MailInfo mailInfo, File reportFile,List<File> attachFileList)
            throws MessagingException, IOException {

        Session mailSession = MailUtil.getMailSession(mailInfo);
        mailSession.setDebug(true);
        Message testMessage = MailUtil.prepareMimeMessage(mailInfo,mailSession);
        Multipart mp = MailUtil.prepareMultipart(reportFile);
        for(File attachFile:attachFileList){
            BodyPart attachPart = MailUtil.prepareMultiAttachement(attachFile);
            mp.addBodyPart(attachPart);
        }
        testMessage.setSubject(mailInfo.getSubject());
        testMessage.setContent(mp);
        System.out.println("Message constructed");
        Transport transport = mailSession.getTransport(MailAttribute.MAIL_TRANS_PROTOCOL);
        transport.connect(mailInfo.getSmtpHost(), mailInfo.getUserName(), mailInfo.getPassword());
        transport.sendMessage(testMessage, testMessage.getAllRecipients());
        transport.close();

        System.out.println("Message sent!");
    }
}
