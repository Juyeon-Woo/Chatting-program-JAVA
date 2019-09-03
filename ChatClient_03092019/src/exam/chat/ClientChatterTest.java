
package exam.chat;

import java.io.*;
import java.net.*;

public class ClientChatterTest {

	public static void main(String[] args) {
		ClientChatter chatter = new ClientChatter();

		chatter.login(); // 대화명 입력
		chatter.ready(); // 대화 시작을 기다린다.

		chatter.start(); // 1번쓰레드
		chatter.chatProcess(); // 2번쓰레드
	}
}

class ClientChatter extends Thread {
	Socket socket;
	String id;

	BufferedReader stdin; // 표준입력객체(키보드)

	BufferedReader br; // 소켓 입력 객체
	PrintWriter pw; // 소켓 출력 객체
	

	public ClientChatter() {
		try {
			socket = new Socket("127.0.0.1", 9002);

			br = new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));
			pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println("Socket 생성 및 i/o stream얻기에서 예외발생..");
		}
	}

	public void ready() { // 맨처음 시작 메세지를 처리
		try {
			String msg = br.readLine();
			System.out.println(msg);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println("ready() 에서 예외 발생....");
		}
	}

	// 로그인 처리 - 키보드로 아이디 입력받아 서버로 보낸다.
	public void login() {
		try {
			stdin = new BufferedReader(new InputStreamReader(System.in));
			System.out.print("id를 입력하시오 ==> ");
			id = stdin.readLine();
			pw.println(id);
			pw.flush();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println("login()중 예외 발생....");
		}
	}
	
	// 두번째 쓰레드에서 처리되는 부분
	public void chatProcess() { // 채팅 처리 - 메세지를 입력받아 서버에 보낸다.
		try {
			String msg = "";
			while (!msg.equals("bye")) {
				System.out.println("메세지를 입력하시오==>");
				msg = stdin.readLine();
				pw.println(msg);
				pw.flush();
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println("메세지를 입력받아 전송중 예외 발생....");
		} finally {
			close();
			System.out.println("chatProcess() 종료....");
		}
	}

//서버에서 오는 내용을 받아 화면에 출력하는 쓰레드
	public void run() { // 출력 기능만 수행하면 된다.
		try {
			String msg = "";
			while (!msg.equals("bye")) {
				msg = br.readLine();
				System.out.println(msg);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println("쓰레드에서 예외 발생....");
		} finally {
			close();
		}
	}

	public void close() {
		try {
			if(br!=null)close();
			if(pw!=null)close();
			if(socket!=null)close();
		} catch (Exception e) {
		}
	}
}
