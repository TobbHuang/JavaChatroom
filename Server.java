import java.awt.BorderLayout;
import java.awt.FileDialog;
import java.awt.GridLayout;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;


public class Server {

	static Thread [] thread=new Thread [50];//创建数组管理线程
	static ThreadedHandler[] threaded=new ThreadedHandler[50];
	static String [] Name=new String[50];//记录对应的用户名
	static int i=0;//记录已登入的人数

	public static void main(String[] args) {
		// TODO 自动生成的方法存根

		ServerFrame frame=new ServerFrame();
		frame.setTitle("服务器");
		frame.setLayout(new BorderLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel panel1=new JPanel();
		panel1.setLayout(new GridLayout(1,2,20,20));
		frame.add(panel1,BorderLayout.CENTER);

		TextArea nametextArea=new TextArea("",50,30,TextArea.SCROLLBARS_VERTICAL_ONLY);
		nametextArea.setText("在线人员：\n");
		nametextArea.setEditable(false);
		panel1.add(nametextArea);


		TextArea textArea=new TextArea("",50,30,TextArea.SCROLLBARS_VERTICAL_ONLY);
		textArea.setEditable(false);
		panel1.add(textArea);

		JPanel panel2=new JPanel();
		panel2.setLayout(new GridLayout(1,3,10,10));
		frame.add(panel2,BorderLayout.SOUTH);

		JTextField textField=new JTextField();
		panel2.add(textField);

		JButton TButton=new JButton("踢人确认");
		TButton.addActionListener(new TAction(textField,nametextArea,frame,textArea));
		panel2.add(TButton);

		JButton saveButton=new JButton("保存聊天记录");
		saveButton.addActionListener(new saveAction(textArea,frame));
		panel2.add(saveButton);

		frame.setVisible(true);
		frame.setLocationRelativeTo(null);

		try {
			ServerSocket ss=new ServerSocket(8111);
			while(true){
				Socket s=ss.accept();
				ThreadedHandler r=new ThreadedHandler(s,textArea,nametextArea);
				threaded[i]=r;
				Thread t=new Thread(r);
				thread[i]=t;
				i++;
				t.start();
			}
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}
}

class ThreadedHandler implements Runnable{
	Socket s;
	TextArea textArea;
	TextArea nametextArea;

	PrintWriter out;
	OutputStream outStream;

	ThreadedHandler(Socket i,TextArea textArea2,TextArea nametextArea2){
		s=i;
		this.textArea=textArea2;
		this.nametextArea=nametextArea2;
		try {
			outStream=s.getOutputStream();
			out=new PrintWriter(outStream,true);
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}


	@Override
	public void run() {
		// TODO 自动生成的方法存根
		try{
			InputStream inStream;
			inStream = s.getInputStream();
			outStream=s.getOutputStream();

			Scanner in=new Scanner(inStream);

			File file=new File("F:\\","Enter.txt");

			String message1;
			String message2;
			String message3;
			String message4;

			while(true){
				if(in.hasNextLine()){

					message1=in.nextLine();
					message2=in.nextLine();
					message3=in.nextLine();
					message4=in.nextLine();

					if(message1.equals("Enter")){
						//将帐号信息写入内存

						String [] Enterstr = new String [100];//最多50个帐号
						RandomAccessFile EnterStream=new RandomAccessFile(file,"r");
						for(int k=0;k<100;k++){
							Enterstr[k]=EnterStream.readLine();
						}
						EnterStream.close();

						int i=0;//标记是否找到用户名
						int k=0;
						String j=" ";//标记是否登入成功，success代表成功，PasswordError代表密码错误，NoUsername代表帐号不存在

						while(k<99){
							if(message2.equals(Enterstr[k])) {
								i=1;										
								break;
							}
							k++;
						}
						if(i==1&&message3.equals(Enterstr[k+1])){
							j="success";
							out.println(j);

							for(int m=0;m<Server.i;m++)
								Server.threaded[m].out.println(message2+"已经成功登陆！");

							textArea.append(message2+"已经成功登陆！"+"\n");
							Server.Name[Server.i-1]=message2;
							String namelist="在线人员："+"\n";
							for(int m=0;m<Server.i;m++)
								namelist=namelist+Server.Name[m]+"\n";
							nametextArea.setText(namelist);
						}

						else if(i==1&&(message3.equals(Enterstr[k+1]))==false){
							j="PasswordError";
							out.println(j);
						}

						else{
							j="NoUsername";
							out.println(j);
						}
					}
					else if(message1.equals("Register")){
						try {

							file=new File("F:\\","Enter.txt");

							FileWriter writeOut = new FileWriter(file,true);
							writeOut.write("\r\n"+message2);
							writeOut.write("\r\n"+message3);

							writeOut.close();
						} catch (FileNotFoundException e) {
							// TODO 自动生成的 catch 块
							e.printStackTrace();
						} catch (IOException e) {
							// TODO 自动生成的 catch 块
							e.printStackTrace();
						}
					}

					else if(message1.equals("Send")){

						for(int m=0;m<Server.i;m++)
							Server.threaded[m].out.println(message2);

						textArea.append(message2+"\n");
					}

					else if(message1.equals("ChatTo")){
						int m;
						for(m=0;m<Server.i;m++){
							if(Server.Name[m].equals(message2))
								break;
						}
						
						if(m<Server.i){
							Server.threaded[m].out.println(message3);
							textArea.append(message3+"\n");
							for(m=0;m<Server.i;m++){
								if(Server.Name[m].equals(message4))
									break;
							}
							Server.threaded[m].out.println(message3);
						}
						
					}

					else if(message1.equals("Quit")){
						int m;
						for(m=0;m<Server.i;m++){
							if(Server.Name[m].equals(message2)){
								break;
							}
						}
						textArea.append(Server.Name[m]+"已下线！"+"\n");
						for(int j=m;j<Server.i-1;j++){
							Server.thread[j]=Server.thread[j+1];
							Server.threaded[j]=Server.threaded[j+1];
							Server.Name[j]=Server.Name[j+1];
						}
						Server.thread[Server.i-1]=null;
						Server.threaded[Server.i-1]=null;
						Server.Name[Server.i-1]=null;
						Server.i--;
						String namelist="在线人员："+"\n";
						for(int j=0;j<Server.i;j++)
							namelist=namelist+Server.Name[j]+"\n";
						nametextArea.setText(namelist);
					}
				}
			}
		}catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}

}

class ServerFrame extends JFrame{
	private static final int DEFAULT_WIDTH=600;
	private static final int DEFAULT_HEIGHT=400;

	ServerFrame(){
		setSize(DEFAULT_WIDTH,DEFAULT_HEIGHT);
	}
}

class TAction implements ActionListener{
	JTextField textField;
	TextArea nametextArea;
	JFrame frame;
	TextArea textArea;

	TAction(JTextField textField,TextArea nametextArea2,JFrame frame,TextArea textArea2){
		this.textField=textField;
		this.nametextArea=nametextArea2;
		this.frame=frame;
		this.textArea=textArea2;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO 自动生成的方法存根
		String name=textField.getText();
		int i=0;//标记是否找到在线用户
		int m;
		for(m=0;m<Server.i;m++){
			if(Server.Name[m].equals(name)){
				i=1;
				break;
			}
		}
		if(i==1){

			textArea.append(Server.Name[m]+"已被服务器踢出！"+"\n");

			Server.threaded[m].out.println("你已被服务器踢出！");
			Server.thread[m].stop();
			for(int j=m;j<Server.i-1;j++){
				Server.thread[j]=Server.thread[j+1];
				Server.threaded[j]=Server.threaded[j+1];
				Server.Name[j]=Server.Name[j+1];
			}
			Server.thread[Server.i-1]=null;
			Server.threaded[Server.i-1]=null;
			Server.Name[Server.i-1]=null;
			Server.i--;
			
			for(int n=0;n<Server.i;n++)
				Server.threaded[n].out.println(Server.Name[m]+"已被服务器踢出！"+"\n");
			
			String namelist="在线人员："+"\n";
			for(int j=0;j<Server.i;j++)
				namelist=namelist+Server.Name[j]+"\n";
			nametextArea.setText(namelist);
		}
		else{
			JOptionPane.showMessageDialog(frame, "未找到指定用户！");
		}

	}

}

class saveAction implements ActionListener{

	TextArea textArea;
	JFrame frame;

	saveAction(TextArea textArea2,JFrame frame){
		this.textArea=textArea2;
		this.frame=frame;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO 自动生成的方法存根
		FileDialog saveFile=new FileDialog(frame,"保存聊天记录",FileDialog.SAVE);
		saveFile.setVisible(true);
		File file=new File(saveFile.getDirectory(),saveFile.getFile());
		try {
			FileWriter writeOut = new FileWriter(file);
			writeOut.write(textArea.getText());
			writeOut.close();
		} catch (IOException f) {
			// TODO 自动生成的 catch 块
			f.printStackTrace();
		}
	}

}
