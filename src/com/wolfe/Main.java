/*

    Lab 3: Advanced Problem 2 - Prototyping/Agile Go Fish Card Game
    Jeremy Wolfe 09/16/2016

    Version 2.0

    This version adds data structures to hold cards. Logic fully updated to process individual cards.

    An ArrayList is used to hold the deck of cards with the data being a two position alphanumeric
    representing the rank and suit.

    HashMaps are used to hold player hands and completed books. Data in the form of key=rank, arraylist
    holds the suits.

    This is a fully functioning game.
    TODO end of game... fix logic for player has no cards, computer has 3, one card in pool


    Jeremy Wolfe 09/09/2016

    Version 1.0

    This program plays the children's card game Go Fish - the computer against one player.
    Rules for the game can be found at https://en.wikipedia.org/wiki/Go_Fish

    Version 1.0 is a prototype focusing on the simplest game. The main focus is to establish
    the logic flow without implementing the actual mechanics of the individual logic requirements.
    Game play simulated by displaying console messages for each particular action.

    Player decisions made by binary random computer choice. The number of books formed is used to control
    the end of the game. Two card types are used for this prototype - a fizzbit and widget. All of the
    logic paths seem to be taken with the simulation as currently programmed.

    Certain items are not programmed for this prototype:
        - no actual deck of 52 cards is used, individual cards are not matched
        - books are arbitrarily formed by random flip of the coin
        - the number of cards dealt, held by each player, and taken out of play by forming a book, is not tracked
        - no formal computer game-play strategy is programmed in this prototype
        - no provisions made for passing multiple cards from one player to another

     Items that are programmed into the prototype:
        - the outmost game play loop
        - the inner game loop controlling the flow of the game and individual taking of turns per game rules
        - taking a card from the pool
        - asking opponent for a particular card
        - checking held hand for match card from opponents request
        - giving the current player another round of play if the card drawn from the pool matches the player's
          requested card
        - books formed are counted for each player
        - cards taken from pool are counted although the count is not used to control any logic
        - logic flow is random, each time game is played a different outcome occurs
*/
package com.wolfe;

import java.util.*;

public class Main {

    //Create two scanners, one for Strings, and one for numbers - int and float values.

    //Use this scanner to read text data that will be stored in String variables
    static Scanner stringScanner = new Scanner(System.in);
    //Use this scanner to read in numerical data that will be stored in int or double variables
    static Scanner numberScanner = new Scanner(System.in);

    static int playerBookCount = 0;     // player book counts
    static int computerBookCount = 0;
    static int cardsRemaining = 52;
    static int bookCount = 13;              // 13 books of 4 cards in a deck,
                                            // used to track end of game when equal to zero
    static boolean deBug = true;           // testing flag used to output game state information
    static boolean autoPlay = true;         // used for testing - computer picks card for player

    public static void main(String[] args) {

        // playing card data structures:
        // deck of cards
        ArrayList<String> initialDeck = new ArrayList<String>(Arrays.asList("AH", "AS", "AC", "AD",
                "2H", "2S", "2C", "2D",
                "3H", "3S", "3C", "3D",
                "4H", "4S", "4C", "4D",
                "5H", "5S", "5C", "5D",
                "6H", "6S", "6C", "6D",
                "7H", "7S", "7C", "7D",
                "8H", "8S", "8C", "8D",
                "9H", "9S", "9C", "9D",
                "1H", "1S", "1C", "1D",
                "JH", "JS", "JC", "JD",
                "QH", "QS", "QC", "QD",
                "KH", "KS", "KC", "KD" ));

        ArrayList<String> deck = new ArrayList<String>();       // shuffled deck

        // HashMaps for held hands of dealt cards
        HashMap<String, ArrayList<String>> pHandMap = new HashMap<String, ArrayList<String>>(); // player hand
        HashMap<String, ArrayList<String>> cHandMap = new HashMap<String, ArrayList<String>>(); // computer hand
        HashMap<String, ArrayList<String>> pBookMap = new HashMap<String, ArrayList<String>>(); // player books
        HashMap<String, ArrayList<String>> cBookMap = new HashMap<String, ArrayList<String>>(); // computer books

        // start of code

        // get player name
        String name = getPlayersName();
        String playerChoice = "y";          // play another game flag

        // outermost do-while loop until player quits the game
        do {

            deck = initializeGame(initialDeck, pHandMap, cHandMap, pBookMap, cBookMap);

            String playerTurnFlag = "p";    // p = human, c = computer


            // computer shuffles the deck and deals seven cards to each player
            shuffleDeck(deck);
            dealCards(deck, pHandMap);  // deal seven cards to player
            dealCards(deck, cHandMap);  // deal seven cards to computer


            // inner game play do-while loop until all books formed (book count = 0)
            // player and computer alternate turns based on playerTurnFlag
            do {
                if (playerTurnFlag.equals("p")) {
                    System.out.println("*************  Start Player's   Turn  ****************");
                } else {
                    System.out.println("*************  Start Computer's Turn  ****************");
                }
                System.out.println("Player hand = " + pHandMap);
                System.out.println("Computer hand = " + cHandMap);
                System.out.println();
                System.out.println("Player books = " + pBookMap);
                System.out.println("Computer books = " + cBookMap);
                System.out.println();
                System.out.println("Remaining books = " + bookCount + "  Cards remaining = " + cardsRemaining +
                 " player books = " + playerBookCount + " computer books = " + computerBookCount);


                // player or computer asks for a card
                String requestedCard = playerAskOpponentForCard(playerTurnFlag, cHandMap, pHandMap);

                /* opponent looks for match, if found, gives player the card(s)
                   if not found, method tells player to go fish */
                String cardGiven = checkForRequestedMatch(playerTurnFlag, requestedCard,
                                                            pHandMap, cHandMap);

                if (deBug) {
                    System.out.println("cardGiven = " + cardGiven);
                    System.out.println("requestedCard = " + requestedCard);
                }

                /* if player given card(s), put card(s) in player's hand and see if book formed */
                if (cardGiven.equals(requestedCard)) { // opponent has requested card(s), player's turn is over
                    playerReceivesRequested(playerTurnFlag, requestedCard,
                                            pHandMap, cHandMap);
                    playerTurnFlag = flipPlayerTurn(playerTurnFlag);
                } else {
                    /* if player fishing for card player gets card from pool (computer deals player card from deck)
                       if drawn card matches player last card request, current player gets another turn,
                       otherwise opponent takes a turn */
                    if (cardGiven.equals("no match")) { // opponent does not have requested card

                        String newCardDealt = " ";

                        if (deck.size() > 0) {
                            newCardDealt = dealOneCard(deck);

                            if (playerTurnFlag.equals("p")) {
                                addCardToHand(newCardDealt, pHandMap);
                            } else {
                                addCardToHand(newCardDealt, cHandMap);
                            }
                            if (deBug) {
                                System.out.println("requestedCard = " + requestedCard);
                                System.out.println("newCardDealt = " + newCardDealt);
                            }
                        }

                        String rank = String.valueOf(newCardDealt.charAt(0));
                        rank = rank.toUpperCase();

                        if (rank.equals(requestedCard)) {
                            System.out.println(" ***    current player gets another turn    ***");
                        } else {
                            playerTurnFlag = flipPlayerTurn(playerTurnFlag);
                        }
                    }
                }
                checkForBook("p", pHandMap, pBookMap);
                checkForBook("c", cHandMap, cBookMap);
             }
            // check book count, if 0 end game
            while (bookCount > 0);

            // ask player if they want to play another game
            playerChoice = getPlayerContinue();

        } // end main game loop
        while (playerChoice.equals("y") || playerChoice.equals("Y"));

        // Close scanners. Good practice to clean up resources you use.
        // Don't try to use scanners after this point. All code that uses scanners goes above here.
        stringScanner.close();
        numberScanner.close();
    } // end of main method

    // initialize game data structures
    private static ArrayList<String> initializeGame(ArrayList<String> initialDeck,
                                                    HashMap<String, ArrayList<String>> pHandMap,
                                                    HashMap<String, ArrayList<String>> cHandMap,
                                                    HashMap<String, ArrayList<String>> pBookMap,
                                                    HashMap<String, ArrayList<String>> cBookMap) {

        //http://stackoverflow.com/questions/715650/how-to-clone-arraylist-and-also-clone-its-contents
        // copy contents of initialDeck to deck
        ArrayList<String> deck = new ArrayList<String>(initialDeck);

        // reset hands and books
        pHandMap.clear();
        cHandMap.clear();
        pBookMap.clear();
        cBookMap.clear();

        playerBookCount = 0;
        computerBookCount = 0;
        cardsRemaining = 52;
        bookCount = 13;

        return deck;
    }

    // add a card to player's hand
    private static void addCardToHand(String newCardDealt,
                                      HashMap<String, ArrayList<String>> handMap) {

        String rank = String.valueOf(newCardDealt.charAt(0));
        rank = rank.toUpperCase();
        String suit = String.valueOf(newCardDealt.charAt(1));
        suit = suit.toUpperCase();


        // attempt to get existing key in map
        ArrayList<String> itemsArray = handMap.get(rank);

        // if key not found, create a new ArrayList and add item
        if (itemsArray == null) {
            ArrayList<String> myArray = new ArrayList<String>();
            myArray.add(suit);
            handMap.put(rank, myArray);   // add new key, value pair to HashMap
        } else {  // add item to existing ArrayList if not a duplicate
            if (!itemsArray.contains(suit)) {
                itemsArray.add(suit);
            }
        }

    }

    // randomly shuffle the deck using the Collections method
    private static void shuffleDeck(ArrayList<String> deck) {
        Collections.shuffle(deck);
    }

    // flip value of player turn flag
    private static String flipPlayerTurn(String flag) {
        System.out.println("Flipping player turn");
        if (flag.equals("p")) {
            return "c";
        } else {
            return "p";
        }
    }

    // deal one card to current player and decrement card count (remove card from pool)
    private static String dealOneCard(ArrayList<String> deck) {
        if (deck.size() > 0) {
            String dealt = deck.remove(0);
            cardsRemaining--;
            System.out.println();
            System.out.println("One card dealt from deck: " + dealt);
            System.out.println();
            return dealt;
        } else {
            System.out.println("***   No  More  Cards  In  The  Pool   ***");
            return null;
        }
    }

    // player gets the requested card and places in their hand.
    // another method will be called to determine if a book can be made from the player's hand
    private static void playerReceivesRequested(String flag, String requestCard,
                                               HashMap<String, ArrayList<String>> pHandMap,
                                               HashMap<String, ArrayList<String>> cHandMap) {
        if (deBug) {
            System.out.println("Before transfer");
            System.out.println("Player hand = " + pHandMap);
            System.out.println("Computer hand = " + cHandMap);
            System.out.println("requestCard = " + requestCard);
        }

        if (flag.equals("p")) {
            // get card(s) from computer's hand
            // attempt to get existing key in map
            ArrayList<String> sourceArray = cHandMap.get(requestCard);
            ArrayList<String> destArray = pHandMap.get(requestCard);

            if (deBug) {
                System.out.println("sourceArray.size() = " + sourceArray.size());
                System.out.println("sourceArray = " + sourceArray);
                System.out.println("destArray = " + destArray);
            }

            if (destArray != null) {
                for ( String test : sourceArray ) {
                    destArray.add(test);
                }
            }

            cHandMap.remove(requestCard);

        } else {
            // get card(s) from player's hand and
            // put card(s) in computer's hand
            ArrayList<String> sourceArray = pHandMap.get(requestCard);
            ArrayList<String> destArray = cHandMap.get(requestCard);

            if (deBug) {
                System.out.println("sourceArray = " + sourceArray);
                System.out.println("destArray = " + destArray);
                System.out.println("sourceArray.size() = " + sourceArray.size());
            }

            if (sourceArray != null) {
                for ( String test : sourceArray ) {
                    destArray.add(test);
                }
            }

            pHandMap.remove(requestCard);
        }
        if (deBug) {
            System.out.println("After transfer");
            System.out.println("Player hand = " + pHandMap);
            System.out.println("Computer hand = " + cHandMap);
        }
    }

    // move a book from player hand to player book HashMap
    private static void checkForBook(String flag,
                                     HashMap<String, ArrayList<String>> handMap,
                                     HashMap<String, ArrayList<String>> bookMap) {


        // http://stackoverflow.com/questions/1884889/iterating-over-and-removing-from-a-map
        // add iterator to avoid concurrency error when removing entry
     //   String key = " ";
        Iterator<Map.Entry<String, ArrayList<String>>> iter = handMap.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<String, ArrayList<String>> entry = iter.next();

                ArrayList<String> newList = new ArrayList<String>();
                String key = entry.getKey();
                ArrayList<String> list = entry.getValue();

                if (list.size() == 4) {

                    for (int i = 0 ; i < list.size() ; i++) {
                        newList.add(list.get(i));
                    }

                    if (deBug) {
                        System.out.println("newList = " + newList);
                    }

                    // create a new book
                    bookMap.put(key, newList);   // add new key, value pair to HashMap
                    iter.remove();
                    bookCount--;
                    if (flag.equals("p")) {
                        playerBookCount++;
                    } else {
                        computerBookCount++;
                    }

                    if (deBug) {
                        System.out.println("playerBookCount = " + playerBookCount);
                        System.out.println("computerBookCount = " + computerBookCount);
                        System.out.println("bookCount = " + bookCount);
                        System.out.println("handMap = " + handMap);
                        System.out.println("bookMap = " + bookMap);
                    }
                }
            }
    }


    // compare requestCard to held hand,
    // if found, return card literal
    // if not found, return "no match" literal
    private static String checkForRequestedMatch(String playerFlag, String requestCard,
                                                 HashMap<String, ArrayList<String>> pHandMap,
                                                 HashMap<String, ArrayList<String>> cHandMap) {

        if (deBug) {
            System.out.println("entering checkForRequestedMatch");
            System.out.println("bookCount = " + bookCount);
            System.out.println("playerFlag = " + playerFlag);
            System.out.println("requestCard = " + requestCard);
        }

        if (playerFlag.equals("p")){
            boolean keyFound = cHandMap.containsKey(requestCard);
            if (keyFound) {
                System.out.println("Computer replies: Here is your card");
                return requestCard;
            } else {
                System.out.println("Computer replies: Go Fish");
                return "no match";
            }
        } else {
            boolean keyFound = pHandMap.containsKey(requestCard);
            if (keyFound) {
                System.out.println("Player replies: Here is your card");
                return requestCard;
            } else {
                System.out.println("Player replies: Go Fish");
                return "no match";
            }
        }
    }

    // scan cards and determine groups of ranks. ask for type of highest quantity rank group, or
    // or... if they don't have the last card drawn rank, ask for that rank
    private static String playerAskOpponentForCard(String flag, HashMap<String, ArrayList<String>> cHandMap,
                                                                HashMap<String, ArrayList<String>> pHandMap) {
        if (flag.equals("p")){
            if (autoPlay && bookCount >= 3) {
                System.out.print("Player Auto Request: ");
                String requestCard = computeRequest(pHandMap);
                System.out.println(requestCard);
                System.out.println("Player asks: Computer, do you have a " + requestCard + "?");
                return requestCard;
            } else {
                System.out.println("Player: What card (rank) would you like?");
                System.out.println("Player: Enter a single digit alphanumeric:");
                String requestCard = stringScanner.nextLine();
                System.out.println();
                System.out.println("Player asks: Computer, do you have a " + requestCard + "?");
                requestCard = requestCard.toUpperCase();
                return requestCard;
            }
        } else {
            String requestCard = computeRequest(cHandMap);
            System.out.println();
            System.out.println("Computer asks: Player, do you have a " + requestCard + "?");
            return requestCard;
        }
    }

    // computer logic to ask the player for a card held in computer's hand
    // if card rank has more than one card, ask for that rank
    // otherwise return the last card rank found
    private static String computeRequest(HashMap<String, ArrayList<String>> handMap) {

        String key = " ";
        for (HashMap.Entry<String, ArrayList<String>> entry : handMap.entrySet()) {
            key = entry.getKey();
            ArrayList<String> list = entry.getValue();

            if (list.size() > 1) {
                return key;
            }
        }
        return key;
    }

    // deal seven cards to each player and populate the HashMap
    private static void dealCards(ArrayList<String> deck,
                                  HashMap<String, ArrayList<String>> handMap) {

        for ( int i = 0 ; i < 7 ; i++ ) {
            String card = dealOneCard(deck);
            String rank = String.valueOf(card.charAt(0));
            String suit = String.valueOf(card.charAt(1));

            // attempt to get existing key in map
            ArrayList<String> itemsArray = handMap.get(rank);

            // if key not found, create a new ArrayList and add item
            if (itemsArray == null) {
                ArrayList<String> myArray = new ArrayList<String>();
                myArray.add(suit);
                handMap.put(rank, myArray);   // add new key, value pair to HashMap
            } else {  // add item to existing ArrayList if not a duplicate
                if (!itemsArray.contains(suit)) {
                    itemsArray.add(suit);
                }
            }
        }

        if (deBug) {
            System.out.println("Hand dealt: " + handMap);
        }
    }

    // report end of game stats, get player input to continue game or not
    private static String getPlayerContinue() {
        System.out.println();
        System.out.println("************  End of Game  *************");
        System.out.println("Player Book Count = " + playerBookCount);
        System.out.println("Computer Book Count = " + computerBookCount);
        System.out.println("Card count remaining: " + cardsRemaining);
        System.out.println("Do you want to play another game (y or n): ");
        return stringScanner.nextLine();
    }

    // get player name for display during game play
    // TODO add display of name to sysout during game play
    private static String getPlayersName() {
        System.out.println("*************   Start of Game   ***************");
        System.out.println("Please enter your name: ");
        String name = stringScanner.nextLine();
        System.out.println();
        System.out.println("Welcome to the game of GO FISH!");
        System.out.println("The player will ask the computer for a card. The player must have");
        System.out.println("that card in their hand. Enter a single character e.g.");
        System.out.println("a = Ace, k = King, etc. 2 = 2, 1 = 10");
        System.out.println();
        return name;
    }

} // end of Main class
