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

import org.apache.commons.lang3.StringUtils;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLightLaf;
import com.github.devcode.studio.component.AppTreeNode;
import com.github.devcode.studio.panel.JavaPropertiesPanel;
import com.github.devcode.studio.panel.OsEnvPanel;
import com.github.devcode.studio.panel.UuidPanel;

/**
 * devcode-studio
 */
public class DevcodeStudioLauncher extends JFrame {

	private static final long serialVersionUID = 1L;

	private JSplitPane bodySplitPane;
	private JPanel rightPanel;
	private JTabbedPane bodyTabPane;
	
	public DevcodeStudioLauncher() {
		setTitle("devcode-studio");

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
		setSize(1000, 800);
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frm = this.getSize();
        int xpos = (int)(screen.getWidth() / 2 - frm.getWidth() / 2);
        int ypos = (int)(screen.getHeight() / 2 - frm.getHeight() / 2);
        setLocation(xpos, ypos);

		initComponent();

		setVisible(true);
	}
	
	/**
	 * 종료
	 */
	public void windowClosing() {
		int confirm = JOptionPane.showConfirmDialog(this, "종료하시겠습니까?", "확인", JOptionPane.YES_NO_OPTION);
		if(confirm == JOptionPane.YES_OPTION) {
			System.out.println("종료");
			DevcodeStudioLauncher.this.setVisible(false);
			DevcodeStudioLauncher.this.dispose();
		}
	}
	
	private void initComponent() {
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
		
		devTooltreeNode.add(javaPropertiesTreeNode);
		devTooltreeNode.add(osEnvTreeNode);
		devTooltreeNode.add(uuidNode);
		
		JTree devTooltree = new JTree(devTooltreeNode);
		devTooltree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 2) {
					DefaultMutableTreeNode dblclickTreeNode = (DefaultMutableTreeNode)devTooltree.getLastSelectedPathComponent();
					if(dblclickTreeNode == null) {
						return;
					}
					
					System.out.println("더블클릭 : " + dblclickTreeNode.toString());
					
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
	}
	
	/**
	 * 기능별 탭 추가
	 * 
	 * @param appTreeNode
	 */
	public void addFunctionTab(AppTreeNode appTreeNode) {
		String nodeId = appTreeNode.getNodeId();
		String nodeName = appTreeNode.getNodeName();
		
		System.out.println("탭 생성 functionId : " + nodeId);
		int tabCount = bodyTabPane.getTabCount();
		System.out.println("tabCount : " + tabCount);
		
		if(StringUtils.equalsAnyIgnoreCase(nodeId, AppTreeNode.JAVA_PROPERTIES_NODE)) {
			bodyTabPane.addTab(nodeName, new JavaPropertiesPanel());	
			setTabFocus();
		}else if(StringUtils.equalsAnyIgnoreCase(nodeId, AppTreeNode.OS_ENV_NODE)) {
			bodyTabPane.addTab(nodeName, new OsEnvPanel());
			setTabFocus();
		}else if(StringUtils.equalsAnyIgnoreCase(nodeId, AppTreeNode.UUID_NODE)) {
			bodyTabPane.addTab(nodeName, new UuidPanel());
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
			e.printStackTrace();
		}
	}
	
}
