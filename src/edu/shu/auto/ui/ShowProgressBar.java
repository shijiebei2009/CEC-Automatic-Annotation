package edu.shu.auto.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;
import javax.swing.SwingConstants;

import edu.shu.auto.log.MyLogger;

/**
 * 
 * <p>
 * ClassName ShowProgressBar
 * </p>
 * <p>
 * Description 该类主要是用来显示进度条
 * </p>
 * 
 * @author wangxu wangx89@126.com
 *         <p>
 *         Date 2014-7-19 上午12:10:06
 *         </p>
 * @version V1.0.0
 * 
 */
@SuppressWarnings("serial")
public class ShowProgressBar extends JWindow implements Runnable {

	// 定义加载窗口大小
	public static final int LOAD_WIDTH = 455;
	public static final int LOAD_HEIGHT = 55;
	// 获取屏幕窗口大小
	public static final int WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width;
	public static final int HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height;
	// 定义进度条组件
	public JProgressBar progressbar;
	// 定义标签组件
	public JLabel label;

	// 构造函数
	public ShowProgressBar() {

		// 创建标签,并在标签上放置一张图片
		// label = new JLabel(new ImageIcon("images/background.jpg"));
		label = new JLabel("数据正在处理中，请稍后... ...", SwingConstants.CENTER);
		label.setFont(new Font("华文楷体", Font.ITALIC, 30));
		label.setBounds(0, 0, LOAD_WIDTH, LOAD_HEIGHT - 25);
		label.setBackground(new Color(204, 0, 1));// 背景色设置之后无效，不知道为何
		label.setForeground(new Color(69, 133, 243));
		// 创建进度条
		progressbar = new JProgressBar();
		// 显示当前进度值信息
		progressbar.setStringPainted(true);
		// 设置进度条边框不显示
		progressbar.setBorderPainted(false);
		// 设置进度条的前景色，即进度条的颜色
		progressbar.setForeground(new Color(153, 230, 40));
		// 设置进度条的背景色，即进度条的背景色
		progressbar.setBackground(new Color(27, 27, 24));
		progressbar.setBounds(0, LOAD_HEIGHT - 15, LOAD_WIDTH, 15);
		// 添加组件
		this.add(label);
		this.add(progressbar);
		// 设置布局为空
		this.setLayout(null);
		// 设置窗口初始位置
		this.setLocation((WIDTH - LOAD_WIDTH) / 2, (HEIGHT - LOAD_HEIGHT) / 2);
		// 设置窗口大小
		this.setSize(LOAD_WIDTH, LOAD_HEIGHT);
		// 设置窗口显示
		this.setVisible(true);

	}

	@Override
	public void run() {
		for (int i = 0; i < 500; i++) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
				MyLogger.logger.error(e.getMessage());
			}
			progressbar.setValue(i);
		}
		// JOptionPane.showMessageDialog(this, "加载完成");
		this.dispose();

	}

}
