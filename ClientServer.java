import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ClientServer {

	static String name;

	public static void main(String[] args) {
		// TODO 自动生成的方法存根
		new makeFrame();
	}
}

class SimpleFrame extends JFrame{
	private static final int DEFAULT_WIDTH=600;
	private static final int DEFAULT_HEIGHT=400;

	public SimpleFrame(){
		setSize(DEFAULT_WIDTH,DEFAULT_HEIGHT);
	}
}

class EnterFrame extends JFrame{
	private static final int DEFAULT_WIDTH1=300;
	private static final int DEFAULT_HEIGHT1=225;

	public EnterFrame(){
		setSize(DEFAULT_WIDTH1,DEFAULT_HEIGHT1);
	}
}

class makeFrame{
	makeFrame(){

		SimpleFrame frame=new SimpleFrame();
		frame.setTitle("客户端");
		frame.setLayout(new BorderLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel panel1=new JPanel();
		panel1.setLayout(new GridLayout(2,1,5,5));

		TextArea textArea1=new TextArea("",50,30,TextArea.SCROLLBARS_VERTICAL_ONLY);
		textArea1.setEditable(false);
		textArea1.setText("Welcome to chatroom!   ——designed by HT");
		panel1.add(textArea1);

		JTextArea textArea2=new JTextArea();
		textArea2.setLineWrap(true);
		panel1.add(textArea2);

		JPanel panel2=new JPanel(new GridLayout(2,5,5,5));

		JLabel IP=new JLabel("服务器IP：");
		panel2.add(IP);

		JTextField IPtextField=new JTextField();
		panel2.add(IPtextField);

		JButton Yesbutton=new JButton("确认");	
		Yesbutton.addActionListener(new YesAction(IPtextField));
		panel2.add(Yesbutton);

		JButton Enterbutton=new JButton("登入");	
		Enterbutton.addActionListener(new EnterAction(textArea1));
		panel2.add(Enterbutton);

		JButton Registerbutton=new JButton("注册");		
		Registerbutton.addActionListener(new RegisterAction());
		panel2.add(Registerbutton);

		JLabel ChatTo=new JLabel("私聊对象：");
		panel2.add(ChatTo);

		JTextField nameField=new JTextField();
		panel2.add(nameField);

		JButton Sendbutton=new JButton("发送");		
		Sendbutton.addActionListener(new SendAction(textArea2,nameField));
		panel2.add(Sendbutton);

		JButton Quitbutton=new JButton("退出");
		panel2.add(Quitbutton);
		Quitbutton.addActionListener(new QuitAction());

		frame.add(panel1,BorderLayout.CENTER);
		frame.add(panel2,BorderLayout.SOUTH);

		frame.setVisible(true);
		frame.setLocationRelativeTo(null);
	}

	class YesAction implements ActionListener{
		JTextField IP;
		YesAction(JTextField IP){
			this.IP=IP;
		}
		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO 自动生成的方法存根
			new Connect(IP.getText());
		}

	}
	class EnterAction implements ActionListener{
		TextArea textArea1;
		EnterAction(TextArea textArea12){
			this.textArea1=textArea12;
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO 自动生成的方法存根
			EnterFrame enterframe=new EnterFrame();
			enterframe.setLayout(new GridLayout(3,2,25,25));
			enterframe.setTitle("登入");

			JLabel UserNamelabel=new JLabel("帐号：");
			JTextField UserNametextField=new JTextField();
			enterframe.add(UserNamelabel);
			enterframe.add(UserNametextField);

			JLabel PassWordlabel=new JLabel("密码：");
			JPasswordField PassWordtextField=new JPasswordField();
			enterframe.add(PassWordlabel);
			enterframe.add(PassWordtextField);

			JButton Enterbutton=new JButton("登入");
			Enterbutton.addActionListener(new Enter1Action(UserNametextField,PassWordtextField,enterframe));
			enterframe.add(Enterbutton);

			JButton Quitbutton=new JButton("取消");
			Quitbutton.addActionListener(new frameQuitAction(enterframe));
			enterframe.add(Quitbutton);

			enterframe.setVisible(true);
			enterframe.setLocationRelativeTo(null);
		}
		class Enter1Action implements ActionListener{
			JFrame frame;
			JTextField a;
			JTextField b;
			String i;//标记是否登入成功
			Enter1Action(JTextField UserNametextField,JTextField PassWordtextField,JFrame frame){
				a=UserNametextField;
				b=PassWordtextField;
				this.frame=frame;
			}
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO 自动生成的方法存根
				String UserName=a.getText();
				String PassWord=b.getText();

				try {

					OutputStream outStream = Connect.s.getOutputStream();
					PrintWriter out=new PrintWriter(outStream,true);

					InputStream inStream=Connect.s.getInputStream();
					Scanner in=new Scanner(inStream);

					out.println("Enter");
					out.println(UserName);
					out.println(PassWord);
					out.println("Useless");

					while(true){
						if(in.hasNextLine()){
							i=in.nextLine();
							break;
						}
					}

				} catch (IOException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}

				if(i.equals("success")){
					frame.setVisible(false);
					ClientServer.name=UserName;

					Runnable r=new ClientThread(textArea1);
					Thread t=new Thread(r);
					t.start();

				}
				else if(i.equals("PasswordError")){
					JFrame errorframe1=new JFrame();
					errorframe1.setSize(200,150);
					errorframe1.setLayout(new BorderLayout());

					JLabel errorlabel1=new JLabel("密码错误！");
					errorframe1.add(errorlabel1,BorderLayout.CENTER);

					JButton OKbutton=new JButton("确定");
					OKbutton.addActionListener(new frameQuitAction(errorframe1));
					errorframe1.add(OKbutton,BorderLayout.SOUTH);

					errorframe1.setLocationRelativeTo(null);
					errorframe1.setVisible(true);
				}

				else{
					JFrame errorframe2=new JFrame();
					errorframe2.setSize(200,150);
					errorframe2.setLayout(new BorderLayout());

					JLabel errorlabel2=new JLabel("帐号不存在！");
					errorframe2.add(errorlabel2,BorderLayout.CENTER);

					JButton OKbutton=new JButton("确定");
					OKbutton.addActionListener(new frameQuitAction(errorframe2));
					errorframe2.add(OKbutton,BorderLayout.SOUTH);

					errorframe2.setLocationRelativeTo(null);
					errorframe2.setVisible(true);
				}
			}

		}
	}

	class RegisterAction implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO 自动生成的方法存根
			EnterFrame registerframe=new EnterFrame();
			registerframe.setLayout(new GridLayout(3,2,25,25));
			registerframe.setTitle("注册");

			JLabel UserNamelabel=new JLabel("帐号：");
			JTextField UserNametextField=new JTextField();
			registerframe.add(UserNamelabel);
			registerframe.add(UserNametextField);

			JLabel PassWordlabel=new JLabel("密码：");
			JPasswordField PassWordtextField=new JPasswordField();
			registerframe.add(PassWordlabel);
			registerframe.add(PassWordtextField);

			JButton register1button=new JButton("注册");
			register1button.addActionListener(new Register1Action(UserNametextField,PassWordtextField,registerframe));
			registerframe.add(register1button);

			JButton Quitbutton=new JButton("取消");
			Quitbutton.addActionListener(new frameQuitAction(registerframe));
			registerframe.add(Quitbutton);

			registerframe.setVisible(true);
			registerframe.setLocationRelativeTo(null);
		}
		class Register1Action implements ActionListener{
			JFrame frame;
			JTextField a;
			JTextField b;
			Register1Action(JTextField UserNametextField,JTextField PassWordtextField,JFrame frame){
				a=UserNametextField;
				b=PassWordtextField;
				this.frame=frame;
			}
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO 自动生成的方法存根
				String UserName=a.getText();
				String PassWord=b.getText();

				OutputStream outStream;
				try {
					outStream = Connect.s.getOutputStream();
					PrintWriter out=new PrintWriter(outStream,true);

					out.println("Register");
					out.println(UserName);
					out.println(PassWord);
					out.println("Useless");

					frame.setVisible(false);
				} catch (IOException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}

			}
		}
	}

	class SendAction implements ActionListener{
		JTextArea textArea2;
		JTextField nameField;
		SendAction(JTextArea textArea2,JTextField nameField){
			this.textArea2=textArea2;
			this.nameField=nameField;
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			if(nameField.getText().equals("")){
				// TODO 自动生成的方法存根
				try {
					OutputStream outStream=Connect.s.getOutputStream();

					PrintWriter out=new PrintWriter(outStream,true);

					out.println("Send");
					out.println(ClientServer.name+":"+textArea2.getText());
					out.println("Useless");
					out.println("Useless");

					textArea2.setText("");

				} catch (IOException e1) {
					// TODO 自动生成的 catch 块
					e1.printStackTrace();
				}
			}
			else{
				try {
					OutputStream outStream=Connect.s.getOutputStream();

					PrintWriter out=new PrintWriter(outStream,true);

					String name=nameField.getText();
					
					out.println("ChatTo");
					out.println(name);
					out.println("(私聊)"+ClientServer.name+" to "+name+":"+textArea2.getText());
					out.println(ClientServer.name);

					textArea2.setText("");

				} catch (IOException e1) {
					// TODO 自动生成的 catch 块
					e1.printStackTrace();
				}
			}
		}
	}

	class QuitAction implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO 自动生成的方法存根
			OutputStream outStream;
			try {
				outStream = Connect.s.getOutputStream();
				PrintWriter out=new PrintWriter(outStream,true);
				
				out.println("Quit");
				out.println(ClientServer.name);
				out.println("Useless");
				out.println("Useless");
				
			} catch (IOException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
			System.exit(0);
		}
	}

	class frameQuitAction implements ActionListener{
		JFrame frame;
		frameQuitAction(JFrame frame){
			this.frame=frame;
		}
		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO 自动生成的方法存根
			frame.setVisible(false);
		}
	}
}

class Connect{
	static Socket s;
	Connect(String IPaddress){
		try {
			s=new Socket(IPaddress,8111);
		} catch (UnknownHostException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}
}

class ClientThread implements Runnable{
	TextArea textArea1;
	ClientThread(TextArea textArea12){
		this.textArea1=textArea12;
	}

	@Override
	public void run() {
		// TODO 自动生成的方法存根
		try {
			InputStream inStream=Connect.s.getInputStream();
			Scanner in=new Scanner(inStream);

			while(true){
				if(in.hasNext())
					textArea1.append("\n"+in.nextLine());	
			}
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}
}