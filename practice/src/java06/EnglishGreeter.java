package java06;

public class EnglishGreeter implements Greeter {

	@Override
	public void login(Account guest) {
		// TODO 自動生成されたメソッド・スタブ
		System.out.println(String.format("Hello, %s", guest.getName()));
	}

	@Override
	public void logout(Account guest) {
		// TODO 自動生成されたメソッド・スタブ
		System.out.println(String.format("Bye, %s", guest.getName()));
	}

}
