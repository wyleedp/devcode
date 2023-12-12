package com.github.devcode.studio.example;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;

import com.formdev.flatlaf.FlatLightLaf;

public class JButtonExample extends JFrame {
	
	public JButtonExample() {
		super("버튼 테스트");
		
		FlatLightLaf.setup();
		
		// 버튼을 넣기위한 패널 생성
		JPanel buttonPanel = new JPanel();
		// 'Button'이름을 가진 버튼 생성
		JButton btn = new JButton("Button");
		// 버튼 넣기
		buttonPanel.add(btn);

		// 버튼안에 이미지 넣기
		ImageIcon icon1 = new ImageIcon(".\\src\\red.png");
		ImageIcon icon2 = new ImageIcon(".\\src\\black.png");
		JToggleButton tgbutton = new JToggleButton(icon1);
		buttonPanel.add(tgbutton);

		// '취미'레이블
		JLabel lblHobby = new JLabel("취미");
		// 체크박스
		JCheckBox check1 = new JCheckBox("운동");
		JCheckBox check2 = new JCheckBox("독서");
		JCheckBox check3 = new JCheckBox("영화감상");
		buttonPanel.add(lblHobby);
		buttonPanel.add(check1);
		buttonPanel.add(check2);
		buttonPanel.add(check3);

		// '성별'레이블
		JLabel lblMale = new JLabel("성별");
		//
		JRadioButton rb1 = new JRadioButton("남자");
		JRadioButton rb2 = new JRadioButton("여자");
		ButtonGroup bg = new ButtonGroup();
		bg.add(rb1);
		bg.add(rb2);
		buttonPanel.add(lblMale);
		buttonPanel.add(rb1);
		buttonPanel.add(rb2);

		add(buttonPanel);

		setLocation(150, 200);
		setSize(242, 150);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	public static void main(String[] args) {
		new JButtonExample();
	}

}