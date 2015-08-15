package java06;

public class Main {

	public static void main(String[] args) {
		// TODO 自動生成されたメソッド・スタブ
		Account guest = new Account(args[0],Country.valueOf(args[1]));
		Greeter greeter = GreeterFactory.getGreeter(guest);
		greeter.login(guest);
		greeter.logout(guest);
	}

}
