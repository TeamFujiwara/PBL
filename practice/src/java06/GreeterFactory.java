package java06;

public class GreeterFactory {
	public static Greeter getGreeter(Account guest) {
		switch(guest.getCountry()) {
		case JAPAN:
			return new JapaneseGreeter();
		case USA:
			return new EnglishGreeter();
        default:
            return new JapaneseGreeter();

		}
	}
}
