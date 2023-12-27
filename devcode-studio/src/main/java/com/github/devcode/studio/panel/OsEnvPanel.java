package com.github.devcode.studio.panel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.BorderFactory;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.extras.components.FlatButton;
import com.formdev.flatlaf.extras.components.FlatButton.ButtonType;

/**
 * OS Environment 값을 조회하는 테이블 패널
 */
public class OsEnvPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private static Logger logger = LogManager.getLogger(OsEnvPanel.class);

	private JTable table;
	private DefaultTableModel tableModel; 
	private JLabel informLabel = new JLabel();
	
	public OsEnvPanel() {
		setLayout(new BorderLayout());

		String header[] = { "No", "Key", "Value" };
		tableModel = new DefaultTableModel(header, 0) {
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

		table = new JTable(tableModel);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

		// 테이블 정렬
		table.setRowSorter(new TableRowSorter<DefaultTableModel>(tableModel));
		
		TableColumnModel columnModel = table.getColumnModel();

		Dimension tablePreferredSize = table.getPreferredSize();
		int width0 = Math.round(tablePreferredSize.width * 0.1f);
		int width1 = Math.round(tablePreferredSize.width * 0.3f);
		int width2 = Math.round(tablePreferredSize.width * 0.6f);
		logger.info("TableSize : " + tablePreferredSize + ", width : " + width0 + ", " + width1 + ", " + width2);
		
		columnModel.getColumn(0).setPreferredWidth(width0);
		columnModel.getColumn(0).setMinWidth(width0);
		columnModel.getColumn(1).setPreferredWidth(width1);
		columnModel.getColumn(1).setMinWidth(width1);
		columnModel.getColumn(2).setPreferredWidth(width2);
		columnModel.getColumn(2).setMinWidth(width2);

		JToolBar toolBar = new JToolBar();
		toolBar.add(informLabel);
		
		// Java Properties 정보조회 후 테이블 Set
		javaPropLoad();
		
		JPanel leftPanel = new JPanel(new BorderLayout());
		leftPanel.add(toolBar);
		
		JPanel rightPanel = new JPanel(new BorderLayout());
		FlatButton reloadButton = new FlatButton();
		reloadButton.setIcon(new FlatSVGIcon("com/github/devcode/studio/icons/refresh.svg"));
		reloadButton.setToolTipText("새로고침");
		reloadButton.setButtonType(ButtonType.toolBarButton);
		reloadButton.setHorizontalAlignment(SwingConstants.RIGHT);
		reloadButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				javaPropLoad();
			}
		});
		rightPanel.add(reloadButton, BorderLayout.EAST);
		
		JPanel infoPanel = new JPanel(new GridLayout(1, 2));
		infoPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		infoPanel.add(leftPanel, BorderLayout.WEST);
		infoPanel.add(rightPanel, BorderLayout.EAST);
		
		add(infoPanel, BorderLayout.NORTH);
		add(new JScrollPane(table), BorderLayout.CENTER);
	}
	
	/**
	 * Java Properties 정보조회 후 테이블 Set
	 */
	private void javaPropLoad() {
		tableModel.setRowCount(0);
		
		TreeMap<String, String> propTreeMap = new TreeMap<String, String>();

		Map<String, String> envMap = System.getenv();
		propTreeMap.putAll(envMap);

		int no = 1;
		for (String key : propTreeMap.keySet()) {
			String value = propTreeMap.get(key);

			Object[] record = new Object[3];
			record[0] = no;
			record[1] = key;
			record[2] = value;
			no++;

			tableModel.addRow(record);
		}
		
		String inform = propTreeMap.size() + " 건, " + DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
		informLabel.setText(inform);
	}

}
