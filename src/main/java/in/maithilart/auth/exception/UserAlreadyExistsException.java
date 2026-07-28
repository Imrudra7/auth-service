package in.maithilart.auth.exception;

public class UserAlreadyExistsException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7416809682651084705L;
	
	public UserAlreadyExistsException(String message) {
		
		super(message);
	}
}
