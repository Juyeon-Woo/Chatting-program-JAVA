package exam.chat;

import java.util.*;
import java.io.*;
import java.net.*;

public class ServerChatterTest {
	public static void main(String[] args) {
		
		//접속되어 있는 클라이언트 정보
		ArrayList<ServerChatter> chatters = new ArrayList<ServerChatter>();

		//서버 소켓 
		ServerSocket serverSocket = null;
		Socket socket = null;
		
		//접속된 순서
		int no = 0;
		//클라이언트 정보
		ServerChatter chatter = null;
		try {
			serverSocket = new ServerSocket(9002);
			while(true) {
				System.out.println("******클라이언트 접속 대기중**********");
				socket=serverSocket.accept(); // accept()
			
				// 채팅 객체 생성
				chatter = new ServerChatter(socket, chatters, no);
				chatter.login();//대화명 입력 처리
				
				//채팅 객체를 ArrayList에 저장한다
				chatters.add(chatter);
				no++;
				
				//접속된 순서에 따라 1:1채팅을 시키기 위함
				if(no%2== 0) {
					// 두명의 채터가 들어오면 쓰레드를 시작시킴.
					chatters.get(no-2).start();
					chatters.get(no-1).start();
					
				}
				
			}
			}catch(IOException e) {
				System.out.println(e.getMessage());
			}finally {
		}
	}
}
//1개의 클라이언트
class ServerChatter extends Thread {
	//클라이언트 와 연결되어 있는 소켓
	Socket socket;
	//소켓으로부터 최정 입력 스트림(받아온 데이터를 읽어들이는 곳)
	BufferedReader br;
	//받아온 데이터를 최종 출력 스트림
	PrintWriter pw;
	
	
	ArrayList<ServerChatter> chatters;//접속된 순서 ...	
	int no;	// 아이디 대화명
	String id;
	
	public ServerChatter(Socket socket, ArrayList<ServerChatter> chatters, int no){
		this.socket = socket;
		this.chatters = chatters;
		this.no = no;
		//소켓으로부터 최종 입출력 스트링 얻기
		try {
			br=new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));
			pw=new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
			//pw=new PrintWriter(socket.getOutputStream());
		}catch(IOException e) {
			System.out.println(e.getMessage());
		}
	}
	public void login() {
		try {
			id=br.readLine();
			//password가 있다면 이곳에서 데이터베이스와 연결하여 확인 
			}catch(IOException e){
			System.out.println("login() 중 오류 발생");
			System.out.println(e.getMessage());
		}
	}
	//Thread는 메세지를 받아서 출력하고 클라이언트에 보내는 역할만 한다.
	public void run() {
		//사용자가 채팅을 계속하는 한 자기자신, 연결된 짝에게 읽은 메시지를 보내주면 된다.
		// 0짝수이면 1만큼 큰 요소 ----> 1
		// 1홀수이면 1만큼 작은요소 ---> 0
		//읽은 메시지 처리 부분
		int pairNo = (no % 2 == 0)? no + 1 : no - 1;
		//현재 클라이언트와 채팅 중인 클라이언트를 구하기(얻기)
		ServerChatter pair = chatters.get(pairNo);
		
		this.sendMessage("start");
		
		try {
			String message="";
			while(!message.equals("bye")) {
				System.out.println(id+"클라이언트가 메시지를 기다립니다.");
				message=br.readLine();
				System.out.println("받은 메시지 -->"+id+":"+message);
				
				//자신과 직접 연결된 클라이언트에게 메세지를 다시 전송한다. 
				this.sendMessage(id + ":"+ message);
				//1:1채팅을 하도록 연결된 클라이언트에게 메시지를 전송한다.
				pair.sendMessage(id + ":" + message);
				}
		}catch (IOException e){
			System.out.println("run()메세지 수신송신 중 예외 발생");
			System.out.println(e.getMessage());
		}finally {
			close();
			System.out.println("연결 닫고 쓰레드 종료");
		}
	}
	
	void sendMessage(String message) {
		try {
			pw.println(message);
			pw.flush();
		}catch (Exception e){
				System.out.println("sendMessage()중 예외 발생");
				System.out.println(e.getMessage());
			}
		}

	public void close() {
		try {
			if(pw!=null)pw.close();
			if(br!=null)br.close();
			if(socket!=null)socket.close();
		} catch(Exception e) {
			System.out.println("close() 중 오류 발생");
			System.out.println(e.getMessage());
		}
	}
}