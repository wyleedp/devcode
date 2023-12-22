package com.github.devcode.studio.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.UUID;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import net.miginfocom.swing.MigLayout;

/**
 * UUID를 설정한 개수에 따라 생성하는 패널
 */
public class UuidPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JPanel titlePanel = new JPanel(new BorderLayout());
	private JLabel titleLabel = new JLabel("UUID");
	
	private JPanel bodyPanel = new JPanel();
	private JPanel configPanel = new JPanel();
	private JPanel buttonGroupPanel = new JPanel();
	private JPanel viewPanel = new JPanel();
	
	private JSpinner createCountSpinner = new JSpinner();
	private JButton createUuidButton = new JButton("생성");
	private JTextArea uuidTextArea = new JTextArea("");
	
	private JPanel statusPanel = new JPanel(new BorderLayout());
	private JLabel statusInformLabel = new JLabel("");
	
	/**
	 * UUID 생성 기본값
	 */
	private static final int UUID_CREATE_DEFAULT_VALUE = 10;
	
	public UuidPanel() {
		setLayout(new BorderLayout());

		// 상단 제목
		titleLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		titleLabel.setHorizontalAlignment(SwingConstants.LEFT);
		titlePanel.add(titleLabel);
		
		// 하단 상태바
		statusPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		statusPanel.add(statusInformLabel);
		
		// 메인
		GridBagLayout bodyPanelGridBagLayout = new GridBagLayout();
		bodyPanel.setLayout(bodyPanelGridBagLayout);
		
		JLabel createCountConfigLabel = new JLabel("생성수", JLabel.RIGHT);
		createCountConfigLabel.setPreferredSize(new Dimension(150, 0));
		
		// 생성수 값지정(초기값:10, 최소:1, 최대:100, 증감값:1)
		SpinnerNumberModel numberModel = new SpinnerNumberModel(UUID_CREATE_DEFAULT_VALUE, 1, 100, 1);
		createCountSpinner.setModel(numberModel);
		
		configPanel.setLayout(new MigLayout("", "[][][][][]", "[]"));
		configPanel.add(createCountConfigLabel, "cell 1 0");
		configPanel.add(createCountSpinner, "cell 2 0,growx");
		
		// UUID 생성
		createUuidButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				createUuidButton.setEnabled(false);
				setCreateUuidTextArea();
				createUuidButton.setEnabled(true);
			}
		});
		buttonGroupPanel.add(createUuidButton);
		
		viewPanel.setLayout(new BorderLayout());
		viewPanel.add(new JScrollPane(uuidTextArea), BorderLayout.CENTER);
		viewPanel.setBackground(Color.BLUE);
		
		// GridBagConstraints.BOTH X,Y 전체 채움, GridBagConstraints.HORIZONTAL 가로 채움,  GridBagConstraints.VERTICAL 세로 채움
		gridBagAdd(bodyPanelGridBagLayout, bodyPanel, configPanel, GridBagConstraints.HORIZONTAL, 0, 0, 1, 1);
		gridBagAdd(bodyPanelGridBagLayout, bodyPanel, buttonGroupPanel, GridBagConstraints.HORIZONTAL, 0, 1, 1, 1);
		gridBagAdd(bodyPanelGridBagLayout, bodyPanel, viewPanel, GridBagConstraints.BOTH, 0, 2, 1, 8);
		
		//bodyPanel.add(configPanel);
		//bodyPanel.add(buttonGroupPanel);
		//bodyPanel.add(viewPanel);

		add(titlePanel, BorderLayout.NORTH);
		add(bodyPanel, BorderLayout.CENTER);
		add(statusPanel, BorderLayout.SOUTH);
	}
	
	/**
	 * UUID 생성 후 uuidTextArea 값지정
	 */
	public void setCreateUuidTextArea() {
		String createCountValue = createCountSpinner.getValue().toString();
		int createCount = UUID_CREATE_DEFAULT_VALUE;
		
		if(StringUtils.isNotBlank(createCountValue) && StringUtils.isNumeric(createCountValue)) {
			createCount = Integer.parseInt(createCountValue);
			if(createCount < 0 && createCount > 100) {
				createCount = UUID_CREATE_DEFAULT_VALUE;
			}
		}
		
		if(createCount > 0) {
			StringBuilder sb = new StringBuilder();
			for(int i=0; i<createCount; i++) {
				if(i > 0) {
					sb.append(System.lineSeparator());
				}
				
				sb.append(UUID.randomUUID().toString().toUpperCase());
			}
		
			uuidTextArea.setText(sb.toString());
			statusInformLabel.setText(createCount + "개의 UUID 생성완료. " + DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
		}
	}
	
	public void gridBagAdd(GridBagLayout gridBagLayout, JPanel panel, Component c, int fillConstraints, int x, int y, int w, int h){
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill= fillConstraints; //GridBagConstraints.BOTH;
        gbc.gridx = x;
        gbc.gridy = y;
        //gbc.gridwidth = w;
        //gbc.gridheight = h;
        gbc.weightx = w;      // 비율로 영역분해
        gbc.weighty = h;      // 비율로 영역분해
        
        gridBagLayout.setConstraints (c,gbc);
        panel.add(c);
    }
	
}
