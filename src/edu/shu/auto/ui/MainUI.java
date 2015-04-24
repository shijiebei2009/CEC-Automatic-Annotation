package edu.shu.auto.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicBorders;

/**
 * 
 * <p>
 * ClassName MainUI
 * </p>
 * <p>
 * Description 该类是UI界面的程序入口类
 * </p>
 * 
 * @author wangxu wangx89@126.com
 *         <p>
 *         Date 2014-7-19 上午12:09:19
 *         </p>
 * @version V1.0.0
 * 
 */
public class MainUI extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private OnClickListener onClickListener;
	private JFrame jFrame;
	private JMenuBar jMenuBar;// 菜单栏
	private JMenu fileMenu;// File菜单
	private JMenu editMenu;// Edit菜单
	private JMenu helpMenu;// Help菜单
	private JMenuItem newMenuItem;// 新建
	private JMenuItem openMenuItem;// 打开
	private JMenuItem saveMenuItem;// 保存
	private JMenuItem saveAsMenuItem;// 另存
	private JMenuItem exitMenuItem;// 退出

	private JMenuItem cutMenuItem;// 剪切
	private JMenuItem copyMenuItem;// 复制
	private JMenuItem pasteMenuItem;// 粘贴

	private JMenuItem aboutMenuItem;// 关于

	private JPanel topPanel;
	private JPanel bottomPanel;
	private JScrollPane jScrollPane;// 带滚动条的面板
	private JTextArea jTextArea;// 文本域

	private JLabel title;// 第一个Jpanel的标题
	private JTextField titleContent;// 标题后面的文本框

	private JButton annotation;// 标注按钮

	private Font font;
	private KeyStroke keyStroke;

	public MainUI() {
		// 实例化监听器类
		onClickListener = new OnClickListener();
		// 设置字体，在后面使用
		font = new Font("华文楷体", Font.BOLD, 20);

		int width = (int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 1.5);// 获取屏幕宽度的一半
		int height = (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 1.5);// 获取屏幕高度的一半
		jFrame = new JFrame();
		onClickListener.setJFrame(jFrame);// 把当前对象传递过去
		jTextArea = new JTextArea();
		jTextArea.setLineWrap(true);
		onClickListener.setJTextArea(jTextArea);
		title = new JLabel("Title", SwingConstants.LEFT);
		titleContent = new JTextField("Title is...", 50);
		annotation = new JButton(new ImageIcon(Toolkit.getDefaultToolkit().getImage(
				getClass().getClassLoader().getResource("image/annotation.png"))));
		annotation.setActionCommand("标注");// 设置事件的命令
		annotation.setBorder(new BasicBorders.ButtonBorder(Color.CYAN, Color.blue, Color.GRAY, Color.GREEN));// 设置按钮的边框
		onClickListener.setTitleContent(titleContent);

		topPanel = new JPanel();
		// topPanel.setLayout(new FlowLayout());
		topPanel.add(title);
		topPanel.add(titleContent);

		bottomPanel = new JPanel();
		bottomPanel.add(annotation);

		jScrollPane = new JScrollPane(jTextArea);

		jMenuBar = new JMenuBar();// 创建菜单栏
		fileMenu = new JMenu("File(F)");// 创建父菜单
		fileMenu.setMnemonic('F');// 设置助记符
		helpMenu = new JMenu("Help(H)");
		helpMenu.setMnemonic('H');

		editMenu = new JMenu("Edit(E)");// 创建编辑菜单
		editMenu.setMnemonic('E');

		newMenuItem = new JMenuItem("New(N)");// 新建子菜单
		keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK);
		newMenuItem.setAccelerator(keyStroke);
		// newMenuItem.setMnemonic('N');

		cutMenuItem = new JMenuItem("Cut(T)");
		keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK);
		cutMenuItem.setAccelerator(keyStroke);
		cutMenuItem.setMnemonic('T');

		copyMenuItem = new JMenuItem("Copy(C)");
		keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK);
		copyMenuItem.setAccelerator(keyStroke);
		copyMenuItem.setMnemonic('C');

		pasteMenuItem = new JMenuItem("Paste(P)");
		keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK);
		pasteMenuItem.setAccelerator(keyStroke);
		pasteMenuItem.setMnemonic('P');

		openMenuItem = new JMenuItem("Open File(O)...");
		openMenuItem.setMnemonic('O');
		keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK);
		openMenuItem.setAccelerator(keyStroke);

		aboutMenuItem = new JMenuItem("About...");
		saveMenuItem = new JMenuItem("Save(s)");
		saveMenuItem.setMnemonic('S');

		// 为保存加ctrl+s快捷键
		keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK);
		saveMenuItem.setAccelerator(keyStroke);
		saveMenuItem.setActionCommand("save");
		saveMenuItem.addActionListener(onClickListener);

		saveAsMenuItem = new JMenuItem("Save As(A)...");
		saveAsMenuItem.setMnemonic('A');
		saveAsMenuItem.addActionListener(onClickListener);

		exitMenuItem = new JMenuItem("Exit(X)");
		exitMenuItem.setMnemonic('X');

		// jFrame.setFont(new Font("华文楷体",Font.BOLD,12));
		// jMenu.setFont(new Font("华文楷体",Font.BOLD,12));
		// jMenuBar.setFont(new Font("华文楷体",Font.BOLD,12));
		fileMenu.add(newMenuItem);
		fileMenu.add(openMenuItem);
		fileMenu.add(saveMenuItem);
		fileMenu.add(saveAsMenuItem);
		fileMenu.add(exitMenuItem);

		editMenu.add(cutMenuItem);
		editMenu.add(copyMenuItem);
		editMenu.add(pasteMenuItem);

		title.setFont(font);// 为标题标签设置字体
		titleContent.setFont(font);

		helpMenu.add(aboutMenuItem);

		jMenuBar.add(fileMenu);
		jMenuBar.add(editMenu);
		jMenuBar.add(helpMenu);

		jFrame.add(topPanel, BorderLayout.NORTH);
		jFrame.add(jScrollPane, BorderLayout.CENTER);
		jFrame.add(bottomPanel, BorderLayout.SOUTH);

		jFrame.setVisible(true);
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// 指定关闭操作
		jFrame.setJMenuBar(jMenuBar);// 加入菜单
		String imagePath = "image/icon.png";// 构造图片的路径
		// 将默认图标更改为指定图标，注意这种写法在导出JAR包时无法显示图标
		// Image imageIcon = Toolkit.getDefaultToolkit().getImage(imagePath);
		Image imageIcon = Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource(imagePath));// 这种写法就可以解决导出的Jar包也能显示图标了
		jFrame.setIconImage(imageIcon);

		// jFrame.setLocationRelativeTo(null);//默认从屏幕中央显示
		jFrame.setLocation(300, 200);// 设置从指定坐标点开始显示

		Dimension d = new Dimension(width, height);
		jFrame.setSize(d);// 指定窗体的大小

		Toolkit.getDefaultToolkit().beep();// 发出一个音频嘟嘟声
		jFrame.setTitle("Semi-automatic corpus annotation tool");// 设置标题，半自动语料标注工具

		aboutMenuItem.addActionListener(onClickListener);
		openMenuItem.addActionListener(onClickListener);

		annotation.addActionListener(onClickListener);

	}

	public static void main(String[] args) {
		new MainUI();//启动UI界面
	}
}
