package com.github.devcode.studio.example;

import java.awt.*;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.*;

/**
 * 탭영역에서 팝업메뉴 표시하여 탭추가, 탭 전체를 닫을 수 있는 예제
 */
public final class TabbedPaneDefaultPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;

	private TabbedPaneDefaultPanel() {
		super(new BorderLayout());
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.setComponentPopupMenu(new TabbedPanePopupMenu());
		tabbedPane.addTab("Title", new JLabel("Tab"));
		add(tabbedPane);
		setPreferredSize(new Dimension(320, 240));
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(TabbedPaneDefaultPanel::createAndShowGui);
	}

	private static void createAndShowGui() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (UnsupportedLookAndFeelException ignored) {
			Toolkit.getDefaultToolkit().beep();
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
			ex.printStackTrace();
			return;
		}
		JFrame frame = new JFrame("@title@");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.getContentPane().add(new TabbedPaneDefaultPanel());
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}

final class TabbedPanePopupMenu extends JPopupMenu {

	private static final long serialVersionUID = 1L;
	
	private final JMenuItem closePage;
	private final JMenuItem closeAll;
	private final JMenuItem closeAllButActive;

	/* default */ TabbedPanePopupMenu() {
		super();
		AtomicInteger counter = new AtomicInteger();
		add("New tab").addActionListener(e -> {
			JTabbedPane tabbedPane = (JTabbedPane) getInvoker();
			int iv = counter.getAndIncrement();
			tabbedPane.addTab("Title: " + iv, new JLabel("Tab: " + iv));
			tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
		});
		addSeparator();
		closePage = add("Close");
		closePage.addActionListener(e -> {
			JTabbedPane tabbedPane = (JTabbedPane) getInvoker();
			tabbedPane.remove(tabbedPane.getSelectedIndex());
		});
		addSeparator();
		closeAll = add("Close all");
		closeAll.addActionListener(e -> {
			JTabbedPane tabbedPane = (JTabbedPane) getInvoker();
			tabbedPane.removeAll();
		});
		closeAllButActive = add("Close all bat active");
		closeAllButActive.addActionListener(e -> {
			JTabbedPane tabbedPane = (JTabbedPane) getInvoker();
			int idx = tabbedPane.getSelectedIndex();
			String title = tabbedPane.getTitleAt(idx);
			Component cmp = tabbedPane.getComponentAt(idx);
			tabbedPane.removeAll();
			tabbedPane.addTab(title, cmp);
		});
	}

	@Override
	public void show(Component c, int x, int y) {
		if (c instanceof JTabbedPane) {
			JTabbedPane tabbedPane = (JTabbedPane) c;
			// JDK 1.3: tabIndex = tabbedPane.getUI().tabForCoordinate(tabbedPane, x, y);
			closePage.setEnabled(tabbedPane.indexAtLocation(x, y) >= 0);
			closeAll.setEnabled(tabbedPane.getTabCount() > 0);
			closeAllButActive.setEnabled(tabbedPane.getTabCount() > 0);
			super.show(c, x, y);
		}
	}
	
}