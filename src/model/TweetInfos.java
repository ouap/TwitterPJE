package model;

public class TweetInfos {
	private long id;
	private String tweet;
	private String user;
	private int note;
	private String date;
	private String search;

	public TweetInfos(long id, String user, String tweet, String date,
			String search, int note) {
		this.id = id;
		this.tweet = tweet;
		this.user = user;
		this.note = note;
		this.date = date;
		this.search = search;
	}

	public long getId() {
		return id;
	}

	public String getTweet() {
		return tweet;
	}

	public void setTweet(String tweet) {
		this.tweet = tweet;
	}

	public String getUser() {
		return user;
	}

	public int getNote() {
		return note;
	}

	public void setNote(int note) {
		this.note = note;
	}

	public String getSearch() {
		return search;
	}

	public void setSearch(String search) {
		this.search = search;
	}

	public String getDate() {
		return date;
	}
}
