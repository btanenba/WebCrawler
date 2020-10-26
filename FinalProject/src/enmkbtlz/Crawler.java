package enmkbtlz;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.ResultSet;
//import java.util.ArrayList;
//import java.util.LinkedList;
//import java.util.Queue;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
import java.sql.SQLException;

public class Crawler extends Thread {
	long start;
	long end;
	Shared shared;
	ResultSet results;
	String SQL;
	Scraper scraper;
	ScraperTwo scraperTwo;

	public Crawler(Shared shared, Scraper scraper, ScraperTwo scraperTwo) {
		this.shared = shared;
		this.scraper = scraper;
		this.scraperTwo = scraperTwo;
	}

	public void run() {

		String url = null;

		results = getSQL("SELECT Link FROM LinkQueue where LinkId = (SELECT MIN (LinkId)from LinkQueue )");

		try {
			if (!results.isBeforeFirst()) {
				url = "https://www.touro.edu/";
				shared.internalLinks.add("https://www.touro.edu/");

				SQL = "INSERT INTO InternalLinks VALUES(" + shared.counter + ", 'https://www.touro.edu/');";
				execute();
			}

			else {
				fillProgram(shared);

			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		start = System.currentTimeMillis();
		shared.htmlQueue.offer(getHtml(url));
		end = System.currentTimeMillis();

		scraper.start();
		scraperTwo.start();

		sleepTime();

		sleepTwo();

		while (!shared.linkQueue.isEmpty()) {
			url = shared.linkQueue.poll();

			shared.currentUrl = url;

			setCounter();

			shared.counter++;

			SQL = ("UPDATE Counter SET PageNumber = " + shared.counter + " WHERE PageNumber = " + (shared.counter - 1));

			execute();

			SQL = "DELETE FROM LinkQueue WHERE Link = '" + url + "'";
			execute();

			start = System.currentTimeMillis();
			shared.htmlQueue.offer(getHtml(url));
			end = System.currentTimeMillis();
			sleepTime();

			sleepTwo();
		}

	}

	public void execute() {
		try {
			shared.stmnt.execute(SQL);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void setCounter() {

		results = getSQL("SELECT * FROM Counter");

		try {
			if (!results.isBeforeFirst()) {
				SQL = "Insert into Counter values (1)";
				execute();

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void sleepTwo() {
		if (shared.linkQueue.isEmpty()) {
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}
	}

	private void fillProgram(Shared shared) throws Exception {

		ResultSet results = getSQL("SELECT * FROM LinkQueue");
		while (results.next()) {
			shared.linkQueue.add(results.getString(2));
		}

		ResultSet resultsInternal = getSQL("SELECT * FROM InternalLinks");
		while (resultsInternal.next()) {
			shared.internalLinks.add(resultsInternal.getString(3));
		}

		ResultSet resultsExternal = getSQL("SELECT * FROM ExternalLinks");

		while (resultsExternal.next()) {
			shared.externalLinks.add(resultsExternal.getString(3));
		}

		ResultSet resultsPhone = getSQL("SELECT * FROM PhoneNumbers");

		while (resultsPhone.next()) {
			shared.phoneNumbers.add(resultsPhone.getString(3));
		}

		ResultSet resultsEmail = getSQL("SELECT * FROM EmailAddresses");
		while (resultsEmail.next()) {
			shared.emailAddresses.add(resultsEmail.getString(3));
		}

		ResultSet resultsAddresses = getSQL("SELECT * FROM Addresses");
		while (resultsAddresses.next()) {
			shared.addresses.add(resultsAddresses.getString(3));
		}

		ResultSet resultsDates = getSQL("SELECT * FROM Dates");
		while (resultsDates.next()) {
			shared.dates.add(resultsDates.getString(3));
		}

		ResultSet resultsCounter = getSQL("SELECT * FROM Counter");
		while (resultsCounter.next()) {
			shared.counter = Integer.parseInt(resultsCounter.getString(1));
		}
	}

	public void sleepTime() {
		if ((end - start) / 10000 > 10) {
			try {
				Thread.sleep((end - start) * 2);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	public ResultSet getSQL(String SQL) {

		try {
			results = shared.stmnt.executeQuery(SQL);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return results;
	}

	public boolean getNext(ResultSet results) {

		boolean next = false;
		try {
			next = results.next();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (next)
			return true;
		else
			return false;
	}

	public String getHtml(String webPage) {
		try {
			URL url = new URL(webPage);
			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
			StringBuilder sb = new StringBuilder();
			String line;
			// getting each line from the html page
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			return sb.toString();
		} catch (Exception e) {
			return "ERROR";
		}
	}

}
