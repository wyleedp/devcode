package com.github.devcode.studio.panel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;
import java.util.TreeMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

import org.apache.commons.lang3.time.DateFormatUtils;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.extras.components.FlatButton;
import com.formdev.flatlaf.extras.components.FlatButton.ButtonType;

/**
 * Java Properties 값을 조회하는 테이블 패널
 */
public class JavaPropertiesPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JTable table;
	
	public JavaPropertiesPanel() {
		setLayout(new BorderLayout());

		String header[] = { "No", "Key", "Value" };
		DefaultTableModel defaultTableModel = new DefaultTableModel(header, 0) {
			private static final long serialVersionUID = 1L;

			// 첫번째 컬럼(No)는 읽기모드
			public boolean isCellEditable(int row, int column) {
				return column != 0;
			}

			// 정렬
			@Override
			public Class<?> getColumnClass(int columnIndex) {
				if (columnIndex == 0) {
					return Integer.class;
				} else {
					return super.getColumnClass(columnIndex);
				}
			}
		};

		TreeMap<String, String> propTreeMap = new TreeMap<String, String>();

		Properties properties = System.getProperties();
		for (Enumeration<?> en = properties.propertyNames(); en.hasMoreElements();) {
			String key = (String) en.nextElement();
			String value = properties.getProperty(key);

			propTreeMap.put(key, value);
		}

		int no = 1;
		for (String key : propTreeMap.keySet()) {
			String value = propTreeMap.get(key);

			Object[] record = new Object[3];
			record[0] = no;
			record[1] = key;
			record[2] = value;
			no++;

			defaultTableModel.addRow(record);
		}

		table = new JTable(defaultTableModel);
		//table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

		// 정렬
		table.setRowSorter(new TableRowSorter<DefaultTableModel>(defaultTableModel));
		
		TableColumnModel columnModel = table.getColumnModel();

		Dimension tablePreferredSize = table.getPreferredSize();
		int width0 = Math.round(tablePreferredSize.width * 0.1f);
		int width1 = Math.round(tablePreferredSize.width * 0.3f);
		int width2 = Math.round(tablePreferredSize.width * 0.6f);
		System.out.println("tableSize : " + tablePreferredSize + ", width : " + width0 + ", " + width1 + ", " + width2);
		
		columnModel.getColumn(0).setPreferredWidth(width0);
		columnModel.getColumn(0).setMinWidth(width0);
		columnModel.getColumn(1).setPreferredWidth(width1);
		columnModel.getColumn(1).setMinWidth(width1);
		columnModel.getColumn(2).setPreferredWidth(width2);
		columnModel.getColumn(2).setMinWidth(width2);

		JToolBar toolBar = new JToolBar();
		toolBar.add(new JLabel(propTreeMap.size() + " 건"));
		toolBar.addSeparator();
		//toolBar.add(new JSeparator(SwingConstants.VERTICAL));
		toolBar.add(new JLabel(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss")));
		
		JPanel leftPanel = new JPanel(new BorderLayout());
		leftPanel.add(toolBar);
		
		JPanel rightPanel = new JPanel(new BorderLayout());
		FlatButton reloadButton = new FlatButton();
		reloadButton.setIcon(new FlatSVGIcon("com/github/devcode/studio/icons/refresh.svg"));
		reloadButton.setButtonType(ButtonType.toolBarButton);
		reloadButton.setHorizontalAlignment(SwingConstants.RIGHT);
		rightPanel.add(reloadButton, BorderLayout.EAST);
		
		JPanel infoPanel = new JPanel(new GridLayout(1, 2));
		infoPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		infoPanel.add(leftPanel, BorderLayout.WEST);
		infoPanel.add(rightPanel, BorderLayout.EAST);
		
		add(infoPanel, BorderLayout.NORTH);
		add(new JScrollPane(table), BorderLayout.CENTER);
	}
	
	private void load() {
		
	}

}
