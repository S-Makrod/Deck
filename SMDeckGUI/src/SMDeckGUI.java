//Saad Makrod
//Mr Jay
//ICS 4U1
import java.awt.*;
import javax.imageio.*; // allows image loading
import java.io.*; // allows file access
import javax.swing.*;
import java.awt.event.*;  // Needed for ActionListener
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class SMDeckGUI {//Beginning of SMDeckGUI
    public static void main(String[] args) {//Beginning of main method
        DeckGUIDemo window = new DeckGUIDemo();
        window.setVisible(true);
    }//End of the main method
}//End of the SMGUIDeck class

class DeckGUIDemo extends JFrame implements ActionListener {//Beginning of the DeckGUIDemo
    static Deck deck = new Deck();
    JComboBox dealer = new JComboBox();
    JComboBox group = new JComboBox();
    JComboBox num = new JComboBox();
    JTextField results = new JTextField(50);

    public DeckGUIDemo() {//constructor
        // 1... Create/initialize components

        JButton shuffleBtn = new JButton("Shuffle");
        shuffleBtn.addActionListener(this);
        JButton deal = new JButton("Deal");
        deal.addActionListener(this);
        JButton add = new JButton("Add");
        add.addActionListener(this);
        JButton search = new JButton("Search");
        search.addActionListener(this);
        JButton rank = new JButton("Sort Rank");
        rank.addActionListener(this);
        JButton suit = new JButton("Sort Suit");
        suit.addActionListener(this);

        results.setEditable(false);//cannot edit this textbox

        for (int i = 0; i < deck.getSize(); i++) {//run while i is less then deck size
            dealer.addItem(i);//fill combo box with indexs that can be removed
        }//end loop

        group.addItem("S");
        group.addItem("H");
        group.addItem("C");
        group.addItem("D");

        for (int i = 2; i <= 14; i++) {//run loop while i is less then 15
            num.addItem(i);//fill combo box with nums
        }//end loop

        // 2... Create content pane, set layout
        JPanel content = new JPanel();        // Create a content pane
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS)); // Use BorderLayout for panel
        JPanel north = new JPanel(); // where the buttons, etc. will be
        north.setLayout(new FlowLayout()); // Use FlowLayout for input area

        DrawArea board = new DrawArea(700, 400); // Area for cards to be displayed

        // 3... Add the components to the input area.
        north.add(shuffleBtn);
        north.add(rank);
        north.add(suit);
        north.add(deal);
        north.add(dealer);
        north.add(add);
        north.add(search);
        north.add(num);
        north.add(group);
        north.add(results);

        north.setPreferredSize(new Dimension(700, 100));

        north.setBorder(BorderFactory.createTitledBorder("Options Pane"));//border created
        board.setBorder(BorderFactory.createTitledBorder("Deck Pane"));//border created

        content.add(north, "North"); // Input area
        content.add(board, "South"); // Deck display area

        // 4... Set this window's attributes.
        setContentPane(content);
        pack();
        setTitle("Deck Demo");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);           // Center window.
    }//End DeckGUIDemo constructor

    public void actionPerformed(ActionEvent e) {//Actionperformed method
        if (e.getActionCommand().equals("Shuffle"))
            deck.shuffle(); // shuffle deck
        else if (e.getActionCommand().equals("Sort Rank"))
            deck.quickSort(); // sort deck
        else if (e.getActionCommand().equals("Add")) {
            int x = Integer.parseInt(num.getSelectedItem().toString());
            String y = group.getSelectedItem().toString();
            deck.add(x, y); // add card to deck
        } else if (e.getActionCommand().equals("Deal")) {
            int pos = Integer.parseInt(dealer.getSelectedItem().toString());
            deck.deal(pos); // remove card from deck
        } else if (e.getActionCommand().equals("Search")){
            int x = Integer.parseInt(num.getSelectedItem().toString());
            String y = group.getSelectedItem().toString();
            results.setText("Matches at position: " + Arrays.toString(deck.search(x, y))); // search deck
        }
        else if (e.getActionCommand().equals("Sort Suit"))
            deck.selectionSort(); // sort deck

        dealer.removeAll();//resize the dealer combobox to account for the new positions
        for (int i = 0; i<deck.getSize();i++){//run loop while i is less then the deck size
            dealer.addItem(i);
        }//end loop
        repaint(); // do after each action taken to update deck
    }//end action performed method

    class DrawArea extends JPanel {//beginning of drawArea
        public DrawArea(int width, int height) {//DrawArea start
            this.setPreferredSize(new Dimension(width, height)); // size
        }//DrawArea end

        public void paintComponent(Graphics g) {//paintComponent start
            deck.show(g);
        }//PaintComponent end
    }//end drawarea
}//End of DeckGUIDemo class

class Card {//Card class beginning
    private int rank, suit;
    private Image image;
    private static Image cardback; // shared image for back of card

    public Card(int cardNum)  // Creates card from 0-51
    {
        rank = cardNum % 13;
        suit = cardNum / 13;

        image = null;

        try {
            image = ImageIO.read(new File("cards\\" + (cardNum + 1) + ".gif")); // load file into Image object
            cardback = ImageIO.read(new File("cards\\b.gif")); // load file into Image object
        } catch (IOException e) {
            System.out.println("File not found");
        }
    }//end constructor

    public void show(Graphics g, int x, int y, Boolean faceup) { // draws card face up or face down
        if (faceup)//I changed the faceup variable so that I can flip cards for the search method, it is no longer a global variable
            g.drawImage(image, x, y, null);
        else
            g.drawImage(cardback, x, y, null);
    }//end show method
}//end card class

class Deck {//Deck class start
    private ArrayList<Card> deck;//deck hold the cards
    private ArrayList<Integer> storage;//storage hold the numerical value for each card in deck
    private ArrayList<Boolean> up;//up controls whether the card is face up or not

    public Deck() {//beginning of the deck constructor
        deck = new ArrayList<Card>();
        storage = new ArrayList<Integer>();
        up = new ArrayList<Boolean>();

        for (int x = 0; x < 52; x++)  // for each card in standard deck
        {
            deck.add(new Card(x)); // create card
            storage.add(x);
            up.add(true);
        }//end loop
    }//end constructor

    public void show(Graphics g)  // draws card face up or face down
    {
        for (int c = 0; c < deck.size(); c++) {//loop for the size of deck
            deck.get(c).show(g, c % 13 * 20 + 150, c / 13 * 50 + 20, up.get(c));//up is now controlling whether card is faceup
        }//end loop
    }//end show

    public void shuffle() {//shuffle start
        for (int i = 0; i < up.size(); i++)//loop for size of deck
            up.set(i,true);//all cards are made face up when shuffled

        for (int i = 0; i <= 100; i++) {//run for size of deck
            int x = (int)(Math.random()* deck.size());//random number saved in x
            deck.add(deck.remove(x));//deck swapped
            storage.add(storage.remove(x));//storage number moved with deck
            up.add(up.remove(x));//up moved too, although it is technically not necessary to do this since all cards are up anyway
        }//end loop
    }//end shuffle

    public void quickSort() {//quicksort method one start
        storage = quickSort(storage);//call the overloaded quicksort method

        deck.clear();//clear lists
        up.clear();

        for (int i = 0; i < storage.size(); i++){//for the size of the stored deck
            deck.add(new Card(storage.get(i)));//add the card again with its numerical value
            up.add(true);//the card is up
        }//end loop
    }//end quicksort

    public ArrayList<Integer> quickSort (ArrayList<Integer>A){//quicksort start
        quickSort(A,0,storage.size()-1);//arraylist is accepted and sent to the third quicksort method
        return A;//return the sorted arraylist
    }//quicksort end

    public void quickSort (ArrayList<Integer>A, int low, int high){//quicksort start
        if (low < high+1){//if low is less then high
            int p = partition (A, low, high);//partition is created
            quickSort(A, low, p-1);//create the left side partition
            quickSort(A, p+1, high);//create the right side partition
        }
    }//quicksort end

    public void swap (ArrayList<Integer>A, int index1, int index2){//swap started
        int temp = A.get(index1);//temp values
        A.set(index1,A.get(index2));//swap the two values
        A.set(index2, temp);
    }//end swap

    public int getPivot (int low, int high){//pivot started
        Random rand = new Random();//piviot is randomly picked
        return rand.nextInt((high-low)+1)+low;
    }//end getpivot

    public int partition (ArrayList<Integer>A, int low, int high){//partition start
        swap (A, low, getPivot(low, high));//call swap
        int border = low + 1;//border created
        for (int i = border; i <= high; i++){//run loop while i is less then high
            if (A.get(i)%13 < A.get(low)%13)//modulus used to ignore suit, if number at i is less then number at low
                swap (A,i,border++);//swap
        }//end loop

        swap(A,low,border-1);//swap again
        return border-1;//return border-1
    }//partition end

    public void add(int num, String group) {//beginning of add
        for (int i = 0; i < up.size(); i++)//run for size of deck
            up.set(i,true);//all cards made faceup

        int g = 0;

        if (group.equals("S")) {//according to the card file, if it is spade g is 0
            g = 0;
        } else if (group.equals("H")) {//if it is hearts g is 13
            g = 13;
        } else if (group.equals("C")) {//if it is clubs g is 26
            g = 26;
        } else if (group.equals("D")) {//if it is diamonds g is 39
            g = 39;
        }

        g = g + num-2;//add num to the g value which is the numerical value of the card itself, this results in the card file

        storage.add(g);//add g to storage
        up.add(true);//card is up
        deck.add(new Card(g));//add the card to the deck
    }//end of add

    public void deal(int pos) {//remove start
        deck.remove(pos);//remove at pos
        storage.remove(pos);
        up.remove(pos);
    }//end remove

    public int [] search(int num, String group) {//search start
        int g = 0;//same as aa, solving for the card file number

        if (group.equals("S")) {
            g = 0;
        } else if (group.equals("H")) {
            g = 13;
        } else if (group.equals("C")) {
            g = 26;
        } else if (group.equals("D")) {
            g = 39;
        }

        g = g + num-2;
        int x = 0;
        int [] temp = new int [storage.size()];//array created

        deck.clear();//clear deck and up
        up.clear();

        for (int i = 0; i < storage.size(); i++){//run for the sored deck size
            if (storage.get(i) == g){// if the numerical value of the card is equal to the calculated value (g)
                temp [x] = i;//add i to temp
                x++;
                deck.add(new Card (storage.get(i)));//add the card
                up.add(true);//the card is face up
            }
            else {
                deck.add(new Card(storage.get(i)));//add card
                up.add(false);//the card is face down
            }
        }//end loop

        int [] match = new int [x];//new array made off of x

        for (int i = 0; i < match.length; i++){//run loop fill new array with values from temp
            match [i] = temp [i];
        }//end loop
        return match;//return array to be outputted
    }//end of search

    public void selectionSort() {//selectionsort start
        for (int i = 0; i < storage.size(); i++){//run loop for storage size
            int x = i;// save i in in x
            for (int j = i+1; j < storage.size();j++){//run loop for storage size
                if (storage.get(j)<storage.get(x))// if val at j is less then val at x
                    x=j;//x equal j
            }//end loop
            int temp = storage.get(i);//temp is val at i
            storage.set(i,storage.get(x));//val at i is now the val at x
            storage.set(x,temp);//val at x in now the val at i
        }//end loop

        deck.clear();//clear lists
        up.clear();

        for (int i = 0; i < storage.size(); i++){//run loop for size of deck
            deck.add(new Card(storage.get(i)));//fill up deck with the sorted numbers
            up.add(true);
        }//end loop
    }//end selectionsort

    public int getSize() {//get size
        return deck.size();//used to get deck size out of class
    }//end deck size
}//end class deck
