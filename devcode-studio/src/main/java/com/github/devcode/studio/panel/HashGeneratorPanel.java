package com.github.devcode.studio.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.formdev.flatlaf.fonts.jetbrains_mono.FlatJetBrainsMonoFont;

import net.miginfocom.swing.MigLayout;

/**
 * 해쉬 생성기 패널
 */
public class HashGeneratorPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private static Logger logger = LogManager.getLogger(HashGeneratorPanel.class);
	
	private JPanel titlePanel = new JPanel(new BorderLayout());
	private JLabel titleLabel = new JLabel("Hash 생성기");
	
	private JPanel bodyPanel = new JPanel();
	private JPanel configPanel = new JPanel();
	private JPanel viewPanel = new JPanel();
	
	/** md2 체크박스 */
	private JCheckBox md2CheckBox = new JCheckBox("MD2");
	private JCheckBox md5CheckBox = new JCheckBox("MD5");
	private JCheckBox sha1CheckBox = new JCheckBox("SHA1");
	private JCheckBox sha256CheckBox = new JCheckBox("SHA256");
	private JCheckBox sha384CheckBox = new JCheckBox("SHA384");
	private JCheckBox sha512CheckBox = new JCheckBox("SHA512");
	
	private JTextArea inputTextArea = new JTextArea("");
	private JButton createButton = new JButton("생성");
	private JButton createAllButton = new JButton("전부 생성");
	private JTextArea textArea = new JTextArea("");
	
	private JPanel statusPanel = new JPanel(new BorderLayout());
	private JLabel statusInformLabel = new JLabel("");
	
	public HashGeneratorPanel() {
		setLayout(new BorderLayout());

		// 상단 제목
		titleLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		titleLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 14));
		titleLabel.setHorizontalAlignment(SwingConstants.LEFT);
		titlePanel.add(titleLabel);
		
		// 하단 상태바
		statusPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		statusPanel.add(statusInformLabel);
		
		// input
		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new BorderLayout());
		inputPanel.add(new JScrollPane(inputTextArea), BorderLayout.CENTER);
		
		// TextArea 폰트지정
		textArea.setFont(new Font(FlatJetBrainsMonoFont.FAMILY, Font.PLAIN, 12));
		
		configPanel.setLayout(new MigLayout("", "[][][][]", "[]"));
		
		// Row 0
		configPanel.add(new JLabel("입력 문자열", JLabel.RIGHT), "cell 0 0,width 150,height 20,top");
		configPanel.add(inputPanel, "cell 1 0,width 300,height 60");
		
		// Row 1
		configPanel.add(new JLabel("Hash 종류", JLabel.RIGHT), "cell 0 1,width 150,height 20");
		configPanel.add(md2CheckBox, "cell 1 1,width 10,height 20");
		configPanel.add(md5CheckBox, "cell 1 1,width 10,height 20");
		
		// Row 2
		configPanel.add(sha1CheckBox, "cell 1 2,width 10,height 20");
		configPanel.add(sha256CheckBox, "cell 1 2,width 10,height 20");
		configPanel.add(sha384CheckBox, "cell 1 2,width 10,height 20");
		configPanel.add(sha512CheckBox, "cell 1 2,width 10,height 20");
		
		// Row 3
		configPanel.add(createButton, "cell 1 3,width 100,height 20");
		configPanel.add(createAllButton, "cell 1 3,width 100,height 20");
		
		// 생성 버튼
		createButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setCreateTextArea();
			}
		});
		
		// 전부생성 버튼
		createAllButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				md2CheckBox.setSelected(true);
				md5CheckBox.setSelected(true);
				sha1CheckBox.setSelected(true);
				sha256CheckBox.setSelected(true);
				sha384CheckBox.setSelected(true);
				sha512CheckBox.setSelected(true);
				
				setCreateTextArea();
			}
		});
		
		viewPanel.setLayout(new BorderLayout());
		viewPanel.add(new JScrollPane(textArea), BorderLayout.CENTER);
		viewPanel.setBackground(Color.BLUE);
		
		bodyPanel.setLayout(new BorderLayout());
		bodyPanel.add(configPanel, BorderLayout.NORTH);
		bodyPanel.add(viewPanel, BorderLayout.CENTER);
		
		add(titlePanel, BorderLayout.NORTH);
		add(bodyPanel, BorderLayout.CENTER);
		add(statusPanel, BorderLayout.SOUTH);
	}
	
	/**
	 * TextArea 문자열 생성
	 */
	private void setCreateTextArea() {
		createButton.setEnabled(false);
		createAllButton.setEnabled(false);
		
		try {
			StringBuilder sb = new StringBuilder();
			int createCount = 0;
			String inputText = inputTextArea.getText();
			
			if(StringUtils.isBlank(inputText)) {
				JOptionPane.showMessageDialog(getRootPane(), "입력값이 없습니다.", "확인", JOptionPane.WARNING_MESSAGE);
				inputTextArea.setFocusable(true);
				return;
			}
			
			String ls = System.lineSeparator();
			String ls2 = ls + ls; 
			
			if(md2CheckBox.isSelected()) {
				String encValue = DigestUtils.md2Hex(inputText).toUpperCase();
				createCount++;
				
				sb.append("// MD2").append(ls);
				sb.append(encValue).append(ls2);
			}
			
			if(md5CheckBox.isSelected()) {
				String encValue = DigestUtils.md5Hex(inputText).toUpperCase();
				createCount++;
				
				sb.append("// MD5").append(ls);
				sb.append(encValue).append(ls2);
			}
			
			if(sha1CheckBox.isSelected()) {
				String encValue = DigestUtils.sha1Hex(inputText).toUpperCase();
				createCount++;
				
				sb.append("// SHA1").append(ls);
				sb.append(encValue).append(ls2);
			}
			
			if(sha256CheckBox.isSelected()) {
				String encValue = DigestUtils.sha256Hex(inputText).toUpperCase();
				createCount++;
				
				sb.append("// SHA256").append(ls);
				sb.append(encValue).append(ls2);
			}
			
			if(sha384CheckBox.isSelected()) {
				String encValue = DigestUtils.sha384Hex(inputText).toUpperCase();
				createCount++;
				
				sb.append("// SHA384").append(ls);
				sb.append(encValue).append(ls2);
			}
			
			if(sha512CheckBox.isSelected()) {
				String encValue = DigestUtils.sha512Hex(inputText).toUpperCase();
				createCount++;
				
				sb.append("// SHA512").append(ls);
				sb.append(encValue).append(ls2);
			}
			
			if(createCount == 0) {
				JOptionPane.showMessageDialog(getRootPane(), "선택된 Hash 종류가 없습니다.", "확인", JOptionPane.WARNING_MESSAGE);
				inputTextArea.setFocusable(true);
				return;
			}
			
			logger.info("Hash {}개 생성", createCount);
			
			textArea.setText(sb.toString());
			statusInformLabel.setText(createCount + "개의 Hash 생성완료. " + DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
		}catch (Exception e) {
			logger.error("ERROR", e);
			JOptionPane.showMessageDialog(getRootPane(), e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
		}finally {
			createButton.setEnabled(true);
			createAllButton.setEnabled(true);
		}
	}
	
}