/*
 * Bowler.java
 *
 * Version:
 *     $Id$
 *
 * Revisions:
 *     $Log: Bowler.java,v $
 *     Revision 1.3  2003/01/15 02:57:40  ???
 *     Added accessors and and equals() method
 *
 *     Revision 1.2  2003/01/12 22:23:32  ???
 *     *** empty log message ***
 *
 *     Revision 1.1  2003/01/12 19:09:12  ???
 *     Adding Party, Lane, Bowler, and Alley.
 *
 */

/**
 *  Class that holds all bowler info
 *
 */

public class Bowler {

    private String fullName;
    private String nickName;
    private String email;
	private String date;
	private String score;

    public Bowler( String nickName, String fullName, String email, String date, String score ) {
		this.nickName = nickName;
		this.fullName = fullName;
		this.email = email;
		this.date = date;
		this.score = score;
    }

    public String getNickName() {
        return nickName;
    }

	public String getFullName ( ) {
			return fullName;
	}

	public String getEmail ( ) {
		return email;	
	}

	public String getDate() {
		return date;
	}

	public String getScore() {
		return score;
	}

	public String toString() {
		return nickName + "\t" + date + "\t" + score;
	}

}