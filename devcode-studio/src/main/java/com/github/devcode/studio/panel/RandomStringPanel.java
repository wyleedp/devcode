package com.github.devcode.studio.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.formdev.flatlaf.fonts.jetbrains_mono.FlatJetBrainsMonoFont;

import net.miginfocom.swing.MigLayout;

/**
 * 랜덤문자열 생성 패널
 */
public class RandomStringPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private static Logger logger = LogManager.getLogger(RandomStringPanel.class);
	
	private JPanel titlePanel = new JPanel(new BorderLayout());
	private JLabel titleLabel = new JLabel("랜덤 문자열 생성");
	
	private JPanel bodyPanel = new JPanel();
	private JPanel configPanel = new JPanel();
	private JPanel viewPanel = new JPanel();
	
	/** 랜덤문자열 방식 */
	private JComboBox<String> typeComboBox = new JComboBox<>();
	
	/** 대소문자 체크여부 */
	private JCheckBox upperCaseCheckBox = new JCheckBox();
	private JSpinner createCountSpinner = new JSpinner();
	private JSpinner createStringLengthSpinner = new JSpinner();
	private JButton createUuidButton = new JButton("생성");
	private JTextArea uuidTextArea = new JTextArea("");
	
	private JPanel statusPanel = new JPanel(new BorderLayout());
	private JLabel statusInformLabel = new JLabel("");
	
	/** 생성 문자열 길이 기본값 */
	private static final int CREATE_STRING_LENGTH_VALUE = 30;
	
	/** 생성 기본값 */
	private static final int CREATE_DEFAULT_VALUE = 10;
	
	public RandomStringPanel() {
		setLayout(new BorderLayout());

		// 상단 제목
		titleLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		titleLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 14));
		titleLabel.setHorizontalAlignment(SwingConstants.LEFT);
		titlePanel.add(titleLabel);
		
		// 하단 상태바
		statusPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		statusPanel.add(statusInformLabel);
		
		// TextArea 폰트지정
		uuidTextArea.setFont(new Font(FlatJetBrainsMonoFont.FAMILY, Font.PLAIN, 12));
		
		// 랜덤 문자열 방식 - 콤보박스
		typeComboBox.setModel(new DefaultComboBoxModel<>(new String[]{
			  "알파벳+숫자"
			, "알파벳"
			, "숫자"
		}));
		
		
		// 문자열길이 값지정
		SpinnerNumberModel createStringLengthNumberModel = new SpinnerNumberModel(CREATE_STRING_LENGTH_VALUE, 5, 100, 1);
		createStringLengthSpinner.setModel(createStringLengthNumberModel);
		
		// 생성수 값지정(초기값:10, 최소:1, 최대:100, 증감값:1)
		SpinnerNumberModel numberModel = new SpinnerNumberModel(CREATE_DEFAULT_VALUE, 1, 100, 1);
		createCountSpinner.setModel(numberModel);
		
		configPanel.setLayout(new MigLayout("", "[][][][]", "[]"));
		
		// Row 0
		configPanel.add(new JLabel("랜덤 문자열 방식", JLabel.RIGHT), "cell 0 0,width 150,height 20");
		configPanel.add(typeComboBox, "cell 1 0,width 150,height 20");
		
		// Row 1
		configPanel.add(new JLabel("문자열길이", JLabel.RIGHT), "cell 0 1,width 150,height 20");
		configPanel.add(createStringLengthSpinner, "cell 1 1,width 80,height 20");
		
		// Row 2
		configPanel.add(new JLabel("생성수", JLabel.RIGHT), "cell 0 2,width 150,height 20");
		configPanel.add(createCountSpinner, "cell 1 2,width 80,height 20");
		
		// Row 3
		upperCaseCheckBox.setSelected(true);
		configPanel.add(new JLabel("대문자 변환", JLabel.RIGHT), "cell 0 3,width 150,height 20");
		configPanel.add(upperCaseCheckBox, "cell 1 3,width 80,height 20,height 20");
		
		// Row 4
		configPanel.add(createUuidButton, "cell 1 4,width 100,height 20");
		
		// 랜덤문자열 생성버튼
		createUuidButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				createUuidButton.setEnabled(false);
				setCreateUuidTextArea();
				createUuidButton.setEnabled(true);
			}
		});
		
		viewPanel.setLayout(new BorderLayout());
		viewPanel.add(new JScrollPane(uuidTextArea), BorderLayout.CENTER);
		viewPanel.setBackground(Color.BLUE);
		
		bodyPanel.setLayout(new BorderLayout());
		bodyPanel.add(configPanel, BorderLayout.NORTH);
		bodyPanel.add(viewPanel, BorderLayout.CENTER);
		
		add(titlePanel, BorderLayout.NORTH);
		add(bodyPanel, BorderLayout.CENTER);
		add(statusPanel, BorderLayout.SOUTH);
	}
	
	/**
	 * TextArea 문자열 지정
	 */
	private void setCreateUuidTextArea() {
		String createCountValue = createCountSpinner.getValue().toString();
		int createCount = CREATE_DEFAULT_VALUE;
		
		if(StringUtils.isNotBlank(createCountValue) && StringUtils.isNumeric(createCountValue)) {
			createCount = Integer.parseInt(createCountValue);
			if(createCount < 0 && createCount > 100) {
				createCount = CREATE_DEFAULT_VALUE;
			}
		}
		
		String createStringLengthValue = createStringLengthSpinner.getValue().toString();
		int createStringLength = CREATE_STRING_LENGTH_VALUE;
		
		if(StringUtils.isNotBlank(createStringLengthValue) && StringUtils.isNumeric(createStringLengthValue)) {
			createStringLength = Integer.parseInt(createStringLengthValue);
			if(createStringLength < 0 && createStringLength > 100) {
				createStringLength = CREATE_DEFAULT_VALUE;
			}
		}
		
		if(createCount > 0) {
			StringBuilder sb = new StringBuilder();
			String type = (String)typeComboBox.getSelectedItem();
			
			if(StringUtils.equalsIgnoreCase(type, "알파벳")) {
				for(int i=0; i<createCount; i++) {
					sb.append(RandomStringUtils.randomAlphabetic(createStringLength)).append(System.lineSeparator());
				}
			}else if(StringUtils.equalsIgnoreCase(type, "숫자")) {
				for(int i=0; i<createCount; i++) {
					sb.append(RandomStringUtils.randomNumeric(createStringLength)).append(System.lineSeparator());
				}
			}else if(StringUtils.equalsIgnoreCase(type, "알파벳+숫자")) {
				for(int i=0; i<createCount; i++) {
					sb.append(RandomStringUtils.randomAlphanumeric(createStringLength)).append(System.lineSeparator());
				}
			}else {
				JOptionPane.showMessageDialog(getRootPane(), "설정확인", "확인", JOptionPane.WARNING_MESSAGE);
			}
			
			String text = "";
			
			if(upperCaseCheckBox.isSelected()) {
				text = StringUtils.upperCase(sb.toString());
			}else {
				text = sb.toString();
			}
			
			logger.info("랜덤문자열 {}개 생성", createCount);
			
			uuidTextArea.setText(text);
			statusInformLabel.setText(createCount + "개의 랜덤문자열 생성완료. " + DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
		}
	}
	
}