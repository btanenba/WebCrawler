package enmkbtlz;


import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Shared {
	public Shared() {

	}

	Queue<String> htmlQueue = new LinkedList<>();
	Queue<String> linkQueue = new LinkedList<>();

	ArrayList<String> externalLinks = new ArrayList<>();
	ArrayList<String> phoneNumbers = new ArrayList<>();
	ArrayList<String> emailAddresses = new ArrayList<>();
	ArrayList<String> dates = new ArrayList<>();
	ArrayList<String> addresses = new ArrayList<>();
	ArrayList<String> internalLinks = new ArrayList<>();

	int counter = 1;
	

	Statement stmnt;
	
	String currentUrl = "https://www.touro.edu/";
}
