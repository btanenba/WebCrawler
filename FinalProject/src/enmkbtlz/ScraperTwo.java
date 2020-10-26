package enmkbtlz;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScraperTwo extends Thread {
	Shared shared;
	String currentHtml;
	String SQL;

	public ScraperTwo(Shared shared) {
		this.shared = shared;
	}

	public void run() {
		try {
			Thread.sleep(60000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// read the next html get the links and put on linkQueue

		Pattern patternInternalLink = Pattern.compile("(https?://\\S+.touro.edu(\\S+)?)(\"\\S*)");

		Pattern patternExternalLink = Pattern.compile("(https?://(?!\\S*\\.touro\\.edu)\\S*)(\"\\S*)");

		Pattern patternPhone = Pattern.compile(("(1?)(\\(|-|\\s)?(\\d{3})(\\)?)((-|\\s)\\d{3}(-|\\s)\\d{4})"));

		Pattern patternEmail = Pattern.compile("(href\\S+:)(\\S+@\\S+(\\.\\S+)+)(\"\\S*)");

		Pattern patternDate = Pattern.compile("(\\S+>)(\\S+ \\d{2}, \\d{4})");

		Pattern patternAddress = Pattern.compile(
				"(<br/>)((\\d+-?\\d*)(\\s*)((\\w*\\s*)+,?))(<br/>)?(((\\w*\\s*)+,)(\\s*)([A-Z]{2})(\\s*)(\\d{5}|\\w*))");
		while (!shared.htmlQueue.isEmpty()) {

			synchronized (shared.htmlQueue) {
				currentHtml = shared.htmlQueue.poll();
			}
			getInternalLinks(currentHtml, shared.internalLinks, patternInternalLink);

			getInfo(currentHtml, patternExternalLink, shared.externalLinks, 1, "ExternalLinks");
			getInfoTwo(currentHtml, patternPhone, shared.phoneNumbers, "PhoneNumbers", 3, 5);
			getInfo(currentHtml, patternEmail, shared.emailAddresses, 2, "EmailAddresses");
			getInfo(currentHtml, patternDate, shared.dates, 2, "Dates");
			getInfoTwo(currentHtml, patternAddress, shared.addresses, "Addresses", 2, 8);
			if (shared.htmlQueue.isEmpty()) {
				try {
					Thread.sleep(80000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}

		}

	}

	public void getInternalLinks(String html, ArrayList<String> list, Pattern patternInternalLink) {
		String link;
		Matcher matcherInternalLink;
		matcherInternalLink = patternInternalLink.matcher(html);
		while (matcherInternalLink.find()) {
			link = matcherInternalLink.group(1);

			// sync hashset so that if both threads are holding the same
			// link both dont add it

			synchronized (shared.internalLinks) {

				if (!link.contains("facebook") && !list.contains(link)) {

					shared.internalLinks.add(link);

					shared.linkQueue.offer(link);

					SQL = "INSERT INTO InternalLinks VALUES(" + shared.counter + ", '" + link
							+ "'); INSERT INTO LinkQueue VALUES('" + link + "');";
					execute();
				}
			}
		}
	}

	public void getInfo(String html, Pattern pattern, ArrayList<String> list, int display, String tableName) {
		Matcher matcher = pattern.matcher(html);

		while (matcher.find()) {
			synchronized (list) {

				if (!list.contains(matcher.group(display))) {
					list.add(matcher.group(display));

					SQL = "INSERT INTO " + tableName + " VALUES(" + shared.counter + ", '" + matcher.group(display)
							+ "');";
					execute();

				}
			}
		}
	}

	public void getInfoTwo(String html, Pattern pattern, ArrayList<String> list, String tableName, int display,
			int displayTwo) {
		Matcher matcher = pattern.matcher(html);
		String info;

		while (matcher.find()) {
			info = matcher.group(display) + matcher.group(displayTwo);
			synchronized (list) {
				if (!list.contains(info)) {
					list.add(info);

					SQL = "INSERT INTO " + tableName + " VALUES(" + shared.counter + ", '" + info + "');";
					execute();
				}
			}
		}
	}

	public void execute() {
		try {
			shared.stmnt.execute(SQL);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}