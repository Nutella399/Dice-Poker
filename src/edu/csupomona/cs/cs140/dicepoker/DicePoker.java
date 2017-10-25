package edu.csupomona.cs.cs140.dicepoker;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class DicePoker {
	public static void main(String[] args) throws IOException {
		File leaderboardFile = new File("Leaderboard.txt");
		Scanner sc = new Scanner(System.in);
		mainMenu(sc, leaderboardFile);
	}
	
	public static void mainMenu(Scanner sc, File file) throws IOException{
	System.out.println("Welcome to DicePoker! Would you like to play the Game or look at the Leaderboard?");
	String line = sc.nextLine();
		if(line.equals("Game")) {
			game(sc, file);
		}else if(line.equals("Leaderboard")){
			Scanner fileScanner = new Scanner(file);
			String fileLine = "";

			while (fileScanner.hasNextLine()) {
				fileLine = fileScanner.nextLine();
				System.out.println(fileLine);
			}
			fileScanner.close();
		}else{
			System.out.println("That isn't an option.");
			mainMenu(sc, file);
		}
	}
	public static void game(Scanner sc, File file) throws IOException {
		int playerMoney = 500; 
		int houseMoney = 5000;
		int numOfHands = 0;
		boolean wantsToPlay = true;
		FileWriter writer = new FileWriter(file, true);
		String newline = System.getProperty("line.separator");
		
		while(playerMoney > 0 && houseMoney > 0 && numOfHands < 10 && wantsToPlay){
			int bet = bet(sc);
			int pool = 0; 
			playerMoney -= bet;
			houseMoney -= bet;
			pool += 2*bet; 
			int[] playerHand = handGenerator();
			int[] houseHand = handGenerator();
			Arrays.sort(playerHand);
			Arrays.sort(houseHand);
			System.out.println("Here is your hand!" + Arrays.toString(playerHand));
			int raise = raiseStakes(sc,bet);
			playerMoney -= raise; 
			houseMoney -= raise; 
			pool += 2*raise; 
			reRoll(sc, playerHand);
			Arrays.sort(playerHand);
			System.out.println("Here is your new hand!" + Arrays.toString(playerHand));
			System.out.println("Here is the houses hand" + Arrays.toString(houseHand));
			assignWinner(playerHand, houseHand, playerMoney, houseMoney, pool);
			wantsToPlay = again(sc);
			numOfHands++;
		}
		System.out.println("What is your name?");
		String name = sc.nextLine();
		writer.write("Gameplayer Name: " + name + " Playermoney: " + playerMoney + " Number of Hands: " + numOfHands + newline);
		writer.close();
	}
	
	public static int bet(Scanner sc) {
		System.out.println("What bet would you like to make? It has to be between $10 - $50.");
		int num = sc.nextInt();
		int bet = 0;
		if(num >= 10 && num <= 50){
			bet = num; 
		}else{
			System.out.println("That isn't a valid bet, try again.");
			bet(sc);
		} 
		return bet;
	}
	
	public static int[] handGenerator() {
		Random random = new Random();
		int[] hand = new int [6];
		for(int counter = 0; counter < hand.length; counter++) {
			hand[counter] = random.nextInt(5) + 1;
		}
		return hand;
	}
	
	public static int raiseStakes(Scanner sc, int bet) {
		int raisedStake = 0; 
		System.out.println("Would you like to raise the stakes?");
		sc.nextLine();
		String option = sc.nextLine();
		if(option.equals("Yes")){
			System.out.println("What do you want to raise it to? It can only go up to 2x your bet.");
			int possibleStake = sc.nextInt(); 
			if(possibleStake>= 10 && possibleStake <= 2*bet){
				possibleStake = raisedStake; 
				sc.nextLine();
			}else{
				System.out.println("That isn't a valid option, try again. ");
				raiseStakes(sc, bet);
			}
		}else if(option.equals("No")){
			raisedStake = 0;
			return raisedStake;
		}else {
			System.out.println("That isn't a valid option, try again. ");
			raiseStakes(sc,bet);
		}
		return raisedStake; 
	}
	
	public static void reRoll(Scanner sc, int[] array) {
		System.out.println("Would you like to Re-Roll any of your numbers? ");
		String rollLine = sc.nextLine();
		if(rollLine.equals("Yes")){
			System.out.println("What is the index of the one you would like to Re-Roll?");
			int num = sc.nextInt() -1; 
			Random random = new Random();
			array[num] = random.nextInt(5) + 1;
		} 
	}
	
	public static int occr(int[] numbers, int x) {
		int occuranceCounter = 0;
		for(int counter = 0; counter < numbers.length; counter++){
			if(numbers[counter] == x){
				occuranceCounter++;
			}
		}
		return occuranceCounter; 
	}
	
	public static int mode(int[] numbers) {
		int modeNum = 0; 
		int testingNum = 0; 
		for(int counter = 0; counter < numbers.length; counter++){
			if(testingNum < occr(numbers, numbers[counter])){
				testingNum = occr(numbers, numbers[counter]);
				modeNum = numbers[counter];
			}
		}		
		return modeNum; 
	}
	
	public static int mode(int[] numbers, int mode){
		int otherMode = 0;
		int testingNum = 0; 
		for(int counter = 0; counter < numbers.length; counter++){
			if(testingNum < occr(numbers, numbers[counter]) && numbers[counter] != mode){
				testingNum = occr(numbers, numbers[counter]);
				otherMode = numbers[counter];
			}
		}
		return otherMode; 
	}
	
	
	public static int assignHandType(int[] array) {
		int typeOfHand = 0;
		int mode = mode(array);
		int otherMode = mode(array, mode);
		if(occr(array, mode) == 5){
			typeOfHand = 1; 
		}else if(occr(array, mode) == 4) {
			typeOfHand = 2;
		}else if(occr(array, mode) == 3 && occr(array, otherMode) == 2) {
			typeOfHand = 3; 
		}else if(occr(array, mode) == 1){
			typeOfHand = 4; 
		}else if(occr(array, mode) == 3 && occr(array, otherMode) != 2) {
			typeOfHand = 5; 
		}else if(occr(array, mode) == 2 && occr(array, otherMode) == 2) {
			typeOfHand = 6; 
		}else if(occr(array, mode) == 2) {
			typeOfHand = 7; 
		}
		return typeOfHand; 
	}

	public static void assignWinner(int[] playerHand, int[] houseHand, int playerMoney, int houseMoney, int pool){ 
		int playerHandType = assignHandType(playerHand);
		int houseHandType = assignHandType(houseHand);
		if(playerHandType < houseHandType){
			playerMoney += pool; 
			System.out.println("You win this round!");
		}else {
			houseMoney += pool; 
			System.out.println("You lose this round!");
		}
	}
	
	public static boolean again(Scanner sc){
		sc.nextLine();
		boolean wantsToPlay = false; 
		System.out.println("Would you like to play again?");
		String line = sc.nextLine();
		if(line.equals("Yes")){
			wantsToPlay = true; 
		}else if(line.equals("No")) {
			wantsToPlay = false; 
		}else{
			System.out.println("That isn't an option.");
			again(sc);
		}
		return wantsToPlay; 
	}
}