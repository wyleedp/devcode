package com.github.devcode.studio.panel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.TreeMap;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.extras.components.FlatButton;
import com.formdev.flatlaf.extras.components.FlatButton.ButtonType;

/**
 * Java Properties 값을 조회하는 테이블 패널
 */
public class JavaPropertiesPanel extends JPanel{

	private static final long serialVersionUID = 1L;

	private static Logger logger = LogManager.getLogger(JavaPropertiesPanel.class);
	
	private static final String FILE_SEPARATOR = System.getProperty("file.separator");
	
	private JTable table;
	private DefaultTableModel tableModel; 
	private JLabel informLabel = new JLabel();
	
	private JPopupMenu popupMenu = new JPopupMenu();
	private JMenuItem fileExplorerMenuItem = new JMenuItem("파일탐색기 열기");
	private JMenuItem commandMenuItem = new JMenuItem("터미널 열기");
	
	public JavaPropertiesPanel() {
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
		logger.info("TableWidthSize : {}, {}, {}", width0, width1, width2);
		
		columnModel.getColumn(0).setPreferredWidth(width0);
		columnModel.getColumn(0).setMinWidth(width0);
		columnModel.getColumn(1).setPreferredWidth(width1);
		columnModel.getColumn(1).setMinWidth(width1);
		columnModel.getColumn(2).setPreferredWidth(width2);
		columnModel.getColumn(2).setMinWidth(width2);
		
		// Value 컬럼 Cell 렌더링 - key값이 home, path, dir로 끝나면 폴더아이콘표시(현재 윈도우 플랫폼만 적용)
		if(SystemUtils.IS_OS_WINDOWS) {
			TableColumn column = columnModel.getColumn(2);
			column.setCellRenderer(createRenderer());
		}
		
		// 테이블 우클릭 메뉴
		popupMenu = new JPopupMenu();
		popupMenu.add(fileExplorerMenuItem);
		popupMenu.add(commandMenuItem);
		
		fileExplorerMenuItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int selectedRow = table.getSelectedRow();
				Object valueObj = table.getValueAt(selectedRow, 2);
				String value = valueObj != null ? (String)valueObj : "";
				logger.info("파일탐색기 열기. Dir : {}", value);
				
				if(StringUtils.isBlank(value)) {
					JOptionPane.showMessageDialog(getRootPane(), "값이 존재하지 않습니다.", "확인", JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				File file = new File(value);
				if(!file.exists() || !file.isDirectory()) {
					JOptionPane.showMessageDialog(getRootPane(), "폴더만 파일탐색기를 실행할 수 있습니다.", "확인", JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				// 탐색기 명령어 예)
				//   - c:\Windows\explorer.exe /e/root/select,c:\Windows
				String explorerCommand = System.getenv("SystemRoot") + FILE_SEPARATOR + "explorer.exe";
				
				File explorerCommandFile = new File(explorerCommand);
				if(!explorerCommandFile.exists() || !explorerCommandFile.canExecute()) {
					JOptionPane.showMessageDialog(getRootPane(), "explorer.exe 파일이 존재하지 않거나 실행권한이 없습니다.", "확인", JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				String[] commnds = new String[]{explorerCommand, "/e/root/select,"+value};
				logger.info("ExplorerCommand : {}", StringUtils.join(commnds, " "));
				
				Runtime rt = Runtime.getRuntime();
		        Process process = null;  
		        
		        try {
		            process = rt.exec(commnds);
		            List<String> inputLines = IOUtils.readLines(process.getInputStream(), "EUC-KR");
		            List<String> errorLines = IOUtils.readLines(process.getErrorStream(), "EUC-KR");
		            
		            if(inputLines != null && inputLines.size() > 0) {
		            	logger.error("INPUT - {}", StringUtils.join(inputLines, "<LS>"));
		            }
		            
		            if(errorLines != null && errorLines.size() > 0) {
		            	logger.error("ERROR - {}", StringUtils.join(errorLines, "<LS>"));
		            }
		        } catch (Exception ex) {
		            logger.error("ERROR", ex);
		        }finally {
		        	if(process != null) {
		        		process.destroy();
		        	}
		        }
			}
		});
		
		commandMenuItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int selectedRow = table.getSelectedRow();
				Object valueObj = table.getValueAt(selectedRow, 2);
				String value = valueObj != null ? (String)valueObj : "";
				logger.info("터미널 열기. Dir : {}", value);
				
				if(StringUtils.isBlank(value)) {
					JOptionPane.showMessageDialog(getRootPane(), "값이 존재하지 않습니다.", "확인", JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				File file = new File(value);
				if(!file.exists() || !file.isDirectory()) {
					JOptionPane.showMessageDialog(getRootPane(), "폴더만 터미널을 실행할 수 있습니다.", "확인", JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				// 터미널 명령어 예)
				//   - C:\Windows\system32\cmd.exe /C start "C:\Users\wylee\AppData\Local\Temp\" /D "C:\Users\wylee\AppData\Local\Temp\"
				String cmdCommand = System.getenv("ComSpec");
				if(StringUtils.isBlank(cmdCommand)) {
					cmdCommand = System.getenv("SystemRoot") + FILE_SEPARATOR + "System32" + FILE_SEPARATOR + "cmd.exe";
				}
				
				File cmdCommandFile = new File(cmdCommand);
				if(!cmdCommandFile.exists() || !cmdCommandFile.canExecute()) {
					JOptionPane.showMessageDialog(getRootPane(), "cmd.exe 파일이 존재하지 않거나 실행권한이 없습니다.", "확인", JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				value = StringUtils.wrap(value, "\"");
				String[] commnds = new String[]{cmdCommand, "/C", "start", value, "/D", value};
				logger.info("Command : {}", StringUtils.join(commnds, " "));
				
				Runtime rt = Runtime.getRuntime();
		        Process process = null;
		        
		        try {
		            process = rt.exec(commnds);
		            List<String> inputLines = IOUtils.readLines(process.getInputStream(), "EUC-KR");
		            List<String> errorLines = IOUtils.readLines(process.getErrorStream(), "EUC-KR");
		            
		            if(inputLines != null && inputLines.size() > 0) {
		            	logger.error("INPUT - {}", StringUtils.join(inputLines, "<LS>"));
		            }
		            
		            if(errorLines != null && errorLines.size() > 0) {
		            	logger.error("ERROR - {}", StringUtils.join(errorLines, "<LS>"));
		            }
		        } catch (Exception ex) {
		            logger.error("ERROR", ex);
		        }finally {
		        	if(process != null) {
		        		process.destroy();
		        	}
		        }
			}
		});
		
		table.setComponentPopupMenu(popupMenu);
		table.addMouseListener(new MouseListener() {
			
			@Override
			public void mousePressed(MouseEvent e) {
				// 마우스 우클릭시에도 Row가 선택되게 적용
				Point point = e.getPoint();
		        int currentRow = table.rowAtPoint(point);
		        table.setRowSelectionInterval(currentRow, currentRow);
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				
			}

			@Override
			public void mouseExited(MouseEvent e) {
				
			}
			
		});

		JToolBar toolBar = new JToolBar();
		toolBar.add(informLabel);
		
		// Java Properties 정보조회 후 테이블 Set
		javaPropLoad();
		
		JPanel leftPanel = new JPanel(new BorderLayout());
		leftPanel.add(toolBar);
		
		JPanel rightPanel = new JPanel(new BorderLayout());
		JPanel rightFlowPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		
		// 복사 버튼
		FlatButton copyButton = new FlatButton();
		copyButton.setIcon(new FlatSVGIcon("com/github/devcode/studio/icons/copy.svg"));
		copyButton.setToolTipText("복사");
		copyButton.setButtonType(ButtonType.toolBarButton);
		copyButton.setHorizontalAlignment(SwingConstants.RIGHT);
		copyButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				copyJavaProp();
			}
		});
		rightFlowPanel.add(copyButton);
		
		// 다운로드 버튼
		FlatButton downloadButton = new FlatButton();
		downloadButton.setIcon(new FlatSVGIcon("com/github/devcode/studio/icons/download.svg"));
		downloadButton.setToolTipText("다운로드");
		downloadButton.setButtonType(ButtonType.toolBarButton);
		downloadButton.setHorizontalAlignment(SwingConstants.RIGHT);
		downloadButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				downloadJavaPropTextFile();
			}
		});
		rightFlowPanel.add(downloadButton);
		
		// 리로드 버튼
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
		rightFlowPanel.add(reloadButton);
		
		
		
		rightPanel.add(rightFlowPanel, BorderLayout.EAST);
		
		JPanel infoPanel = new JPanel(new GridLayout(1, 2));
		//infoPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		infoPanel.add(leftPanel, BorderLayout.WEST);
		infoPanel.add(rightPanel, BorderLayout.EAST);
		
		add(infoPanel, BorderLayout.NORTH);
		add(new JScrollPane(table), BorderLayout.CENTER);
	}
	
	/**
	 * key값이 home, path, dir로 끝나고 value 값에 ; 문자열이 없으면 폴더 아이콘 표시
	 * 
	 * @return
	 */
	private static TableCellRenderer createRenderer() {
		return new DefaultTableCellRenderer() {

			private static final long serialVersionUID = 1L;

			{
				super.setHorizontalTextPosition(SwingConstants.LEFT);
			}
		
			@Override
			public Component getTableCellRendererComponent(JTable table, Object valueObj, boolean isSelected, boolean hasFocus, int row, int column) {
				super.getTableCellRendererComponent(table, valueObj, isSelected, hasFocus, row, column);
				
				Object keyColumnValueAt = table.getValueAt(row, 1);
				String key = (String)keyColumnValueAt;
				String value = valueObj != null ? (String)valueObj : "";
				boolean isDirectory = false;
				
				if(StringUtils.isNotBlank(value) && StringUtils.endsWithAny(key.toLowerCase(), "home", "path", "dir") && StringUtils.containsNone(value, ";")) {
					isDirectory = true;
				}
				
				//logger.info("Row : {}, Column : {}, Key : {}", row, column, key);
				
				if(isDirectory) {
					setIcon(UIManager.getIcon("Tree.closedIcon"));
					setIconTextGap(5);
				}else{
					setIcon(null);
					setIconTextGap(0);
				}
				
				
				
				return this;
			}
		};
	}
	
	/**
	 * Java Properties 정보조회 후 테이블 Set
	 */
	private void javaPropLoad() {
		tableModel.setRowCount(0);
		
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

			tableModel.addRow(record);
		}
		
		String inform = propTreeMap.size() + " 건, " + DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
		informLabel.setText(inform);
	}
	
	/**
	 * 자바프로퍼티 테이블을 문자열로 변환
	 * 
	 * @return
	 */
	private String javaPropToString() {
		DefaultTableModel model = (DefaultTableModel)table.getModel();
		
		if(model.getRowCount() == 0) {
			return "";
		}
		
		StringBuilder builder = new StringBuilder();
		
		builder.append("No\tKey\tValue").append(System.lineSeparator());
		
		for(int i=0; i<model.getRowCount(); i++) {
			Object noObj = model.getValueAt(i, 0);
			Object keyObj = model.getValueAt(i, 1);
			Object valueObj = model.getValueAt(i, 2);
			
			Integer no = noObj != null ? (Integer)noObj : null;
			String key = keyObj != null ? (String)keyObj : "";
			String value = valueObj != null ? (String)valueObj : "";
			
			builder.append(no).append("\t");
			builder.append(key).append("\t");
			builder.append(value).append(System.lineSeparator());
		}
		
		return builder.toString();
	}
	
	/**
	 * 텍스트파일 다운로드
	 */
	private void downloadJavaPropTextFile() {
		try {
			String javaPropString = javaPropToString();
			
			if(StringUtils.isBlank(javaPropString)) {
				JOptionPane.showMessageDialog(getRootPane(), "다운로드 데이터가 존재하지 않습니다.", "확인", JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			String textFileName = "JavaProperties_"+DateFormatUtils.format(new Date(), "yyyyMMddHHmmss")+".txt";
			
			JFileChooser fileChooser = new JFileChooser();
			
			// TODO : 최종저장 선택폴더 불러오기(Properties 또는 H2 DB)
			fileChooser.setSelectedFile(new File(FileUtils.getUserDirectoryPath() + File.separator + textFileName));
			
			int showSaveDialog = fileChooser.showSaveDialog(this);
			if(showSaveDialog == JFileChooser.APPROVE_OPTION) {
				File currentDirectory = fileChooser.getCurrentDirectory();  // 현재 선택한 폴더
				File selectedFile = fileChooser.getSelectedFile();          // 선택/입력한 파일경로(파일명포함)
				String selectedInputFileName = selectedFile.getName();
				logger.info("현재폴더 : {}, 선택/입력한 파일 : {}, 선택/입력한 파일명 : {}", currentDirectory.toString(), selectedFile.toString(), selectedInputFileName);
				
				// 파일이 존재하는지 확인(폴더만 선택되게끔 설정되어 있으면 미사용)
				if(selectedFile.exists() && selectedFile.isFile()) {
					int showConfirmDialog = JOptionPane.showConfirmDialog(getRootPane(), "파일이 존재합니다. 덮어씌우시겠습니까?", "확인", JOptionPane.YES_NO_OPTION);
					if(showConfirmDialog == JOptionPane.NO_OPTION) {
						return;
					}
					
					logger.info("덮어씌우기 옵션 YES. 파일삭제 : {}", selectedFile.toString());
					if(selectedFile.exists()) {
						if(selectedFile.delete()) {
							logger.info("덮어씌우기 옵션 YES. 파일삭제성공 : {}", selectedFile.toString());
						}
					}
				}
				
				logger.info("저장하기 파일 : {}", selectedFile.toString());
				
				String textFilePath = FilenameUtils.normalizeNoEndSeparator(FileUtils.getTempDirectoryPath()) + File.separator + textFileName;
				File textFile = new File(textFilePath);
				
				FileUtils.writeStringToFile(textFile, javaPropString, "UTF-8");
				logger.info("TextFilePath 생성완료 - {}, FileSize : {}", textFilePath, FileUtils.byteCountToDisplaySize(textFile.length()));
				
				// 선택한 폴더가 존재하지 않습니다.
				if(!currentDirectory.exists()) {
					JOptionPane.showMessageDialog(getRootPane(), "선택한 폴더가 존재하지 않습니다.", "확인", JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				// 파일 이동
				FileUtils.moveFile(textFile, selectedFile);
				
				// 파일이동완료
				logger.info("파일이동완료. SourceFile - {}, TargetFile = {}", textFile.toString(), selectedFile.toString());
				
				JOptionPane.showMessageDialog(getRootPane(), "파일저장성공("+selectedFile.toString()+")", "확인", JOptionPane.INFORMATION_MESSAGE);
			}
			
		} catch (IOException e) {
			logger.error("ERROR", e);
		}
	}
	
	/**
	 * 클립보드 복사
	 */
	private void copyJavaProp() {
		try {
			String javaPropString = javaPropToString();
			
			if(StringUtils.isBlank(javaPropString)) {
				JOptionPane.showMessageDialog(getRootPane(), "복사할 데이터가 존재하지 않습니다.", "확인", JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			StringSelection stringSelection = new StringSelection(javaPropString);
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(stringSelection, stringSelection);
			
			JOptionPane.showMessageDialog(getRootPane(), "복사가 완료되었습니다.", "확인", JOptionPane.INFORMATION_MESSAGE);
			logger.info("클립보드 복사완료. JavaPropStringLength : {}", javaPropString.length());
			
		} catch (Exception e) {
			logger.error("ERROR", e);
		}
	}

}