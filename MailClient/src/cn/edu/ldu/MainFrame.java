package cn.edu.ldu;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMessage;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author yk
 */
public class MainFrame extends javax.swing.JFrame {

    /**
     * Creates new form MainFrame
     */
    private String userAddr = null;
    private String userPass = null;
    private String smtpAddr = null;
    private String pop3Addr = null;
    private String mimeType=null;
    private StringBuffer bodytext = new StringBuffer();
    private Message[] messages;

    public MainFrame(String userAddr, String userPass, String smtpAddr, String pop3Addr) {
        initComponents();
        this.userAddr = userAddr;
        this.userPass = userPass;
        this.smtpAddr = smtpAddr;
        this.pop3Addr = pop3Addr;
        this.setTitle("收件箱--"+this.userAddr);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        mailTable = new javax.swing.JTable();
        btnSend = new javax.swing.JButton();
        btnReserve = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtArea = new javax.swing.JTextPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        mailTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "发件人", "主题"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        mailTable.setRowHeight(30);
        mailTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                mailTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(mailTable);

        btnSend.setText("发信");
        btnSend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSendActionPerformed(evt);
            }
        });

        btnReserve.setText("收信");
        btnReserve.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReserveActionPerformed(evt);
            }
        });

        txtArea.setEditable(false);
        jScrollPane3.setViewportView(txtArea);
        txtArea.getAccessibleContext().setAccessibleName("txtArea");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnSend)
                    .addComponent(btnReserve, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 362, Short.MAX_VALUE)
                    .addComponent(jScrollPane3)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnSend)
                        .addGap(35, 35, 35)
                        .addComponent(btnReserve))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 172, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void mailTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mailTableMouseClicked
        try {
            // TODO add your handling code here:
            int row = ((JTable) evt.getSource()).rowAtPoint(evt.getPoint());
            if (!messages[row].getFolder().isOpen()) //判断是否open   
                messages[row].getFolder().open(Folder.READ_WRITE);
            String from = messages[row].getFrom()[0].toString();
            Pattern p = Pattern.compile("<(.*?)>",
                    Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(from);
            if (m.find())
                from = m.group();
            String subject = messages[row].getSubject();
            bodytext.setLength(0);
            getMailContent((Part) messages[row]);
            this.txtArea.setContentType(mimeType);
            if(mimeType.endsWith("text/html"))
            {
                String temp=bodytext.toString().replaceAll("<meta(.*?)>","");
                this.txtArea.setText("发件人：" + from + "<br>主题：" 
                        + subject + "<br>正文：<br>" + temp);
            }else{
                this.txtArea.setText("发件人：" + from + "\n主题：" 
                        + subject + "\n正文：\n" + bodytext);
            }
        } catch (Exception ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_mailTableMouseClicked

    private void btnSendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSendActionPerformed
        // TODO add your handling code here:
        this.dispose();
        new SendFrame(userAddr, userPass, smtpAddr, pop3Addr).setVisible(true);
    }//GEN-LAST:event_btnSendActionPerformed

    private void btnReserveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReserveActionPerformed
        try {
            //清空表格内容
            DefaultTableModel tableModel = (DefaultTableModel) mailTable.getModel();
            tableModel.setRowCount(0);
            // TODO add your handling code here:
            String userName;
            String[] temp = userAddr.split("@", 2);
            userName = temp[0];
            // 创建一个有具体连接信息的Properties对象
            Properties props = new Properties();
            props.setProperty("mail.store.protocol", "pop3");
            props.setProperty("mail.pop3.host", pop3Addr);
            // 使用Properties对象获得Session对象
            Session session = Session.getInstance(props);
            // 利用Session对象获得Store对象，并连接pop3服务器
            Store store = session.getStore();
            store.connect(pop3Addr, userName, userPass);
            // 获得邮箱内的邮件夹Folder对象，以"只读"打开
            Folder folder = store.getFolder("inbox");
            folder.open(Folder.READ_ONLY);
            // 获得邮件夹Folder内的所有邮件Message对象
            messages = folder.getMessages();
            int count = messages.length;
            for (int i = 0; i < count; i++) {
                //表格填充数据
                String from = messages[i].getFrom()[0].toString();
                Pattern p = Pattern.compile("<(.*?)>",
                        Pattern.CASE_INSENSITIVE);
                Matcher m = p.matcher(from);
                if (m.find()) {
                    from = m.group();
                }
                tableModel.addRow(new Object[]{from, messages[i].getSubject()});
            }
            //关闭 Folder会真正删除邮件, false 不删除 
            folder.close(false);
            //关闭 store, 断开网络连接
            store.close();
        } catch (Exception ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "邮件接收失败,请检查用户信息填写是否正确",
                    "错误提示", JOptionPane.ERROR_MESSAGE);
            this.dispose();
            LoginUI dialog = new LoginUI(new javax.swing.JFrame(), true);
            dialog.setVisible(true);
        }
    }//GEN-LAST:event_btnReserveActionPerformed

    public void getMailContent(Part part) throws Exception {
        String contenttype = part.getContentType();
        int nameIndex = contenttype.indexOf("name");
        boolean flag = false;
        if (nameIndex != -1) {
            flag = true;
        }
        if (part.isMimeType("text/plain") && !flag) {
            bodytext=new StringBuffer((String) part.getContent());
            mimeType="text/plain";//保存邮件格式，为页面显示做准备
        } else if (part.isMimeType("text/html") && !flag) {
            bodytext=new StringBuffer((String) part.getContent());
            mimeType="text/html";
        } else if (part.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) part.getContent();
            int counts = multipart.getCount();
            for (int i = 0; i < counts; i++) {
                getMailContent(multipart.getBodyPart(i));
            }
        } else if (part.isMimeType("message/rfc822")) {
            getMailContent((Part) part.getContent());
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnReserve;
    private javax.swing.JButton btnSend;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable mailTable;
    private javax.swing.JTextPane txtArea;
    // End of variables declaration//GEN-END:variables

    /**
     * @return the bodytext
     */
    public StringBuffer getBodytext() {
        return bodytext;
    }
}
