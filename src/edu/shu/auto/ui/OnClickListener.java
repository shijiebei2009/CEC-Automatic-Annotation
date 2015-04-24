package edu.shu.auto.ui;

import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;

import edu.shu.auto.log.MyLogger;
import edu.shu.auto.preprocess.PreProcess;
import edu.shu.auto.util.EncodingUtil;

/**
 * 
 * <p>
 * ClassName OnClickListener
 * </p>
 * <p>
 * Description 该函数用来处理对菜单的监听事件
 * </p>
 * 
 * @author wangxu wangx89@126.com
 *         <p>
 *         Date 2014-7-19 上午12:09:50
 *         </p>
 * @version V1.0.0
 * 
 */
public class OnClickListener implements ActionListener {
	private JFrame jFrame;// 用来获取父类对象的frame对象
	private JTextArea jTextArea;
	private JTextField titleContent;
	public static String fileName;// 用户选择打开的文件名
	private String textContent;// 还没有处理的文本内容
	private String filePath;// 打开的文件的路径
	private String saveFileName;// 该变量用来存储打开的文件名全称，因为fileName的后缀被处理掉了，所以在保存的时候，不能使用fileName

	private String openFilePath;// 打开的文件路径

	public void setSaveFileName(String saveFileName) {
		this.saveFileName = saveFileName;
	}

	public String getSaveFileName() {
		return this.saveFileName;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getFilePath() {
		return this.filePath;
	}

	public void setTextContent(String textContent) {
		this.textContent = textContent;
	}

	public String getTextContent() {
		return this.textContent;
	}

	public JFrame getJFrame() {
		return jFrame;
	}

	public void setJFrame(JFrame jFrame) {
		this.jFrame = jFrame;
	}

	public JTextArea getJTextArea() {
		return jTextArea;
	}

	public void setJTextArea(JTextArea jTextArea) {
		this.jTextArea = jTextArea;
	}

	public JTextField getTitleContent() {
		return titleContent;
	}

	public void setTitleContent(JTextField titleContent) {
		this.titleContent = titleContent;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		OnClickListener.fileName = fileName;
	}

	@Override
	/**
	 * 覆写的监听处理方法
	 */
	public void actionPerformed(ActionEvent paramActionEvent) {
		// About...
		String command = paramActionEvent.getActionCommand();
		MyLogger.logger.info("command = " + command);
		if ("About...".equals(command)) {
			// 代表处理的是About...菜单
			handleAbout();
		} else if ("Open File(O)...".equals(command)) {
			// 处理打开菜单事件
			handleOpenFile();
		} else if ("标注".equals(command)) {
			// 处理用户点击标注按钮事件
			// 调用进度条
			ShowProgressBar showProgressBar = new ShowProgressBar();
			new Thread(showProgressBar).start();
			// Thread.currentThread().stop();//此句代码有待修改
			jTextArea.setText(PreProcess.getText(openFilePath, EncodingUtil.CHARSET_GBK).toString());
		} else if ("save".equals(command)) {
			// 处理保存逻辑
			handleSaveFile();
		} else if ("Save As(A)...".equals(command)) {
			// 处理另存逻辑
			handleSaveAsFile();
		}
	}

	// 处理打开文件菜单事件
	private void handleOpenFile() {
		FileDialog fileDialog = new FileDialog(getJFrame(), "打开...", FileDialog.LOAD);
		fileDialog.setVisible(true);// 将其设置为可见，默认是不可见
		// 如果直接使用fileDialog.getDirectory()，那么获取的路径是：C:\Users\wangxu\Desktop\（假设我选中桌面上的一个文件）
		fileName = fileDialog.getFile();// 获取文件名
		MyLogger.logger.info("文件名是：" + fileName);
		setFilePath(fileDialog.getDirectory());
		openFilePath = getFilePath() + fileName;// 获取选择的文件的路径
		MyLogger.logger.info("文件路径是：" + openFilePath);
		setSaveFileName(openFilePath);// 将文件的全路径设置进去
		MyLogger.logger.info("save file path = " + openFilePath);// 很奇怪的是，如果我在文件框中选择取消，那么输出的path是nullnull
		if (!"nullnull".equals(openFilePath)) {
			// 此处处理主要是将文件名后缀去掉
			int end = fileName.lastIndexOf('.');// 这里注意一定要从后面开始找，防止文件名中出现.的情况
			fileName = fileName.substring(0, end);

			// 从选中文件中读取内容
			try {
				// 返回此流使用的字符编码的名称。
				// 如果该编码有历史上用过的名称，则返回该名称；否则返回该编码的规范化名称。

				// 如果使用 InputStreamReader(InputStream, String)
				// 构造方法创建此实例，则返回的由此编码生成的唯一名称可能与传递给该构造方法的名称不一样。如果流已经关闭，则此方法将会返回
				// null。

				// InputStreamReader isr = new InputStreamReader(new
				// FileInputStream(path), "GBK");
				// 指定GBK编码，如果是UTF-8编码是乱码
				InputStreamReader isr = new InputStreamReader(new FileInputStream(openFilePath), EncodingUtil.CHARSET_GBK);
				MyLogger.logger.info("isr.getEncoding()=" + isr.getEncoding());
				BufferedReader br = new BufferedReader(isr);
				StringBuffer sb = new StringBuffer();
				String line;
				while ((line = br.readLine()) != null) {
					sb.append(line + "\n");
				}
				// 关闭资源
				br.close();
				jTextArea.setFont(new Font("宋体", Font.PLAIN, 15));
				jTextArea.setText(new String(sb));
				titleContent.setText(fileName);
				setTextContent(new String(sb));// 将sb赋值给textContent

			} catch (FileNotFoundException e) {
				e.printStackTrace();
				MyLogger.logger.error("没有找到文件:" + e.getMessage());
			} catch (IOException e) {
				e.printStackTrace();
				MyLogger.logger.error("读取文件出错:" + e.getMessage());
			}
		}

	}

	// 处理about菜单点击事件
	public void handleAbout() {
		String messageStr = "-----------------------------------------\nAuthor：wangxu\nDate From：2014/5/7\nEmail：wangx89@126.com\nCopyright：Semantic intelligence laboratory,Shanghai University\n-----------------------------------------";
		String imagePath = "image/water.png";
		URL url = getClass().getClassLoader().getResource(imagePath);
		System.out.println(url);
		Image imageIcon = Toolkit.getDefaultToolkit().getImage(url);// 将默认图标更改为指定图标
		Icon icon = new ImageIcon(imageIcon);
		// parentComponent - 确定在其中显示对话框的 Frame；如果为 null 或者 parentComponent 不具有
		// Frame，则使用默认的 Frame
		// message - 要显示的 Object
		// title - 对话框的标题字符串
		// messageType -
		// 要显示的消息类型：ERROR_MESSAGE、INFORMATION_MESSAGE、WARNING_MESSAGE、QUESTION_MESSAGE
		// 或 PLAIN_MESSAGE
		// icon - 要在对话框中显示的图标，该图标可以帮助用户识别要显示的消息种类
		JOptionPane.showMessageDialog(null, messageStr, "语料标注器", JOptionPane.INFORMATION_MESSAGE, icon);
	}

	// 处理保存文件的菜单
	public void handleSaveFile() {
		if (null == getFilePath() || "null".equals(getFilePath())) {
			handleSaveAsFile();
		} else {
			// 拼接用户打开的文件的路径
			String filePath = getSaveFileName();
			MyLogger.logger.info("handleSaveFile:" + filePath);
			writeToFile(filePath);
		}
	}

	// 处理另存文件的菜单
	public void handleSaveAsFile() {
		// FileDialog fileDialog = new
		// FileDialog(getJFrame(),"保存...",FileDialog.SAVE);
		// fileDialog.setVisible(true);
		// String fileName = fileDialog.getDirectory()+fileDialog.getFile();
		// 设置对话框的风格
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e1) {
			MyLogger.logger.info("设置对话框风格出错！" + e1.getMessage());
			e1.printStackTrace();
		}
		JFileChooser jFileChooser = new JFileChooser();
		// jFileChooser.setMultiSelectionEnabled(true);//如果要多选的话，设置这句话即可
		// 设置默认的保存文件名称
		// jFileChooser.setSelectedFile(new
		// File(getTitleContent().getText().toString()));
		int result = jFileChooser.showSaveDialog(null);
		switch (result) {
		case JFileChooser.APPROVE_OPTION:
			// 这一种方法是把显示内容中的标题取出来作为文件名，暂不采用
			// filePath = jFileChooser.getCurrentDirectory() + File.separator +
			// getTitleContent().getText() + ".xml";
			// 这一种方法是把用户输入的作为保存的文件名
			filePath = jFileChooser.getCurrentDirectory() + File.separator + jFileChooser.getSelectedFile().getName() + ".xml";
			MyLogger.logger.info("改变路径之后，文件的保存路径=" + filePath);
			MyLogger.logger.info("Approve (Open or Save) was clicked ");
			MyLogger.logger.info("这是绝对路径:" + jFileChooser.getSelectedFile().getAbsolutePath());
			writeToFile(filePath);
			break;
		case JFileChooser.CANCEL_OPTION:
			MyLogger.logger.info("Cancle or the close-dialog icon was clicked");
			break;
		case JFileChooser.ERROR_OPTION:
			MyLogger.logger.error("Error...");
			break;
		}
	}

	/**
	 * 
	 * <p>
	 * Title: writeToFile
	 * </p>
	 * <p>
	 * Description: 保存文件的方法
	 * </p>
	 * 
	 * @param filePath
	 *            保存文件的路径
	 *
	 */
	public void writeToFile(String filePath) {
		try {
			OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(filePath), EncodingUtil.CHARSET_GBK);
			BufferedWriter bw = new BufferedWriter(osw);
			// 有乱码
			// FileOutputStream output = new FileOutputStream(filePath);
			byte fileContent[] = getJTextArea().getText().replaceAll("\n", "\r\n").getBytes();// 此处主要是为了在Windows中实现换行操作
			MyLogger.logger.info("保存的内容:" + new String(fileContent));
			// String lineSeparator = java.security.AccessController.doPrivileged(new sun.security.action.Getpr);
			// String content = new String(fileContent);
			// content.replaceAll("\n", "\r\n");
			bw.write(new String(fileContent));
			bw.flush();
			bw.close();
		} catch (FileNotFoundException e) {
			MyLogger.logger.error("文件未找到！" + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			MyLogger.logger.error("IO异常！" + e.getMessage());
			e.printStackTrace();
		}
	}
}