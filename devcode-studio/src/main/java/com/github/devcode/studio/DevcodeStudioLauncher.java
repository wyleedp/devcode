package com.github.devcode.studio;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import java.util.function.BiConsumer;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLightLaf;
import com.github.devcode.studio.component.AppTreeNode;
import com.github.devcode.studio.panel.JavaPropertiesPanel;
import com.github.devcode.studio.panel.OsEnvPanel;
import com.github.devcode.studio.panel.RandomStringPanel;
import com.github.devcode.studio.panel.UuidPanel;

/**
 * devcode-studio
 */
public class DevcodeStudioLauncher extends JFrame {

	private static final long serialVersionUID = 1L;

	private JSplitPane bodySplitPane;
	private JPanel rightPanel;
	private JTabbedPane bodyTabPane;
	
	/**
	 * 예) C:/Users/wylee/devcode/devcode-studio
	 */
	private static final String APP_HOME = FilenameUtils.normalizeNoEndSeparator(SystemUtils.USER_HOME + "/devcode/devcode-studio", true);
	
	private static final String APP_CONF_DIR = APP_HOME + "/conf";
	private static final String APP_CONF_FILE = APP_HOME + "/conf/devcode-studio.conf";
	
	private static final String APP_LOG_DIR = APP_HOME + "/log";
	private static final String APP_LOG_FILE = APP_HOME + "/log/devcode-studio.log";
	
	private static Properties appConfigurationProp = new Properties();
	private static final int WINDOW_WIDTH_DEFAULT_SIZE = 1000;
	private static final int WINDOW_HEIGHT_DEFAULT_SIZE = 800;
	
	private int windowWidthSize = 1000;
	private int windowHeightSize = 800;
	
	private static Logger logger;
	
	/**
	 * 앱 설정정보
	 */
	private void setAppConfiguration() {
		try {
			File appHomeFile = new File(APP_HOME);
			File appConfDir = new File(APP_CONF_DIR);
			File appLogDir = new File(APP_LOG_DIR);
			
			if(!appHomeFile.exists()) FileUtils.forceMkdirParent(appHomeFile);
			if(!appConfDir.exists()) FileUtils.forceMkdirParent(appConfDir);
			if(!appLogDir.exists()) FileUtils.forceMkdirParent(appLogDir);
			
			// window.width.size=1000
			// window.height.size=800
			File appConfFile = new File(APP_CONF_FILE);
			if(!appConfFile.exists()) {
				StringBuilder confBuilder = new StringBuilder();
				confBuilder.append("window.width.size=").append(WINDOW_WIDTH_DEFAULT_SIZE).append(System.lineSeparator());
				confBuilder.append("window.height.size=").append(WINDOW_HEIGHT_DEFAULT_SIZE);
				FileUtils.writeStringToFile(appConfFile, confBuilder.toString(), "UTF-8");
				
				logger.info("Configuration File Created. Path : " + appConfFile.toString());
				
				windowWidthSize = WINDOW_WIDTH_DEFAULT_SIZE;
				windowHeightSize = WINDOW_HEIGHT_DEFAULT_SIZE;
			}
			
			appConfigurationProp.load(new FileInputStream(appConfFile));
			
			Object windowWidthSizeObject = appConfigurationProp.get("window.width.size");
			Object windowHeightSizeObject = appConfigurationProp.get("window.height.size");
			
			if(windowWidthSizeObject != null) {
				windowWidthSize = Integer.parseInt(windowWidthSizeObject.toString());
			}
			
			if(windowHeightSizeObject != null) {
				windowHeightSize = Integer.parseInt(windowHeightSizeObject.toString());
			}
			
			// Log4j2를 이용한 로깅
			ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();
			builder.setStatusLevel(Level.INFO);
			builder.setConfigurationName("BuilderTest");
			builder.add(builder.newFilter("ThresholdFilter", Filter.Result.ACCEPT, Filter.Result.NEUTRAL).addAttribute("level", Level.DEBUG));

			ComponentBuilder<?> triggeringPolicy = builder.newComponent("Policies")
			        .addComponent(builder.newComponent("CronTriggeringPolicy").addAttribute("schedule", "0 0 0 * * ?"))
			        .addComponent(builder.newComponent("SizeBasedTriggeringPolicy").addAttribute("size", "100M"));
			
			// 콘솔
			AppenderComponentBuilder consoleAppenderBuilder = builder.newAppender("Stdout", "CONSOLE")
					.addAttribute("target", ConsoleAppender.Target.SYSTEM_OUT)
			        .add(builder.newLayout("PatternLayout").addAttribute("pattern", "[%d{HH:mm:ss,SSS}] [%t] [%-5level] %c(%M:%L)] - %m%n"))
			        .add(builder.newFilter("MarkerFilter", Filter.Result.DENY, Filter.Result.NEUTRAL).addAttribute("marker", "FLOW"));
			
			// 파일
			AppenderComponentBuilder rollingAppenderBuilder = builder.newAppender("rolling", "RollingFile")
					.add(builder.newLayout("PatternLayout").addAttribute("pattern", "[%d] [%t] [%-5level] %c(%M:%L)] - %m%n"))
					.addAttribute("fileName", APP_LOG_FILE)
					.addAttribute("filePattern", APP_LOG_DIR + "/devcode-studio_%d{yyyy-MM-dd}.log")
					.addComponent(triggeringPolicy);
			
			builder.add(consoleAppenderBuilder);
			builder.add(rollingAppenderBuilder);
			
			builder.add(builder.newRootLogger(Level.INFO)
					.add(builder.newAppenderRef("Stdout"))
					.add(builder.newAppenderRef("rolling"))
					.addAttribute("additivity", false));

			builder.add(builder.newLogger("com.github.devcode.studio", Level.INFO)
					.add(builder.newAppenderRef("Stdout"))
					.add(builder.newAppenderRef("rolling"))
					.addAttribute("additivity", false));
			
			Configurator.initialize(builder.build());
			
			logger = LogManager.getLogger(DevcodeStudioLauncher.class);
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 최초 구성
	 */
	public DevcodeStudioLauncher() {
		try {
			setTitle("devcode-studio");
			setAppConfiguration();
			initComponent();
		}catch(Exception e) {
			logger.error("ERROR", e);
			JOptionPane.showMessageDialog(getRootPane(), e.getMessage(), "확인", JOptionPane.WARNING_MESSAGE);
		}
	}
	
	/**
	 * 종료
	 */
	private void windowClosing() {
		int confirm = JOptionPane.showConfirmDialog(this, "종료하시겠습니까?", "확인", JOptionPane.YES_NO_OPTION);
		if(confirm == JOptionPane.YES_OPTION) {
			logger.info("Closing...");
			
			// 창 크기 저장
			Dimension size = getSize();
			setAppConfiguration("window.width.size", (int)size.getWidth());
			setAppConfiguration("window.height.size", (int)size.getHeight());
			
			DevcodeStudioLauncher.this.setVisible(false);
			DevcodeStudioLauncher.this.dispose();
			
			logger.info("Close Complete.");
		}
	}
	
	/**
	 * App 설정값 저장
	 * 
	 * @param key
	 * @param value
	 */
	private void setAppConfiguration(String key, String value) {
		if(appConfigurationProp == null) {
			return;
		}
		
		if(StringUtils.isBlank(key)) {
			System.out.println("Required key value!!!");
			return;
		}
		
		boolean isNotMatch = true;
		String[] keys = new String[] {"window.width.size", "window.height.size"};
		for(String keyValue : keys) {
			if(StringUtils.equals(key, keyValue)) {
				isNotMatch = false;
			}
		}
		
		if(isNotMatch) {
			System.out.println("Not Support Key. Key : " + key);
			return;
		}
		
		try {
			appConfigurationProp.setProperty(key, value);
			
			// TODO : [2023.12.22] 현재 프로퍼티 저장시 # 등록일시가 기록됨. 처리필요, 구글링해본결과 해제 옵션이 없는듯. Properties 상속받아서 재정의 해야 할듯..
			appConfigurationProp.store(new FileWriter(new File(APP_CONF_FILE)), "");
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	/**
	 * App 설정값 저장
	 * 
	 * @param key
	 * @param intValue
	 */
	private void setAppConfiguration(String key, int intValue) {
		setAppConfiguration(key, Integer.toString(intValue));
	}
	
	/**
	 * 컴포넌트 초기구성
	 */
	private void initComponent() {
		logger.info("컴포넌트 생성");
		
		// FlatLaf - Flat Look and Feel 적용
		// 기본 테마
		FlatLightLaf.setup();
		
		// 다크 테마
		//FlatArcDarkIJTheme.setup();
		
		//FlatLaf.setPreferredMonospacedFontFamily(FlatJetBrainsMonoFont.FAMILY);
		//FlatLightLaf.setPreferredMonospacedFontFamily(FlatJetBrainsMonoFont.FAMILY);
		
		// 종료 이벤트
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				DevcodeStudioLauncher.this.windowClosing();
			}
		});
		
		// 사이즈 지정 및 가운데 표시
		setSize(windowWidthSize, windowHeightSize);
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frm = this.getSize();
        int xpos = (int)(screen.getWidth() / 2 - frm.getWidth() / 2);
        int ypos = (int)(screen.getHeight() / 2 - frm.getHeight() / 2);
        setLocation(xpos, ypos);
		
		// 메뉴 구성
		Container contentPane = getContentPane();
		JMenuBar menuBar = new JMenuBar();
		
		// 종료 메뉴
		JMenuItem menuItemExit = new JMenuItem("Exit");
		menuItemExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				DevcodeStudioLauncher.this.windowClosing();
			}
		});
		
		
		JMenu menuFile = new JMenu("File");
		menuFile.add(menuItemExit);
		
		menuBar.add(menuFile);
		
		
		setJMenuBar(menuBar);
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.setBackground(new Color(237, 28, 36));
		
		bodySplitPane = new JSplitPane();
		bodySplitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		//splitPane.setLayout(new FlowLayout());
		bodySplitPane.setDividerLocation(300);
		
		// devtool 패널 - tree
		DefaultMutableTreeNode devTooltreeNode = new DefaultMutableTreeNode();
		devTooltreeNode.setUserObject(new AppTreeNode(AppTreeNode.DEV_TOOLS_NODE, "DevTools"));
		
		DefaultMutableTreeNode javaPropertiesTreeNode = new DefaultMutableTreeNode("JavaProp");
		javaPropertiesTreeNode.setUserObject(new AppTreeNode(AppTreeNode.JAVA_PROPERTIES_NODE, "JavaProp"));
		
		DefaultMutableTreeNode osEnvTreeNode = new DefaultMutableTreeNode("OS Env");
		osEnvTreeNode.setUserObject(new AppTreeNode(AppTreeNode.OS_ENV_NODE, "OS Env"));
		
		DefaultMutableTreeNode uuidNode = new DefaultMutableTreeNode("UUID");
		uuidNode.setUserObject(new AppTreeNode(AppTreeNode.UUID_NODE, "UUID"));
		
		DefaultMutableTreeNode randomStringNode = new DefaultMutableTreeNode("Random String");
		randomStringNode.setUserObject(new AppTreeNode(AppTreeNode.RANDOM_STRING_NODE, "Random String"));
		
		devTooltreeNode.add(javaPropertiesTreeNode);
		devTooltreeNode.add(osEnvTreeNode);
		devTooltreeNode.add(uuidNode);
		devTooltreeNode.add(randomStringNode);
		
		JTree devTooltree = new JTree(devTooltreeNode);
		devTooltree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 2) {
					DefaultMutableTreeNode dblclickTreeNode = (DefaultMutableTreeNode)devTooltree.getLastSelectedPathComponent();
					if(dblclickTreeNode == null) {
						return;
					}
					
					AppTreeNode appTreeNode = (AppTreeNode)dblclickTreeNode.getUserObject();
					
					// 기능탭 추가
					addFunctionTab(appTreeNode);
				}
				
				//super.mouseClicked(e);
			}
		});
		
		JScrollPane devToolScrollPanel = new JScrollPane(devTooltree, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		bodySplitPane.setLeftComponent(devToolScrollPanel);
	
		// 탭
		bodyTabPane = new JTabbedPane();
		bodyTabPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		bodyTabPane.putClientProperty(FlatClientProperties.TABBED_PANE_TAB_CLOSABLE, true);
		bodyTabPane.putClientProperty(FlatClientProperties.TABBED_PANE_TAB_CLOSE_TOOLTIPTEXT, "닫기");
		bodyTabPane.putClientProperty(FlatClientProperties.TABBED_PANE_TAB_CLOSE_CALLBACK,
			(BiConsumer<JTabbedPane, Integer>) (tabPane, tabIndex) -> {
				//AWTEvent e = EventQueue.getCurrentEvent();
				String tabTitle = tabPane.getTitleAt(tabIndex);
				
				int confirm = JOptionPane.showConfirmDialog(this, tabTitle + " 탭을 닫으시겠습니까?", "확인", JOptionPane.YES_NO_OPTION);
				if(confirm == JOptionPane.YES_OPTION) {
					tabPane.remove(tabIndex);
				}
			} );
		
		rightPanel = new JPanel();
		rightPanel.setLayout(new BorderLayout());
		rightPanel.add(bodyTabPane);
		bodySplitPane.setRightComponent(rightPanel);
		
		mainPanel.add(bodySplitPane);
        contentPane.add(mainPanel);
        
        setVisible(true);
	}
	
	/**
	 * 기능별 탭 추가
	 * 
	 * @param appTreeNode
	 */
	public void addFunctionTab(AppTreeNode appTreeNode) {
		String nodeId = appTreeNode.getNodeId();
		String nodeName = appTreeNode.getNodeName();
		
		logger.info("탭 생성. NodeId : " + nodeId);
		
		if(StringUtils.equalsAnyIgnoreCase(nodeId, AppTreeNode.JAVA_PROPERTIES_NODE)) {
			bodyTabPane.addTab(nodeName, new JavaPropertiesPanel());	
			setTabFocus();
		}else if(StringUtils.equalsAnyIgnoreCase(nodeId, AppTreeNode.OS_ENV_NODE)) {
			bodyTabPane.addTab(nodeName, new OsEnvPanel());
			setTabFocus();
		}else if(StringUtils.equalsAnyIgnoreCase(nodeId, AppTreeNode.UUID_NODE)) {
			bodyTabPane.addTab(nodeName, new UuidPanel());
			setTabFocus();
		}else if(StringUtils.equalsAnyIgnoreCase(nodeId, AppTreeNode.RANDOM_STRING_NODE)) {
			bodyTabPane.addTab(nodeName, new RandomStringPanel());
			setTabFocus();
		}
	}
	
	/**
	 * 새로 생성된 탭 포커싱
	 */
	public void setTabFocus() {
		int tabCount = bodyTabPane.getTabCount();
		if(tabCount == 0) {
			return;
		}
		
		bodyTabPane.setSelectedIndex(tabCount - 1);
	}
	
	/**
	 * 실행 
	 */
	public static void main(String[] args) {
		try {
			SwingUtilities.invokeLater(new Runnable() {
	            @Override
	            public void run() {
	                final DevcodeStudioLauncher studio = new DevcodeStudioLauncher();
	                studio.setVisible(true);
	            }
	        });
		}catch(Exception e) {
			System.err.println("ERROR");
			e.printStackTrace();
		}
	}
	
	
}
