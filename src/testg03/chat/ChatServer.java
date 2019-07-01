package testg03.chat;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.Vector;


public class ChatServer extends Frame implements ActionListener{

		Button btnExit;
		TextArea ta;
		Vector vChatList;
		ServerSocket ss;
		Socket sockClient;
		
		public ChatServer() {
			setTitle("ä�ü���");
			addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					dispose();
				}
			});
			vChatList=new Vector();
			btnExit=new Button("��������");
			btnExit.addActionListener(this);
			ta=new TextArea();
			add(ta,BorderLayout.CENTER);
			add(btnExit, BorderLayout.SOUTH);
			setBounds(250,250,200,200);
			setVisible(true);
			//chatstart() �޼ҵ�ȣ��
			chatStart();
	}
		public void chatStart() {
			//���� ����
			try {
				ss=new ServerSocket(5005);
				while (true) {
					sockClient=ss.accept();
					//�������� IP���
					ta.append(sockClient.getInetAddress().getHostAddress()+"������\n");
					
					ChatHandle threadChat=new ChatHandle();
					vChatList.add(threadChat);
					threadChat.start();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	@Override
	public void actionPerformed(ActionEvent e) {
		dispose();
	}
public static void main(String[] args) {
	new ChatServer();
}
class ChatHandle extends Thread{
	BufferedReader br=null;//�Է� ���
	PrintWriter pw=null;//��´��
	
	public ChatHandle() {
		try {
			InputStream is=sockClient.getInputStream();//�Է�
			br=new BufferedReader(new InputStreamReader(is));
			OutputStream os=sockClient.getOutputStream();//���
			pw=new PrintWriter(new OutputStreamWriter(os));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void sendAllClient(String msg) {
		int size=vChatList.size();
		try {
			for (int i = 0; i < size; i++) {
				ChatHandle chr=(ChatHandle)(vChatList.elementAt(i));
				chr.pw.println(msg);
				chr.pw.flush();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
try {
	String name=br.readLine();
	sendAllClient(name+"�Բ��� ����");
	while (true) {//ä�� ����ޱ�
		String msg=br.readLine();
		String str=sockClient.getInetAddress().getHostName();
		ta.append(msg+"\n");//ä�� ������ ta�� �߰�
		if (msg.equals("@@Exut")) {
			break;
		}else {
			sendAllClient(name+" : "+msg);
			//������ ��ο��� �޼��� ����
		}
	}
} catch (IOException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}finally {
	vChatList.remove(this);
	try {
		br.close();
		pw.close();
		sockClient.close();
	} catch (IOException e) {
	}//catch
}//finally
	}//run
}
}
